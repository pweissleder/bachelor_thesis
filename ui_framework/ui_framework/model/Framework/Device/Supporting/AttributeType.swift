import Foundation

// Oriented on equivalent file from the Unicorn Project
//https://david.y4ng.fr/codable-with-mixed-types-of-data/
public enum AttributeType: String, Codable {
    case OnOff = "OnOff"
    case MeasuredValue = "MeasuredValue"
    case StateValue = "StateValue"
    case ProductName = "ProductName"
    case SerialNumber = "SerialNumber"
    case VendorName = "VendorName"
    
    case UNKOWN = "UNKOWN"
    
    
}
