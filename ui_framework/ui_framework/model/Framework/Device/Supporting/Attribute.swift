import Foundation
import AnyCodable

// Oriented on equivalent file from the Unicorn Project
//https://david.y4ng.fr/codable-with-mixed-types-of-data/
public class Attribute: Codable, Hashable {
    let type: AttributeType
    var value: AnyCodable
    
    enum CodingKeys: String, CodingKey {
        case type,
             value
    }
    init (type: AttributeType, value: AnyCodable) {
        self.type = type
        self.value = value
    }
    
    required public convenience init(from decoder: Decoder) throws{
        let container = try decoder.container(keyedBy: CodingKeys.self)
        
        let type = try container.decode(AttributeType.self, forKey: .type)
        
        let value = try container.decode(AnyCodable.self, forKey: .value)
        
        self.init(type: type, value: value)
    }
    
    public func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        
        try container.encode(type, forKey: .type)
        try container.encode(value, forKey: .value)
    }
    
    public static func == (lhs: Attribute, rhs: Attribute) -> Bool {
        return lhs.type == rhs.type
    }
    
    public func hash(into hasher: inout Hasher) {
        hasher.combine(type)
        hasher.combine(value)
    }
    func update(with newAttribute: Attribute) {
        self.value = newAttribute.value
    }
}



extension AnyCodable: Hashable {
    init<T: Encodable>(_ encodableValue: T) throws {
        let encoder = JSONEncoder()
        let data = try encoder.encode(encodableValue)
        self = try JSONDecoder().decode(AnyCodable.self, from: data)
    }
    public func hash(into hasher: inout Hasher) {
        hasher.combine(self)
    }
}

