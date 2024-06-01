package ba.pascal.weissleder.server.model.framework


// EVTl. komplett raus

import ba.pascal.weissleder.server.model.framework.support.devices.Endpoint
import ba.pascal.weissleder.server.model.framework.support.devices.NodeID
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.google.gson.Gson
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import java.time.Instant
import java.io.Serializable as JavaSerializable

// Only for internal device representation
data class Device(
    val id: NodeID = NodeID(),
    var endpoints: List<Endpoint> = emptyList()
) : JavaSerializable

@Entity
data class DeviceState(
    @Id
    val timestamp: Instant = Instant.now(),
    @ManyToOne
    val id: NodeID = NodeID(),
    @Column(columnDefinition = "TEXT")
    val endpoints: String = "",

    ) : JavaSerializable {
    constructor(device: Device) : this(Instant.now(), device.id, Gson().toJson(device.endpoints))

    fun toDevice(): Device {
        val deviceEndpoints = ObjectMapper().registerModule(
            JavaTimeModule()
        ).readValue(this.endpoints, object : TypeReference<List<Endpoint>?>() {})

        return Device(id, deviceEndpoints ?: emptyList())

    }
}
