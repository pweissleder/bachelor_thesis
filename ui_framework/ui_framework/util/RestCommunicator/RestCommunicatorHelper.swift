import Foundation
import SwiftUI

// Oriented on equivalent file from the Unicorn Project
public class RestCommunicatorHelper {
    
    init() {}
    
    //Initial modle fetch, starts updateModelLoop
    static func fetchModelinit(model: Model) {
        Task.init{
            do {
                try await withCheckedThrowingContinuation { continuation in
                    DispatchQueue.global().async {
                        fetchModel(model: model)
                        DispatchQueue.main.async {
                            continuation.resume()
                        }
                    }
                    
                }
            } catch {
                print("Unexpected error: \(error)")
            }
        }
        RestCommunicatorHelper.updateModelLoop(model: model)
    }
    
    
    // Request User data
    static func fetchModel(model: Model){
        Task {
            guard let userCred: String = RestCommunicatorHelper.createBasicAuthHeader(email: model.user.email, password:  model.user.password) else {
                return
            }
            
            guard let user: User = await RestCommunicator.fetchUserSelf(userCred: userCred) else {
                print("Error: Could not fetsh data from /users/self")
                return
            }
            
            DispatchQueue.main.async {
                model.setModelData(user: user)
            }
            return
        }
    }
    
    // Loop for updating the state of User associated data
    static func updateModelLoop(model: Model) {
        let queue = DispatchQueue(label: "update-queue")
        queue.async {
            Task{
                while true {
                    try await Task.sleep(nanoseconds: 10_000_000_000)
                    fetchModel(model: model)
                }
            }
        }
    }
    
    // Send the request for device control
    static func applyCommands(model: Model,
                              deviceId: NodeID,
                              commandMessage: CommandMessage) {
        guard model.getDeviceIndex(deviceId: deviceId) != nil else {
            return
        }
        
        guard let userCred: String = RestCommunicatorHelper.createBasicAuthHeader(email: model.user.email, password:  model.user.password) else {
            return
        }
        
        Task {
            await RestCommunicator.sendCommand(commandMessage: commandMessage, userCred: userCred)
        }
    }
    
    //Helper Methods
    static func createBasicAuthHeader(email: String, password: String) -> String? {
        let credentials = "\(email):\(password)"
        guard let data = credentials.data(using: .utf8) else {
            return nil
        }
        let base64Credentials = data.base64EncodedString()
        return "Basic \(base64Credentials)"
    }
    
    // Builds the CommmandMessage
    static func buildCommand(contextId: Int64, deviceId: NodeID, endpoint: Int, cluster: ClusterType,
                             commandType: CommandType, attribute: Attribute) throws -> CommandMessage {
        return CommandMessage(contextId: contextId, command: Command(nodeId: deviceId, endpoint: endpoint, cluster: cluster, command: commandType, attribute: attribute))
    }
    
}

