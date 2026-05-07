package kg.touragency.init

import kg.touragency.repository.TourRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.net.URI

@Component
class TourImageInitializer(
    private val tourRepository: TourRepository
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        // Для каждого направления есть картинка по умолчанию.
        val images = mapOf(
            "Иссык-Куль" to "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1200&q=80",
            "Ала-Тоо" to "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?auto=format&fit=crop&w=1200&q=80",
            "Каракол" to "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?auto=format&fit=crop&w=1200&q=80",
            "Сон-Куль" to "https://images.unsplash.com/photo-1519904981063-b0cf448d479e?auto=format&fit=crop&w=1200&q=80",
            "Ош" to "https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=1200&q=80",
            "Анталья" to "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1200&q=80",
            "Каппадокия" to "https://images.unsplash.com/photo-1524231757912-21f4fe3a7200?auto=format&fit=crop&w=1200&q=80",
            "Дубай" to "https://images.unsplash.com/photo-1512453979798-5ea266f8880c?auto=format&fit=crop&w=1200&q=80",
            "Пхукет" to "https://images.unsplash.com/photo-1506929562872-bb421503ef21?auto=format&fit=crop&w=1200&q=80",
            "Бангкок" to "https://images.unsplash.com/photo-1508009603885-50cf7c579365?auto=format&fit=crop&w=1200&q=80",
            "Рим" to "https://images.unsplash.com/photo-1529260830199-42c24126f198?auto=format&fit=crop&w=1200&q=80",
            "Шарм" to "https://images.unsplash.com/photo-1544551763-46a013bb70d5?auto=format&fit=crop&w=1200&q=80",
            "Пекин" to "https://images.unsplash.com/photo-1508804185872-d7badad00f7d?auto=format&fit=crop&w=1200&q=80",
            "Тбилиси" to "https://images.unsplash.com/photo-1565008576549-57569a49371d?auto=format&fit=crop&w=1200&q=80",
            "Бали" to "https://images.unsplash.com/photo-1537996194471-e657df975ab4?auto=format&fit=crop&w=1200&q=80",
            "Япония" to "https://images.unsplash.com/photo-1528360983277-13d401cdc186?auto=format&fit=crop&w=1200&q=80"
        )

        val tours = tourRepository.findAll()
        for (tour in tours) {
            if (tour.coverImage != null) continue

            // Ищем картинку по названию или направлению тура.
            val url = images.entries
                .firstOrNull { tour.title.contains(it.key, ignoreCase = true) || tour.destination.contains(it.key, ignoreCase = true) }
                ?.value
                ?: continue

            try {
                // Скачиваем картинку и сохраняем ее в тур.
                tour.coverImage = URI(url).toURL().readBytes()
                tour.coverImageType = "image/jpeg"
                tourRepository.save(tour)
            } catch (e: Exception) {
                println("Не удалось скачать картинку для тура: ${tour.title}")
            }
        }
    }
}
