package kg.touragency.entity

import jakarta.persistence.*
import java.time.LocalDateTime

enum class UserRole { TOURIST, OPERATOR, ADMIN }

@Entity
@Table(name = "users")
class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(unique = true, nullable = false)
    var email: String = "",

    @Column(nullable = false)
    var password: String = "",

    var fullName: String = "",
    var phone: String = "",

    @Enumerated(EnumType.STRING)
    var role: UserRole = UserRole.TOURIST,

    var createdAt: LocalDateTime = LocalDateTime.now()
)
