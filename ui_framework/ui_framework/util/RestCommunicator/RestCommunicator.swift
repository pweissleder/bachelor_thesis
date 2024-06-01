import Foundation

// Oriented on equivalent file from the Unicorn Project
public enum APIError: Error {
    case castError(object: String)
    case fetchError(object: String)
}

public class RestCommunicator {
    
    init() {}
    
    static func echo(input: String) async -> String {
        do {
            let params = NetworkURLs.createParams(params: ["name": input])
            let (data, _) = try await
            NetworkService.makeRequest(method: .GET,
                                       endpoint: "/echo\(params)",
                                       body: nil,
                                       headers:[:] ,
                                       expectedStatus: 200)
            
            guard let decodedData = String(data: data, encoding: .utf8) else {
                throw NetworkError.decodingError
            }
            return decodedData
            
        } catch {
            let errorMsg = "Unexpected error: \(error)"
            print(errorMsg)
            return errorMsg
        }
    }
    
    static func sendCommand(commandMessage: CommandMessage, userCred: String) async {
        do {
            guard let body = try? JSONEncoder().encode(commandMessage) else {
                throw NetworkError.encodingError
            }
            print("Send Command:")
            print(String( format:"%@",String(data: body, encoding: .utf8)!))
            let (data, _) = try await
            NetworkService.makeRequest(method: .POST,
                                       endpoint: "/devices/command",
                                       body:body,
                                       headers: ["AUTHORIZATION" : userCred],
                                       expectedStatus: 200)
            
            guard String(data: data, encoding: .utf8) != nil else {
                throw NetworkError.decodingError
            }
        } catch {
            print("Unexpected error: \(error)")
        }
    }
    

    static func fetchUserSelf(userCred: String) async -> User? {
        do {
            let (data, _) = try await
            NetworkService.makeRequest(method: .GET,
                                       endpoint: "/users/self",
                                       body: nil,
                                       headers: ["AUTHORIZATION" : userCred ],
                                       expectedStatus: 200)
            guard let decodedData = try? JSONDecoder().decode(User.self, from: data) else {
                throw NetworkError.decodingError
            }

            return decodedData
        } catch {
            print("Unexpected error: \(error)\(error.localizedDescription)")
            return nil
        }
    }
}
