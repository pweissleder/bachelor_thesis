import asyncio
import json
import sys
import threading
import traceback
import logging

import yaml
from yaml import SafeLoader

from src.communicators.mqtt_communicator import MqttCommunicator
from src.communicators.rest_communicator import RestCommunicator
from src.gateway.gateway import Gateway, execute_with_timeout

logging.basicConfig(level=logging.DEBUG)
logger = logging.getLogger(__name__)


# The structure of the following class is oriented to the Unicorn Project
def main():
    gateway_config_file_name = sys.argv[1] if len(
        sys.argv) > 1 else "configuration/gateway_configuration.yml"
    integration_config_file_name = sys.argv[1] if len(
        sys.argv) > 1 else "configuration/integration_configuration.yml"

    try:
        with open(gateway_config_file_name) as gateway_file, open(
                integration_config_file_name) as integration_file:
            gateway_cfg = yaml.load(gateway_file, SafeLoader)["gateway"]
            integration_cfg = yaml.load(integration_file, SafeLoader)["integrations"]
            asyncio.run(run(
                gateway_cfg["server"]["server_address"],
                gateway_cfg["server"]["server_port"],
                gateway_cfg["server"]["authentication_endpoint"],

                gateway_cfg["message_broker"]["broker_address"],
                gateway_cfg["message_broker"]["broker_port"],

                gateway_cfg["auth"]["gateway_id"],
                gateway_cfg["auth"]["auth_token"],

                # Add Integration Configurations here
            ), debug=True)
    except OSError as e:
        logger.error(
            f"One of the configuration files was not found! Or the connection to the server failed!")
        logger.exception(e)
        traceback.print_exc()
    except KeyError:
        logger.error(
            "Not every required parameter was specified by the configuration file!")
        traceback.print_exc()


async def run(server_base_url, server_port, server_authentication_endpoint,
              broker_url, broker_port,
              gateway_id, auth_token
              ):
    try:
        logger.info(f"Starting Gateway {gateway_id}")
        rest_communicator = RestCommunicator(server_base_url, server_port, server_authentication_endpoint)
        message_communicator = MqttCommunicator(broker_url, broker_port, gateway_id)

        gateway = Gateway(gateway_id, auth_token, rest_communicator, message_communicator)

        await gateway.setup_gateway()

        await execute_with_timeout(gateway.setup_integrations(),  # Add Integration Configurations here
                                   timeout=40.0,
                                   message="Integration setup failed")

        gateway.start_message_loop()

        await gateway.run()
    except Exception as e:
        print("app Error: ", e)
        traceback.print_exc()


if __name__ == "__main__":
    main()
