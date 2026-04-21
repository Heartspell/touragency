package kg.touragency.service

import kg.touragency.dto.BookingRequest
import kg.touragency.entity.Booking
import kg.touragency.entity.BookingStatus
import kg.touragency.entity.User
import kg.touragency.repository.BookingRepository
import kg.touragency.repository.TourDateRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookingService(
    private val bookingRepository: BookingRepository,
    private val tourDateRepository: TourDateRepository
) {

    @Transactional
    fun createBooking(request: BookingRequest, tourist: User): Booking {
        val tourDate = tourDateRepository.findById(request.tourDateId)
            .orElseThrow { IllegalArgumentException("Tour date not found") }

        if (tourDate.availableSeats < request.participants) {
            throw IllegalStateException("Not enough seats available")
        }

        tourDate.bookedSeats += request.participants
        tourDateRepository.save(tourDate)

        val booking = Booking(
            tourist = tourist,
            tourDate = tourDate,
            participants = request.participants,
            totalPrice = tourDate.tour!!.price.multiply(request.participants.toBigDecimal()),
            status = BookingStatus.CONFIRMED,
            notes = request.notes
        )
        return bookingRepository.save(booking)
    }

    @Transactional
    fun cancel(bookingId: Long, tourist: User) {
        val booking = bookingRepository.findById(bookingId)
            .orElseThrow { IllegalArgumentException("Booking not found") }
        if (booking.tourist?.id != tourist.id) throw SecurityException("Access denied")
        if (booking.status == BookingStatus.CANCELLED) return

        booking.status = BookingStatus.CANCELLED
        val tourDate = booking.tourDate!!
        tourDate.bookedSeats -= booking.participants
        tourDateRepository.save(tourDate)
        bookingRepository.save(booking)
    }

    fun getByTourist(tourist: User): List<Booking> =
        bookingRepository.findByTouristOrderByCreatedAtDesc(tourist)

    fun getByOperator(operator: User): List<Booking> =
        bookingRepository.findByTourDate_Tour_OperatorOrderByCreatedAtDesc(operator)

    fun findById(id: Long): Booking? = bookingRepository.findById(id).orElse(null)

    fun count(): Long = bookingRepository.count()

    fun countByOperator(operator: User): Long = bookingRepository.countByTourDate_Tour_Operator(operator)
}
