package ru.tinkoff.fintech.listener

import org.springframework.stereotype.Service
import ru.tinkoff.fintech.client.CardServiceClient
import ru.tinkoff.fintech.client.ClientService
import ru.tinkoff.fintech.client.LoyaltyServiceClient
import ru.tinkoff.fintech.db.entity.LoyaltyPaymentEntity
import ru.tinkoff.fintech.db.repository.LoyaltyPaymentRepository
import ru.tinkoff.fintech.model.NotificationMessageInfo
import ru.tinkoff.fintech.model.Transaction
import ru.tinkoff.fintech.model.TransactionInfo
import ru.tinkoff.fintech.service.cashback.CashbackCalculator
import ru.tinkoff.fintech.service.notification.NotificationService
import java.time.LocalDateTime

@Service
class TransactionListener(
    private val cardServiceClient: CardServiceClient,
    private val clientService: ClientService,
    private val loyaltyServiceClient: LoyaltyServiceClient,
    private val cashbackCalculator: CashbackCalculator,
    private val notificationService: NotificationService,
    private val loyaltyPaymentRepository: LoyaltyPaymentRepository
) {

    fun onMessage(transaction: Transaction) {
        val card = cardServiceClient.getCard(transaction.cardNumber)
        val client = clientService.getClient(card.client)
        val loyaltyProgram = loyaltyServiceClient.getLoyaltyProgram(card.loyaltyProgram)

        val transactionInfo = TransactionInfo(
            loyaltyProgram.name,
            transaction.value,
            3000.0,
            transaction.mccCode,
            client.birthDate!!.toLocalDate(),
            client.firstName!!,
            client.lastName!!,
            client.middleName
        )

        val cashback = cashbackCalculator.calculateCashback(transactionInfo)
        val localDateTime = LocalDateTime.now()
        val notificationMessageInfo = NotificationMessageInfo(
            "",
            card.cardNumber,
            cashback,
            transaction.value,
            loyaltyProgram.name,
            localDateTime
        )

        val loyaltyPaymentEntity = LoyaltyPaymentEntity(
            0,
            cashback,
            card.cardNumber,
            "three hundred bucks",
            transaction.transactionId,
            localDateTime
        )
        loyaltyPaymentRepository.save(loyaltyPaymentEntity)

        notificationService.sendNotification(client.id, notificationMessageInfo)
    }
}


