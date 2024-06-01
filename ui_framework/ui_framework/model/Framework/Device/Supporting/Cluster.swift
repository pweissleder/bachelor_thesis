import Foundation
import SwiftUI
import AnyCodable

public class Cluster: Codable, Identifiable, Hashable {
    public let type: ClusterType
    var attributes: [Attribute]
    let commands: [CommandType]
    
    init (type: ClusterType, attributes: [Attribute], commands: [CommandType]) {
        self.type = type
        self.attributes = attributes
        self.commands = commands
    }
    
    public static func == (lhs: Cluster, rhs: Cluster) -> Bool {
        return lhs.type == rhs.type
    }
    
    public func hash(into hasher: inout Hasher) {
        hasher.combine(type)
        hasher.combine(attributes)
        hasher.combine(commands)
    }
    
    enum CodingKeys: String, CodingKey {
        case type,
             attributes,
             commands
    }
    
    required public convenience init(from decoder: Decoder) throws{
        let container = try decoder.container(keyedBy: CodingKeys.self)
        
        let type = try container.decode(ClusterType.self, forKey: .type)
        let attr = try container.decode([Attribute].self, forKey: .attributes)
        
        let commands = try container.decode([CommandType].self, forKey: .commands)
        self.init(type: type, attributes: attr, commands: commands)
    }
    
    public func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        
        try container.encode(type, forKey: .type)
        try container.encode(attributes, forKey: .attributes)
        try container.encode(commands, forKey: .commands)
    }
    
    public func toggle() {
        if self.type == .OnOff {
            guard let on = self.attributes.first(where: {$0.type == .OnOff})?.value as? Bool else {
                return
            }
            self.attributes.first(where: {$0.type == .OnOff})?.value = try! AnyCodable(!on)
        }
    }
    public var id: String {
        return self.type.rawValue
       }
}
