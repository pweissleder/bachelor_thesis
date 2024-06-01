package ba.pascal.weissleder.server.repositories

import ba.pascal.weissleder.server.model.framework.support.GatewaySession
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*


interface GatewaySessionRepository : JpaRepository<GatewaySession, UUID> {
    fun findBySessionToken(sessionToken: String): GatewaySession?

}
