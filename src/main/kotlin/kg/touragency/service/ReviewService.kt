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

    fun getByTour(tour: Tour): List<Review> {
        return reviewRepository.findByTourOrderByCreatedAtDesc(tour)
    }

    @Transactional
    fun addReview(tourist: User, tour: Tour, rating: Int, comment: String): Review {
        val existing = reviewRepository.findByTouristAndTour(tourist, tour)
        val review: Review

        // Если отзыв уже был, меняем его. Если нет, создаем новый.
        if (existing != null) {
            review = existing
        } else {
            review = Review()
        }

        review.tourist = tourist
        review.tour = tour
        review.rating = rating.coerceIn(1, 5)
        review.comment = comment
        val saved = reviewRepository.save(review)

        // После отзыва пересчитываем рейтинг тура.
        val reviews = reviewRepository.findByTourOrderByCreatedAtDesc(tour)
        var ratingSum = 0

        for (item in reviews) {
            ratingSum += item.rating
        }

        tour.rating = ratingSum.toDouble() / reviews.size
        tour.reviewCount = reviews.size
        tourRepository.save(tour)

        return saved
    }
}
