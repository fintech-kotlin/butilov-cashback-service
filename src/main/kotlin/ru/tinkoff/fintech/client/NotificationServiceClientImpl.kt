package ru.tinkoff.fintech.client

import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class NotificationServiceClientImpl : NotificationServiceClient {
    override fun sendNotification(clientId: String, message: String) {
        val restTemplate = RestTemplate()

        val uri = "http://13.79.17.165/notification-service/api/v1/client/${clientId}/message"

        val headers = HttpHeaders()
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        val entity = HttpEntity(message, headers)

        val result = restTemplate.exchange(uri, HttpMethod.POST, entity, String::class.java)
        val statusCode = result.statusCode
        if (statusCode != HttpStatus.OK) {
            println("Client error. Http status code $statusCode")
        }
    }
}