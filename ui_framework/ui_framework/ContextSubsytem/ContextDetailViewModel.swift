import Foundation
import Combine
import SwiftUI

@Observable
class ContextDetailViewModel {
    var model: Model
    var context: Context

    init(model: Model, context: Context) {
        self.model = model
        self.context = context
    }

    var deviceIds: [NodeID] {
        return context.devices
    }

    var subContexts: [Context] {
        return context.subContexts
    }
    
    func getDevice(deviceID: NodeID) -> Device? {
        return self.model.getDevice(deviceId: deviceID)
    }
}
