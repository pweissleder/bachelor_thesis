package ba.pascal.weissleder.server.repositories

import ba.pascal.weissleder.server.model.framework.Context
import ba.pascal.weissleder.server.model.framework.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    /** Login **/
    fun existsByEmail(email: String): Boolean

    fun findIdByEmail(email: String): Long

    fun findByEmail(email: String): User?

    @Query("SELECT u FROM User u WHERE :context MEMBER OF u.contexts")
    fun findByContextsContaining(@Param("context") context: Context): List<User>

}


