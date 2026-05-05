package kg.touragency.controller

import kg.touragency.entity.*
import kg.touragency.repository.TourDateRepository
import kg.touragency.service.BookingService
import kg.touragency.service.TourService
import kg.touragency.service.UserService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.math.BigDecimal
import java.time.LocalDate

@Controller
@RequestMapping("/operator")
class OperatorController(
    private val tourService: TourService,
    private val bookingService: BookingService,
    private val tourDateRepository: TourDateRepository,
    private val userService: UserService
) {

    private fun currentOperator(principal: UserDetails) =
        userService.findByEmail(principal.username) ?: throw SecurityException("Not found")

    @GetMapping
    fun dashboard(@AuthenticationPrincipal principal: UserDetails, model: Model): String {
        val operator = currentOperator(principal)
        val tours = tourService.findByOperator(operator)
        val activeTourCount = tours.count { it.status == TourStatus.ACTIVE }
        val toursWithRating = tours.filter { it.reviewCount > 0 }
        val avgRating = if (toursWithRating.isNotEmpty())
            toursWithRating.map { it.rating }.average()
        else null
        model.addAttribute("operator", operator)
        model.addAttribute("tours", tours)
        model.addAttribute("bookings", bookingService.getByOperator(operator))
        model.addAttribute("bookingCount", bookingService.countByOperator(operator))
        model.addAttribute("activeTourCount", activeTourCount)
        model.addAttribute("avgRating", avgRating)
        model.addAttribute("categories", TourCategory.values())
        return "operator/cabinet"
    }

    @PostMapping("/tours")
    fun createTour(
        @RequestParam title: String,
        @RequestParam description: String,
        @RequestParam destination: String,
        @RequestParam country: String,
        @RequestParam durationDays: Int,
        @RequestParam price: BigDecimal,
        @RequestParam category: String,
        @RequestParam(required = false) coverImage: MultipartFile?,
        @AuthenticationPrincipal principal: UserDetails,
        redirectAttributes: RedirectAttributes
    ): String {
        val operator = currentOperator(principal)
        val tour = Tour(
            title = title, description = description,
            destination = destination, country = country,
            durationDays = durationDays, price = price,
            category = TourCategory.valueOf(category),
            status = TourStatus.ACTIVE, operator = operator
        )
        if (coverImage != null && !coverImage.isEmpty) {
            tour.coverImage = coverImage.bytes
            tour.coverImageType = coverImage.contentType
        }
        tourService.save(tour)
        redirectAttributes.addFlashAttribute("success", "Тур «${tour.title}» успешно добавлен!")
        return "redirect:/operator"
    }

    @GetMapping("/tours/{id}/edit")
    fun editForm(
        @PathVariable id: Long,
        @AuthenticationPrincipal principal: UserDetails,
        model: Model
    ): String {
        val operator = currentOperator(principal)
        val tour = tourService.findById(id) ?: return "redirect:/operator"
        if (tour.operator?.id != operator.id && operator.role != UserRole.ADMIN) return "redirect:/operator"
        model.addAttribute("tour", tour)
        model.addAttribute("categories", TourCategory.values())
        return "operator/tour-edit"
    }

    @PostMapping("/tours/{id}/update")
    fun updateTour(
        @PathVariable id: Long,
        @RequestParam title: String,
        @RequestParam description: String,
        @RequestParam destination: String,
        @RequestParam country: String,
        @RequestParam durationDays: Int,
        @RequestParam price: BigDecimal,
        @RequestParam category: String,
        @RequestParam(required = false) coverImage: MultipartFile?,
        @AuthenticationPrincipal principal: UserDetails,
        redirectAttributes: RedirectAttributes
    ): String {
        val operator = currentOperator(principal)
        val tour = tourService.findById(id) ?: return "redirect:/operator"
        if (tour.operator?.id != operator.id && operator.role != UserRole.ADMIN) return "redirect:/operator"

        tour.title = title
        tour.description = description
        tour.destination = destination
        tour.country = country
        tour.durationDays = durationDays
        tour.price = price
        tour.category = TourCategory.valueOf(category)
        if (coverImage != null && !coverImage.isEmpty) {
            tour.coverImage = coverImage.bytes
            tour.coverImageType = coverImage.contentType
        }
        tourService.save(tour)
        redirectAttributes.addFlashAttribute("success", "Тур обновлён!")
        return "redirect:/operator"
    }

    @PostMapping("/tours/{id}/delete")
    fun deleteTour(
        @PathVariable id: Long,
        @AuthenticationPrincipal principal: UserDetails,
        redirectAttributes: RedirectAttributes
    ): String {
        val operator = currentOperator(principal)
        val tour = tourService.findById(id) ?: return "redirect:/operator"
        if (tour.operator?.id != operator.id && operator.role != UserRole.ADMIN) return "redirect:/operator"
        try {
            tourService.delete(id)
            redirectAttributes.addFlashAttribute("success", "Тур удалён.")
        } catch (e: DataIntegrityViolationException) {
            redirectAttributes.addFlashAttribute(
                "error",
                "Этот тур нельзя удалить: у него есть даты или бронирования. Сначала удалите связанные данные или оставьте тур в архиве."
            )
        }
        return "redirect:/operator"
    }

    @PostMapping("/tours/{id}/dates")
    fun addDate(
        @PathVariable id: Long,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) departureDate: LocalDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) returnDate: LocalDate,
        @RequestParam totalSeats: Int,
        @AuthenticationPrincipal principal: UserDetails,
        redirectAttributes: RedirectAttributes
    ): String {
        val operator = currentOperator(principal)
        val tour = tourService.findById(id) ?: return "redirect:/operator"
        if (tour.operator?.id != operator.id && operator.role != UserRole.ADMIN) return "redirect:/operator"
        tourDateRepository.save(TourDate(tour = tour, departureDate = departureDate, returnDate = returnDate, totalSeats = totalSeats))
        redirectAttributes.addFlashAttribute("success", "Дата добавлена!")
        return "redirect:/operator/tours/${id}/edit"
    }
}
