import Foundation
import SwiftUI

// Oriented on equivalent file from the Unicorn Project
struct Command: Codable {
    let nodeId: NodeID
    let endpoint: Int
    let cluster: ClusterType
    let command: CommandType
    var commandMode: Int? 
    let attribute: Attribute
}

