package ru.tinkoff.fintech.client

import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import ru.tinkoff.fintech.model.Client

@Service
class ClientServiceImpl : ClientService {
    override fun getClient(id: String): Client {
        val restTemplate = RestTemplate()

        val uri = "http://13.79.17.165/client-service/api/v1/client/${id}"

        val headers = HttpHeaders()
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        val entity = HttpEntity("", headers)

        val result = restTemplate.exchange(uri, HttpMethod.GET, entity, Client::class.java)
        val statusCode = result.statusCode
        if (statusCode != HttpStatus.OK) {
            println("Client error. Http status code $statusCode")
        }
        return result.body!!
    }
}