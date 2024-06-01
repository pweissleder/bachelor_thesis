import Foundation
import Combine
import AnyCodable
import SwiftUI

// Oriented on equivalent file from the Unicorn Project
@Observable
class DeviceDetailViewModel {
    var model: Model
    var contextId: Int64
    
    var deviceId: NodeID
    
    var device: Device? {
        return model.getDevice(deviceId: self.deviceId)
    }
        
    var endpoints : [Endpoint]{
        return  device?.endpoints ?? []
    }
    var subcontexts: [Context] {
        return getContexts()
    }
    
    
    init(model: Model, device: Device, contextId: Int64) {
        self.model = model
        self.deviceId = device.id
        self.contextId = contextId
    }
    
    func toggleOnOff(endpointId: Int) {
            do {
                let value = getAttributeofType(endpointId: endpointId, clusterType: .OnOff, attributeType: .OnOff)?.value.value as? Bool ?? Bool(false)
                let commandMessage: CommandMessage = try RestCommunicatorHelper.buildCommand(contextId: self.contextId, deviceId: self.deviceId, endpoint: endpointId, cluster: .OnOff, commandType: .Toggle, attribute: Attribute(type: .OnOff, value: AnyCodable(value)))
                RestCommunicatorHelper.applyCommands(model: self.model,
                                        deviceId: self.deviceId,
                                        commandMessage: commandMessage)
            } catch {
                print("Error: \(error.localizedDescription)")
            }
    }
    
    func getClusterofType(endpointId: Int, clusterType: ClusterType) ->  Cluster?{
        return self.device?.endpoints.first(where: {$0.id == endpointId})?.clusters.first(where: {$0.type == clusterType})
    }
    
    func getAttributeofType(endpointId: Int, clusterType: ClusterType, attributeType:AttributeType) ->  Attribute?{
        return self.getClusterofType(endpointId: endpointId, clusterType: clusterType)?.attributes.first
    }
    
    
    func getContexts() ->  [Context]{
        model.findContextsForDevice(deviceId: self.deviceId)
    }
    
    func contextHasInvocationPermission() -> Bool {
        return model.contextHasInvocationPermission(contextId: self.contextId)
    }
    
    func contextHasReadPermission() -> Bool {
       return model.contextHasReadPermission(contextId: self.contextId)
    }
    
    func contextHasWritePermission() -> Bool {
        return model.contextHasWritePermission(contextId: self.contextId)
    }
}
