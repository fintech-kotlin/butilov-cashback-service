package ru.tinkoff.fintech

import org.apache.kafka.clients.consumer.KafkaConsumer
import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer
import ru.tinkoff.fintech.model.Transaction
import java.util.*

@SpringBootApplication
open class CashBackApplication {

    fun main(args: Array<String>) {
        runApplication<CashBackApplication>(*args) { setBannerMode(Banner.Mode.OFF) }
    }

    @Bean
    open fun getKafkaTransactionConsumer(): KafkaConsumer<String, Transaction> {
        val props = Properties()
        props["bootstrap.servers"] = "40.69.78.245:19092"
        props["group.id"] = "cashback"
        props["key.deserializer"] = JsonSerializer::class.java
        props["value.deserializer"] = JsonDeserializer::class.java
        props["fetch.min.bytes"] = 10000
        return KafkaConsumer(props)
    }
}