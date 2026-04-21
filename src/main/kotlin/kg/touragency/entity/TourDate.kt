package kg.touragency.entity

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "tour_dates")
class TourDate(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id")
    var tour: Tour? = null,

    var departureDate: LocalDate = LocalDate.now(),
    var returnDate: LocalDate = LocalDate.now(),
    var totalSeats: Int = 20,
    var bookedSeats: Int = 0
) {
    val availableSeats get() = totalSeats - bookedSeats
}
