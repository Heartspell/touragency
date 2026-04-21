package kg.touragency.repository

import kg.touragency.entity.Tour
import kg.touragency.entity.TourDate
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface TourDateRepository : JpaRepository<TourDate, Long> {
    fun findByTourAndDepartureDateAfterOrderByDepartureDate(tour: Tour, date: LocalDate): List<TourDate>
    fun findByTour(tour: Tour): List<TourDate>
}
