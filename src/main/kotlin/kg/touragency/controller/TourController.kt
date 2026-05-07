package kg.touragency.controller

import kg.touragency.dto.TourFilter
import kg.touragency.entity.TourCategory
import kg.touragency.repository.TourDateRepository
import kg.touragency.service.ReviewService
import kg.touragency.service.TourService
import kg.touragency.service.TourImageService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.time.LocalDate

@Controller
@RequestMapping("/tours")
class TourController(
    private val tourService: TourService,
    private val tourDateRepository: TourDateRepository,
    private val reviewService: ReviewService,
    private val tourImageService: TourImageService
) {

    @GetMapping
    fun list(
        @RequestParam(required = false) destination: String?,
        @RequestParam(required = false) country: String?,
        @RequestParam(required = false) category: String?,
        @RequestParam(required = false) minPrice: BigDecimal?,
        @RequestParam(required = false) maxPrice: BigDecimal?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) departureDateFrom: LocalDate?,
        model: Model
    ): String {
        // Превращаем текст категории в enum. Если текст неправильный, категории не будет.
        var selectedCategory: TourCategory? = null
        if (category != null) {
            try {
                selectedCategory = TourCategory.valueOf(category)
            } catch (_: Exception) {
                selectedCategory = null
            }
        }

        // Собираем все фильтры в один объект.
        val filter = TourFilter(destination, country, selectedCategory, minPrice, maxPrice, departureDateFrom)

        model.addAttribute("tours", tourService.search(filter))
        model.addAttribute("categories", TourCategory.values())
        model.addAttribute("filter", filter)
        return "tours/list"
    }

    @GetMapping("/{id}")
    fun detail(@PathVariable id: Long, model: Model): String {
        val tour = tourService.findById(id)
        if (tour == null) {
            return "redirect:/tours"
        }

        val dates = tourDateRepository.findByTourAndDepartureDateAfterOrderByDepartureDate(tour, LocalDate.now())
        val reviews = reviewService.getByTour(tour)
        val images = tourImageService.findByTour(tour)
        model.addAttribute("tour", tour)
        model.addAttribute("dates", dates)
        model.addAttribute("reviews", reviews)
        model.addAttribute("images", images)
        return "tours/detail"
    }

    @GetMapping("/{id}/cover")
    @ResponseBody
    fun cover(@PathVariable id: Long): ResponseEntity<ByteArray> {
        val tour = tourService.findById(id)
        if (tour != null && tour.coverImage != null) {
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(tour.coverImageType ?: "image/jpeg"))
                .body(tour.coverImage)
        }
        return ResponseEntity.notFound().build()
    }

    @GetMapping("/{tourId}/images/{imageId}")
    @ResponseBody
    fun image(@PathVariable tourId: Long, @PathVariable imageId: Long): ResponseEntity<ByteArray> {
        val image = tourImageService.findById(imageId)
        if (image != null && image.tour?.id == tourId && image.data != null) {
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.contentType ?: "image/jpeg"))
                .body(image.data)
        }
        return ResponseEntity.notFound().build()
    }
}
