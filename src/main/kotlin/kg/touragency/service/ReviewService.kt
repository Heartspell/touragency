package kg.touragency.service

import kg.touragency.entity.Review
import kg.touragency.entity.Tour
import kg.touragency.entity.User
import kg.touragency.repository.ReviewRepository
import kg.touragency.repository.TourRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReviewService(
    private val reviewRepository: ReviewRepository,
    private val tourRepository: TourRepository
) {

    fun getByTour(tour: Tour): List<Review> =
        reviewRepository.findByTourOrderByCreatedAtDesc(tour)

    @Transactional
    fun addReview(tourist: User, tour: Tour, rating: Int, comment: String): Review {
        val existing = reviewRepository.findByTouristAndTour(tourist, tour)
        val review = existing ?: Review()
        review.tourist = tourist
        review.tour = tour
        review.rating = rating.coerceIn(1, 5)
        review.comment = comment
        val saved = reviewRepository.save(review)

        // Update tour rating
        val reviews = reviewRepository.findByTourOrderByCreatedAtDesc(tour)
        tour.rating = reviews.map { it.rating }.average()
        tour.reviewCount = reviews.size
        tourRepository.save(tour)

        return saved
    }
}
