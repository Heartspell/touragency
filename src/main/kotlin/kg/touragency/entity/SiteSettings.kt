package kg.touragency.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "site_settings")
class SiteSettings(
    @Id var key: String = "",
    @Column(columnDefinition = "TEXT") var value: String = ""
)
