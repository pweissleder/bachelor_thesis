package ba.pascal.weissleder.server.services.prototype

import ba.pascal.weissleder.server.controllers.framework.DeviceController
import ba.pascal.weissleder.server.controllers.framework.GatewayController
import ba.pascal.weissleder.server.controllers.prototype.MeetingController
import ba.pascal.weissleder.server.model.framework.support.devices.NodeID
import ba.pascal.weissleder.server.model.framework.support.messages.DeviceStatusMessage
import ba.pascal.weissleder.server.model.prototype.Meeting
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service

@Service
class SystemStateService(
    @Lazy
    private var deviceController: DeviceController,
    @Lazy
    private var meetingController: MeetingController,

    @Lazy
    private var gatewayController: GatewayController
) {

    // Device States

    // alternative implementation over last saved device state
    fun getDeviceStatus(deviceId: NodeID): DeviceStatus {
        val res = gatewayController.getGatewayIdByActiveDeviceId(deviceId)
        return if (res == null) {
            DeviceStatus.UNREACHABLE
        } else {
            DeviceStatus.REACHABLE
        }
    }

    fun getAllDeviceStatus(): List<DeviceStatusMessage> {
        val deviceIDs = deviceController.getAllNodeIDs()
        val res = mutableListOf<DeviceStatusMessage>()

        for (deviceID in deviceIDs) {
            val gatewayId = gatewayController.getGatewayIdByActiveDeviceId(deviceID)
            if (gatewayId != null) {
                res.add(DeviceStatusMessage(deviceID,DeviceStatus.REACHABLE))
            } else {
                res.add(DeviceStatusMessage(deviceID,DeviceStatus.UNREACHABLE))
            }
        }
        return res
    }


    //Meeting States
    fun getMeetingStatus(meetingId: Long): MeetingStatus {
        val meeting = meetingController.getMeetingById(meetingId).body ?: throw NoSuchElementException()
        if (meeting.startDate.before(java.util.Date()) && meeting.endDate?.after(java.util.Date()) ?: false) {
            return MeetingStatus.ONGOING
        } else if (meeting.endDate?.before(java.util.Date()) ?: false) {
            return MeetingStatus.CONCLUDED
        }
        return MeetingStatus.SCHEDULED
    }

    fun getOngoingMeetings(): List<Meeting> {
        val meetings = meetingController.getAllMeetings().body ?: throw NoSuchElementException()
        return meetings.filter { it.id?.let { it1 -> getMeetingStatus(it1) } == MeetingStatus.ONGOING }
    }

    fun getConcludedMeetings(): List<Meeting> {
        val meetings = meetingController.getAllMeetings().body ?: throw NoSuchElementException()
        return meetings.filter { it.id?.let { it1 -> getMeetingStatus(it1) } == MeetingStatus.CONCLUDED }
    }

    fun getScheduledMeetings(): List<Meeting> {
        val meetings = meetingController.getAllMeetings().body ?: throw NoSuchElementException()

        return meetings.filter { it.id?.let { it1 -> getMeetingStatus(it1) } == MeetingStatus.SCHEDULED }
    }

    enum class MeetingStatus {
        SCHEDULED,
        ONGOING,
        CONCLUDED
    }

    enum class DeviceStatus {
        REACHABLE,
        UNREACHABLE
    }


}

