package kg.touragency.repository

import kg.touragency.entity.Tour
import kg.touragency.entity.TourImage
import org.springframework.data.jpa.repository.JpaRepository

interface TourImageRepository : JpaRepository<TourImage, Long> {
    fun findByTourOrderById(tour: Tour): List<TourImage>
}
