package ru.tinkoff.fintech.client

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class NotificationServiceClientImpl(
    @Value("\${service.notification.url}")
    private val url: String,
    private val restTemplate: RestTemplate
) : NotificationServiceClient {

    companion object {
        private val logger = KotlinLogging.logger { }
    }

    override fun sendNotification(clientId: String, message: String) {
        val response = restTemplate.postForEntity(url, String::class.java, String::class.java, mapOf("clientId" to clientId))
        val statusCode = response.statusCode
        if (statusCode != HttpStatus.OK) {
            logger.error("Client error. Http status code $statusCode")
        }
    }
}