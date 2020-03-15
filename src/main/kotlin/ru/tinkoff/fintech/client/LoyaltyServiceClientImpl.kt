package ru.tinkoff.fintech.client

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import ru.tinkoff.fintech.model.Client
import ru.tinkoff.fintech.model.LoyaltyProgram

@Service
class LoyaltyServiceClientImpl(
    @Value("\${service.loyalty.url}")
    private val url: String,
    private val restTemplate: RestTemplate
) : LoyaltyServiceClient {

    companion object {
        private val logger = KotlinLogging.logger { }
    }

    override fun getLoyaltyProgram(id: String): LoyaltyProgram {
        val response = restTemplate.getForEntity(url, LoyaltyProgram::class.java, mapOf("id" to id))
        val statusCode = response.statusCode
        if (statusCode != HttpStatus.OK) {
            logger.error("Client error. Http status code $statusCode")
        }
        return response.body!!
    }
}