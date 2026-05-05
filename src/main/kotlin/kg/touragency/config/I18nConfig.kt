package kg.touragency.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.i18n.CookieLocaleResolver
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.util.*

@Configuration
class I18nConfig : WebMvcConfigurer {

    /**
     * Сохраняет выбранный язык в cookie
     * Работает с фронтенд cookie 'tkg-lang' и параметром 'lang' в URL
     */
    @Bean
    fun localeResolver(): LocaleResolver {
        val resolver = CookieLocaleResolver()
        resolver.setDefaultLocale(Locale("ru"))
        resolver.setCookieName("tkg-lang") // Совпадает с фронтенд cookie
        resolver.setCookieMaxAge(365 * 24 * 60 * 60) // 1 год
        return resolver
    }

    /**
     * Перехватывает параметр 'lang' в URL и меняет язык
     * Пример: /tours?lang=kg
     */
    @Bean
    fun localeChangeInterceptor(): LocaleChangeInterceptor {
        val interceptor = LocaleChangeInterceptor()
        interceptor.paramName = "lang"
        return interceptor
    }

    /**
     * Регистрирует перехватчик
     */
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(localeChangeInterceptor())
    }
}
