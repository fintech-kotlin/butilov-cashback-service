package ru.tinkoff.fintech.service.cashback

import ru.tinkoff.fintech.model.TransactionInfo

typealias Transaction = TransactionInfo
typealias Condition = TransactionInfo.() -> Boolean

@DslMarker
annotation class RuleDsl

sealed class Operation
object Conjunction : Operation()
object Disjunctive : Operation()

@RuleDsl
class Conditions {
    private val conditions = mutableListOf<Condition>()

    var operation: Operation = Disjunctive

    fun condition(cond: Condition) = conditions.add(cond)

    fun areMet(transaction: Transaction): Boolean {
        when (operation) {
            Disjunctive -> {
                for (condition in conditions) {
                    if (transaction.condition()) return true
                }
                return false
            }
            Conjunction -> {
                for (condition in conditions) {
                    if (transaction.condition()) return false
                }
                return true
            }
        }
    }
}

@RuleDsl
class Award {
    var percent: Double? = null
    var money: Double? = null
}

@RuleDsl
class Rule(val ruleName: String) {

    private val conditions = Conditions()
    private val award: Award = Award()

    fun conditions(c: Conditions.() -> Unit) = conditions.also(c)

    fun award(a: Award.() -> Unit) = award.also(a)

    fun calculate(transaction: Transaction): Double {
        if (conditions.areMet(transaction)) {
            award.percent?.let {
                return it * transaction.transactionSum
            }
            award.money?.let {
                return it
            }
        }
        return 0.0
    }
}

fun rule(create: Rule.() -> Unit) = rule("", create)

fun rule(ruleName: String, create: Rule.() -> Unit) = Rule(ruleName).also(create)

fun main() {

    val rule = rule("666 Award") {
        conditions {
            operation = Conjunction
            condition { transactionSum % 666.0 == 0.0 }
            condition { cashbackTotalValue > 0.0 }
        }
        award {
            money = 6.66
        }
    }


    val transaction = TransactionInfo(
        "", 6660.0, 0.0,
        1, null, "", "", ""
    )
    println(rule.calculate(transaction))

}


/*
  val rules = cashbackRules {
     check = all
     group {
        rule {
            condition { transactionSum % 666.0 == 0.0 }
            award {
                money = 6.66
            }
        }
     }
     group {
        group {
            rule {
                 condition { loyaltyProgramName == LOYALTY_PROGRAM_BLACK }
                 award {
                     percent = 0.01
                 }
             }
             rule {
                 check = all
                 condition { mccCode == MCC_SOFTWARE }
                 condition { loyaltyProgramName == LOYALTY_PROGRAM_ALL }
                 condition { isCustomPalindrome(transactionSum) }
                 award {
                     percent = nok(firstName.length, lastName.length) / 100000.0
                 }
             }
        }
        group {
            condition { loyaltyProgramName == LOYALTY_PROGRAM_BEER }
            rule {
                check = all
                condition { firstName == "олег"}
                condition { lastName == "олегов"}
                award {
                    percent = 0.1
                }
            }
            rule {
                condition { firstName == "олег" }
                award {
                    percent = 0.07
                }
            }
            rule {
                condition { firstName[0] == monthValueToFirstRusLetterMap[now().month.value] }
                award {
                    percent = 0.05
                }
            }
            rule {
                condition { firstName[0] == monthValueToFirstRusLetterMap[now().month.minus(1).value] }
                condition { firstName[0] == monthValueToFirstRusLetterMap[now().month.plus(1).value] }
                award {
                    percent = 0.03
                }
            }
        }
  }

  val cashback = rules.calculate(transactionInfo)

 */