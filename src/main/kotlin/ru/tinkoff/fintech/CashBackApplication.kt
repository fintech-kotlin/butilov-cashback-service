package ru.tinkoff.fintech

import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.EnableKafka

@EnableKafka
@SpringBootApplication
class CashBackApplication {

    fun main(args: Array<String>) {
        runApplication<CashBackApplication>(*args) { setBannerMode(Banner.Mode.OFF) }
    }
}