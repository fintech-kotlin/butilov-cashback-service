package ru.tinkoff.fintech.client

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import ru.tinkoff.fintech.model.Client

@Service
class ClientServiceImpl(
    @Value("\${service.client.url}")
    private val url: String,
    private val restTemplate: RestTemplate
) : ClientService {

    companion object {
        private val logger = KotlinLogging.logger { }
    }

    override suspend fun getClient(id: String): Client {
        val response = restTemplate.getForEntity(url, Client::class.java, mapOf("id" to id))
        val statusCode = response.statusCode
        if (statusCode != HttpStatus.OK) {
            logger.error("Client error. Http status code $statusCode")
        }
        return response.body!!
    }
}