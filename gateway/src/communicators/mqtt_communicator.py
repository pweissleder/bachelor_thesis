import logging
import time
import queue
import socket

import paho.mqtt.client as mqtt

# Sources:
# https://github.com/eclipse/paho.mqtt.python
# https://www.emqx.com/en/blog/how-to-use-mqtt-in-python
# https://www.hivemq.com/blog/mqtt-client-library-paho-python/
# https://cedalo.com/blog/configuring-paho-mqtt-python-client-with-examples/

logging.basicConfig(level=logging.DEBUG)
logger = logging.getLogger(__name__)

FIRST_RECONNECT_DELAY = 1
RECONNECT_RATE = 2
MAX_RECONNECT_COUNT = 12
MAX_RECONNECT_DELAY = 60

LEADER_QOS = 2
COMMAND_QOS = 1
COMMAND_RESPONSE_QOS = 1
GATEWAY_QOS = 2

message_queue = queue.Queue()

# Set Last Will and Testament (LWT)
LWT_MESSAGE = b"false"
LWT_QOS = 2
LWT_RETAIN = False
global_client_id = None


def on_subscribe(client, userdata, mid, reason_code_list, properties):
    if reason_code_list[0].is_failure:
        logging.error(f"Broker rejected you subscription: {reason_code_list[0]}")
    else:
        logging.debug(f"Broker granted the following QoS: {reason_code_list[0].value}")


def on_unsubscribe(client, userdata, mid, reason_code_list, properties):
    if len(reason_code_list) == 0 or not reason_code_list[0].is_failure:
        logging.debug("unsubscribe succeeded.")
    else:
        logging.error(f"Broker replied with failure: {reason_code_list[0]}")
    client.disconnect()


def on_message(client, userdata, message):
    logger.debug(f"Received message '{message.payload.decode()}' on topic '{message.topic}' with QoS {message.qos}:")
    userdata.put(message)


def on_connect(client, userdata, flags, reason_code, properties):
    if reason_code.is_failure:
        logging.error(f"Failed to connect: {reason_code}. loop_forever() will retry connection")
    else:
        logging.info(f"Connected with result code: {reason_code}")
        return


def on_disconnect(client, userdata, rc):
    logging.info("Disconnected with result code: %s", rc)
    reconnect_count, reconnect_delay = 0, FIRST_RECONNECT_DELAY
    while reconnect_count < MAX_RECONNECT_COUNT:
        logging.info("Reconnecting in %d seconds...", reconnect_delay)
        time.sleep(reconnect_delay)

        try:
            client.reconnect()
            logging.info("Reconnected successfully!")
            return
        except Exception as err:
            logging.error("%s. Reconnect failed. Retrying...", err)

        reconnect_delay *= RECONNECT_RATE
        reconnect_delay = min(reconnect_delay, MAX_RECONNECT_DELAY)
        reconnect_count += 1
    logging.info("Reconnect failed after %s attempts. Exiting...", reconnect_count)


def on_log(client, userdata, paho_log_level, messages):
    if paho_log_level == mqtt.LogLevel.MQTT_LOG_INFO:
        logging.info(messages)


class MqttCommunicator:
    global global_client_id

    def __init__(self, broker, port, client_id):

        self.broker = broker
        self.port = port

        self.client = mqtt.Client(client_id=client_id, userdata=message_queue,
                                  transport="tcp",
                                  callback_api_version=mqtt.CallbackAPIVersion.VERSION2,
                                  protocol=mqtt.MQTTv5)
        self.client.username_pw_set(username="GatewayUser", password="gateway_pass")
        self.client.on_connect = on_connect
        self.client.on_disconnect = on_disconnect

        self.client.on_message = on_message
        self.client.on_subscribe = on_subscribe
        self.client.on_unsubscribe = on_unsubscribe
        self.client.on_log = on_log

        lwt_topic = f"gateways/{client_id}/reachable"
        self.client.will_set(lwt_topic, LWT_MESSAGE, qos=LWT_QOS, retain=LWT_RETAIN)

    def connect(self):
        try:
            self.client.connect(self.broker, self.port)
        except socket.gaierror as e:
            logging.error(f"Failed to connect to MQTT broker at {self.broker}:{self.port}", e)

    def start_loop(self, gateway_id):
        try:
            self.client.publish(f"gateways/{gateway_id}/reachable", "true", qos=LEADER_QOS, retain=True)
            self.client.loop_start()
        except KeyboardInterrupt:
            print("Exiting...")
            # self.client.disconnect()

    def stop_loop(self):
        pass


def pop_message() -> mqtt.MQTTMessage:
    return message_queue.get(block=False)  # Sometimes causes a queue.Empty exception
