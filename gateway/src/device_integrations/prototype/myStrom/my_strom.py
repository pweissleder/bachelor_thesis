import asyncio
import traceback
from typing import List

from pymystrom.bulb import MyStromBulb
from pymystrom.discovery import discover_devices as discover_my_strom_devices

from src.models.devices.device import Device
from src.device_integrations.prototype.myStrom.devices.my_strom_light_bulb import MyStromLightBulb


# Oriented on the Unicorn Project implementation of the equivalent class
async def discover_devices() -> List[Device]:
    result = []
    print("Start looking for myStrom devices")
    try:
        devices = await discover_my_strom_devices()
        await asyncio.sleep(5)  # Wait a bit for the socket to close and avoid crashes

        for device in devices:
            mac = device.mac.replace(':', '')

            # light bulb is 102
            if device.type == 102:
                async with MyStromBulb(device.host, mac) as lib_bulb:
                    await lib_bulb.get_state()
                    state = lib_bulb.state

                    result.append(MyStromLightBulb(
                        lib_bulb,
                        state,
                        mac
                    ))
            else:
                print("Device Type ", device.type, " not implemented yet")
            print("Found MyStrom devices: ", result)

    except Exception as e:
        print('Discovery crash')
        print(e)
        traceback.print_stack()
        await asyncio.sleep(5)
    print("Stopped looking for myStrom devices")
    return result
