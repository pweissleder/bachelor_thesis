import Foundation
import UIKit
import SwiftUI


fileprivate extension Color {
    
    typealias SystemColor = UIColor
    
    var colorComponents: (red: CGFloat, green: CGFloat, blue: CGFloat, alpha: CGFloat)? {
        var colorR: CGFloat = 0
        var colorG: CGFloat = 0
        var colorB: CGFloat = 0
        var colorA: CGFloat = 1
        
        guard SystemColor(self).getRed(&colorR, green: &colorG, blue: &colorB, alpha: &colorA) else {
            return nil
        }
        return (colorR, colorG, colorB, colorA)
    }
}

extension Color: Codable {
    enum CodingKeys: String, CodingKey {
        case r = "r"
        case g = "g"
        case b = "b"
    }
    
    public init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        let colorR = try container.decode(Float.self, forKey: .r)
        let colorG = try container.decode(Float.self, forKey: .g)
        let colorB = try container.decode(Float.self, forKey: .b)
        
        self.init(red: Double(colorR), green: Double(colorG), blue: Double(colorB))
    }
    
    public func encode(to encoder: Encoder) throws {
        guard let colorComponents = self.colorComponents else {
            return
        }
        
        var container = encoder.container(keyedBy: CodingKeys.self)
        
        try container.encode(Float(colorComponents.red) * 255, forKey: .r)
        try container.encode(Float(colorComponents.green) * 255, forKey: .g)
        try container.encode(Float(colorComponents.blue) * 255, forKey: .b)
    }
}

extension UIColor {
    static func hexStringToUIColor (hex:String) -> UIColor {
        var cString:String = hex.trimmingCharacters(in: .whitespacesAndNewlines).uppercased()

        if (cString.hasPrefix("#")) {
            cString.remove(at: cString.startIndex)
        }

        if ((cString.count) != 6) {
            return UIColor.gray
        }

        var rgbValue:UInt64 = 0
        Scanner(string: cString).scanHexInt64(&rgbValue)

        return UIColor(
            red: CGFloat((rgbValue & 0xFF0000) >> 16) / 255.0,
            green: CGFloat((rgbValue & 0x00FF00) >> 8) / 255.0,
            blue: CGFloat(rgbValue & 0x0000FF) / 255.0,
            alpha: CGFloat(1.0)
        )
    }
}
