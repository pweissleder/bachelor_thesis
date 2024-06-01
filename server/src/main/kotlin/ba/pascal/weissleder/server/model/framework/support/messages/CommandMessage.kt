package ba.pascal.weissleder.server.model.framework.support.messages

import ba.pascal.weissleder.server.model.framework.support.devices.Command
import java.io.Serializable

// The structure of the following class is based to the Unicorn Project
data class CommandMessage(
    val contextId: Long,
    val command: Command<Serializable?>
)
