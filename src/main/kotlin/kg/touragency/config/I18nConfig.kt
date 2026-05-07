package kg.touragency.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.i18n.CookieLocaleResolver
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.time.Duration
import java.util.Locale

@Configuration
class I18nConfig : WebMvcConfigurer {

    // Сохраняем выбранный язык в cookie.
    @Bean
    fun localeResolver(): LocaleResolver {
        val resolver = CookieLocaleResolver("tkg-lang")
        resolver.setDefaultLocale(Locale.of("ru"))
        resolver.setCookieMaxAge(Duration.ofDays(365))
        return resolver
    }

    // Меняем язык, если в адресе есть ?lang=...
    @Bean
    fun localeChangeInterceptor(): LocaleChangeInterceptor {
        val interceptor = LocaleChangeInterceptor()
        interceptor.paramName = "lang"
        return interceptor
    }

    // Подключаем смену языка к сайту.
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(localeChangeInterceptor())
    }
}
