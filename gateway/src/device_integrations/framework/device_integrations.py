from typing import List

from src.models.devices.device import Device


class DeviceIntegration:
    discovered_devices: [Device] = []
    removed_devices: [Device] = []

    async def discover_devices(self) -> List[Device]:
        pass

    async def get_devices(self) -> (List[Device], List[Device]):
        # if removed_devices is not cleared before each call, devices within the list are ignored
        pass
