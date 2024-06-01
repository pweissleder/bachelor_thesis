package ba.pascal.weissleder.server.repositories


import ba.pascal.weissleder.server.model.framework.Gateway
import ba.pascal.weissleder.server.model.framework.support.devices.IntegrationType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface GatewayRepository : JpaRepository<Gateway, UUID> {
    fun findByAuthToken(authToken: String): Gateway?

    @Query("SELECT c.id FROM Gateway c JOIN c.reachableDevices d WHERE d.integrationType = :integrationType AND  d.persistentAttribute = :persistentAttribute")
    fun getFirstReachableGatewayByDeviceId(integrationType: IntegrationType, persistentAttribute: String): UUID?

    @Query("SELECT c.id FROM Gateway c JOIN c.activeDevices d WHERE d.integrationType = :integrationType AND  d.persistentAttribute = :persistentAttribute")
    fun findGatewayIdByActiveDeviceId(integrationType: IntegrationType, persistentAttribute: String): UUID?

}