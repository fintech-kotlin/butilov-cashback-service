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
                    if (!transaction.condition()) return false
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

interface Calculator {
    fun calculate(transaction: Transaction): Double
}

@RuleDsl
class Rule(val ruleName: String) : Calculator {

    private val conditions = Conditions()
    private val award: Award = Award()

    fun conditions(c: Conditions.() -> Unit) = conditions.also(c)

    fun condition(cond: Condition) = conditions.condition(cond)

    fun award(a: Award.() -> Unit) = award.also(a)

    override fun calculate(transaction: Transaction): Double {
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

@RuleDsl
open class Group : Calculator {

    var operation: Operation = Disjunctive

    var condition: Condition? = null

    private val rules = mutableListOf<Rule>()
    private val groups = mutableListOf<Group>()

    fun rule(create: Rule.() -> Unit) = rule("", create).also(rules::add)

    fun rule(ruleName: String, create: Rule.() -> Unit) = Rule(ruleName).also(create).also(rules::add)

    fun group(create: Group.() -> Unit) = Group().also(create).also(groups::add)

    fun condition(cond: Condition) {
        condition = cond
    }

    override fun calculate(transaction: Transaction): Double {

        condition?.let {
            if (transaction.it().not()) {
                return 0.0
            }
        }

        if (rules.isNotEmpty() && groups.isEmpty()) {
            calcByOperation(transaction, rules)
        }
        if (groups.isNotEmpty() && rules.isEmpty()) {
            calcByOperation(transaction, groups)
        }
        return 0.0
    }

    private fun <T : Calculator> calcByOperation(transaction: Transaction, calcList: MutableList<out T>): Double {
        when (operation) {
            Disjunctive -> {
                var money = 0.0
                for (calculator in calcList) {
                    money += calculator.calculate(transaction)
                }
                return money
            }
            Conjunction -> {
                for (calculator in calcList) {
                    val money = calculator.calculate(transaction)
                    if (money != 0.0) return money
                }
                return 0.0
            }
        }
    }
}

class CashbackRules: Group()

fun cashbackRules(create: CashbackRules.() -> Unit) = CashbackRules().also(create)