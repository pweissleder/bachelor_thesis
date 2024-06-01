import Foundation

@Observable
class Model {
    var user: User
    var devices: [Device] = []
    var contexts: [Context] {
        return self.user.contexts
    }
    var gateway: [Gateway] = []
       
    private var updateLoopSet: Bool = false
    
    var selectedContext: Int64?
    
    var selectedDevice: NodeID?
    
    var messageCommunicator: MqttClient?
    
    var devicesReachable: [NodeID: Bool] = [:]
    
    public func selectDevice(deviceId: NodeID){
        DispatchQueue.main.async {
            self.selectedDevice = deviceId
        }
    }
    public func deselectDevice(){
        DispatchQueue.main.async {
            self.selectedDevice = nil
        }
    }
    
    public func getSelectedDevice() -> NodeID? {
        return self.selectedDevice
    }
        
    public func setContext(contextId: Int64){
        DispatchQueue.main.async {
            self.selectedContext = contextId
        }
    }
    public func resetContext(){
        DispatchQueue.main.async {
            self.selectedContext = nil
        }
    }
    
    public func getcurrentContext() -> Int64? {
            return self.selectedContext
    }
    
    func setModelData(user: User) {
        self.user = user
        if !updateLoopSet {
            updateLoopSet = true
            RestCommunicatorHelper.fetchModelinit(model: self)
        }
        }
    
    init(user: User) {
        self.user = user
        setupCommunicator()
    }
    
    func setupCommunicator(){
        self.messageCommunicator = MqttClient(model: self)
        self.messageCommunicator?.connect()
        self.messageCommunicator?.updateDevicesLoop()
    }
    
    //Getter
    public func getDeviceIndex(deviceId: Device.ID) -> Int? {
        return devices.firstIndex(where: { $0.id == deviceId })
    }
    
    public func getDevice(deviceId: Device.ID) -> Device? {
        guard let index = getDeviceIndex(deviceId: deviceId) else {
            return nil
        }
        return devices[index]
    }
    
    public func getDeviceEnpoints(deviceId: Device.ID) -> [Endpoint] {
        return getDevice(deviceId: deviceId)?.endpoints ?? []
    }
    
    public func DeviceHasCluster(deviceId: Device.ID, clusterId: Int) -> Bool {
        return getDevice(deviceId: deviceId)?.endpoints.contains{ $0.id == clusterId } ?? false
    }
    
    func setUser(user: User){
        self.user = user
    }
    
    func updateDevice(device: Device?) {
        guard let uDevice = device else {
            print("Error: Device was not updated, no device was passed")
            return
        }
        guard let index = getDeviceIndex(deviceId: uDevice.id) else {
            devices.append(uDevice)
            print("Added Device to devices")
            return
        }
        devices[index] = uDevice
    }
    
    func updateDeviceReachable(deviceId: NodeID, status: Bool){
        devicesReachable[deviceId] = status
    }
    
    public func deviceisReachable(nodeID: NodeID) -> Bool {
        return devicesReachable[nodeID] ?? false
    }
       
   public func getAllReachabilityStatus() -> [NodeID: Bool] {
       return devicesReachable
   }
    
    
    private func getCapabilityContext(contextId: Int64) -> Capability {
        return self.getContext(contextId: contextId)?.capability ?? .R// Example return value
    }

    func contextHasInvocationPermission(contextId: Int64) -> Bool {
        let capability = getCapabilityContext(contextId: contextId)
        switch capability {
        case .RI, .RIW:
            return true
        default:
            return false
        }
    }

    func contextHasReadPermission(contextId: Int64) -> Bool {
        let capability = getCapabilityContext(contextId: contextId)
        switch capability {
        case .R, .RI, .RW, .RIW:
            return true
        default:
            return false
        }
    }

    func contextHasWritePermission(contextId: Int64) -> Bool {
        let capability = getCapabilityContext(contextId: contextId)
        switch capability {
        case .RW, .RIW:
            return true
        default:
            return false
        }
    }

    func getTimeContexts() -> [TimeContext] {
            return contexts.compactMap { $0 as? TimeContext }
        }


        func getSpaceContexts() -> [SpaceContext] {
            return contexts.compactMap { $0 as? SpaceContext }
        }


        func getLogicContexts() -> [LogicContext] {
            return contexts.compactMap { $0 as? LogicContext }
        }

    
     func findContextsForDevice(deviceId: NodeID) -> [Context] {
          var matchingContexts: [Context] = []

          func checkContextForDevice(_ context: Context) {
              if context.devices.contains(where: { $0 == deviceId }) {
                  matchingContexts.append(context)
              }
              for subContext in context.subContexts {
                  checkContextForDevice(subContext)
              }
          }

          for context in contexts {
              checkContextForDevice(context)
          }

          return matchingContexts
      }
    
    func getContext(contextId: Int64) -> Context?{
        var res : [Context] = []
        for context in self.user.contexts{
            res.append(context)
            for subContext in context.getAllSubcontexts() {
                res.append(subContext)
            }
        }
        return res.first(where: {$0.id == contextId})
    }
    
    func getAllDevices() -> [NodeID] {
        var res : [NodeID] = []
        for context in self.user.contexts{
            for nodeID in context.getAllDevices() {
                res.append(nodeID)
            }
        }
        return res
    }
    
    
}
