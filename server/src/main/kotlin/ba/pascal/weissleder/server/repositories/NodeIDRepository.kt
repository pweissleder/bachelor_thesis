package ba.pascal.weissleder.server.repositories

import ba.pascal.weissleder.server.model.framework.support.devices.NodeID
import org.springframework.data.jpa.repository.JpaRepository

interface NodeIDRepository : JpaRepository<NodeID, NodeID> {}