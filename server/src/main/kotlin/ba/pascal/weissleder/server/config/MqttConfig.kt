package ba.pascal.weissleder.server.config


import org.eclipse.paho.mqttv5.client.IMqttClient
import org.eclipse.paho.mqttv5.client.MqttClient
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions
import org.eclipse.paho.mqttv5.common.MqttException
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

// Sources:
// https://eclipse.dev/paho/index.php?page=clients/java/index.php
// https://eclipse.dev/paho/files/javadoc/index.html
// https://github.com/eclipse/paho.mqtt.java
// https://www.hivemq.com/blog/mqtt-client-library-encyclopedia-eclipse-paho-java/
@Configuration
class MqttConfig {

    val MQTT_BROKER_URI = "tcp://mqtt:1883"
    val MQTT_CLIENT_ID = "server"
    val MQTT_PASSW = "server_pass"
    val MQTT_USER = "ServerUser"

    @Bean
    fun mqttClient(): IMqttClient {
        val mqttClient = MqttClient(MQTT_BROKER_URI, MQTT_CLIENT_ID)

        val options = MqttConnectionOptions()
        options.isAutomaticReconnect = true
        options.connectionTimeout = 10
        options.password = MQTT_PASSW.toByteArray()
        options.userName = MQTT_USER


        try {
            mqttClient.connect(options)
        } catch (e: MqttException) {
            e.printStackTrace()
        }
        return mqttClient
    }
}