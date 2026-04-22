package kg.touragency.controller

import kg.touragency.dto.BookingRequest
import kg.touragency.repository.TourDateRepository
import kg.touragency.service.BookingService
import kg.touragency.service.UserService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping("/booking")
class BookingController(
    private val bookingService: BookingService,
    private val tourDateRepository: TourDateRepository,
    private val userService: UserService
) {

    @GetMapping("/new")
    fun wizard(@RequestParam tourDateId: Long, model: Model): String {
        val tourDate = tourDateRepository.findById(tourDateId).orElse(null) ?: return "redirect:/tours"
        model.addAttribute("tourDate", tourDate)
        model.addAttribute("tour", tourDate.tour)
        return "booking/wizard"
    }

    @PostMapping("/create")
    fun create(
        @ModelAttribute request: BookingRequest,
        @AuthenticationPrincipal principal: UserDetails,
        redirectAttributes: RedirectAttributes
    ): String {
        val tourist = userService.findByEmail(principal.username) ?: return "redirect:/login"
        return try {
            val booking = bookingService.createBooking(request, tourist)
            // Redirect to payment method selection
            "redirect:/payment/choose?bookingId=${booking.id}&amount=${booking.totalPrice}"
        } catch (e: Exception) {
            redirectAttributes.addFlashAttribute("error", e.message)
            "redirect:/booking/new?tourDateId=${request.tourDateId}"
        }
    }

    @PostMapping("/{id}/cancel")
    fun cancel(
        @PathVariable id: Long,
        @AuthenticationPrincipal principal: UserDetails,
        redirectAttributes: RedirectAttributes
    ): String {
        val tourist = userService.findByEmail(principal.username) ?: return "redirect:/login"
        return try {
            bookingService.cancel(id, tourist)
            redirectAttributes.addFlashAttribute("success", "Бронирование отменено.")
            "redirect:/cabinet"
        } catch (e: Exception) {
            redirectAttributes.addFlashAttribute("error", e.message)
            "redirect:/cabinet"
        }
    }
}
