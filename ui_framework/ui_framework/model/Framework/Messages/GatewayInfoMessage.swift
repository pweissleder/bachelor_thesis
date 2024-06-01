

import Foundation
class GatewayInfoMessage: Codable {
    var gatewayId: UUID
    var activeDevices: [NodeID]
    var reachableDevices: [NodeID]

    init(gatewayId: UUID, activeDevices: [NodeID], reachableDevices: [NodeID]) {
        self.gatewayId = gatewayId
        self.activeDevices = activeDevices
        self.reachableDevices = reachableDevices
    }
}
