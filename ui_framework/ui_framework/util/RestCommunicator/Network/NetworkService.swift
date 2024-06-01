import Foundation
import SwiftUI
import os

// Oriented on equivalent file from the Unicorn Project
public enum RestMethod: String {
    case POST
    case GET
    case PUT
    case DELETE
}

struct NetworkURLs {
    static let APIScheme = "http"
    static let APIHost = "[ServerIP]"
    static let APIPath = ":8080"
    
    static var  APIbaseURL: String {
        return "\(APIScheme)://\(APIHost)\(APIPath)"
        
    }
    static func createParams(params: [String:String]) -> String {
        var res = "?"
        for (key, value) in params {
            res += "\(key)=\(value)&"
        }
        return String(res.dropLast())
    }
}

public class NetworkService {
    static let baseURL: String = NetworkURLs.APIbaseURL
    
    static func makeRequest(method: RestMethod,
                            endpoint: String,
                            body: Data?,
                            headers: [String: String]?,
                            expectedStatus: Int) async throws -> (Data, Int) {
        //Build URL
        guard let url = URL(string: baseURL + endpoint) else {
            throw NetworkError.invalidUrl
        }
        //Build Request
        var request = URLRequest(url: url)
        request.httpMethod = method.rawValue
        
        if let headers = headers {
            if !headers.isEmpty {
                request.allHTTPHeaderFields = headers
            }
        }
        
        if let data = body {
            request.httpBody = data
        }
        
        request.addValue("application/json;", forHTTPHeaderField: "Content-Type")
        request.addValue("application/json;", forHTTPHeaderField: "Accept")
        self.logRequest(method: method, url: url, expectedStatus: expectedStatus)
        //Send Request
        guard let (data, response) = try? await URLSession.shared.data(for: request) else {
            throw NetworkError.requestError
        }
        
        // Check response status
        guard let responseNew = response as? HTTPURLResponse, responseNew.statusCode == expectedStatus else {
            let responseJSON = try? JSONSerialization.jsonObject(with: data, options: [])
            if let responseJSON = responseJSON as? [String: Any] {
                print(responseJSON)
            }
            throw NetworkError.statusNotOk
        }
        self.logResponse(method: method, url: url, status: responseNew.statusCode)
        return (data, responseNew.statusCode)
    }
    
    static func logRequest(method: RestMethod, url: URL, expectedStatus: Int){
        print("Log request: \(method) - url: \(url) expected status: \(expectedStatus)")
    }
    
    static func logResponse(method: RestMethod, url: URL, status: Int){
        print("Log response: \(method) - url: \(url) status: \(status)")
    }
}

