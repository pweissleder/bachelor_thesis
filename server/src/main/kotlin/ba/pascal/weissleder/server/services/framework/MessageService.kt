package ba.pascal.weissleder.server.services.framework

import ba.pascal.weissleder.server.controllers.framework.DeviceController
import ba.pascal.weissleder.server.controllers.framework.GatewayController
import ba.pascal.weissleder.server.model.framework.Gateway
import ba.pascal.weissleder.server.model.framework.support.devices.NodeID
import ba.pascal.weissleder.server.model.framework.support.messages.GatewayInfoMessage
import com.google.gson.Gson
import jakarta.annotation.PostConstruct
import org.eclipse.paho.mqttv5.client.*
import org.eclipse.paho.mqttv5.common.MqttException
import org.eclipse.paho.mqttv5.common.MqttMessage
import org.eclipse.paho.mqttv5.common.packet.MqttProperties
import org.springframework.stereotype.Service
import java.util.*

// Sources:
// https://eclipse.dev/paho/files/javadoc/index.html
// https://github.com/eclipse/paho.mqtt.java
// https://www.hivemq.com/blog/mqtt-client-library-encyclopedia-eclipse-paho-java/
@Service
class MessageService(
    private val mqttClient: IMqttClient,
    private val gatewayController: GatewayController,
    private val deviceController: DeviceController
) {
    companion object {
        private val patterns = mapOf(
            "gateway" to "gateways/{id}",
            "gateway_reachable" to "gateways/{id}/reachable",
            "device_command" to "devices/{id}/command",
            "device_command_response" to "devices/{id}/command/r",
            "device_leader" to "devices/{id}/leader",
            "device" to "devices/{id}",
            "commission" to "gateways/{id}/commission",
            "all_gateways" to "gateways/+",
            "all_devices" to "devices/+"
        )
    }

    fun createTopic(patternKey: String, id: String): String {
        val pattern = patterns[patternKey] ?: throw IllegalArgumentException("Invalid pattern key provided.")
        return pattern.replace("{id}", id)
    }


    fun matchTopic(topic: String): Pair<String, String>? {
        patterns.forEach { (key, pattern) ->
            val preparedPattern = pattern.replace("{id}", "([^/]+)")
            val regexPattern = "^$preparedPattern$"
            val matchResult = Regex(regexPattern).find(topic)
            if (matchResult != null) {
                return key to matchResult.groupValues[1]
            }
        }
        return null
    }


    fun handleMqttMessage(topic: String, message: MqttMessage) {
        val (patternKey, topicId) = matchTopic(topic) ?: throw IllegalArgumentException("No matching pattern found")
        when (patternKey) {
            "gateway" -> handleGatewayTopic(topic, message)
            "gateway_reachable" -> handleGatewayReachableTopic(topic, message)
            "device" -> handleDeviceTopic(topic, message)
            else -> println("No handler available for this pattern")
        }

    }

    fun handleDeviceTopic(topic: String, message: MqttMessage) {
        println("Received on $topic: ${message.toDebugString()}")
        val messageString = message.toString()
        deviceController.saveDeviceState(messageString)
    }

    fun handleGatewayTopic(topic: String, message: MqttMessage) {
        println("Received on $topic: ${message.toDebugString()}")
        val messageString = message.toString()
        val gatewayInfoMessage: GatewayInfoMessage = Gson().fromJson(messageString, GatewayInfoMessage::class.java)

        val oldGateway: Gateway =
            gatewayController.getGatewayById(gatewayInfoMessage.gatewayId).body ?: throw Exception(
                "Gateway" +
                        " not found"
            )
        val newGateway = oldGateway.updateGateway(gatewayInfoMessage)

        gatewayController.updateGatewayDevices(newGateway)

        handleDeviceChanges(oldGateway, newGateway)
    }

    fun handleGatewayReachableTopic(topic: String, message: MqttMessage) {
        println("Received on $topic: ${message.toDebugString()}")
        if (message.toString() == "false") {
            val (patternKey, gatewayId) = matchTopic(topic)
                ?: throw IllegalArgumentException("No matching pattern found")
            var gateway: Gateway = gatewayController.getGatewayById(UUID.fromString(gatewayId)).body ?: throw Exception(
                "Gateway not found"
            )

            val oldActiveDevices = gateway.activeDevices

            gatewayController.deleteDevicesOfGateway(gateway.id)
            gatewayController.deleteSession(gateway.id)

            gateway = gatewayController.getGatewayById(gateway.id).body ?: throw Exception("Gateway not found")

            val gatewayInfoMessage = gateway.buildGatewayInfoMessage()
            val payload = Gson().toJson(gatewayInfoMessage)

            publishMessage(createTopic("gateway", gatewayId), payload, 2, true)

            for (deviceId in oldActiveDevices) {
                findNewLeaderForDevice(deviceId)
            }
        }
    }

    @PostConstruct
    fun setup() {
        mqttClient.setCallback(object : MqttCallback {
            override fun disconnected(disconnectResponse: MqttDisconnectResponse?) {
                println("Disconnected")
            }

            override fun mqttErrorOccurred(exception: MqttException?) {
                println("Error occurred")
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                if (message != null && topic != null) {
                    println("Message received from $topic: ${String(message.payload)}")
                    handleMqttMessage(topic, message)
                }
            }

            override fun deliveryComplete(token: IMqttToken?) {
                println("Delivery complete")
            }

            override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                println("Connected")
            }

            override fun authPacketArrived(reasonCode: Int, properties: MqttProperties?) {
                println("Auth packet arrived")
            }
        })
    }


    @Throws(MqttException::class)
    fun subscribeToTopic(topic: String, qos: Int = 1) {
        if (!mqttClient.isConnected) {
            mqttClient.connect()
        }
        mqttClient.subscribe(topic, qos)
        println("Subscribed to $topic")
    }

    @Throws(MqttException::class)
    fun unsubscribeFromTopic(topic: String) {
        if (!mqttClient.isConnected) {
            mqttClient.connect()
        }
        mqttClient.unsubscribe(topic)
        println("Unsubscribed from $topic")
    }

    @Throws(MqttException::class)
    fun publishMessage(topic: String, payload: String, qos: Int = 0, retained: Boolean = false) {
        if (!mqttClient.isConnected) {
            mqttClient.connect()
        }
        mqttClient.publish(topic, payload.toByteArray(), qos, retained)
        println("Message published to $topic")
    }


    @PostConstruct
    fun subscribeToControllerTopics() {
        try {
            subscribeToTopic(createTopic("all_gateways", ""))
            subscribeToTopic(createTopic("all_devices", ""))
        } catch (e: MqttException) {
            println("Failed to subscribe to gateway topics")
        }
        try {
            val gatewayIds =
                gatewayController.getAllGatewayIds().body ?: throw Exception("Failed to get the gateway ids")

            gatewayIds.map { id -> createTopic("gateway_reachable", id.toString()) }
                .forEach { subscribeToTopic(it) }
        } catch (e: MqttException) {
            println("Failed to subscribe to gateway reachable topics")
        }
    }

    fun handleDeviceChanges(oldGateway: Gateway, newGateway: Gateway) {
        var oldActiveDevices = oldGateway.activeDevices
        var newActiveDevices = newGateway.activeDevices

        var removedActiveDevices = oldActiveDevices.filter { !newActiveDevices.contains(it) }

        var oldReachableDevices = oldGateway.reachableDevices
        var newReachableDevices = newGateway.reachableDevices

        var addedReachableDevices = newReachableDevices.filter { !oldReachableDevices.contains(it) }

        // 2 Cases
        // 1. Device was added to reachable devices
        for (deviceId in addedReachableDevices) {
            if (gatewayController.getGatewayIdByActiveDeviceId(deviceId) == null) {
                // Device is not assigned to any gateway
                // Find new leader
                assignNewLeaderForDevice(deviceId, newGateway.id)
            }
        }

        // 2. Device was removed from activeDevices
        for (deviceId in removedActiveDevices) {
            findNewLeaderForDevice(deviceId)
        }
    }

    fun findNewLeaderForDevice(deviceId: NodeID) {
        var newLeaderId = gatewayController.getFirstReachableGatewayIdByDeviceId(deviceId)
        if (newLeaderId != null) {
            //update gateway and publish change
            publishMessage(createTopic("device_leader", deviceId.toReadableString()), newLeaderId.toString(), 2, true)
        } else {
            publishMessage(createTopic("device_leader", deviceId.toReadableString()), "unreachable", 2, true)
        }
    }

    fun assignNewLeaderForDevice(deviceId: NodeID, newLeaderId: UUID) {
        //update gateway and publish change
        publishMessage(createTopic("device_leader", deviceId.toReadableString()), newLeaderId.toString(), 2, true)
    }
}

