package kg.touragency.repository

import kg.touragency.entity.Booking
import kg.touragency.entity.BookingStatus
import kg.touragency.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface BookingRepository : JpaRepository<Booking, Long> {
    fun findByTouristOrderByCreatedAtDesc(tourist: User): List<Booking>
    fun findByTourDate_Tour_OperatorOrderByCreatedAtDesc(operator: User): List<Booking>
    fun countByTourDate_Tour_Operator(operator: User): Long
    fun findByStatusAndPaymentDeadlineBefore(status: BookingStatus, deadline: LocalDateTime): List<Booking>
}
