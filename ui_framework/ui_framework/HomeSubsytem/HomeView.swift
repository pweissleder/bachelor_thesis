import SwiftUI

struct HomeView: View {
    @State var viewModel: HomeViewModel
    
    init( model: Model) {
        self.viewModel = HomeViewModel(model: model)
    }
    
    
    var body: some View {
        VStack {
            if let contextId = viewModel.model.getcurrentContext() {
                if let context = viewModel.model.getContext(contextId: contextId){
                    ContextDetailView(viewModel: ContextDetailViewModel(model: self.viewModel.model, context: context ))
                } else {
                    Text("Not Found")
                    Button {
                        viewModel.model.resetContext()
                    } label: {
                        Text("Home")
                    }
                }} else {
                    List {
                        Text("Username: \(self.viewModel.model.user.name)")
                        Section(header: Text("Space Contexts")) {
                            ForEach(self.viewModel.model.getSpaceContexts()) { context in
                                Button {
                                    viewModel.model.setContext(contextId: context.id)
                                } label: {
                                    VStack(alignment: .leading) {
                                        Text(context.name)
                                        if let spaceContext = context as? SpaceContext {
                                            Text(spaceContext.location).font(.subheadline)
                                        }
                                    }
                                }
                                
                            }
                        }
                        
                        Section(header: Text("Time Contexts")) {
                            ForEach(self.viewModel.model.getTimeContexts()) { context in
                                Button {
                                    viewModel.model.setContext(contextId: context.id)
                                } label: {
                                    VStack(alignment: .leading) {
                                        Text(context.name)
                                        if let timeContext = context as? TimeContext {
                                            Text("\(timeContext.startDate) - \(timeContext.endDate ?? Date())").font(.subheadline)
                                        }
                                    }
                                }
                            }
                        }
                        
                        Section(header: Text("Logic Contexts")) {
                            ForEach(self.viewModel.model.getLogicContexts()) { context in
                                Button {
                                    viewModel.model.setContext(contextId: context.id)
                                } label: {
                                    VStack(alignment: .leading) {
                                        Text(context.name)
                                    }
                                }
                            }
                        }
                    }
                }
        }
    }
}
