import Foundation
import SwiftUI
import CocoaMQTT
import Combine

// https://github.com/emqx/CocoaMQTT
public class MqttClient {
    let host = "[BrokerIP]"
    let port = 1883
    let username = "UIUser"
    let password = "ui_pass"
    
    let model: Model
    let mqtt: CocoaMQTT5
    
    
    
    init(model: Model) {
        let clientID = "UIUser-" + String(ProcessInfo().processIdentifier)
        self.mqtt = CocoaMQTT5(clientID: clientID, host: host, port: UInt16(port))
        
        let connectProperties = MqttConnectProperties()
        connectProperties.topicAliasMaximum = .max
        connectProperties.sessionExpiryInterval = 60
        connectProperties.receiveMaximum = .max
        connectProperties.maximumPacketSize = .max
        self.mqtt.connectProperties = connectProperties
        
        self.mqtt.keepAlive = 60
        
        self.mqtt.username = self.username
        self.mqtt.password = self.password
        
        self.model = model
        
        self.mqtt.autoReconnect = true
        
        self.mqtt.didConnectAck = { mqtt, ack, decAck in
            if ack == CocoaMQTTCONNACKReasonCode.success {
                print("Connected to Broker")
            } else {
                print("Could not connect to Broker")
            }
        }
        
        self.mqtt.didReceiveMessage = { mqtt, message, msgid
            ,publishRecProperties  in 
            print("Message received in topic \(message.topic) with payload \(message.string!)")
            if message.topic.contains("devices/") {
                if message.topic.contains("/leader") {
                    self.handleDeviceLeaderMessage(mqttPayload: message.payload, topic: message.topic)
                } else if message.topic.contains("/command/r") {
                    self.handleCommandResponseMessage(mqttPayload: message.payload)
                } else {
                    self.handleDeviceMessage(mqttPayload: message.payload)
                }
            }
        }
    }
    
    func connect(){
        self.mqtt.connect()
    }
    
    func subscribe(topic: String){
        if self.mqtt.subscriptions.contains(where: {$0.key == topic}) {
            return
        }
        let subscription = MqttSubscription(topic: topic)
        subscription.retainHandling = CocoaRetainHandlingOption.sendOnSubscribe
        self.mqtt.subscribe([subscription])
        print("Subscribed to topic: \(topic)")
    }
    
    
    func subscribeToDevices(deviceIds:[NodeID]){
        for nodeID in deviceIds {
            self.subscribe(topic: "devices/\(nodeID.description)")
            self.subscribe(topic: "devices/\(nodeID.description)/leader")
            self.subscribe(topic: "devices/\(nodeID.description)/command/r")
        }
    }
    
    func handleDeviceMessage(mqttPayload: [UInt8]){
        let data = Data(mqttPayload)
        guard let device = try? JSONDecoder().decode(Device.self, from: data) else {
            return
        }
        self.model.updateDevice(device: device)
    }
    
    func handleDeviceLeaderMessage(mqttPayload: [UInt8], topic: String){
        guard let status = String(bytes: mqttPayload, encoding: .utf8)
        else {
            return
        }
        guard let deviceId = self.extractDeviceId(from: topic) else {
            return
        }
        if status.contains("unreachable") {
            self.model.updateDeviceReachable(deviceId: deviceId, status: false)
        } else {
            self.model.updateDeviceReachable(deviceId: deviceId, status: true)
        }
    }
    
    private func extractDeviceId(from topic: String) -> NodeID? {
        let components = topic.split(separator: "/")
        
        guard components.count > 2, components[0] == "devices", components[2] == "leader" else {
            return nil
        }
        
        let integrationTypeString = components[1].split(separator: "-").first
        let persistentAttribute = components[1].split(separator: "-").dropFirst().joined(separator: "-")
        
        guard let integrationType = IntegrationType(rawValue: String(integrationTypeString ?? "")) else {
            return nil
        }
        return NodeID(integrationType: integrationType, persistentAttribute: persistentAttribute)
    }
    
    
    // Not further implemented
    func handleCommandResponseMessage(mqttPayload: [UInt8]){
        let data = Data(mqttPayload)
        guard let res: Int = try? JSONDecoder().decode(Int.self, from: data) else {
            return
        }
        print("Command was executed with result \(res)")
    }
    
    func updateDevicesLoop() {
        let queue = DispatchQueue(label: "update-queue-devices")
        queue.async {
            Task{
                while true {
                    var uniquIds = Set(self.model.getAllDevices())
                    self.subscribeToDevices(deviceIds: Array(uniquIds))
                    try await Task.sleep(nanoseconds: 5_000_000_000)
                }
            }
        }
    }
}
