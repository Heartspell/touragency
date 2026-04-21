package kg.touragency.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

enum class TourCategory { BEACH, MOUNTAIN, CITY, ADVENTURE, CULTURAL }
enum class TourStatus { ACTIVE, DRAFT, ARCHIVED }

@Entity
@Table(name = "tours")
class Tour(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    var title: String = "",
    @Column(columnDefinition = "TEXT") var description: String = "",
    var destination: String = "",
    var country: String = "",
    var durationDays: Int = 7,
    @Column(precision = 12, scale = 2) var price: BigDecimal = BigDecimal.ZERO,
    @Enumerated(EnumType.STRING) var category: TourCategory = TourCategory.BEACH,
    @Enumerated(EnumType.STRING) var status: TourStatus = TourStatus.ACTIVE,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "operator_id") var operator: User? = null,
    var rating: Double = 0.0,
    var reviewCount: Int = 0,
    @Column(columnDefinition = "BYTEA") var coverImage: ByteArray? = null,
    var coverImageType: String? = null,
    var createdAt: LocalDateTime = LocalDateTime.now()
)
