package ba.pascal.weissleder.server.model.framework.support.devices

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import kotlinx.serialization.Serializable
import java.util.UUID.randomUUID
import java.io.Serializable as JavaSerializable

// The structure of the following class is based to the Unicorn Project
@Entity
@Serializable
@IdClass(NodeID::class)
data class NodeID(
    @Id val integrationType: IntegrationType = IntegrationType.UNKNOWN,
    @Id val persistentAttribute: String = randomUUID().toString()
) : JavaSerializable {
    fun toReadableString(): String {
        return integrationType.name + "-" + persistentAttribute
    }

    constructor(compositeNodeId: String) : this(
        IntegrationType.valueOf(compositeNodeId.split("-".toRegex())[0]), compositeNodeId.split("-".toRegex(), 2)[1]
    )
}

