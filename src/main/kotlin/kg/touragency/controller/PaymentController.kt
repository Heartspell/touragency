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

    // In-memory store: transactionId -> code
    private val pendingTransactions = ConcurrentHashMap<String, String>()

    @GetMapping("/choose")
    fun choose(@RequestParam bookingId: Long, @RequestParam amount: BigDecimal, model: Model): String {
        val booking = bookingRepository.findById(bookingId).orElse(null) ?: return "redirect:/cabinet"
        model.addAttribute("bookingId", bookingId)
        model.addAttribute("amount", amount)
        model.addAttribute("booking", booking)
        model.addAttribute("tour", booking.tourDate?.tour)
        model.addAttribute("tourDate", booking.tourDate)
        model.addAttribute("deadline", booking.paymentDeadline)
        return "payment/choose"
    }

    /**
     * GET /payment/mbank?bookingId={id}&amount={amount}
     * Show the M-Bank payment page
     */
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

    /**
     * POST /payment/mbank/initiate
     * Simulate sending an SMS — returns transactionId
     */
    @PostMapping("/mbank/initiate")
    @ResponseBody
    fun initiate(@RequestBody request: InitiateRequest): ResponseEntity<Map<String, String>> {
        val txnId = "TXN-" + (100000 + Random.nextInt(900000)).toString()
        // In simulation the code is always "1234"
        pendingTransactions[txnId] = "1234"

        return ResponseEntity.ok(mapOf(
            "status" to "sms_sent",
            "transactionId" to txnId
        ))
    }

    /**
     * POST /payment/mbank/confirm
     * Check the OTP code
     */
    @PostMapping("/mbank/confirm")
    @ResponseBody
    fun confirm(@RequestBody request: ConfirmRequest): ResponseEntity<Map<String, String>> {
        val expectedCode = pendingTransactions[request.transactionId]

        return if (expectedCode != null && request.code == expectedCode) {
            // Keep transaction for the complete step
            ResponseEntity.ok(mapOf(
                "status" to "success",
                "message" to "Оплата прошла успешно! Ваш заказ подтверждён."
            ))
        } else {
            ResponseEntity.ok(mapOf(
                "status" to "error",
                "message" to "Неверный код. Попробуйте ещё раз или запросите новый."
            ))
        }
    }

    /**
     * POST /payment/mbank/complete
     * Finalize: mark booking as CONFIRMED and redirect to cabinet
     */
    @PostMapping("/mbank/complete")
    fun complete(
        @RequestParam bookingId: Long,
        @RequestParam(required = false) transactionId: String?,
        redirectAttributes: RedirectAttributes
    ): String {
        return try {
            val booking = bookingRepository.findById(bookingId).orElse(null)
            if (booking != null) {
                booking.status = BookingStatus.CONFIRMED
                bookingRepository.save(booking)
                // Clean up transaction
                if (transactionId != null) pendingTransactions.remove(transactionId)
                redirectAttributes.addFlashAttribute(
                    "success",
                    "Оплата успешно завершена! Бронирование #${bookingId} подтверждено."
                )
            } else {
                redirectAttributes.addFlashAttribute("error", "Бронирование не найдено.")
            }
            "redirect:/cabinet"
        } catch (e: Exception) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при подтверждении: ${e.message}")
            "redirect:/cabinet"
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
