package kg.touragency.dto

import kg.touragency.entity.TourCategory
import java.math.BigDecimal
import java.time.LocalDate

data class TourFilter(
    val destination: String? = null,
    val country: String? = null,
    val category: TourCategory? = null,
    val minPrice: BigDecimal? = null,
    val maxPrice: BigDecimal? = null,
    val departureDateFrom: LocalDate? = null
)
