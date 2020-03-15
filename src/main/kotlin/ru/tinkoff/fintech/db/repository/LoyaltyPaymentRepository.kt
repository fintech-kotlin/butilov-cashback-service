package ru.tinkoff.fintech.db.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.tinkoff.fintech.db.entity.LoyaltyPaymentEntity

@Repository
interface LoyaltyPaymentRepository : JpaRepository<LoyaltyPaymentEntity, Long>
