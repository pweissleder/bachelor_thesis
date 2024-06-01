import Foundation
import SwiftUI
import AnyCodable

// Oriented on equivalent file from the Unicorn Project
struct CommandMessage: Codable {
    let contextId: Int64
    let command: Command
}
