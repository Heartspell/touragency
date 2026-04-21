package kg.touragency.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

enum class BookingStatus { PENDING, CONFIRMED, CANCELLED, COMPLETED }

@Entity
@Table(name = "bookings")
class Booking(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tourist_id")
    var tourist: User? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tour_date_id")
    var tourDate: TourDate? = null,

    var participants: Int = 1,

    @Column(precision = 12, scale = 2)
    var totalPrice: BigDecimal = BigDecimal.ZERO,

    @Enumerated(EnumType.STRING)
    var status: BookingStatus = BookingStatus.PENDING,

    var notes: String = "",
    var createdAt: LocalDateTime = LocalDateTime.now()
)
