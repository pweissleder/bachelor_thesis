import Foundation

// Oriented on equivalent file from the Unicorn Project
//https://david.y4ng.fr/codable-with-mixed-types-of-data/
public enum ClusterType: String, Codable {
    case BasicInformation = "BasicInformation"
    case OnOff = "OnOff"
    case FlowMeasurement = "FlowMeasurement"
    
    case UNKOWN = "UNKOWN"
}
