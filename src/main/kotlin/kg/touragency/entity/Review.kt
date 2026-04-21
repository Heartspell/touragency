package kg.touragency.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "reviews")
class Review(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tourist_id")
    var tourist: User? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id")
    var tour: Tour? = null,

    var rating: Int = 5,

    @Column(columnDefinition = "TEXT")
    var comment: String = "",

    var createdAt: LocalDateTime = LocalDateTime.now()
)
