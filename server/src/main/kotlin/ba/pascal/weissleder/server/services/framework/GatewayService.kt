package ba.pascal.weissleder.server.services.framework

import ba.pascal.weissleder.server.model.framework.Gateway
import ba.pascal.weissleder.server.model.framework.support.devices.NodeID
import ba.pascal.weissleder.server.repositories.GatewayRepository
import jakarta.annotation.PostConstruct
import jakarta.transaction.Transactional
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import java.util.*

const val LEN_AUTH_TOKEN: Int = 20

@Service
class GatewayService(

    private val gatewayRepository: GatewayRepository,

    @Lazy private val deviceService: DeviceService

) {
    @Transactional
    @PostConstruct
    fun clearAllSessions() {  // clear all prior sessions and devices, does cover the case if gateway does not exist properly and state is not updated in the database
        val gateways = gatewayRepository.findAll()
        for (c in gateways) {
            this.deleteDevicesOfGateway(c.id)
            this.deleteSession(c.id)
        }
    }

    fun createGateway(): Gateway {
        val authToken = this.generateAuthToken()
        val gateway = Gateway(authToken = authToken)
        return gatewayRepository.save(gateway)
    }

    fun getGatewayById(id: UUID): Gateway? {
        return gatewayRepository.findById(id).orElse(null)
    }

    fun deleteGateway(id: UUID) {
        gatewayRepository.deleteById(id)
    }

    @Transactional
    fun deleteDevicesOfGateway(gatewayId: UUID) {
        val gateway: Gateway = gatewayRepository.findById(gatewayId).orElse(null) ?: return

        gateway.activeDevices.clear()
        gateway.reachableDevices.clear()

        gatewayRepository.saveAndFlush(gateway)
    }

    @Transactional
    fun deleteSession(gatewayId: UUID) {
        val gateway: Gateway = gatewayRepository.findById(gatewayId).orElse(null) ?: return
        gateway.session = null
        gatewayRepository.saveAndFlush(gateway)
    }

    fun getFirstReachableGatewayIdByDeviceId(deviceId: NodeID): UUID? {
        return gatewayRepository.getFirstReachableGatewayByDeviceId(
            deviceId.integrationType, deviceId.persistentAttribute
        )
    }

    fun getGatewayIdByActiveDeviceId(deviceId: NodeID): UUID? {
        return gatewayRepository.findGatewayIdByActiveDeviceId(deviceId.integrationType, deviceId.persistentAttribute)
    }

    @Transactional
    fun updateGatewayDevices(newGateway: Gateway) {
        val oldGateway = getGatewayById(newGateway.id)

        deviceService.saveAllNodeIDs(newGateway.reachableDevices)

        if (oldGateway != null) {
            oldGateway.activeDevices.clear()
            oldGateway.activeDevices.addAll(newGateway.activeDevices)

            oldGateway.reachableDevices.clear()
            oldGateway.reachableDevices.addAll(newGateway.reachableDevices)

            gatewayRepository.saveAndFlush(oldGateway)
        }
    }

    fun updateGatewaySession(gateway: Gateway) {
        gatewayRepository.saveAndFlush(gateway)
    }

    fun getAllGatewayIds(): List<UUID> {
        return gatewayRepository.findAll().mapNotNull { it.id }
    }

    fun getAllGateways(): List<Gateway> {
        return gatewayRepository.findAll()
    }

    fun generateAuthToken(): String {
        while (true) {
            val authToken = SecurityService.getRandomString(LEN_AUTH_TOKEN)
            if (gatewayRepository.findByAuthToken(authToken) == null) {
                return authToken
            }
        }
    }
}
