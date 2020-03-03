package ru.tinkoff.fintech.service.cashback

import ru.tinkoff.fintech.model.TransactionInfo
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime.now
import java.time.Month.*

internal const val LOYALTY_PROGRAM_BLACK = "BLACK"
internal const val LOYALTY_PROGRAM_ALL = "ALL"
internal const val LOYALTY_PROGRAM_BEER = "BEER"
internal const val MAX_CASH_BACK = 3000.0
internal const val MCC_SOFTWARE = 5734
internal const val MCC_BEER = 5921

class CashbackCalculatorImpl : CashbackCalculator {

    companion object {
        private val monthValueToFirstRusLetterMap = mapOf(
            JANUARY.value to 'я',
            FEBRUARY.value to 'ф',
            MARCH.value to 'м',
            APRIL.value to 'а',
            MAY.value to 'м',
            JUNE.value to 'и',
            JULY.value to 'и',
            AUGUST.value to 'а',
            SEPTEMBER.value to 'с',
            OCTOBER.value to 'о',
            NOVEMBER.value to 'н',
            DECEMBER.value to 'д'
        )
    }

    override fun calculateCashback(transactionInfo: TransactionInfo): Double {
        val possibleCashback = MAX_CASH_BACK - transactionInfo.cashbackTotalValue
        return if (possibleCashback != 0.0) {
            val cashback = calculate(transactionInfo)
            if (possibleCashback <= cashback) possibleCashback else cashback
        } else {
            0.0
        }
    }

    private fun calculate(info: TransactionInfo): Double {
        val cashback = getAward666(info) + info.transactionSum * getLoyaltyPart(info)
        return round(cashback)
    }

    private fun getAward666(info: TransactionInfo) = if (info.transactionSum % 666.0 == 0.0) 6.66 else 0.0

    private fun getLoyaltyPart(info: TransactionInfo): Double = when (info.loyaltyProgramName) {
        LOYALTY_PROGRAM_BLACK -> 0.01
        LOYALTY_PROGRAM_ALL -> getAllLoyaltyPart(info)
        LOYALTY_PROGRAM_BEER -> getBeerLoyaltyPart(info)
        else -> 0.0
    }

    private fun getAllLoyaltyPart(info: TransactionInfo) = with(info) {
        if (mccCode == MCC_SOFTWARE && isCustomPalindrome(transactionSum)) {
            (nok(firstName.length, lastName.length) / 100000.0)
        } else {
            0.0
        }
    }

    private fun getBeerLoyaltyPart(info: TransactionInfo): Double = with(info) {
        if (mccCode == MCC_BEER) {
            val firstName = info.firstName.toLowerCase()
            val lastName = info.lastName.toLowerCase()
            val currentMonth = now().month
            when {
                firstName == "олег" && lastName == "олегов" -> 0.10
                firstName == "олег" -> 0.07
                firstName[0] == monthValueToFirstRusLetterMap[currentMonth.value] -> 0.05
                firstName[0] == monthValueToFirstRusLetterMap[currentMonth.minus(1).value] -> 0.03
                firstName[0] == monthValueToFirstRusLetterMap[currentMonth.plus(1).value] -> 0.03
                else -> 0.02
            }
        } else {
            0.0
        }
    }

    private fun round(cashback: Double): Double {
        return BigDecimal(cashback).setScale(2, RoundingMode.HALF_EVEN).toDouble()
    }

    private fun isCustomPalindrome(transactionSum: Double): Boolean {
        val sum = transactionSum * 100
        return if (sum == 0.0) {
            true
        } else {
            val array = sum.toInt().toString().toCharArray()
            customCheck(array)
        }
    }

    private tailrec fun customCheck(array: CharArray, count: Int = 0, i: Int = 0, n: Int = array.size): Boolean {
        return if (count == 2) {
            false
        } else {
            if (i > n shr 1) {
                count < 2
            } else {
                val newCount = if (array[i] == array[n - i - 1]) 0 else 1
                customCheck(array, newCount, i + 1)
            }
        }
    }

    private fun nok(val1: Int, val2: Int): Int {
        return val1 * (val2 / nod(val1, val2))
    }

    private tailrec fun nod(val1: Int, val2: Int): Int {
        val mod = val1 % val2
        return if (mod <= 0) val2 else nod(val2, mod)
    }

}