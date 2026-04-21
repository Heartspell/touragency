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

    fun getFeatured(): List<Tour> =
        tourRepository.findTop8ByStatusOrderByCreatedAtDesc(TourStatus.ACTIVE)

    fun search(filter: TourFilter): List<Tour> {
        val spec = Specification<Tour> { root, _, cb ->
            val predicates = mutableListOf<Predicate>()
            predicates.add(cb.equal(root.get<TourStatus>("status"), TourStatus.ACTIVE))
            filter.destination?.takeIf { it.isNotBlank() }?.let {
                predicates.add(cb.like(cb.lower(root.get("destination")), "%${it.lowercase()}%"))
            }
            filter.country?.takeIf { it.isNotBlank() }?.let {
                predicates.add(cb.like(cb.lower(root.get("country")), "%${it.lowercase()}%"))
            }
            filter.category?.let {
                predicates.add(cb.equal(root.get<TourCategory>("category"), it))
            }
            filter.minPrice?.let {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), it))
            }
            filter.maxPrice?.let {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), it))
            }
            cb.and(*predicates.toTypedArray())
        }
        return tourRepository.findAll(spec)
    }

    fun findById(id: Long): Tour? = tourRepository.findById(id).orElse(null)

    fun findByOperator(operator: User): List<Tour> = tourRepository.findByOperator(operator)

    @Transactional
    fun save(tour: Tour): Tour = tourRepository.save(tour)

    @Transactional
    fun delete(id: Long) = tourRepository.deleteById(id)

    fun count(): Long = tourRepository.count()

    fun countActive(): Long = tourRepository.findByStatus(TourStatus.ACTIVE).size.toLong()

    fun findAll(): List<Tour> = tourRepository.findAll()
}
