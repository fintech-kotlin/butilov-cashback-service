package ru.tinkoff.fintech.service.transaction

import org.apache.kafka.clients.consumer.Consumer
import org.springframework.stereotype.Component
import ru.tinkoff.fintech.listener.TransactionListener
import ru.tinkoff.fintech.model.Transaction
import java.time.Duration
import kotlin.concurrent.thread

@Component
class TransactionHandler(
    private val transactionConsumer: Consumer<String, Transaction>,
    private val transactionListener: TransactionListener
) {

    init {
        thread { start() }
    }

    private fun start() {
        transactionConsumer.subscribe(listOf("transactions"))

        while (true) {
            readFromTransactionTopic()
        }
    }

    private fun readFromTransactionTopic() {
        val records = transactionConsumer.poll(Duration.ofSeconds(1))
        records.iterator().forEach {
            val transaction: Transaction? = it.value()
            transaction?.let { t -> transactionListener.onMessage(t) }
        }
    }
}