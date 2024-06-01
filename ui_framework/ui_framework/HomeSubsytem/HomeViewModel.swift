import Foundation
import Combine
import SwiftUI

@Observable
class HomeViewModel {
    var model: Model
    
    init(model: Model) {
        self.model = model
    }
}
