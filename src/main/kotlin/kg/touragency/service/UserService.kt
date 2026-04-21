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
        val user = userRepository.findByEmail(email)
            .orElseThrow { UsernameNotFoundException("User not found: $email") }
        return org.springframework.security.core.userdetails.User(
            user.email,
            user.password,
            listOf(SimpleGrantedAuthority("ROLE_${user.role.name}"))
        )
    }

    @Transactional
    fun register(email: String, password: String, fullName: String, phone: String, role: UserRole): User {
        if (userRepository.findByEmail(email).isPresent) {
            throw IllegalArgumentException("Email already registered")
        }
        val user = User(
            email = email,
            password = passwordEncoder.encode(password),
            fullName = fullName,
            phone = phone,
            role = role
        )
        return userRepository.save(user)
    }

    fun findByEmail(email: String): User? = userRepository.findByEmail(email).orElse(null)

    fun findAll(): List<User> = userRepository.findAll()

    fun findById(id: Long): User? = userRepository.findById(id).orElse(null)

    @Transactional
    fun save(user: User): User = userRepository.save(user)

    @Transactional
    fun deleteById(id: Long) = userRepository.deleteById(id)

    fun search(query: String): List<User> =
        if (query.isBlank()) userRepository.findAll()
        else userRepository.searchByEmailOrName(query)

    fun count(): Long = userRepository.count()
}
