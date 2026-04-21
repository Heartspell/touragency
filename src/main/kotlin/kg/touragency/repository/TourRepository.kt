package kg.touragency.repository

import kg.touragency.entity.Tour
import kg.touragency.entity.TourStatus
import kg.touragency.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface TourRepository : JpaRepository<Tour, Long>, JpaSpecificationExecutor<Tour> {
    fun findByStatus(status: TourStatus): List<Tour>
    fun findByOperator(operator: User): List<Tour>
    fun findTop8ByStatusOrderByCreatedAtDesc(status: TourStatus): List<Tour>
}
