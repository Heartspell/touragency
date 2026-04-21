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
        model.addAttribute("tours", tourService.getFeatured())
        model.addAttribute("tourCount", tourService.count())
        model.addAttribute("userCount", userService.count())

        val s = siteSettingsService.getAll()
        model.addAttribute("heroTitle", s["hero_title"] ?: "Откройте Кыргызстан и мир")
        model.addAttribute("heroSubtitle", s["hero_subtitle"] ?: "Лучшие туры от проверенных операторов.")
        model.addAttribute("heroBadge", s["hero_badge"] ?: "✈ Более 100 направлений")
        model.addAttribute("heroBtnPrimary", s["hero_btn_primary"] ?: "Найти тур")
        model.addAttribute("heroBtnSecondary", s["hero_btn_secondary"] ?: "Смотреть все туры")
        model.addAttribute("statsTours", s["stats_tours"] ?: "500+")
        model.addAttribute("statsClients", s["stats_clients"] ?: "12 000+")
        model.addAttribute("statsYears", s["stats_years"] ?: "8")
        model.addAttribute("statsRating", s["stats_rating"] ?: "4.9")
        model.addAttribute("featuredSectionTitle", s["featured_section_title"] ?: "Популярные направления")
        return "index"
    }
}
