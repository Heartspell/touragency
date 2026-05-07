package kg.touragency.controller

import kg.touragency.service.SiteSettingsService
import kg.touragency.service.TourService
import kg.touragency.service.UserService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class HomeController(
    private val tourService: TourService,
    private val userService: UserService,
    private val siteSettingsService: SiteSettingsService
) {

    @GetMapping("/")
    fun index(model: Model): String {
        // Находим данные, которые нужны главной странице.
        val popularTours = tourService.getFeatured()
        val tourCount = tourService.count()
        val userCount = userService.count()

        // Передаем данные в HTML-страницу.
        model.addAttribute("tours", popularTours)
        model.addAttribute("tourCount", tourCount)
        model.addAttribute("userCount", userCount)

        // Берем тексты сайта из настроек.
        val settings = siteSettingsService.getAll()

        // Если настройки нет, показываем обычный текст.
        val heroTitle = settings["hero_title"] ?: "Откройте Кыргызстан и мир"
        val heroSubtitle = settings["hero_subtitle"] ?: "Лучшие туры от проверенных операторов."
        val heroBadge = settings["hero_badge"] ?: "✈ Более 100 направлений"
        val heroBtnPrimary = settings["hero_btn_primary"] ?: "Найти тур"
        val heroBtnSecondary = settings["hero_btn_secondary"] ?: "Смотреть все туры"
        val statsTours = settings["stats_tours"] ?: "500+"
        val statsClients = settings["stats_clients"] ?: "12 000+"
        val statsYears = settings["stats_years"] ?: "8"
        val statsRating = settings["stats_rating"] ?: "4.9"
        val featuredSectionTitle = settings["featured_section_title"] ?: "Популярные направления"

        // Отправляем тексты в HTML-страницу.
        model.addAttribute("heroTitle", heroTitle)
        model.addAttribute("heroSubtitle", heroSubtitle)
        model.addAttribute("heroBadge", heroBadge)
        model.addAttribute("heroBtnPrimary", heroBtnPrimary)
        model.addAttribute("heroBtnSecondary", heroBtnSecondary)
        model.addAttribute("statsTours", statsTours)
        model.addAttribute("statsClients", statsClients)
        model.addAttribute("statsYears", statsYears)
        model.addAttribute("statsRating", statsRating)
        model.addAttribute("featuredSectionTitle", featuredSectionTitle)

        // Показываем файл templates/index.html.
        return "index"
    }
}
