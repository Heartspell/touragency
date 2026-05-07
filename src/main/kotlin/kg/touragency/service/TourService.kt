package kg.touragency.service

import jakarta.persistence.criteria.Predicate
import kg.touragency.dto.TourFilter
import kg.touragency.entity.*
import kg.touragency.repository.TourRepository
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TourService(private val tourRepository: TourRepository) {

    fun getFeatured(): List<Tour> {
        // Берем 8 последних активных туров для главной страницы.
        return tourRepository.findTop8ByStatusOrderByCreatedAtDesc(TourStatus.ACTIVE)
    }

    fun search(filter: TourFilter): List<Tour> {
        val spec = Specification<Tour> { root, _, cb ->
            val predicates = mutableListOf<Predicate>()

            // Показываем только активные туры.
            predicates.add(cb.equal(root.get<TourStatus>("status"), TourStatus.ACTIVE))

            // Добавляем фильтры только если пользователь их заполнил.
            if (filter.destination != null && filter.destination.isNotBlank()) {
                val destinationText = filter.destination.lowercase()
                predicates.add(cb.like(cb.lower(root.get("destination")), "%$destinationText%"))
            }

            if (filter.country != null && filter.country.isNotBlank()) {
                val countryText = filter.country.lowercase()
                predicates.add(cb.like(cb.lower(root.get("country")), "%$countryText%"))
            }

            if (filter.category != null) {
                predicates.add(cb.equal(root.get<TourCategory>("category"), filter.category))
            }

            if (filter.minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), filter.minPrice))
            }

            if (filter.maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), filter.maxPrice))
            }

            cb.and(*predicates.toTypedArray())
        }
        return tourRepository.findAll(spec)
    }

    fun findById(id: Long): Tour? {
        return tourRepository.findById(id).orElse(null)
    }

    fun findByOperator(operator: User): List<Tour> {
        return tourRepository.findByOperator(operator)
    }

    @Transactional
    fun save(tour: Tour): Tour {
        return tourRepository.save(tour)
    }

    @Transactional
    fun delete(id: Long) {
        tourRepository.deleteById(id)
    }

    fun count(): Long {
        return tourRepository.count()
    }

    fun countActive(): Long {
        return tourRepository.findByStatus(TourStatus.ACTIVE).size.toLong()
    }

    fun findAll(): List<Tour> {
        return tourRepository.findAll()
    }
}
