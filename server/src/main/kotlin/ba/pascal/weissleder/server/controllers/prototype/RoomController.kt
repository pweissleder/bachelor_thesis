package ba.pascal.weissleder.server.controllers.prototype

import ba.pascal.weissleder.server.model.prototype.Room
import ba.pascal.weissleder.server.services.prototype.RoomService
import jakarta.persistence.EntityNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rooms")
class RoomController @Autowired constructor(private val roomService: RoomService) {

    @GetMapping
    fun getRoomById(@RequestParam id: Long): ResponseEntity<Room> {
        return try {
            ResponseEntity.ok(roomService.getRoomById(id) as Room)
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @GetMapping("/all")
    fun getAllRooms(): ResponseEntity<List<Room>> {
        return try {
            ResponseEntity.ok(roomService.getAllRooms())
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @PostMapping
    fun createRoom(@RequestBody room: Room): ResponseEntity<Room> {
        return try {
            ResponseEntity.ok(roomService.createRoom(room))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @DeleteMapping
    fun deleteRoom(@RequestParam id: Long): ResponseEntity<Void> {
        return try {
            roomService.deleteRoom(id)
            ResponseEntity.ok().build()
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    // For add/remove devices & subcontexts from rooms use the endpoints provided by the ContextController
}