package ba.pascal.weissleder.server.model.framework.support.messages

import ba.pascal.weissleder.server.model.framework.support.devices.NodeID
import ba.pascal.weissleder.server.services.prototype.SystemStateService

data class DeviceStatusMessage (
    val deviceId : NodeID,
    val deviceStatus: SystemStateService.DeviceStatus
)

