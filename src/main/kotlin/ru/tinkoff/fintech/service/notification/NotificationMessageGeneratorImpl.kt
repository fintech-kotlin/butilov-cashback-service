package ru.tinkoff.fintech.service.notification

import org.springframework.stereotype.Service
import ru.tinkoff.fintech.model.NotificationMessageInfo

@Service
class NotificationMessageGeneratorImpl(
    private val cardNumberMasker: CardNumberMasker
) : NotificationMessageGenerator {

    override fun generateMessage(messageInfo: NotificationMessageInfo): String {
        val maskedNumber = cardNumberMasker.mask(messageInfo.cardNumber, '#', 11, 15)
        return """
            Уважаемый, ${messageInfo.name}!
            Спешим Вам сообщить, что на карту $maskedNumber
            начислен cashback в размере ${messageInfo.cashback}
            за категорию ${messageInfo.category}.
            Спасибо за покупку ${messageInfo.transactionDate}
        """.trimIndent()
    }
}