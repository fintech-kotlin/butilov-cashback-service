package ru.tinkoff.fintech.service.notification

import ru.tinkoff.fintech.model.NotificationMessageInfo

interface NotificationMessageGenerator {

    fun generateMessage(messageInfo: NotificationMessageInfo): String
}