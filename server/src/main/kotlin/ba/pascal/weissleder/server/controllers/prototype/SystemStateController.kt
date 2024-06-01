package ba.pascal.weissleder.server.controllers.prototype

import ba.pascal.weissleder.server.model.framework.support.devices.NodeID
import ba.pascal.weissleder.server.model.framework.support.messages.DeviceStatusMessage
import ba.pascal.weissleder.server.model.prototype.Meeting
import ba.pascal.weissleder.server.services.prototype.SystemStateService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/system-states")
class SystemStateController(private val systemStateService: SystemStateService) {

    @GetMapping("/device-status")
    fun getDeviceStatus(@RequestBody deviceId: NodeID): ResponseEntity<SystemStateService.DeviceStatus> {
        return try {
            ResponseEntity.ok(systemStateService.getDeviceStatus(deviceId))
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @GetMapping("/device-status/all")
    fun getAllDeviceStatus(): ResponseEntity<List<DeviceStatusMessage>> {
        return try {
            ResponseEntity.ok(systemStateService.getAllDeviceStatus())
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @GetMapping("/meeting-status")
    fun getMeetingStatus(@RequestParam id: Long): ResponseEntity<SystemStateService.MeetingStatus> {
        return try {
            ResponseEntity.ok(systemStateService.getMeetingStatus(id))
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @GetMapping("/meetings/ongoing")
    fun getOngoingMeetings(): ResponseEntity<List<Meeting>> {
        return try {
            ResponseEntity.ok(systemStateService.getOngoingMeetings())
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @GetMapping("/meetings/concluded")
    fun getConcludedMeetings(): ResponseEntity<List<Meeting>> {
        return try {
            ResponseEntity.ok(systemStateService.getConcludedMeetings())
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @GetMapping("/meetings/scheduled")
    fun getScheduledMeetings(): ResponseEntity<List<Meeting>> {
        return try {
            ResponseEntity.ok(systemStateService.getScheduledMeetings())
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }
}