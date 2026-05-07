package kg.touragency.service

import kg.touragency.entity.SiteSettings
import kg.touragency.repository.SiteSettingsRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SiteSettingsService(private val repo: SiteSettingsRepository) {

    fun get(key: String, default: String = ""): String {
        val setting = repo.findById(key).orElse(null)

        if (setting == null) {
            return default
        }

        return setting.value
    }

    @Transactional
    fun set(key: String, value: String) {
        repo.save(SiteSettings(key = key, value = value))
    }

    fun getAll(): Map<String, String> {
        val settings = mutableMapOf<String, String>()

        // Превращаем список настроек в Map: ключ -> значение.
        for (setting in repo.findAll()) {
            settings[setting.key] = setting.value
        }

        return settings
    }
}
