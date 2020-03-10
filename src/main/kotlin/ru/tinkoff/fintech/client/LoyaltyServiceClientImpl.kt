package ru.tinkoff.fintech.client

import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import ru.tinkoff.fintech.model.LoyaltyProgram

@Service
class LoyaltyServiceClientImpl : LoyaltyServiceClient {
    override fun getLoyaltyProgram(id: String): LoyaltyProgram {
        val restTemplate = RestTemplate()

        val uri = "http://13.79.17.165/loyalty-service/api/v1/program/${id}"

        val headers = HttpHeaders()
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        val entity = HttpEntity("", headers)

        val result = restTemplate.exchange(uri, HttpMethod.GET, entity, LoyaltyProgram::class.java)
        val statusCode = result.statusCode
        if (statusCode != HttpStatus.OK) {
            println("Client error. Http status code $statusCode")
        }
        return result.body!!
    }
}