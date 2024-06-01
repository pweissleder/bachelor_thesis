package ba.pascal.weissleder.server.model.framework


import ba.pascal.weissleder.server.model.framework.support.GatewaySession
import ba.pascal.weissleder.server.model.framework.support.devices.NodeID
import ba.pascal.weissleder.server.model.framework.support.messages.GatewayInfoMessage
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import jakarta.persistence.*
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import java.util.*
@Entity
data class Gateway(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID = UUID.randomUUID(),

    val authToken: String = "NotSet",
    @ManyToMany(cascade = [CascadeType.PERSIST, CascadeType.REMOVE], fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    val activeDevices: MutableList<NodeID> = mutableListOf(),

    @ManyToMany(cascade = [CascadeType.PERSIST, CascadeType.REMOVE], fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    val reachableDevices: MutableList<NodeID> = mutableListOf(),

    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "gatewaySessionId", referencedColumnName = "gatewayId")
    var session: GatewaySession? = null
) {
    fun buildGatewayInfoMessage(): GatewayInfoMessage {
        return GatewayInfoMessage(
            gatewayId = this.id,
            activeDevices = this.activeDevices,
            reachableDevices = this.reachableDevices
        )
    }

    fun updateGateway(gatewayInfoMessage: GatewayInfoMessage): Gateway {
        return this.copy(
            activeDevices = gatewayInfoMessage.activeDevices,
            reachableDevices = gatewayInfoMessage.reachableDevices
        )
    }
}