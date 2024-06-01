from typing import List

from pyShelly import pyShelly

from src.device_integrations.framework.device_integrations import DeviceIntegration
from src.device_integrations.prototype.shelly.shelly import discover_devices
from src.models.devices.device import Device


# Oriented on the Unicorn Project implementation of the equivalent class
class ShellyDeviceIntegration(DeviceIntegration):

    def __init__(self):
        print("Shelly device finder is being initialized")
        self.shelly = pyShelly()
        self.shelly.start()

    async def get_devices(self) -> (List[Device], List[Device]):
        print(self.__class__.__name__, "searching ...")

        self.removed_devices.clear()
        new_discovered_devices = await discover_devices(self.shelly)
        new_discovered_device_ids = [device.id for device in new_discovered_devices]
        for device in self.discovered_devices:
            if device.id not in new_discovered_device_ids:
                self.removed_devices.append(device)
        self.discovered_devices = new_discovered_devices

        return self.discovered_devices, self.removed_devices
