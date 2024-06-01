from typing import Any

from pymystrom.bulb import MyStromBulb

from src.models.devices.device import Device, Cluster, ClusterType, CommandType, \
    Endpoint, AttributeType, Attribute
from matter_server.client import MatterClient
from src.models.devices.nodeID import NodeID, IntegrationType


# Missing Color brightness and color control
# Oriented on the Unicorn Project implementation of the equivalent class
class MyStromLightBulb(Device):

    def __init__(self, lib_bulb: MyStromBulb, on, identifier):

        self.id = NodeID(IntegrationType.MYSTROM, identifier)
        self.libBulb = lib_bulb
        self.mac = identifier

        self.on = on

        self.set_endpoints()

    def set_endpoints(self):
        self.endpoints = []

        # Basic Information Cluster
        basic_info_cluster = Cluster(ClusterType.BasicInformation, [], [
            Attribute(AttributeType.ProductName,
                      "MyStrom Bulb"),
            Attribute(AttributeType.SerialNumber,
                      self.mac),
            Attribute(AttributeType.VendorName,
                      "MyStrom")
        ])
        self.endpoints.append(Endpoint(0, [basic_info_cluster]))

        on_off_cluster = Cluster(ClusterType.OnOff,
                                 [CommandType.Toggle, CommandType.On, CommandType.Off],
                                 [Attribute(AttributeType.OnOff, self.on)
                                  ])
        self.endpoints.append(Endpoint(1, [on_off_cluster]))

    async def apply_commands(self, endpoint_id: int, cluster_type: ClusterType, command: CommandType,
                             attribute: Any = None):

        for endpoint in self.endpoints:
            if endpoint.id == endpoint_id:
                for cluster in endpoint.clusters:
                    if cluster.type == cluster_type:
                        if command is command.On:
                            response = await self.libBulb.set_on()
                            # potential work around to immmitate the state change of the bulb
                            # cluster.set_attribute(Attribute(AttributeType.OnOff, response[self.mac]['on']))
                            break
                        if command is command.Off:
                            response = await self.libBulb.set_off()
                            break
                        if command is command.Toggle:
                            current_onoff_state = cluster.get_attribute(AttributeType.OnOff).value
                            if current_onoff_state:
                                response = await self.libBulb.set_off()
                            else:
                                response = await self.libBulb.set_on()
                            break
