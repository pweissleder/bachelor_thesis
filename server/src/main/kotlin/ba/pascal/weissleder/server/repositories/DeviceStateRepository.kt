package ba.pascal.weissleder.server.repositories

import ba.pascal.weissleder.server.model.framework.DeviceState
import ba.pascal.weissleder.server.model.framework.support.devices.NodeID
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.Instant
import java.util.*


//saves json version of seperated device states
interface DeviceStateRepository : JpaRepository<DeviceState, Instant> {
    fun findFirstById(nodeId: NodeID): Optional<DeviceState>
    fun findByIdAndTimestampBefore(nodeId: NodeID, timestamp: Instant): List<DeviceState>

    @Query("DELETE FROM DeviceState ds WHERE ds.id = :nodeId")
    fun deleteByNodeId(nodeId: NodeID)

}