import Foundation

// Oriented on equivalent file from the Unicorn Project
//https://david.y4ng.fr/codable-with-mixed-types-of-data/
public enum IntegrationType: String, Codable {
    case MOCK = "MOCK"
    case SHELLY = "SHELLY"
    case MYSTROM = "MYSTROM"

    case UNKOWN = "UNKOWN"
}
