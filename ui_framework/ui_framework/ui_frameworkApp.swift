import SwiftUI
import SwiftData

@main
struct ui_frameworkApp: App {
    @State var model: Model = Model(user: User(email: "admin@example.com", password: "admin123"))

    var body: some Scene {
        WindowGroup {
            HomeView(model: model).onAppear(perform: {
                model.setModelData(user:model.user)
            })
        }
    }
}
