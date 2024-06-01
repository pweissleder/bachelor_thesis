import SwiftUI
import AnyCodable

struct ContextDetailView: View {
    @State var viewModel: ContextDetailViewModel
    
    var body: some View {
        if viewModel.context.id == viewModel.model.getcurrentContext() {
            VStack{
                if let deviceId = viewModel.model.getSelectedDevice() {
                    if let device = viewModel.model.getDevice(deviceId: deviceId){
                        DeviceDetailView(viewModel: DeviceDetailViewModel(model: viewModel.model, device: device, contextId: viewModel.context.id))
                    } else {
                        Text("Not Found")
                        Button {
                            viewModel.model.resetContext()
                        } label: {
                            Text("Home")
                        }
                    }} else {
                        HStack{
                            Button {
                                viewModel.model.resetContext()
                            } label: {
                                Text("Home")
                            }.padding(.horizontal,20)
                            Spacer()
                        }
                        
                        List {
                            
                            Section(header: Text("Context Information")) {
                                Text("Name: \(viewModel.context.name)")
                                Text("Capability: \(viewModel.context.capability.rawValue)")
                                if let location = (viewModel.context as? SpaceContext)?.location {
                                    Text("Location: \(location)")
                                }
                                if let rationale = (viewModel.context as? LogicContext)?.rationale {
                                    Text("Rationale: \(rationale)")
                                }
                                if let startDate = (viewModel.context as? TimeContext)?.startDate {
                                    Text("\(startDate) - \((viewModel.context as? TimeContext)?.endDate ?? Date())").font(.subheadline)
                                }
                            }
                            
                            Section(header: Text("Devices")) {
                                ForEach(viewModel.deviceIds , id: \.self) { deviceId in
                                    if let device = viewModel.getDevice(deviceID: deviceId) {
                                        Button {
                                            self.viewModel.model.selectDevice(deviceId: deviceId)
                                        } label: {
                                            Text(deviceId.description)
                                        }
                                    }
                                }
                            }
                            
                            Section(header: Text("Subcontexts")) {
                                ForEach(viewModel.subContexts) { subContext in
                                    Button {
                                        viewModel.model.setContext(contextId: subContext.id)
                                    } label: {
                                        VStack(alignment: .leading) {
                                            Text(subContext.name)
                                            if let startDate = (subContext as? TimeContext)?.startDate {
                                                Text("\(startDate) - \((subContext as? TimeContext)?.endDate ?? Date())").font(.subheadline)
                                            }
                                            if let location = (subContext as? SpaceContext)?.location {
                                                Text("Location: \(location)")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
            }
        } else {
            HomeView(model: viewModel.model)
        }}}

