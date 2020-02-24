package ru.tinkoff.fintech.service.notification

class CardNumberMaskerImpl: CardNumberMasker {

    override fun mask(cardNumber: String, maskChar: Char, start: Int, end: Int): String {
        if (start > end) throw Exception("Start index cannot be greater than end index")
        val chars = cardNumber.mapIndexed { i, char -> if (i in start until end) maskChar else char }
        return String(chars.toCharArray())
    }
}