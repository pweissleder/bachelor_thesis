from enum import Enum
from abc import ABC, abstractmethod
from dataclasses import dataclass
from typing import List, Any, Dict

from dataclasses_json import dataclass_json, Undefined, LetterCase

from src.models.devices.nodeID import NodeID


# The structure of the following classes is based to the Unicorn Project
@dataclass_json(undefined=Undefined.EXCLUDE, letter_case=LetterCase.CAMEL)
class CommandType(str, Enum):
    # on/off
    On = 'On'
    Off = 'Off'
    Toggle = 'Toggle'

    Unknown = 'Unknown'


@dataclass_json(undefined=Undefined.EXCLUDE, letter_case=LetterCase.CAMEL)
class ClusterType(str, Enum):
    BasicInformation = 'BasicInformation',
    OnOff = 'OnOff'
    FlowMeasurement = 'FlowMeasurement'

    Unknown = 'Unknown'


@dataclass_json(undefined=Undefined.EXCLUDE, letter_case=LetterCase.CAMEL)
class AttributeType(str, Enum):
    # basic information
    VendorName = 'VendorName'
    ProductName = 'ProductName'
    SerialNumber = 'SerialNumber'

    # on/off
    OnOff = 'OnOff'

    # flow measurement
    MeasuredValue = 'MeasuredValue'

    Unknown = 'Unknown'


@dataclass_json(letter_case=LetterCase.CAMEL, undefined=Undefined.EXCLUDE)
@dataclass
class Attribute:
    type: AttributeType
    value: Any

    def __eq__(self, other):
        if isinstance(other, Attribute):
            return self.type == other.type and self.value == other.value
        return False

    def __hash__(self):
        return hash((self.type, self.value))


@dataclass_json(letter_case=LetterCase.CAMEL, undefined=Undefined.EXCLUDE)
@dataclass
class Cluster:
    type: ClusterType
    commands: List[CommandType]
    attributes: List[Attribute]

    def get_attribute(self, attribute_type: AttributeType) -> Attribute | None:
        for attribute in self.attributes:
            if attribute.type == attribute_type:
                return attribute
        return None

    def set_attribute(self, other: Attribute):
        for attribute in self.attributes:
            if attribute.type == other.type:
                self.attributes.remove(attribute)
        self.attributes.append(other)

    def __hash__(self):
        return hash((self.type, tuple(self.commands), tuple(attribute.__hash__() for attribute in self.attributes)))


@dataclass_json(letter_case=LetterCase.CAMEL, undefined=Undefined.EXCLUDE)
@dataclass
class Endpoint:
    id: int
    clusters: List[Cluster]

    def __hash__(self):
        return hash((self.id, tuple(cluster.__hash__() for cluster in self.clusters)))


@dataclass_json(letter_case=LetterCase.CAMEL, undefined=Undefined.EXCLUDE)
@dataclass
class Device(ABC):
    id: NodeID
    endpoints: List[Endpoint]

    @abstractmethod
    def set_endpoints(self):
        pass

    @abstractmethod
    async def apply_commands(self, endpoint_id: int, cluster_type: ClusterType,
                             command: CommandType, attribute: Any = None):
        pass

    def to_dict(self) -> Dict:
        return self.to_dict()

    def has_changed_o(self, other) -> bool:
        if isinstance(other, Device):
            for endpoint in self.endpoints:
                other_endpoint = next((e for e in other.endpoints if e.id == endpoint.id), None)
                if other_endpoint is None or endpoint != other_endpoint:
                    return True
        return False

    def has_changed(self, prior_hash) -> bool:
        if prior_hash != hash(self):
            return True
        return False

    def __hash__(self):
        return hash((self.id, tuple(endpoint.__hash__() for endpoint in self.endpoints)))

    # def safe_copy(self):
    #     new_device = Device()  # replace with your actual constructor
    #     # Copy all attributes except the non-serializable ones
    #     # This is just an example, replace with your actual attributes
    #     new_device.id = self.id
    #     new_device.endpoints = [endpoint.safe_copy() for endpoint in self.endpoints]  # assuming Endpoint has a similar safe_copy method
    #     # Do not copy the lock attribute
    #     # new_device.lock = self.lock  # Do NOT do this
    #     return new_device


@dataclass_json(letter_case=LetterCase.CAMEL, undefined=Undefined.EXCLUDE)
@dataclass
class Command:
    nodeId: NodeID
    endpoint: int
    cluster: ClusterType
    command: CommandType
    attribute: Attribute
    commandMode: int = 0
