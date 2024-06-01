import logging
from typing import List, Any

from src.device_integrations.framework.device_integrations import DeviceIntegration
from src.models.devices.device import Device, CommandType, Endpoint, Cluster, ClusterType, \
    Attribute, AttributeType
from src.models.devices.nodeID import NodeID, IntegrationType

logging.basicConfig(level=logging.DEBUG)
logger = logging.getLogger(__name__)


# Oriented on the Unicorn Project implementation of the equivalent class
class MockDevice(Device):

    def __init__(self, node_id: int) -> None:
        self.id = NodeID(IntegrationType.MOCK, str(node_id))

        on_off_cluster = Cluster(ClusterType.OnOff, [CommandType.Toggle], [Attribute(AttributeType.OnOff, True)])

        self.endpoints = [
            Endpoint(
                1,
                [on_off_cluster]
            )
        ]

    def set_endpoints(self):
        pass

    async def apply_commands(self, endpoint_id: int, cluster_type: ClusterType, command: CommandType,
                             attribute: Any = None):
        for endpoint in self.endpoints:
            if endpoint.id == endpoint_id:
                for cluster in endpoint.clusters:
                    if cluster.type == cluster_type:
                        if command is command.Toggle:
                            if cluster.get_attribute(AttributeType.OnOff).value is False:
                                cluster.set_attribute(Attribute(AttributeType.OnOff, True))
                            else:
                                cluster.set_attribute(Attribute(AttributeType.OnOff, False))
                            break


class MockDeviceIntegration(DeviceIntegration):
    mock_device_a = MockDevice(68)
    mock_device_b = MockDevice(69)

    async def discover_devices(self) -> List[Device]:
        return [self.mock_device_a, self.mock_device_b]

    async def get_devices(self) -> (List[Device], List[Device]):
        logging.debug("Getting devices")
        self.discovered_devices = await self.discover_devices()
        return self.discovered_devices, []
