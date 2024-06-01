import Foundation
import AnyCodable

// Oriented on equivalent file from the Unicorn Project
//https://david.y4ng.fr/codable-with-mixed-types-of-data/
public class Device: Codable, Identifiable, Equatable {
    public var id: NodeID
    var endpoints: [Endpoint]
    
    init (id: NodeID, endpoints: [Endpoint] = []) {
        self.id = id
        self.endpoints = endpoints
    }
    
    enum CodingKeys: String, CodingKey {
        case id, endpoints
    }
    
    required public convenience init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        
        let id = try container.decode(NodeID.self, forKey: .id)
        let endpoints = try container.decode([Endpoint].self, forKey: .endpoints)
        
        self.init(id: id, endpoints: endpoints)
    }
    
    public func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        
        try container.encode(id, forKey: .id)
        try container.encode(endpoints, forKey: .endpoints)
    }
}

extension Device: Hashable {
    public func hash(into hasher: inout Hasher) {
        hasher.combine(self.id)
    }
    
    public static func == (lhs: Device, rhs: Device) -> Bool {
        lhs.id == rhs.id
    }
}
