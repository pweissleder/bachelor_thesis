package ba.pascal.weissleder.server.services.prototype

import ba.pascal.weissleder.server.controllers.framework.ContextController
import ba.pascal.weissleder.server.model.framework.support.devices.NodeID
import ba.pascal.weissleder.server.model.framework.support.messages.DeviceToContextMessage
import ba.pascal.weissleder.server.model.prototype.Meeting
import ba.pascal.weissleder.server.services.framework.UserService
import org.springframework.stereotype.Service

@Service
class MeetingService(

    private val contextController: ContextController,
    private val userService: UserService

) {
    //CRUD
    fun getMeetingById(id: Long): Meeting {
            val context = contextController.getContext(id).body ?: throw Exception("Contexts not found")
            return context as Meeting
    }

    fun getAllMeetings(): List<Meeting> {
        val contexts = contextController.getAllContexts().body ?: throw Exception("Contexts not found")
        return contexts.filter { it::class == Meeting::class }.map { it as Meeting }
    }

    fun createMeeting(context: Meeting): Meeting {
        return contextController.createContext(context).body as Meeting
    }

    fun deleteMeeting(id: Long) {
        contextController.deleteContext(id)
    }

    fun editNotesOfMeeting(id: Long, notes: String) {
        val meeting = getMeetingById(id)
        meeting.notes = notes
        contextController.createContext(meeting)
    }

    fun addDeviceToMeeting(contextId: Long, deviceId: NodeID) {
        contextController.addDeviceToContext(DeviceToContextMessage(contextId, deviceId))
    }

    fun removeDeviceFromMeeting(contextId: Long, deviceId: NodeID) {
        contextController.deleteDeviceFromContext(DeviceToContextMessage(contextId, deviceId))
    }

    fun addSubContextToMeeting(contextId: Long, subContextId: Long) {
        contextController.addSubContext(contextId, subContextId)
    }

    fun removeSubContextFromMeeting(contextId: Long, subContextId: Long) {
        contextController.removeSubContext(contextId, subContextId)
    }

    fun authorizeUserForMeeting(userId: Long, code: Int) {
        val contextId = this.getAllMeetings().find { it.code == code }?.id ?: throw NoSuchElementException()
        userService.addContextToUser(userId, contextId)
    }

    fun removeMeetingFromUser(userId: Long, code: Int) {
        val contextId = this.getAllMeetings().find { it.code == code }?.id ?: throw NoSuchElementException()
        userService.removeContextFromUser(userId, contextId)
    }

}