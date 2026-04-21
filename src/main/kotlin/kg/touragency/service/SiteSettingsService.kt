package kg.touragency.service

import kg.touragency.entity.SiteSettings
import kg.touragency.repository.SiteSettingsRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SiteSettingsService(private val repo: SiteSettingsRepository) {

    fun get(key: String, default: String = ""): String =
        repo.findById(key).map { it.value }.orElse(default)

    @Transactional
    fun set(key: String, value: String) {
        repo.save(SiteSettings(key = key, value = value))
    }

    fun getAll(): Map<String, String> =
        repo.findAll().associate { it.key to it.value }
}
