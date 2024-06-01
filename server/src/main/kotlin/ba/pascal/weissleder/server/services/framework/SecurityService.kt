package ba.pascal.weissleder.server.services.framework

import ba.pascal.weissleder.server.controllers.framework.ContextController
import ba.pascal.weissleder.server.controllers.framework.GatewayController
import ba.pascal.weissleder.server.controllers.framework.UserController
import ba.pascal.weissleder.server.model.framework.User
import ba.pascal.weissleder.server.model.framework.support.GatewaySession
import ba.pascal.weissleder.server.model.framework.support.devices.Command
import ba.pascal.weissleder.server.repositories.GatewaySessionRepository
import jakarta.transaction.Transactional
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import java.security.Principal
import java.util.*
import java.io.Serializable as JavaSerializable

const val GATEWAY_SESSION_TOKEN_VALID_DAYS: Int = 90
const val LEN_SESSION_TOKEN: Int = 20

@Service
class SecurityService(
    private val gatewaySessionRepository: GatewaySessionRepository,
    private val gatewayController: GatewayController,
    private val userController: UserController,
    private val contextController: ContextController
) {

    @Transactional
    fun generateSessionToken(id: UUID, authToken: String): GatewaySession {
        val gateway = gatewayController.getGatewayById(id).body ?: throw NoSuchElementException()
        if (gateway.authToken != authToken) {
            println("Authentication request failed")
            throw NoSuchElementException()
        }

        val sessionToken = generateNewSessionToken()
        val expireDate = generateExpirationDate()

        val res = GatewaySession(id, sessionToken, expireDate)
        gateway.session = res
        gatewayController.updateGatewaySession(gateway)
        println("Distributed SessionToken: $sessionToken to Gateway: $id valid until $expireDate")
        return res
    }


    fun authenticateGateway(sessionToken: String): Boolean {
        val session = gatewaySessionRepository.findBySessionToken(sessionToken)
        if (session == null) {
            println("Authorization request failed")
            return false
        }

        if (session.expireDate.before(Date())) {
            println("Authorization request failed")
            return false
        }

        println("Authorization request successful")
        return true
    }

    // TODO: Improve
    fun checkUserAuthForCommand(userId: Long, contextId: Long, command: Command<JavaSerializable?>): Boolean {
        if (userController.userHasContext(userId, contextId) &&
            userController.userHasDevice(userId, command.nodeId) &&
            contextController.contextHasDevice(contextId, command.nodeId) &&
            contextController.contextHasInvocationPermission(contextId)
        ) {
            println("Authorization request for Command successful")
            return true
        }
        println("Authorization request failed")
        return false
    }

    fun generateExpirationDate(): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, GATEWAY_SESSION_TOKEN_VALID_DAYS)
        return calendar.time
    }

    fun generateNewSessionToken(): String {
        while (true) {
            val sessionToken = getRandomString(LEN_SESSION_TOKEN)
            if (gatewaySessionRepository.findBySessionToken(sessionToken) == null) {
                return sessionToken
            }
        }
    }

    // GatewaySession Repository
    fun getAllActiveGatewayIds(): List<UUID> {
        return gatewaySessionRepository.findAll().mapNotNull { it.gatewayId }
    }

    fun saveGatewaySession(session: GatewaySession) {
        gatewaySessionRepository.saveAndFlush(session)
    }

    fun deleteGatewaySession(id: UUID) {
        gatewaySessionRepository.deleteById(id)
    }

    companion object {
        fun getRandomString(length: Int): String {
            val allowedChars = ('A'..'Z') + ('0'..'9')
            return (1..length)
                .map { allowedChars.random() }
                .joinToString("")
        }

        fun getUserFromPrincipal(principal: Principal): User? {
            val auth = principal as Authentication
            return auth.principal as? User
        }
    }

}

