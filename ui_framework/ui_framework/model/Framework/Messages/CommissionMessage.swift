import Foundation

// See comment in AdminCommissionVM

struct CommissionMessage: Codable {
    let controllerId: Controller.id
    let message: String
    
    init(commissionData: CommissionData, controllerId: Controller.id) {
        self.controllerId = controllerId
        
        switch commissionData.commissionMode {
        case .ble_wifiM: self.message = "pairing ble-wifi ${NODE_ID} \(commissionData.wifiNetwork.ssid) \(commissionData.wifiNetwork.password) \(commissionData.matterDevice.manualCode)"
            
        case .ble_wifiQr: self.message = "\(commissionData.wifiNetwork.ssid) \(commissionData.wifiNetwork.password) \(commissionData.matterDevice.mt)"
            
        case .ble_threadQr: self.message = "\(commissionData.matterDevice.mt)"
            
        case .ble_threadM: self.message = "\(commissionData.matterDevice.manualCode)"
        }
    }
}
