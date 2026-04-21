package kg.touragency.repository

import kg.touragency.entity.Booking
import kg.touragency.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface BookingRepository : JpaRepository<Booking, Long> {
    fun findByTouristOrderByCreatedAtDesc(tourist: User): List<Booking>
    fun findByTourDate_Tour_OperatorOrderByCreatedAtDesc(operator: User): List<Booking>
    fun countByTourDate_Tour_Operator(operator: User): Long
}
