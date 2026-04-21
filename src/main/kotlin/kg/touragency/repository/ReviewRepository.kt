package kg.touragency.repository

import kg.touragency.entity.Review
import kg.touragency.entity.Tour
import kg.touragency.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface ReviewRepository : JpaRepository<Review, Long> {
    fun findByTourOrderByCreatedAtDesc(tour: Tour): List<Review>
    fun findByTouristAndTour(tourist: User, tour: Tour): Review?
}
