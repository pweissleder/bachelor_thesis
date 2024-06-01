from enum import Enum
from abc import ABC, abstractmethod
from dataclasses import dataclass
from typing import List, Any, Dict
from dataclasses_json import dataclass_json, Undefined, LetterCase

# The structure of the Command class is based to the Unicorn Project
class IntegrationType(str, Enum):
    MOCK = "MOCK"
    SHELLY = "SHELLY"
    MYSTROM = "MYSTROM"


@dataclass_json(undefined=Undefined.EXCLUDE, letter_case=LetterCase.CAMEL)
@dataclass(frozen=True)
class NodeID:
    integration_type: IntegrationType
    persistent_attribute: str

    def to_string(self) -> str:
        return f"{self.integration_type.value}-{self.persistent_attribute}"

    def to_dict(self) -> dict:
        return {
            'integrationType': self.integration_type.value,
            'persistentAttribute': self.persistent_attribute
        }

    @classmethod
    def from_dict(cls, data: dict):
        integration_type = IntegrationType(data['integrationType'])
        persistent_attribute = data['persistentAttribute']
        return cls(integration_type, persistent_attribute)

    @classmethod
    def from_composite_id(cls, composite_NodeId: str):
        parts = composite_NodeId.split("-", 1)
        if len(parts) < 2:
            raise ValueError("Invalid composite node ID format")
        integration_type = IntegrationType(parts[0])
        persistent_attribute = parts[1]
        return cls(integration_type, persistent_attribute)

    @staticmethod
    def to_json(self) -> str:
        return json.dumps(self.to_dict())

    @staticmethod
    def from_json(json_str: str) -> 'NodeID':
        data = json.loads(json_str)
        return NodeID.from_dict(data)

    @staticmethod
    def from_string(self, node_id: str) -> 'NodeID':
        return NodeID.from_json(node_id)
