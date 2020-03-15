package ru.tinkoff.fintech.client

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import ru.tinkoff.fintech.model.Card

@Service
class CardServiceClientImpl(
    @Value("\${service.card.url}")
    private val url: String,
    private val restTemplate: RestTemplate
) : CardServiceClient {

    companion object {
        private val logger = KotlinLogging.logger { }
    }

    override fun getCard(id: String): Card {
        val response = restTemplate.getForEntity(url, Card::class.java, mapOf("id" to id))
        val statusCode = response.statusCode
        if (statusCode != HttpStatus.OK) {
            logger.error("Client error. Http status code $statusCode")
        }
        return response.body!!
    }

}