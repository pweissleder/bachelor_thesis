import asyncio
import traceback
from typing import List

from pyShelly import Block

from src.models.devices.device import Device
from src.device_integrations.prototype.shelly.shelly_devices import ShellyDevice


# Oriented on the Unicorn Project implementation of the equivalent class
async def discover_devices(shelly) -> List[Device]:
    print("Looking for shelly devices")
    result = []

    try:
        for block in shelly.blocks.values():
            assert isinstance(block, Block)
            if not block.available():
                continue
            result.append(ShellyDevice(block))
    except Exception as e:
        print('Discovery crash')
        print(e)
        traceback.print_stack()
        await asyncio.sleep(5)
    print("Stopped looking for shelly devices")
    return result
