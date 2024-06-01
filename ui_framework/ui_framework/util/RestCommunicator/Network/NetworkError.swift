import Foundation

// Oriented on equivalent file from the Unicorn Project
enum NetworkError: Error {
    case invalidUrl
    case requestError
    case decodingError
    case encodingError
    case statusNotOk
    case emptyBody
    case invalidJSON
    
    public var errorDescription: String? {
        switch self {
        case .invalidUrl:
            return NSLocalizedString("The URL is invalid",
                                     comment: "InvalidURL")
        case .requestError:
            return NSLocalizedString("No response to the request",
                                     comment: "RequestError")
        case .decodingError:
            return NSLocalizedString("An decoding error occured",
                                     comment: "DecodingError")
        case .encodingError:
            return NSLocalizedString("An encoding error occured",
                                     comment: "EncodingError")
        case .statusNotOk:
            return NSLocalizedString("The response is not valid",
                                     comment: "HTTPS Status Not Ok")
        case .emptyBody:
            return NSLocalizedString("The body of the request was empty",
                                     comment: "Empty body")
        case .invalidJSON:
            return NSLocalizedString("The body is not JSON serializable",
                                     comment: "Empty body")
        }
    }
}
