package ru.tinkoff.fintech.service.transaction

import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import ru.tinkoff.fintech.client.CardServiceClient
import ru.tinkoff.fintech.client.ClientService
import ru.tinkoff.fintech.client.LoyaltyServiceClient
import ru.tinkoff.fintech.db.entity.LoyaltyPaymentEntity
import ru.tinkoff.fintech.db.repository.LoyaltyPaymentRepository
import ru.tinkoff.fintech.model.*
import ru.tinkoff.fintech.service.cashback.CashbackCalculator
import ru.tinkoff.fintech.service.notification.NotificationService
import java.time.LocalDateTime
import java.time.LocalDateTime.now

@Service
class TransactionServiceImpl(
    private val cardServiceClient: CardServiceClient,
    private val clientService: ClientService,
    private val loyaltyServiceClient: LoyaltyServiceClient,
    private val cashbackCalculator: CashbackCalculator,
    private val notificationService: NotificationService,
    private val loyaltyPaymentRepository: LoyaltyPaymentRepository
) : TransactionService {

    override fun handle(transaction: Transaction) = runBlocking {
        val card = cardServiceClient.getCard(transaction.cardNumber)
        val clientTask = async {
            clientService.getClient(card.client)
        }

        val loyaltyProgram = loyaltyServiceClient.getLoyaltyProgram(card.loyaltyProgram)

        val client = clientTask.await()
        val cashback = calculateCashBack(loyaltyProgram, transaction, client)

        val transactionData = TransactionData(transaction, card, cashback)
        launch {
            saveLoyaltyPayment(transactionData)
        }
        sendNotification(transactionData, loyaltyProgram, client)
    }

    private fun sendNotification(transactionData: TransactionData, loyaltyProgram: LoyaltyProgram, client: Client) {
        with(transactionData) {
            NotificationMessageInfo(
                client.firstName!!, card.cardNumber, cashback, transaction.value, loyaltyProgram.name, time
            ).also {
                notificationService.sendNotification(client.id, it)
            }
        }
    }

    private suspend fun saveLoyaltyPayment(transactionData: TransactionData) {
        with(transactionData) {
            LoyaltyPaymentEntity(
                0, cashback, card.cardNumber, sign, transaction.transactionId, time
            ).also {
                loyaltyPaymentRepository.save(it)
            }
        }
    }

    private fun calculateCashBack(loyaltyProgram: LoyaltyProgram, transaction: Transaction, client: Client): Double {
        TransactionInfo(
            loyaltyProgram.name,
            transaction.value,
            3000.0, // todo где взять?
            transaction.mccCode,
            client.birthDate!!.toLocalDate(),
            client.firstName!!,
            client.lastName!!,
            client.middleName
        ).also {
            return cashbackCalculator.calculateCashback(it)
        }
    }

    companion object {
        private const val sign = "not compile time const"

        private data class TransactionData(
            val transaction: Transaction,
            val card: Card,
            val cashback: Double,
            val time: LocalDateTime = now()
        )
    }
}