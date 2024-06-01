package ba.pascal.weissleder.server.model.prototype

import ba.pascal.weissleder.server.model.framework.Capability
import ba.pascal.weissleder.server.model.framework.Context
import ba.pascal.weissleder.server.model.framework.TimeContext
import ba.pascal.weissleder.server.model.framework.support.devices.NodeID
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import java.util.*

@Entity
@DiscriminatorValue("meetingContext")
open class Meeting(
    id: Long?,
    name: String,
    capability: Capability,
    devices: MutableList<NodeID>,
    subContexts: MutableList<Context>,
    startDate: Date,
    endDate: Date?,
    open var code: Int = 0,
    open var notes: String = "",
) : TimeContext(id, name, capability, devices, subContexts, startDate, endDate) {
    constructor() : this(null, "", Capability.R, mutableListOf(), mutableListOf(), Date(), null) {
    }
}
