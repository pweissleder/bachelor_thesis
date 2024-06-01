//
//  Item.swift
//  ui_framework
//
//  Created by Pascal Weißleder on 08.05.24.
//

import Foundation
import SwiftData

@Model
final class Item {
    var timestamp: Date
    
    init(timestamp: Date) {
        self.timestamp = timestamp
    }
}
