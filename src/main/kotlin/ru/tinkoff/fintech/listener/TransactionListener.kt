package ru.tinkoff.fintech.listener

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import ru.tinkoff.fintech.model.Transaction
import ru.tinkoff.fintech.service.transaction.TransactionService

@Component
class TransactionListener(private val transactionService: TransactionService) {

    //    @KafkaListener(topics = ["testTopic"], groupId = "testGroup")
    @KafkaListener(topics = ["transactions"], groupId = "cashback")
    fun onMessage(transaction: Transaction) {
        if (transaction.mccCode != null) {
            transactionService.handle(transaction)
        }
    }
}


