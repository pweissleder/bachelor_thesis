package ba.pascal.weissleder.server.model.framework.support.messages

import ba.pascal.weissleder.server.model.framework.Gateway
import ba.pascal.weissleder.server.model.framework.support.devices.NodeID
import java.util.*


data class GatewayInfoMessage(
    val gatewayId: UUID,
    val activeDevices: MutableList<NodeID> = mutableListOf(),
    val reachableDevices: MutableList<NodeID> = mutableListOf(),
) {
    companion object {
        fun buildMessageFromGateway(gateway: Gateway): GatewayInfoMessage {
            return GatewayInfoMessage(
                gatewayId = gateway.id,
                activeDevices = gateway.activeDevices,
                reachableDevices = gateway.reachableDevices
            )
        }
    }
}
