package ba.pascal.weissleder.server.controllers.framework;

import ba.pascal.weissleder.server.model.framework.Gateway
import ba.pascal.weissleder.server.model.framework.support.devices.NodeID
import ba.pascal.weissleder.server.services.framework.GatewayService
import jakarta.persistence.EntityNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/gateways")
class GatewayController @Autowired constructor(
    private val gatewayService: GatewayService, @Lazy private val securityController: SecurityController

) {

    @GetMapping("/gateway-by-id")
    fun getGatewayById(@RequestParam id: UUID): ResponseEntity<Gateway> {
        return try {
            ResponseEntity.ok().body(gatewayService.getGatewayById(id))
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @GetMapping("/all")
    fun getAllGateways(): ResponseEntity<List<Gateway>> {
        return try {
            ResponseEntity.ok().body(gatewayService.getAllGateways())
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @GetMapping("/all/ids")
    fun getAllGatewayIds(): ResponseEntity<List<UUID>> {
        return try {
            ResponseEntity.ok().body(gatewayService.getAllGatewayIds())
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @GetMapping("/active")
    fun getActiveGateways(): ResponseEntity<List<UUID>> {
        return try {
            ResponseEntity.ok().body(securityController.getAllActiveControllerIds())
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @GetMapping("/register")
    fun registerGateways(): ResponseEntity<Gateway> {
        return try {
            ResponseEntity.ok().body(gatewayService.createGateway())
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @DeleteMapping
    fun deleteGateway(@RequestParam id: UUID): ResponseEntity<Unit> {
        return try {
            ResponseEntity.ok().body(gatewayService.deleteGateway(id))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    fun getFirstReachableGatewayIdByDeviceId(deviceId: NodeID): UUID? {
        return gatewayService.getFirstReachableGatewayIdByDeviceId(deviceId)
    }

    fun getGatewayIdByActiveDeviceId(deviceId: NodeID): UUID? {
        return gatewayService.getGatewayIdByActiveDeviceId(deviceId)
    }

    fun updateGatewayDevices(newGateway: Gateway) {
        gatewayService.updateGatewayDevices(newGateway)
    }

    fun updateGatewaySession(gateway: Gateway) {
        gatewayService.updateGatewaySession(gateway)
    }

    fun deleteDevicesOfGateway(gatewayId: UUID) {
        gatewayService.deleteDevicesOfGateway(gatewayId)
    }

    fun deleteSession(id: UUID) {
        gatewayService.deleteSession(id)
    }
}