package kg.touragency.controller

import kg.touragency.entity.UserRole
import kg.touragency.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
class AuthController(private val userService: UserService) {

    // Legacy pages kept for redirect fallback
    @GetMapping("/login")
    fun login() = "auth/login"

    @GetMapping("/register")
    fun registerPage() = "auth/login"

    @PostMapping("/register")
    @ResponseBody
    fun register(
        @RequestParam email: String,
        @RequestParam password: String,
        @RequestParam fullName: String,
        @RequestParam(defaultValue = "") phone: String
    ): ResponseEntity<Map<String, String>> {
        return try {
            userService.register(email, password, fullName, phone, UserRole.TOURIST)
            ResponseEntity.ok(mapOf("status" to "ok"))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("error" to (e.message ?: "Ошибка")))
        }
    }
}
