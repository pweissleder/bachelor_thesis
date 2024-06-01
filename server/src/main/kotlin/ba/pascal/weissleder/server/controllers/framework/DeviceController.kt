package ba.pascal.weissleder.server.controllers.framework

import ba.pascal.weissleder.server.model.framework.Device
import ba.pascal.weissleder.server.model.framework.DeviceState
import ba.pascal.weissleder.server.model.framework.support.devices.NodeID
import ba.pascal.weissleder.server.model.framework.support.messages.CommandMessage
import ba.pascal.weissleder.server.services.framework.DeviceService
import ba.pascal.weissleder.server.services.framework.SecurityService
import jakarta.persistence.EntityNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpClientErrorException
import java.security.Principal
import java.time.Instant

@RestController
@RequestMapping("/devices")
class DeviceController @Autowired constructor(
    private val deviceService: DeviceService
) {

    //E.g. of permission based endpoint
    // @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping("/command")
    fun applyCommand(
        principal: Principal,
        @RequestBody commandMessage: CommandMessage
    ): ResponseEntity<Unit> {
        return try {
            val userId = SecurityService.getUserFromPrincipal(principal)?.id ?: throw NoSuchElementException()
            ResponseEntity.ok().body(
                deviceService.applyCommand(
                    userId,
                    commandMessage.contextId,
                    commandMessage.command
                )
            )
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        } catch (e: HttpClientErrorException) {
            ResponseEntity.status(e.statusCode.value()).build()
        }
    }

    @GetMapping
    fun getDeviceState(
        @RequestBody nodeID: NodeID
    ): ResponseEntity<Device> {
        return try {
            ResponseEntity.ok().body(
                deviceService.getDevice(nodeID)
            )
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/list")
    fun getDevicesByIds(
        @RequestBody nodeIds: List<NodeID>
    ): ResponseEntity<List<Device>> {
        return try {
            ResponseEntity.ok().body(
                deviceService.getDevicesByIds(nodeIds)
            )
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/state-until")
    fun getDeviceStateUntil(
        @RequestBody nodeID: NodeID, untilInstant: Instant
    ): ResponseEntity<List<DeviceState>> {
        return try {
            ResponseEntity.ok().body(
                deviceService.getDeviceStateUntil(nodeID, untilInstant)
            )
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping()
    fun removeDeviceStatesById(
        @RequestBody nodeID: NodeID
    ): ResponseEntity<Unit> {
        return try {
            ResponseEntity.ok().body(
                deviceService.removeDeviceStatesById(nodeID)
            )
        } catch (e: EntityNotFoundException) {
            ResponseEntity.notFound().build()
        }
    }

    fun saveDeviceState(message: String) {
        deviceService.saveDeviceState(message)
    }

    fun getAllNodeIDs(): List<NodeID> {
        return deviceService.getAllNodeIDs()
    }

}
