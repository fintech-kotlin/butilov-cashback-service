package ru.tinkoff.fintech.service.transaction

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

@Service
class TransactionServiceImpl(
    private val cardServiceClient: CardServiceClient,
    private val clientService: ClientService,
    private val loyaltyServiceClient: LoyaltyServiceClient,
    private val cashbackCalculator: CashbackCalculator,
    private val notificationService: NotificationService,
    private val loyaltyPaymentRepository: LoyaltyPaymentRepository
) : TransactionService {

    override fun handle(transaction: Transaction) {
        val card = cardServiceClient.getCard(transaction.cardNumber)
        val client = clientService.getClient(card.client)
        val loyaltyProgram = loyaltyServiceClient.getLoyaltyProgram(card.loyaltyProgram)

        val cashback = calculateCashBack(loyaltyProgram, transaction, client)
        val time = LocalDateTime.now()
        saveLoyaltyPayment(cashback, card, transaction, time)
        sendNotification(card, cashback, transaction, loyaltyProgram, time, client)
    }

    private fun sendNotification(
        card: Card,
        cashback: Double,
        transaction: Transaction,
        loyaltyProgram: LoyaltyProgram,
        localDateTime: LocalDateTime,
        client: Client
    ) {
        val notificationMessageInfo = NotificationMessageInfo(
            client.firstName!!,
            card.cardNumber,
            cashback,
            transaction.value,
            loyaltyProgram.name,
            localDateTime
        )
        notificationService.sendNotification(client.id, notificationMessageInfo)
    }

    private fun saveLoyaltyPayment(
        cashback: Double,
        card: Card,
        transaction: Transaction,
        localDateTime: LocalDateTime
    ) {
        val loyaltyPaymentEntity = LoyaltyPaymentEntity(
            0,
            cashback,
            card.cardNumber,
            "three hundred bucks",
            transaction.transactionId,
            localDateTime
        )
        loyaltyPaymentRepository.save(loyaltyPaymentEntity)
    }

    private fun calculateCashBack(
        loyaltyProgram: LoyaltyProgram,
        transaction: Transaction,
        client: Client
    ): Double {
        val transactionInfo = TransactionInfo(
            loyaltyProgram.name,
            transaction.value,
            3000.0, // todo где взять?
            transaction.mccCode,
            client.birthDate!!.toLocalDate(),
            client.firstName!!,
            client.lastName!!,
            client.middleName
        )

        val cashback = cashbackCalculator.calculateCashback(transactionInfo)
        return cashback
    }
}