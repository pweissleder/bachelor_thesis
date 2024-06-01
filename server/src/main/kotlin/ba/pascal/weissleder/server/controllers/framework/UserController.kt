package ba.pascal.weissleder.server.controllers.framework

import ba.pascal.weissleder.server.model.framework.Context
import ba.pascal.weissleder.server.model.framework.User
import ba.pascal.weissleder.server.model.framework.support.devices.NodeID
import ba.pascal.weissleder.server.services.framework.SecurityService
import ba.pascal.weissleder.server.services.framework.UserService
import jakarta.persistence.EntityNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/users")
class UserController @Autowired constructor(
    private val userService: UserService
) {
    @GetMapping
    fun getUser(@RequestParam id: Long): ResponseEntity<User> {
        return try {
            ResponseEntity.ok().body(userService.getUserById(id))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @GetMapping("/self")
    fun getUseSpecific(principal: Principal): ResponseEntity<User> {
        return try {
            val userId = SecurityService.getUserFromPrincipal(principal)?.id ?: throw java.util.NoSuchElementException()
            ResponseEntity.ok().body(userService.getUserById(userId))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @GetMapping("/all")
    fun getAllUsers(): ResponseEntity<List<User>> {
        return try {
            ResponseEntity.ok().body(userService.getAllUsers())
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    // used both for create (w.o. id) and update (w. id)
    @PostMapping
    fun updateUser(@RequestBody user: User): ResponseEntity<User> {
        return try {
            ResponseEntity.ok().body(userService.updateUser(user))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @DeleteMapping
    fun deleteUser(@RequestParam id: Long): ResponseEntity<Unit> {
        return try {
            ResponseEntity.ok().body(userService.deleteUser(id))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    // User Device Mapping
    @GetMapping("/devices")
    fun getDevicesForUserId(@RequestParam id: Long): ResponseEntity<List<NodeID>> {
        return try {
            ResponseEntity.ok().body(userService.getUserDevices(id))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    // User Context Mapping
    @PostMapping("/contexts")
    fun addContextToUser(@RequestParam userId: Long, @RequestParam contextId: Long): ResponseEntity<Unit> {
        return try {
            ResponseEntity.ok().body(userService.addContextToUser(userId, contextId))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @DeleteMapping("/contexts")
    fun removeContextFromUser(@RequestParam userId: Long, @RequestParam contextId: Long): ResponseEntity<Unit> {
        return try {
            ResponseEntity.ok().body(userService.removeContextFromUser(userId, contextId))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    fun findByContextsContaining(context: Context): List<User> {
        return userService.findByContextsContaining(context)
    }

    fun userHasContext(userId: Long, contextId: Long): Boolean {
        return userService.userHasContext(userId, contextId)
    }

    fun userHasDevice(userId: Long, deviceID: NodeID): Boolean {
        return userService.userHasDevice(userId, deviceID)
    }
}