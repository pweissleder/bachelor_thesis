package ba.pascal.weissleder.server.controllers.prototype

import ba.pascal.weissleder.server.model.framework.support.messages.DeviceToContextMessage
import ba.pascal.weissleder.server.model.prototype.Meeting
import ba.pascal.weissleder.server.services.prototype.MeetingService
import jakarta.persistence.EntityNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/meetings")
class MeetingController @Autowired constructor(
    private val meetingService: MeetingService
) {
    @GetMapping
    fun getMeetingById(@RequestParam id: Long): ResponseEntity<Meeting> {
        return try {
            ResponseEntity.ok(meetingService.getMeetingById(id))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @GetMapping("/all")
    fun getAllMeetings(): ResponseEntity<List<Meeting>> {
        return try {
            ResponseEntity.ok(meetingService.getAllMeetings())
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @PostMapping
    fun createMeeting(@RequestBody meeting: Meeting): ResponseEntity<Meeting> {
        return try {
            ResponseEntity.ok(meetingService.createMeeting(meeting))
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @DeleteMapping
    fun deleteMeeting(@RequestParam id: Long): ResponseEntity<Unit> {
        return try {
            meetingService.deleteMeeting(id)
            ResponseEntity.ok().build()
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @PutMapping("/notes")
    fun editNotesOfMeeting(@RequestParam id: Long, @RequestBody notes: String): ResponseEntity<Unit> {
        return try {
            meetingService.editNotesOfMeeting(id, notes)
            ResponseEntity.ok().build()
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @PostMapping("/devices")
    fun addDeviceToMeeting(@RequestBody deviceToContextMessage: DeviceToContextMessage): ResponseEntity<Unit> {
        return try {
            meetingService.addDeviceToMeeting(deviceToContextMessage.contextId, deviceToContextMessage.deviceId)
            ResponseEntity.ok().build()
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @DeleteMapping("/devices")
    fun removeDeviceFromMeeting(@RequestBody deviceToContextMessage: DeviceToContextMessage): ResponseEntity<Unit> {
        return try {
            meetingService.removeDeviceFromMeeting(deviceToContextMessage.contextId, deviceToContextMessage.deviceId)
            ResponseEntity.ok().build()
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @PostMapping("/rooms")
    fun addRoomToMeeting(@RequestParam meetingId: Long, @RequestParam roomId: Long): ResponseEntity<Unit> {
        return try {
            meetingService.addSubContextToMeeting(meetingId, roomId)
            ResponseEntity.ok().build()
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @DeleteMapping("/rooms")
    fun removeRoomFromMeeting(@RequestParam meetingId: Long, @RequestParam roomId: Long): ResponseEntity<Unit> {
        return try {
            meetingService.removeSubContextFromMeeting(meetingId, roomId)
            ResponseEntity.ok().build()
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @PostMapping("/authorize-user")
    fun authorizeUserForMeeting(@RequestParam userId: Long, @RequestParam code: Int): ResponseEntity<Unit> {
        return try {
            meetingService.authorizeUserForMeeting(userId, code)
            ResponseEntity.ok().build()
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @DeleteMapping("/remove-user")
    fun removeMeetingFromUser(@RequestParam userId: Long, @RequestParam code: Int): ResponseEntity<Unit> {
        return try {
            meetingService.removeMeetingFromUser(userId, code)
            ResponseEntity.ok().build()
        } catch (e: EntityNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }
}