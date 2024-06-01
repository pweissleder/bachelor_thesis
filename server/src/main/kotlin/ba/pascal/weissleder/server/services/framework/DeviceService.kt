package ba.pascal.weissleder.server.services.framework

import ba.pascal.weissleder.server.controllers.framework.MessageController
import ba.pascal.weissleder.server.controllers.framework.SecurityController
import ba.pascal.weissleder.server.model.framework.Device
import ba.pascal.weissleder.server.model.framework.DeviceState
import ba.pascal.weissleder.server.model.framework.support.devices.Command
import ba.pascal.weissleder.server.model.framework.support.devices.NodeID
import ba.pascal.weissleder.server.repositories.DeviceStateRepository
import ba.pascal.weissleder.server.repositories.NodeIDRepository
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import org.springframework.context.annotation.Lazy
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import java.time.Instant
import java.io.Serializable as JavaSerializable

@Service
class DeviceService(
    private val deviceStateRepository: DeviceStateRepository,
    private val nodeIDRepository: NodeIDRepository,
    @Lazy
    private val securityController: SecurityController,
    @Lazy
    private val messageController: MessageController
) {

    fun applyCommand(userId: Long, contextId: Long, command: Command<JavaSerializable?>) {
        if (securityController.checkUserAuthForCommand(userId, contextId, command)) {
            val payload = Gson().toJson(command)
            messageController.publishMessage(
                messageController.createTopic("device_command", command.nodeId.toReadableString()),
                payload,
                2,
                false
            )
        } else {
            throw HttpClientErrorException(
                HttpStatus.UNAUTHORIZED,
                "User is not Authorised to control the device with the given context"
            )
        }
    }

    fun saveDeviceState(message: String) {
        val device = ObjectMapper().readValue(message, Device::class.java)
        val deviceState = DeviceState(device)
        deviceStateRepository.save(deviceState)
        return
    }

    fun getDevice(nodeID: NodeID): Device {
        val deviceState = deviceStateRepository.findFirstById(nodeID).get()
        return deviceState.toDevice()
    }

    fun getAllDevices(): List<Device> {
        var res: MutableSet<Device> = mutableSetOf()
        val nodeIds = nodeIDRepository.findAll()
        for (nodeId in nodeIds) {
            res.add(getDevice(nodeId))
        }
        return res.toList()
    }

    fun getDevicesByIds(nodeIds: List<NodeID>): List<Device> {
        val res: MutableSet<Device> = mutableSetOf()
        for (nodeId in nodeIds) {
            res.add(getDevice(nodeId))
        }
        return res.toList()
    }

    fun getDeviceStateUntil(nodeID: NodeID, untilInstant: Instant): List<DeviceState> {
        return deviceStateRepository.findByIdAndTimestampBefore(nodeID, untilInstant)
    }

    fun removeDeviceStatesById(nodeID: NodeID) {
        deviceStateRepository.deleteByNodeId(nodeID)
    }

    fun getAllNodeIDs(): List<NodeID> {
        return nodeIDRepository.findAll()
    }

    fun saveAllNodeIDs(nodeIDs: List<NodeID>) {
        nodeIDRepository.saveAllAndFlush(nodeIDs)
    }

}

