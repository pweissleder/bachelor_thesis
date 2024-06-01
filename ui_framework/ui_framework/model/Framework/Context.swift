import Foundation
import AnyCodable

//https://david.y4ng.fr/codable-with-mixed-types-of-data/
public enum Capability: String, Codable {
    case R = "R"
    case RI = "RI"
    case RW = "RW"
    case RIW = "RIW"
}

public class ContextFactory {
    static func createContext(from decoder: Decoder) throws -> Context {
        let container = try decoder.container(keyedBy: Context.CodingKeys.self)
        let type = try container.decode(Context.ContextType.self, forKey: .type)
        
        switch type {
        case .context:
            return try Context(from: decoder)
        case .timeContext:
            return try TimeContext(from: decoder)
        case .spaceContext:
            return try SpaceContext(from: decoder)
        case .logicContext:
            return try LogicContext(from: decoder)
        }
    }
}

public protocol ContextProtocol: Codable, Identifiable {
    var id: Int64 { get set }
    var name: String { get set }
    var capability: Capability { get set }
    var devices: [NodeID] { get set }
    var subContexts: [Context] { get set }
}

public class Context: ContextProtocol {
    public var id: Int64
    public var name: String
    public var capability: Capability
    public var devices: [NodeID]
    public var subContexts: [Context]
    
    init(id: Int64 = 0, name: String = "", capability: Capability = .R, devices: [NodeID] = [], subContexts: [Context] = []) {
        self.id = id
        self.name = name
        self.capability = capability
        self.devices = devices
        self.subContexts = subContexts
    }
    
    func getAllDevices() -> [NodeID] {
        var allDevices = self.devices
        
        for subContext in subContexts {
            allDevices.append(contentsOf: subContext.getAllDevices())
        }
        
        return allDevices
    }
    
    func getAllSubcontexts() -> [Context] {
        var allSubContexts = self.subContexts
        
        for subContext in subContexts {
            allSubContexts.append(contentsOf: subContext.getAllSubcontexts())
        }
        
        return allSubContexts
    }
    
    enum CodingKeys: String, CodingKey {
        case id, name, capability, devices, subContexts, type
    }
    
    enum ContextType: String, Codable {
        case context = "context"
        case timeContext = "timeContext"
        case spaceContext = "spaceContext"
        case logicContext = "logicContext"
    }
    
    var type: ContextType {
        if self is TimeContext {
            return .timeContext
        } else if self is SpaceContext {
            return .spaceContext
        } else if self is LogicContext {
            return .logicContext
        } else {
            return .context
        }
    }
    
    public func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        try container.encode(id, forKey: .id)
        try container.encode(name, forKey: .name)
        try container.encode(capability, forKey: .capability)
        try container.encode(devices, forKey: .devices)
        try container.encode(subContexts, forKey: .subContexts)
        try container.encode(type.rawValue, forKey: .type)
    }
    
    public required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        
        self.id = try container.decode(Int64.self, forKey: .id)
        self.name = try container.decode(String.self, forKey: .name)
        self.capability = try container.decode(Capability.self, forKey: .capability)
        self.devices = try container.decode([NodeID].self, forKey: .devices)
        
        var subContextsContainer = try container.nestedUnkeyedContainer(forKey: .subContexts)
        var subContexts: [Context] = []
        
        while !subContextsContainer.isAtEnd {
            let subContext = try ContextFactory.createContext(from: subContextsContainer.superDecoder())
            subContexts.append(subContext)
        }
        
        self.subContexts = subContexts
    }
}

public class TimeContext: Context {
    var startDate: Date
    var endDate: Date?
    
    init(id: Int64 = 0, name: String = "", capability: Capability = .R, devices: [NodeID] = [], subContexts: [Context] = [], startDate: Date = Date(), endDate: Date? = nil) {
        self.startDate = startDate
        self.endDate = endDate
        super.init(id: id, name: name, capability: capability, devices: devices, subContexts: subContexts)
    }
    
    public override func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        try container.encode(startDate, forKey: .startDate)
        try container.encode(endDate, forKey: .endDate)
        try super.encode(to: encoder)
    }
    
    required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        self.startDate = try container.decode(Date.self, forKey: .startDate)
        self.endDate = try container.decode(Date?.self, forKey: .endDate)
        try super.init(from: decoder)
    }
    
    private enum CodingKeys: String, CodingKey {
        case startDate, endDate
    }
}

public class SpaceContext: Context {
    var location: String
    
    init(id: Int64 = 0, name: String = "", capability: Capability = .R, devices: [NodeID] = [], subContexts: [Context] = [], location: String = "") {
        self.location = location
        super.init(id: id, name: name, capability: capability, devices: devices, subContexts: subContexts)
    }
    
    public override func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        try container.encode(location, forKey: .location)
        try super.encode(to: encoder)
    }
    
    required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        self.location = try container.decode(String.self, forKey: .location)
        try super.init(from: decoder)
    }
    
    private enum CodingKeys: String, CodingKey {
        case location
    }
}

public class LogicContext: Context {
    var rationale: String
    
    init(id: Int64 = 0, name: String = "", capability: Capability = .R, devices: [NodeID] = [], subContexts: [Context] = [], rationale: String = "") {
        self.rationale = rationale
        super.init(id: id, name: name, capability: capability, devices: devices, subContexts: subContexts)
    }
    
    public override func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        try container.encode(rationale, forKey: .rationale)
        try super.encode(to: encoder)
    }
    
    required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        self.rationale = try container.decode(String.self, forKey: .rationale)
        try super.init(from: decoder)
    }
    
    private enum CodingKeys: String, CodingKey {
        case rationale
    }
}
