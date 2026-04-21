package kg.touragency.repository

import kg.touragency.entity.SiteSettings
import org.springframework.data.jpa.repository.JpaRepository

interface SiteSettingsRepository : JpaRepository<SiteSettings, String>
