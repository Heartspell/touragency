package kg.touragency.controller

import kg.touragency.entity.BookingStatus
import kg.touragency.repository.BookingRepository
import kg.touragency.service.UserService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import java.math.BigDecimal
import java.math.RoundingMode

@Controller
@RequestMapping("/voucher")
class VoucherController(
    private val bookingRepository: BookingRepository,
    private val userService: UserService
) {
    @GetMapping("/{bookingId}")
    fun voucher(
        @PathVariable bookingId: Long,
        @AuthenticationPrincipal principal: UserDetails,
        model: Model
    ): String {
        val booking = bookingRepository.findById(bookingId).orElse(null)
        if (booking == null) {
            return "redirect:/cabinet"
        }

        val currentUser = userService.findByEmail(principal.username)
        if (currentUser == null) {
            return "redirect:/cabinet"
        }

        // Ваучер видит только владелец брони или админ.
        val isOwner = booking.tourist?.id == currentUser.id
        val isAdmin = currentUser.role.name == "ADMIN"
        if (!isOwner && !isAdmin) {
            return "redirect:/cabinet"
        }

        // Ваучер доступен только после оплаты.
        if (booking.status != BookingStatus.CONFIRMED) {
            return "redirect:/cabinet"
        }

        model.addAttribute("booking", booking)
        model.addAttribute("tour", booking.tourDate?.tour)
        model.addAttribute("tourDate", booking.tourDate)
        model.addAttribute("tourist", booking.tourist)

        // Номер ваучера: год и id брони.
        val year = booking.createdAt.year
        val bookingNumber = booking.id.toString().padStart(6, '0')
        val voucherNumber = "TKG-$year-$bookingNumber"
        model.addAttribute("voucherNumber", voucherNumber)

        // Цена за одного человека.
        val participants = booking.participants.coerceAtLeast(1)
        val pricePerPerson = booking.totalPrice
            .divide(BigDecimal(participants), 0, RoundingMode.HALF_UP)
        model.addAttribute("pricePerPerson", pricePerPerson)

        return "voucher/voucher"
    }
}
