package ba.pascal.weissleder.server.controllers.framework

import ba.pascal.weissleder.server.model.framework.support.GatewaySession
import ba.pascal.weissleder.server.model.framework.support.devices.Command
import ba.pascal.weissleder.server.model.framework.support.messages.SessionTokenRequestMessage
import ba.pascal.weissleder.server.services.framework.SecurityService
import jakarta.persistence.EntityNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.Serializable
import java.util.*

@RestController
@RequestMapping("/security")
class SecurityController @Autowired constructor(
    private val securityService: SecurityService
) {
    @PostMapping("/getSessionToken")
    fun getSessionToken(@RequestBody request: SessionTokenRequestMessage): ResponseEntity<GatewaySession> {
        return try {
            ResponseEntity.ok().body(securityService.generateSessionToken(request.id, request.authToken))
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    fun checkUserAuthForCommand(userId: Long, contextId: Long, command: Command<Serializable?>): Boolean {
        return securityService.checkUserAuthForCommand(userId, contextId, command)
    }

    fun getAllActiveControllerIds(): List<UUID> {
        return securityService.getAllActiveGatewayIds()
    }

    fun deleteControllerSession(id: UUID) {
        securityService.deleteGatewaySession(id)
    }

}