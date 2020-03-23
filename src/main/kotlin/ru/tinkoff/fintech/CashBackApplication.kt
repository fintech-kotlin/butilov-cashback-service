package ru.tinkoff.fintech

import kotlinx.coroutines.Dispatchers
import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.web.client.RestTemplate
import java.time.Duration

@EnableKafka
@SpringBootApplication
class CashBackApplication

fun main(args: Array<String>) {
    runApplication<CashBackApplication>(*args) { setBannerMode(Banner.Mode.OFF) }
}

@Configuration
class RestConfiguration {

    @Bean
    fun restTemplate(builder: RestTemplateBuilder): RestTemplate = builder
        .setConnectTimeout(Duration.ofMillis(3000))
        .setReadTimeout(Duration.ofMillis(3000))
        .build()
}

@Configuration
class RuntimeConfiguration {

    @Bean
    fun coroutineDispatcher() = Dispatchers.Default
}