package kg.touragency.service

import kg.touragency.dto.BookingRequest
import kg.touragency.entity.Booking
import kg.touragency.entity.BookingStatus
import kg.touragency.entity.User
import kg.touragency.repository.BookingRepository
import kg.touragency.repository.TourDateRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class BookingService(
    private val bookingRepository: BookingRepository,
    private val tourDateRepository: TourDateRepository
) {

    @Transactional
    fun createBooking(request: BookingRequest, tourist: User): Booking {
        // Сначала находим дату тура.
        val tourDate = tourDateRepository.findById(request.tourDateId)
            .orElseThrow { IllegalArgumentException("Tour date not found") }

        // Проверяем, что мест хватает.
        if (tourDate.availableSeats < request.participants) {
            throw IllegalStateException("Not enough seats available")
        }

        // Занимаем места и создаем бронь.
        tourDate.bookedSeats += request.participants
        tourDateRepository.save(tourDate)

        val tour = tourDate.tour!!
        val priceForOnePerson = tour.price
        val peopleCount = request.participants.toBigDecimal()
        val totalPrice = priceForOnePerson.multiply(peopleCount)

        val booking = Booking(
            tourist = tourist,
            tourDate = tourDate,
            participants = request.participants,
            totalPrice = totalPrice,
            status = BookingStatus.PENDING,
            notes = request.notes
        )

        return bookingRepository.save(booking)
    }

    @Transactional
    fun cancel(bookingId: Long, tourist: User) {
        val booking = bookingRepository.findById(bookingId)
            .orElseThrow { IllegalArgumentException("Booking not found") }

        // Турист может отменить только свою бронь.
        if (booking.tourist?.id != tourist.id) {
            throw SecurityException("Access denied")
        }

        if (booking.status == BookingStatus.CANCELLED) {
            return
        }

        // Освобождаем занятые места.
        booking.status = BookingStatus.CANCELLED
        val tourDate = booking.tourDate!!
        tourDate.bookedSeats -= booking.participants
        tourDateRepository.save(tourDate)
        bookingRepository.save(booking)
    }

    @Scheduled(fixedDelay = 60_000)
    @Transactional
    fun cancelExpiredBookings() {
        // Каждую минуту отменяем неоплаченные брони.
        val now = LocalDateTime.now()
        val expired = bookingRepository.findByStatusAndPaymentDeadlineBefore(BookingStatus.PENDING, now)
        for (booking in expired) {
            booking.status = BookingStatus.CANCELLED
            val tourDate = booking.tourDate
            if (tourDate != null) {
                tourDate.bookedSeats -= booking.participants
                tourDateRepository.save(tourDate)
            }
            bookingRepository.save(booking)
        }
    }

    fun getByTourist(tourist: User): List<Booking> {
        return bookingRepository.findByTouristOrderByCreatedAtDesc(tourist)
    }

    fun getByOperator(operator: User): List<Booking> {
        return bookingRepository.findByTourDate_Tour_OperatorOrderByCreatedAtDesc(operator)
    }

    fun findById(id: Long): Booking? {
        return bookingRepository.findById(id).orElse(null)
    }

    fun count(): Long {
        return bookingRepository.count()
    }

    fun countByOperator(operator: User): Long {
        return bookingRepository.countByTourDate_Tour_Operator(operator)
    }
}
