package ru.tinkoff.fintech.service.notification

import org.springframework.stereotype.Service
import ru.tinkoff.fintech.client.NotificationServiceClient
import ru.tinkoff.fintech.model.NotificationMessageInfo

@Service
class NotificationServiceImpl(
    private val notificationMessageGenerator: NotificationMessageGenerator,
    private val notificationServiceClient: NotificationServiceClient
) : NotificationService {

    override fun sendNotification(clientId: String, notificationMessageInfo: NotificationMessageInfo) {
        val message = notificationMessageGenerator.generateMessage(notificationMessageInfo)
        notificationServiceClient.sendNotification(clientId, message)
    }
}