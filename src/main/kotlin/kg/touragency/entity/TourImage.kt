package kg.touragency.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "tour_images")
class TourImage(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id")
    var tour: Tour? = null,

    @Column(columnDefinition = "BYTEA")
    var data: ByteArray? = null,

    var contentType: String? = null,

    var createdAt: LocalDateTime = LocalDateTime.now()
)
