import Foundation
import SwiftUI

// Oriented on equivalent file from the Unicorn Project
//https://david.y4ng.fr/codable-with-mixed-types-of-data/
public class Endpoint: Codable, ObservableObject, Hashable, Identifiable{
    
    public let id: Int
    var clusters: [Cluster]
    
    init (id: Int, clusters: [Cluster]) {
        self.id = id
        self.clusters = clusters
        
    }
    
    public static func == (lhs: Endpoint, rhs: Endpoint) -> Bool {
        return lhs.id == rhs.id
    }
    
    public func hash(into hasher: inout Hasher) {
        hasher.combine(id)
    }
    
    enum CodingKeys: String, CodingKey {
        case id,
             clusters
    }
    
    required public convenience init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        
        let id = try container.decode(Int.self, forKey: .id)
        let clusters = try container.decode([Cluster].self, forKey: .clusters)
        self.init(id: id, clusters: clusters)
    }
    
    public func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        
        try container.encode(id, forKey: .id)
        try container.encode(clusters, forKey: .clusters)
    }
}
