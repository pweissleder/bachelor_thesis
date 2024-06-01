package ba.pascal.weissleder.server.config

import ba.pascal.weissleder.server.model.framework.User
import ba.pascal.weissleder.server.repositories.UserRepository
import ba.pascal.weissleder.server.services.framework.SecurityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.filter.OncePerRequestFilter
import java.util.*

// The following classes are based on the Unicorn Project
// Additional sources: https://www.baeldung.com/security-spring
@Configuration
@EnableWebSecurity
class SecurityConfiguration {

    @Autowired
    lateinit var securityService: SecurityService

    @Autowired
    lateinit var userRepo: UserRepository

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .cors(Customizer.withDefaults())  // Allow default CORS configuration as per Spring Security
            .sessionManagement { sessionManagement ->
                sessionManagement
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }

            .authorizeHttpRequests() {
                it
                    .requestMatchers("/echo", "/security/getSessionToken").permitAll()  // Public endpoints
                    .requestMatchers("/controllers/**").hasAuthority("ADMIN") // as an Example
                     .requestMatchers ("/**").permitAll()  // for unauthenticated  testing, remove in production!
                    .anyRequest().authenticated()  // Other endpoints require authentication
            }
            .addFilterBefore(
                AuthenticationFilter(securityService, userRepo),
                UsernamePasswordAuthenticationFilter::class.java
            )  // Add custom authentication filter
        return http.build()
    }
}


class AuthenticationFilter(
    private val securityService: SecurityService,
    private val userRepository: UserRepository
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: jakarta.servlet.http.HttpServletRequest,
        response: jakarta.servlet.http.HttpServletResponse,
        filterChain: jakarta.servlet.FilterChain
    ) {
        var authenticated = false
        var user: User? = null
        // get token from the request header
        val token = request.getHeader(AUTHORIZATION)

        // basic auth for webapp check if username password are in db
        if (token != null && token.startsWith("Basic")) {
            val credentials = String(Base64.getDecoder().decode(token.substring(6))).split(":")
            if (credentials.size == 2) {
                val dbUser = userRepository.findByEmail(credentials[0])
                if (dbUser?.password == credentials[1]) {
                    user = dbUser
                    authenticated = true
                }
            }
        } else if (token != null && securityService.authenticateGateway(token)) {
            authenticated = true
        }

        val auth = MyAuth(user, authenticated)
        if (user != null) {
            auth.addAuthority(SimpleGrantedAuthority(user.role.toString()))
        }
        SecurityContextHolder.getContext().authentication = auth
        filterChain.doFilter(request, response)
    }
}


class MyAuth(
    private val user: User?,
    private val innerAuthenticated: Boolean,
) : Authentication {
    private val authorities = mutableListOf<GrantedAuthority>()

    fun addAuthority(authority: GrantedAuthority) {
        authorities.add(authority)
    }

    override fun getName(): String {
        return user?.name ?: "NotSet"
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = authorities
    override fun getCredentials(): Any = "NotSet"
    override fun getDetails(): Long? {
        return user?.id
    }

    override fun getPrincipal(): User? = user
    override fun isAuthenticated(): Boolean = innerAuthenticated
    override fun setAuthenticated(isAuthenticated: Boolean) {
        throw Exception("Pass the immutable constructor variable")
    }

}