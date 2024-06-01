import Foundation
// Oriented on equivalent file from the Unicorn Project
//https://david.y4ng.fr/codable-with-mixed-types-of-data/

public enum CommandType: Equatable, Hashable, Codable {
    case On, Off,Toggle

    public enum ValueKeys: String, CodingKey {
        case On, Off, Toggle
    }

    public enum CodingKeys: String, CodingKey {
        case type
    }

   public init(from decoder: Decoder) throws {
        let singleContainer = try decoder.singleValueContainer()

        let type = try
           singleContainer.decode(String.self)
        switch type {
        case CommandType.ValueKeys.On.rawValue:
            self = .On
        case CommandType.ValueKeys.Off.rawValue:
            self = .Off
        case CommandType.ValueKeys.Toggle.rawValue:
            self = .Toggle
        default:
            print(type)
            fatalError("Unknown type of content.")
        }
    }
    public func encode(to encoder: Encoder) throws {
        var singleContainer = encoder.singleValueContainer()
        switch self {
        case .On:
            try singleContainer.encode(ValueKeys.On.rawValue)
        case .Off:
            try singleContainer.encode(ValueKeys.Off.rawValue)
        case .Toggle:
            try singleContainer.encode(ValueKeys.Toggle.rawValue)
        }
    }
}
