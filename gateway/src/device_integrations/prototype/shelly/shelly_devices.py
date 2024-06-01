from typing import Any

from pyShelly.block import Block
from pyShelly.relay import Relay
from pyShelly.trv import Trv
from pyShelly.roller import Roller
from pyShelly.powermeter import PowerMeter
from pyShelly.sensor import DoorWindow, Motion, ExtSwitch, ExtTemp, ExtHumidity

from src.models.devices.device import Device, Cluster, ClusterType, CommandType, \
    Endpoint, Attribute, AttributeType

from src.models.devices.nodeID import NodeID, IntegrationType


# Oriented on the Unicorn Project implementation of the equivalent class
class ShellyDevice(Device):

    def __init__(self, lib_block: Block):
        self.id = NodeID(IntegrationType.SHELLY, lib_block.id)

        self.libBlock = lib_block
        self.set_endpoints()

    @property
    def libBlock(self):
        return self._libBlock

    @libBlock.setter
    def libBlock(self, value):
        self._libBlock = value
        self.set_endpoints()

    def set_endpoints(self):
        self.endpoints = []

        basic_info_cluster = Cluster(ClusterType.BasicInformation, [], [
            Attribute(AttributeType.ProductName,
                      self.libBlock.type_name()),
            Attribute(AttributeType.SerialNumber,
                      self.libBlock.id),
            Attribute(AttributeType.VendorName,
                      "Shelly")
        ])
        self.endpoints.append(Endpoint(0, [basic_info_cluster]))

        endpoint_id = 1
        for dev in self.libBlock.devices:
            state = dev.state
            if isinstance(dev, Relay):
                on_off_cluster = Cluster(ClusterType.OnOff,
                                         [CommandType.Toggle, CommandType.On, CommandType.Off],
                                         [
                                             Attribute(AttributeType.OnOff, state)
                                         ])
                self.endpoints.append(Endpoint(endpoint_id, [on_off_cluster]))

            elif isinstance(dev, PowerMeter):
                flow_measurement_cluster = Cluster(ClusterType.FlowMeasurement,
                                                   [],
                                                   [
                                                       Attribute(AttributeType.MeasuredValue, state)
                                                   ])
                self.endpoints.append(Endpoint(endpoint_id, [flow_measurement_cluster]))

            # Shelly extensions. Not Implemented!
            elif isinstance(dev, DoorWindow):
                pass
            elif isinstance(dev, Roller):
                pass
            elif isinstance(dev, Motion):
                pass
            elif isinstance(dev, ExtTemp):
                pass
            elif isinstance(dev, ExtHumidity):
                pass
            elif isinstance(dev, ExtSwitch):
                pass
            elif isinstance(dev, Trv):
                pass

            endpoint_id += 1

    async def apply_commands(self, endpoint_id: int, cluster_type: ClusterType, command: CommandType,
                             attribute: Any = None):
        for endpoint in self.endpoints:
            if endpoint.id == endpoint_id:
                device = self.libBlock.devices[endpoint_id - 1]
                for cluster in endpoint.clusters:
                    if cluster.type == cluster_type:

                        # Switch and Relay commands
                        if command is command.On:
                            device.turn_on()
                        if command is command.Off:
                            device.turn_off()
                        if command is command.Toggle:
                            current_onoff_state = cluster.get_attribute(AttributeType.OnOff)
                            # relay on/off
                            if current_onoff_state.value:
                                device.turn_off()
                            else:
                                device.turn_on()
                            # cluster.set_attribute(Attribute(AttributeType.OnOff, device.state))

                        break
