import Foundation

//https://david.y4ng.fr/codable-with-mixed-types-of-data/
public enum Role: String, Codable {
    case ADMIN
    case USER
    case EVENTHOST
    case EVENTMANAGER
}

public class User: Codable {
    var id: Int64
    var name: String
    var email: String
    var password: String
    var role: Role
    var contexts: [Context]
    
    init(id: Int64 = 0, name: String = "", email: String = "", password: String = "", role: Role = .USER, contexts: [Context] = []) {
        self.id = id
        self.name = name
        self.email = email
        self.password = password
        self.role = role
        self.contexts = contexts
    }
    init(email: String = "", password: String = "") {
        self.id = 0
        self.name = ""
        self.email = email
        self.password = password
        self.role = .USER
        self.contexts = []
    }
    
    enum CodingKeys: String, CodingKey {
        case id, name, email, password, role, contexts
    }
    
    public required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        self.id = try container.decode(Int64.self, forKey: .id)
        self.name = try container.decode(String.self, forKey: .name)
        self.email = try container.decode(String.self, forKey: .email)
        self.password = try container.decode(String.self, forKey: .password)
        self.role = try container.decode(Role.self, forKey: .role)
        
        var contextsContainer = try container.nestedUnkeyedContainer(forKey: .contexts)
        var contexts: [Context] = []
        
        while !contextsContainer.isAtEnd {
            let subContext = try ContextFactory.createContext(from: contextsContainer.superDecoder())
            contexts.append(subContext)
        }
        
        self.contexts = contexts
    }
    
    public func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        try container.encode(id, forKey: .id)
        try container.encode(name, forKey: .name)
        try container.encode(email, forKey: .email)
        try container.encode(password, forKey: .password)
        try container.encode(role, forKey: .role)
        try container.encode(contexts, forKey: .contexts)
    }
}


