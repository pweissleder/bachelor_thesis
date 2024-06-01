package ba.pascal.weissleder.server.controllers.framework

import ba.pascal.weissleder.server.services.framework.MessageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller

@Controller
class MessageController @Autowired constructor(
    private val messageService: MessageService
) {
    fun publishMessage(topic: String, payload: String, qos: Int, retained: Boolean) {
        messageService.publishMessage(topic, payload, qos, retained)
    }

    fun subscribeToTopic(topic: String, qos: Int) {
        messageService.subscribeToTopic(topic, qos)
    }

    fun unsubscribeFromTopic(topic: String) {
        messageService.unsubscribeFromTopic(topic)
    }

    fun createTopic(patternKey: String, id: String): String {
        return messageService.createTopic(patternKey, id)
    }

}