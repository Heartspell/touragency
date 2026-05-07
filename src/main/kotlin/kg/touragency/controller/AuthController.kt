package kg.touragency.controller

import kg.touragency.entity.UserRole
import kg.touragency.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
class AuthController(private val userService: UserService) {

    // Страница входа также показывает форму регистрации.
    @GetMapping("/login")
    fun login(): String {
        return "auth/login"
    }

    @GetMapping("/register")
    fun registerPage(): String {
        return "auth/login"
    }

    @PostMapping("/register")
    @ResponseBody
    fun register(
        @RequestParam email: String,
        @RequestParam password: String,
        @RequestParam fullName: String,
        @RequestParam(defaultValue = "") phone: String
    ): ResponseEntity<Map<String, String>> {
        try {
            userService.register(email, password, fullName, phone, UserRole.TOURIST)

            val answer = mapOf("status" to "ok")
            return ResponseEntity.ok(answer)
        } catch (e: IllegalArgumentException) {
            val message = e.message ?: "Ошибка"
            val answer = mapOf("error" to message)
            return ResponseEntity.badRequest().body(answer)
        }
    }
}
