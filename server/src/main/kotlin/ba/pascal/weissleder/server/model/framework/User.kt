package ba.pascal.weissleder.server.model.framework

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import jakarta.persistence.*

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    var name: String = "",
    val email: String = "",
    var password: String = "",
    var role: Role = Role.USER,
    @ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    val contexts: MutableList<Context> = mutableListOf()
)

enum class Role {
    ADMIN,
    USER,
    EVENTHOST,
    EVENTMANAGER
}

