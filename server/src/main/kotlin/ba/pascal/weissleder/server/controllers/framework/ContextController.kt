package ba.pascal.weissleder.server.controllers.framework

import ba.pascal.weissleder.server.model.framework.Context
import ba.pascal.weissleder.server.model.framework.support.devices.NodeID
import ba.pascal.weissleder.server.model.framework.support.messages.DeviceToContextMessage
import ba.pascal.weissleder.server.services.framework.ContextService
import jakarta.persistence.EntityNotFoundException
import org.postgresql.util.PSQLException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/contexts")
class ContextController(private val contextService: ContextService) {


    @GetMapping
    fun getContext(@RequestParam id: Long): ResponseEntity<Context> {
        return try {
            ResponseEntity.ok().body(contextService.getContextById(id))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @GetMapping("/all")
    fun getAllContexts(): ResponseEntity<List<Context>> {
        return try {
            ResponseEntity.ok().body(contextService.getAllContexts())
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        } catch (e: PSQLException) {
            ResponseEntity.notFound().build()
        }
    }


    @PostMapping
    fun createContext(@RequestBody context: Context): ResponseEntity<Context> {
        return try {
            ResponseEntity.ok().body(contextService.createContext(context))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        } catch (e: PSQLException) {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping
    fun deleteContext(@RequestParam id: Long): ResponseEntity<Unit> {
        return try {
            ResponseEntity.ok().body(contextService.deleteContext(id))
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    // Device Context Mapping
    @GetMapping("/devices")
    fun getDevicesForContext(@RequestParam id: Long): ResponseEntity<List<NodeID>> {
        return try {
            ResponseEntity.ok().body(contextService.getContextDevices(id))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @GetMapping("/devices/includeSubContexts")
    fun getAllDevicesForContext(@RequestParam id: Long): ResponseEntity<List<NodeID>> {
        return try {
            ResponseEntity.ok().body(contextService.getAllDevicesInContextAndSubContexts(id))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @PostMapping("/devices")
    fun addDeviceToContext(@RequestBody deviceToContextMessage: DeviceToContextMessage): ResponseEntity<Unit> {
        return try {
            ResponseEntity.ok().body(
                contextService.addDeviceToContext(
                    deviceToContextMessage.contextId, deviceToContextMessage.deviceId
                )
            )
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        } catch (e: PSQLException) {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/devices")
    fun deleteDeviceFromContext(@RequestBody deviceToContextMessage: DeviceToContextMessage): ResponseEntity<Unit> {
        return try {
            ResponseEntity.ok().body(
                contextService.deleteDeviceFromContext(
                    deviceToContextMessage.contextId, deviceToContextMessage.deviceId
                )
            )
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        } catch (e: PSQLException) {
            ResponseEntity.notFound().build()
        }
    }

    // Sub Context Mapping
    @PostMapping("/subcontexts")
    fun addSubContext(
        @RequestParam parentId: Long, @RequestParam subContextId: Long
    ): ResponseEntity<Unit> {
        return try {
            ResponseEntity.ok().body(contextService.addSubContext(parentId, subContextId))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        } catch (e: PSQLException) {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/subcontexts")
    fun removeSubContext(
        @RequestParam parentId: Long, @RequestParam subContextId: Long
    ): ResponseEntity<Unit> {
        return try {
            ResponseEntity.ok().body(contextService.removeSubContext(parentId, subContextId))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        } catch (e: PSQLException) {
            ResponseEntity.notFound().build()
        }
    }

    fun contextHasDevice(contextId: Long, deviceId: NodeID): Boolean {
        return contextService.contextHasDevice(contextId, deviceId)
    }

    fun contextHasInvocationPermission(contextId: Long): Boolean {
        return contextService.contextHasInvocationPermission(contextId)
    }
}