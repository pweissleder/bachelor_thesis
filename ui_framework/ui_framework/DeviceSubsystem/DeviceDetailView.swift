import SwiftUI

// Oriented on equivalent file from the Unicorn Project
struct DeviceDetailView: View {
    @State var viewModel: DeviceDetailViewModel
    
    var body: some View {
        if viewModel.deviceId == viewModel.model.getSelectedDevice() {
            VStack{
                HStack{
                    Button {
                        viewModel.model.deselectDevice()
                    } label: {
                        Text("Back")
                    }.padding(.horizontal,20)
                    Spacer()
                }
                Text("\(viewModel.deviceId.description)")
                    .bold()
                    .foregroundColor(viewModel.model.deviceisReachable(nodeID: viewModel.deviceId) ? .green : .red)
                List {
                    Section(header: Text("Endpoints")) {
                        ForEach(viewModel.endpoints, id: \.id) { endpoint in
                            VStack {
                                HStack {
                                    Spacer()
                                    Text("Endpoint \(endpoint.id)")
                                    Spacer()
                                }
                                ForEach(endpoint.clusters, id: \.id) { cluster in
                                    buildClusterView(for: cluster, endpoint: endpoint.id)
                                }
                            }
                        }
                    }
                    Section(header: Text("Contexts")) {
                        ForEach(viewModel.subcontexts) { context in
                            Button {
                                viewModel.model.setContext(contextId: context.id)
                                viewModel.model.deselectDevice()
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
                }
            }
        } else {
            HomeView(model: viewModel.model)
        }}
        
        @ViewBuilder
        private func buildClusterView(for cluster: Cluster, endpoint: Int) -> some View {
            switch cluster.type {
            case .OnOff:
                if let onOffValue = cluster.attributes.first(where: { $0.type == .OnOff })?.value.value as? Bool {
                    OnOff(viewModel: $viewModel, endpointId: endpoint, onOffValue: onOffValue)
                } else {
                    Text("OnOff value is missing")
                }
            case .FlowMeasurement:
                if let flowMeasurementValue = cluster.attributes.first(where: { $0.type == .MeasuredValue })?.value.value as? Double {
                    FlowMeasurement(viewModel: $viewModel, flowMeasurementValue: flowMeasurementValue)
                } else {
                    Text("Flow Measurement value is missing")
                }
            case .BasicInformation:
                if let productName = cluster.attributes.first(where: { $0.type == .ProductName })?.value.value as? String,
                   let vendorName = cluster.attributes.first(where: { $0.type == .VendorName })?.value.value as? String,
                   let serialNumber = cluster.attributes.first(where: { $0.type == .SerialNumber })?.value.value as? String {
                    BasicInformation(viewModel: $viewModel, productNameValue: productName, vendorNameValue: vendorName, serialNumberValue: serialNumber)
                } else {
                    Text("Basic Information value is missing")
                }
            default:
                Text("")
            }
        }

}

struct OnOff: View {
    @Binding var viewModel: DeviceDetailViewModel
    let endpointId: Int
    var onOffValue: Bool
    
    var body: some View {
        VStack {
            Text("Cluster OnOff").bold()
            if viewModel.contextHasReadPermission() {
                onOffValue ? Text("On") : Text("Off")
            }
            if viewModel.contextHasInvocationPermission() {
                HStack{
                    Spacer()
                    Button(action: {
                        viewModel.toggleOnOff(endpointId: endpointId)
                    }) {
                        Image(systemName: "power.circle").resizable()
                            .frame(width: 200, height: 200)
                            .scaledToFit()
                            .foregroundColor(onOffValue ? .blue : .gray)
                    }
                    Spacer()
                }
                
            }
        }
    }
}

struct FlowMeasurement: View {
    @Binding var viewModel: DeviceDetailViewModel
    var flowMeasurementValue: Double
    
    var body: some View {
            VStack {
                Text("Cluster FlowMeasurement").bold()
                if self.viewModel.contextHasReadPermission() {
                    HStack{
                        Text("Flow Measurement: \(flowMeasurementValue)")
                        Spacer()
                    }
                }
            }
        }
}

struct BasicInformation: View {
    @Binding var viewModel: DeviceDetailViewModel
    
    var productNameValue: String = "Unknown"
    var vendorNameValue: String = "Unknown"
    var serialNumberValue: String = "Unknown"
    
    var body: some View {
        VStack {
            Text("Cluster BasicInformation").bold()
            VStack {
                if self.viewModel.contextHasReadPermission() {
                    HStack{
                        Text("ProductName: \(productNameValue)")
                        Spacer()
                    }
                    HStack{
                        Text("VendorName: \(vendorNameValue)")
                        Spacer()
                    }
                    HStack{
                    Text("SerialNumber: \(serialNumberValue)")
                        Spacer()
                    }
                }
            }
        }
    }
}
