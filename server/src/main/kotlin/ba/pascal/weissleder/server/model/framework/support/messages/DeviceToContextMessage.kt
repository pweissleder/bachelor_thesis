package ba.pascal.weissleder.server.model.framework.support.messages

import ba.pascal.weissleder.server.model.framework.support.devices.NodeID

data class DeviceToContextMessage(
    val contextId: Long,
    val deviceId: NodeID,
)
