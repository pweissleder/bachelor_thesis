import Foundation

// Oriented on equivalent file from the Unicorn Project
//https://david.y4ng.fr/codable-with-mixed-types-of-data/
public struct NodeID: Codable, Comparable, Hashable, Identifiable {
    public static func < (lhs: NodeID, rhs: NodeID) -> Bool {
        return lhs.description == rhs.description
    }
    
    let integrationType: IntegrationType
    let persistentAttribute: String
    
    init(integrationType: IntegrationType, persistentAttribute: String){
        self.integrationType = integrationType
        self.persistentAttribute = persistentAttribute
    }
    
    enum CodingKeys: String, CodingKey {
        case integrationType,
             persistentAttribute
    }
    
    public init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        
        let integrationType = try container.decode(IntegrationType.self, forKey: .integrationType)
        let persistentAttribute = try container.decode(String.self, forKey: .persistentAttribute)
        self.init(integrationType: integrationType, persistentAttribute: persistentAttribute)
    }
    
    public var description: String {
        return "\(self.integrationType)-\(self.persistentAttribute)"
    }
    public var id: String {
        return description
    }
    
}
