package ba.pascal.weissleder.server.model.prototype

import ba.pascal.weissleder.server.model.framework.Capability
import ba.pascal.weissleder.server.model.framework.Context
import ba.pascal.weissleder.server.model.framework.SpaceContext
import ba.pascal.weissleder.server.model.framework.support.devices.NodeID
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity

@Entity
@DiscriminatorValue("roomContext")
open class Room(
    id: Long?,
    name: String,
    capability: Capability,
    devices: MutableList<NodeID>,
    subContexts: MutableList<Context>,
    location: String,
) : SpaceContext(id, name, capability, devices, subContexts, location) {
    constructor() : this(null, "", Capability.R, mutableListOf(), mutableListOf(), "") {
    }
}
