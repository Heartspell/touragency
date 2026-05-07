package kg.touragency.controller

import kg.touragency.entity.BookingStatus
import kg.touragency.repository.BookingRepository
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.math.BigDecimal
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

@Controller
@RequestMapping("/payment")
class PaymentController(
    private val bookingRepository: BookingRepository
) {

    // Здесь временно храним код оплаты: transactionId -> code.
    private val pendingTransactions = ConcurrentHashMap<String, String>()

    @GetMapping("/choose")
    fun choose(@RequestParam bookingId: Long, @RequestParam amount: BigDecimal, model: Model): String {
        val booking = bookingRepository.findById(bookingId).orElse(null)
        if (booking == null) {
            return "redirect:/cabinet"
        }

        model.addAttribute("bookingId", bookingId)
        model.addAttribute("amount", amount)
        model.addAttribute("booking", booking)
        model.addAttribute("tour", booking.tourDate?.tour)
        model.addAttribute("tourDate", booking.tourDate)
        model.addAttribute("deadline", booking.paymentDeadline)
        return "payment/choose"
    }

    @GetMapping("/mbank")
    fun showMbank(
        @RequestParam bookingId: Long,
        @RequestParam amount: BigDecimal,
        model: Model
    ): String {
        model.addAttribute("bookingId", bookingId)
        model.addAttribute("amount", amount)
        return "payment/mbank"
    }

    @PostMapping("/mbank/initiate")
    @ResponseBody
    fun initiate(@RequestBody request: InitiateRequest): ResponseEntity<Map<String, String>> {
        // Создаем случайный номер операции.
        val randomNumber = 100000 + Random.nextInt(900000)
        val transactionId = "TXN-$randomNumber"

        // Для учебной оплаты код всегда 1234.
        pendingTransactions[transactionId] = "1234"

        val answer = mapOf(
            "status" to "sms_sent",
            "transactionId" to transactionId
        )

        return ResponseEntity.ok(answer)
    }

    @PostMapping("/mbank/confirm")
    @ResponseBody
    fun confirm(@RequestBody request: ConfirmRequest): ResponseEntity<Map<String, String>> {
        val expectedCode = pendingTransactions[request.transactionId]

        if (expectedCode != null && request.code == expectedCode) {
            // Код верный, оплату можно завершать.
            val answer = mapOf(
                "status" to "success",
                "message" to "Оплата прошла успешно! Ваш заказ подтверждён."
            )
            return ResponseEntity.ok(answer)
        } else {
            val answer = mapOf(
                "status" to "error",
                "message" to "Неверный код. Попробуйте ещё раз или запросите новый."
            )
            return ResponseEntity.ok(answer)
        }
    }

    @PostMapping("/mbank/complete")
    fun complete(
        @RequestParam bookingId: Long,
        @RequestParam(required = false) transactionId: String?,
        redirectAttributes: RedirectAttributes
    ): String {
        try {
            val booking = bookingRepository.findById(bookingId).orElse(null)

            if (booking != null) {
                booking.status = BookingStatus.CONFIRMED
                bookingRepository.save(booking)

                // После оплаты код больше не нужен.
                if (transactionId != null) {
                    pendingTransactions.remove(transactionId)
                }

                redirectAttributes.addFlashAttribute(
                    "success",
                    "Оплата успешно завершена! Бронирование #${bookingId} подтверждено."
                )
            } else {
                redirectAttributes.addFlashAttribute("error", "Бронирование не найдено.")
            }

            return "redirect:/cabinet"
        } catch (e: Exception) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при подтверждении: ${e.message}")
            return "redirect:/cabinet"
        }
    }

    data class InitiateRequest(
        val bookingId: Long = 0,
        val amount: String = "",
        val phone: String = ""
    )

    data class ConfirmRequest(
        val code: String = "",
        val transactionId: String = ""
    )
}
