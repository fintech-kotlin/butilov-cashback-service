package ru.tinkoff.fintech.db.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import ru.tinkoff.fintech.db.entity.LoyaltyPaymentEntity
import java.time.LocalDateTime

@Repository
interface LoyaltyPaymentRepository : JpaRepository<LoyaltyPaymentEntity, Long> {

     suspend fun findAllByCardIdAndSignAndDateTimeAfter(
          cardId: String,
          sign: String,
          dateTime: LocalDateTime
     ): Set<LoyaltyPaymentEntity>

//     @Query("SELECT SUM(value) FROM LoyaltyPaymentEntity " +
//             "WHERE cardId = ?1 AND sign = ?2 AND dateTime > ?3 ")
//     suspend fun findPossibleCashback(cardId: String, sign: String, currentMonth: LocalDateTime): Double
}
