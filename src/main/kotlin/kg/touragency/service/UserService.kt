package kg.touragency.service

import kg.touragency.entity.User
import kg.touragency.entity.UserRole
import kg.touragency.repository.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : UserDetailsService {

    override fun loadUserByUsername(email: String): UserDetails {
        // Spring Security вызывает этот метод во время входа.
        val user = userRepository.findByEmail(email)
            .orElseThrow { UsernameNotFoundException("User not found: $email") }

        // Добавляем роль пользователя: ADMIN, OPERATOR или TOURIST.
        val role = SimpleGrantedAuthority("ROLE_${user.role.name}")

        return org.springframework.security.core.userdetails.User(
            user.email,
            user.password,
            listOf(role)
        )
    }

    @Transactional
    fun register(email: String, password: String, fullName: String, phone: String, role: UserRole): User {
        // Нельзя создать двух пользователей с одним email.
        if (userRepository.findByEmail(email).isPresent) {
            throw IllegalArgumentException("Email already registered")
        }

        // Пароль сохраняем не открытым текстом, а в зашифрованном виде.
        val encodedPassword = passwordEncoder.encode(password)

        val user = User(
            email = email,
            password = encodedPassword,
            fullName = fullName,
            phone = phone,
            role = role
        )
        return userRepository.save(user)
    }

    fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email).orElse(null)
    }

    fun findAll(): List<User> {
        return userRepository.findAll()
    }

    fun findById(id: Long): User? {
        return userRepository.findById(id).orElse(null)
    }

    @Transactional
    fun save(user: User): User {
        return userRepository.save(user)
    }

    @Transactional
    fun deleteById(id: Long) {
        userRepository.deleteById(id)
    }

    fun search(query: String): List<User> {
        if (query.isBlank()) {
            return userRepository.findAll()
        }

        return userRepository.searchByEmailOrName(query)
    }

    fun count(): Long {
        return userRepository.count()
    }
}
