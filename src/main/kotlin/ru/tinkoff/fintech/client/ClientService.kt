package ru.tinkoff.fintech.client

import ru.tinkoff.fintech.model.Client

interface ClientService {

    suspend fun getClient(id: String): Client
}