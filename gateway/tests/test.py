import unittest

from src.device_integrations.mock.mocks import MockDevice
from src.models.devices.device import Attribute, AttributeType

class TestDevice(unittest.TestCase):
    def test_has_changed_f(self):
        # Original device state

        mock_device_a = MockDevice(68)
        mock_device_b = mock_device_a

        # Test that has_changed returns True
        self.assertFalse(mock_device_a.has_changed(mock_device_b))

    def test_has_changed_t(self):
        # Original device state

        mock_device_a = MockDevice(68)
        hash_a = hash(mock_device_a)
        mock_device_b = MockDevice(69)
        hash_b = hash(mock_device_b)
        mock_device_b.endpoints[0].clusters[0].set_attribute(Attribute(AttributeType.OnOff, False))
        hash_b_a = hash(mock_device_b)
        mock_device_b.endpoints[0].clusters[0].set_attribute(Attribute(AttributeType.OnOff, True))
        hash_b_r = hash(mock_device_b)
        print(hash_a)
        print(hash_b)
        print(hash_b_a)
        print(hash_b_r)
        self.assertTrue(hash_a != hash_b)
        self.assertTrue(hash_b != hash_b_a)
        self.assertTrue(hash_b == hash_b_r)

        # Test that has_changed returns True
        self.assertTrue(mock_device_a.has_changed(mock_device_b))

if __name__ == '__main__':
    unittest.main()