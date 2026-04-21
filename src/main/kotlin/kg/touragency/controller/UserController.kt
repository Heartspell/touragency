package kg.touragency.controller

import kg.touragency.service.BookingService
import kg.touragency.service.UserService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/cabinet")
class UserController(
    private val userService: UserService,
    private val bookingService: BookingService
) {

    @GetMapping
    fun cabinet(@AuthenticationPrincipal principal: UserDetails, model: Model): String {
        val user = userService.findByEmail(principal.username) ?: return "redirect:/login"
        val bookings = bookingService.getByTourist(user)
        model.addAttribute("user", user)
        model.addAttribute("bookings", bookings)
        return "user/cabinet"
    }
}
