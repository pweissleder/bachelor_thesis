import Foundation


// Premiminary implementation, not yet used
//https://david.y4ng.fr/codable-with-mixed-types-of-data/
public class Gateway: Codable, Identifiable {
    public var id: UUID
    var authToken: String
    var activeDevices: [NodeID]
    var reachableDevices: [NodeID]
    
    init(id: UUID = UUID(), authToken: String = "NotSet", activeDevices: [NodeID] = [], reachableDevices: [NodeID] = []) {
        self.id = id
        self.authToken = authToken
        self.activeDevices = activeDevices
        self.reachableDevices = reachableDevices
    }
    
    
    enum CodingKeys: String, CodingKey {
        case id, authToken, activeDevices, reachableDevices, session
    }
    
    public required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        self.id = try container.decode(UUID.self, forKey: .id)
        self.authToken = try container.decode(String.self, forKey: .authToken)
        self.activeDevices = try container.decode([NodeID].self, forKey: .activeDevices)
        self.reachableDevices = try container.decode([NodeID].self, forKey: .reachableDevices)
    }
    
    public func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        try container.encode(id, forKey: .id)
        try container.encode(authToken, forKey: .authToken)
        try container.encode(activeDevices, forKey: .activeDevices)
        try container.encode(reachableDevices, forKey: .reachableDevices)
    }
}

