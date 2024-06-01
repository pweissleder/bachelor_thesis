from dataclasses import dataclass
from typing import List


from dataclasses_json import dataclass_json, LetterCase, Undefined
from src.models.devices.device import Command
from src.models.devices.nodeID import NodeID


# The structure of the following class is based to the Unicorn Project
@dataclass_json(letter_case=LetterCase.CAMEL, undefined=Undefined.EXCLUDE)
@dataclass
class CommandMessage:
    device_id: NodeID
    commands: List[Command]

