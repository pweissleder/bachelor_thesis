package ba.pascal.weissleder.server.model.framework.support.devices

import java.io.Serializable as JavaSerializable

// The structure of the Command class is based to the Unicorn Project
data class Command<A : JavaSerializable?>(
    val nodeId: NodeID,
    val endpoint: Int,
    val cluster: ClusterType,
    val command: CommandType,
    val commandMode: Int? = null,

    val attribute: Attribute<A>
) : JavaSerializable