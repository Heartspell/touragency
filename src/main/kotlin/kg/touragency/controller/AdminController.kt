package kg.touragency.controller

import kg.touragency.entity.UserRole
import kg.touragency.service.BookingService
import kg.touragency.service.SiteSettingsService
import kg.touragency.service.TourService
import kg.touragency.service.UserService
import kg.touragency.service.TourImageService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping("/admin")
class AdminController(
    private val userService: UserService,
    private val tourService: TourService,
    private val bookingService: BookingService,
    private val siteSettingsService: SiteSettingsService,
    private val tourImageService: TourImageService
) {

    @GetMapping
    fun dashboard(model: Model): String {
        model.addAttribute("userCount", userService.count())
        model.addAttribute("tourCount", tourService.count())
        model.addAttribute("activeTourCount", tourService.countActive())
        model.addAttribute("bookingCount", bookingService.count())
        model.addAttribute("recentUsers", userService.findAll().sortedByDescending { it.createdAt }.take(10))
        model.addAttribute("users", userService.findAll())
        model.addAttribute("settings", siteSettingsService.getAll())
        model.addAttribute("activeTab", "dashboard")
        return "admin/panel"
    }

    @GetMapping("/users")
    fun users(@RequestParam(required = false) q: String?, model: Model): String {
        val users = if (q.isNullOrBlank()) userService.findAll() else userService.search(q)
        model.addAttribute("users", users)
        model.addAttribute("q", q ?: "")
        model.addAttribute("settings", siteSettingsService.getAll())
        model.addAttribute("userCount", userService.count())
        model.addAttribute("tourCount", tourService.count())
        model.addAttribute("activeTourCount", tourService.countActive())
        model.addAttribute("bookingCount", bookingService.count())
        model.addAttribute("activeTab", "users")
        return "admin/panel"
    }

    @GetMapping("/users/{id}")
    fun userDetail(@PathVariable id: Long, model: Model): String {
        val user = userService.findById(id) ?: return "redirect:/admin/users"
        model.addAttribute("editUser", user)
        model.addAttribute("users", userService.findAll())
        model.addAttribute("settings", siteSettingsService.getAll())
        model.addAttribute("activeTab", "users")
        return "admin/panel"
    }

    @PostMapping("/users/{id}/role")
    fun changeRole(
        @PathVariable id: Long,
        @RequestParam role: String,
        ra: RedirectAttributes
    ): String {
        val user = userService.findById(id)
        if (user != null) {
            try {
                user.role = UserRole.valueOf(role)
                userService.save(user)
                ra.addFlashAttribute("successMsg", "Роль пользователя обновлена.")
            } catch (e: IllegalArgumentException) {
                ra.addFlashAttribute("errorMsg", "Неверная роль: $role")
            }
        }
        return "redirect:/admin/users"
    }

    @PostMapping("/users/{id}/delete")
    fun deleteUser(@PathVariable id: Long, ra: RedirectAttributes): String {
        userService.deleteById(id)
        ra.addFlashAttribute("successMsg", "Пользователь удалён.")
        return "redirect:/admin/users"
    }

    @GetMapping("/cms")
    fun cms(model: Model): String {
        val settings = siteSettingsService.getAll()
        model.addAttribute("settings", settings)
        model.addAttribute("users", userService.findAll())
        model.addAttribute("userCount", userService.count())
        model.addAttribute("tourCount", tourService.count())
        model.addAttribute("activeTourCount", tourService.countActive())
        model.addAttribute("bookingCount", bookingService.count())
        model.addAttribute("activeTab", "cms")
        return "admin/panel"
    }

    @PostMapping("/cms")
    fun saveCms(@RequestParam params: Map<String, String>, ra: RedirectAttributes): String {
        val allowed = setOf(
            "hero_title", "hero_subtitle", "hero_badge", "hero_btn_primary", "hero_btn_secondary",
            "stats_tours", "stats_clients", "stats_years", "stats_rating",
            "featured_section_title", "footer_phone", "footer_email", "footer_address"
        )
        params.filter { it.key in allowed }.forEach { (k, v) -> siteSettingsService.set(k, v) }
        ra.addFlashAttribute("successMsg", "Настройки сайта сохранены.")
        return "redirect:/admin/cms"
    }

    @GetMapping("/tours")
    fun adminTours(model: Model): String {
        model.addAttribute("tours", tourService.findAll())
        model.addAttribute("settings", siteSettingsService.getAll())
        model.addAttribute("userCount", userService.count())
        model.addAttribute("tourCount", tourService.count())
        model.addAttribute("activeTourCount", tourService.countActive())
        model.addAttribute("bookingCount", bookingService.count())
        model.addAttribute("activeTab", "tours")
        return "admin/tours"
    }

    @GetMapping("/tours/{id}/images")
    fun manageImages(@PathVariable id: Long, model: Model): String {
        val tour = tourService.findById(id) ?: return "redirect:/admin/tours"
        val images = tourImageService.findByTour(tour)
        model.addAttribute("tour", tour)
        model.addAttribute("images", images)
        model.addAttribute("settings", siteSettingsService.getAll())
        model.addAttribute("userCount", userService.count())
        model.addAttribute("tourCount", tourService.count())
        model.addAttribute("activeTourCount", tourService.countActive())
        model.addAttribute("bookingCount", bookingService.count())
        model.addAttribute("activeTab", "tours")
        return "admin/tour-images"
    }

    @PostMapping("/tours/{id}/images")
    fun uploadImages(@PathVariable id: Long, @RequestParam("images") images: Array<MultipartFile>, ra: RedirectAttributes): String {
        val tour = tourService.findById(id) ?: return "redirect:/admin/tours"
        images.filter { it != null && !it.isEmpty }.forEach { file -> tourImageService.save(tour, file) }
        ra.addFlashAttribute("successMsg", "Изображения загружены.")
        return "redirect:/admin/tours/${id}/images"
    }

    @PostMapping("/tours/{id}/images/{imageId}/delete")
    fun deleteImage(@PathVariable id: Long, @PathVariable imageId: Long, ra: RedirectAttributes): String {
        val img = tourImageService.findById(imageId)
        if (img != null && img.tour?.id == id) {
            tourImageService.deleteById(imageId)
            ra.addFlashAttribute("successMsg", "Изображение удалено.")
        } else {
            ra.addFlashAttribute("errorMsg", "Изображение не найдено.")
        }
        return "redirect:/admin/tours/${id}/images"
    }

    @PostMapping("/tours/images/recompress")
    fun recompressAllImages(ra: RedirectAttributes): String {
        Thread {
            try {
                val count = tourImageService.recompressAll()
                println("Recompressed images: $count")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
        ra.addFlashAttribute("successMsg", "Пересжатие изображений запущено в фоне.")
        return "redirect:/admin/tours"
    }
}
