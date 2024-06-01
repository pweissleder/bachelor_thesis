package ba.pascal.weissleder.server.model.framework

import ba.pascal.weissleder.server.model.framework.support.devices.NodeID
import ba.pascal.weissleder.server.model.prototype.Meeting
import ba.pascal.weissleder.server.model.prototype.Room
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import jakarta.persistence.*
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import java.util.*

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = Context::class, name = "context"),
    JsonSubTypes.Type(value = TimeContext::class, name = "timeContext"),
    JsonSubTypes.Type(value = SpaceContext::class, name = "spaceContext"),
    JsonSubTypes.Type(value = LogicContext::class, name = "logicContext"),
    JsonSubTypes.Type(value = Room::class, name = "roomContext"),
    JsonSubTypes.Type(value = Meeting::class, name = "meetingContext"),
)
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
@DiscriminatorValue("context")
open class Context(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) open val id: Long? = null, open var name: String = "",

    open var capability: Capability = Capability.R,

    @ManyToMany(
        cascade = [CascadeType.PERSIST, CascadeType.REMOVE]
    ) @Fetch(FetchMode.JOIN) open val devices: MutableList<NodeID> = mutableListOf(),

    @ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER) open val subContexts: MutableList<Context> = mutableListOf()
) {
    constructor() : this(null, "", Capability.R, mutableListOf(), mutableListOf()) {

    }
}

@Entity
@DiscriminatorValue("timeContext")
open class TimeContext(
    id: Long?,
    name: String,
    capability: Capability,
    devices: MutableList<NodeID>,
    subContexts: MutableList<Context>,
    open var startDate: Date = Date(),
    open var endDate: Date? = null
) : Context(id, name, capability, devices, subContexts) {
    constructor() : this(null, "", Capability.R, mutableListOf(), mutableListOf(), Date(), null) {
    }

}

@Entity
@DiscriminatorValue("spaceContext")
open class SpaceContext(
    id: Long?,
    name: String,
    capability: Capability,
    devices: MutableList<NodeID>,
    subContexts: MutableList<Context>,
    open var location: String
) : Context(id, name, capability, devices, subContexts) {
    constructor() : this(null, "", Capability.R, mutableListOf(), mutableListOf(), "") {
    }
}


@Entity
@DiscriminatorValue("logicContext")
open class LogicContext(
    id: Long?,
    name: String,
    capability: Capability,
    devices: MutableList<NodeID>,
    subContexts: MutableList<Context>,
    open var rationale: String = ""
) : Context(id, name, capability, devices, subContexts) {
    constructor() : this(null, "", Capability.R, mutableListOf(), mutableListOf(), "") {
    }
}

enum class Capability(val capability: String) {
    R("r"), RI("ri"), RW("rw"), RIW("riw")
}
