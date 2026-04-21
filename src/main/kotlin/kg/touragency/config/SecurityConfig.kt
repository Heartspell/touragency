package kg.touragency.config

import kg.touragency.service.UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AuthenticationSuccessHandler

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenticationProvider(userService: UserService, passwordEncoder: PasswordEncoder): DaoAuthenticationProvider {
        val provider = DaoAuthenticationProvider()
        provider.setUserDetailsService(userService)
        provider.setPasswordEncoder(passwordEncoder)
        return provider
    }

    @Bean
    fun successHandler(): AuthenticationSuccessHandler {
        return AuthenticationSuccessHandler { _, response, authentication ->
            val roles = authentication.authorities.map { it.authority }
            val redirect = when {
                "ROLE_ADMIN" in roles -> "/admin"
                "ROLE_OPERATOR" in roles -> "/operator"
                else -> "/cabinet"
            }
            response.sendRedirect(redirect)
        }
    }

    @Bean
    fun filterChain(http: HttpSecurity, successHandler: AuthenticationSuccessHandler): SecurityFilterChain {
        http
            .csrf { it.ignoringRequestMatchers("/h2-console/**", "/register", "/payment/**") }
            .headers { it.frameOptions { fo -> fo.sameOrigin() } }
            .authorizeHttpRequests {
                it.requestMatchers(
                    "/", "/tours/**", "/login", "/register",
                    "/css/**", "/js/**", "/static/**",
                    "/h2-console/**", "/error"
                ).permitAll()
                it.requestMatchers("/admin/**").hasRole("ADMIN")
                it.requestMatchers("/operator/**").hasAnyRole("OPERATOR", "ADMIN")
                it.requestMatchers("/booking/**", "/cabinet/**", "/review/**", "/payment/**").authenticated()
                it.anyRequest().authenticated()
            }
            .formLogin {
                it.loginPage("/login")
                it.loginProcessingUrl("/login")
                it.successHandler(successHandler)
                it.failureUrl("/login?error")
                it.permitAll()
            }
            .logout {
                it.logoutUrl("/logout")
                it.logoutSuccessUrl("/")
                it.permitAll()
            }
        return http.build()
    }
}
