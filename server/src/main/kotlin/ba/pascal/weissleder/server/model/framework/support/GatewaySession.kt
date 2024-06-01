package ba.pascal.weissleder.server.model.framework.support

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@Entity
data class GatewaySession(  // TODO: Rename to GatewaySessionToken
    @Id val gatewayId: UUID = UUID.randomUUID(),
    @Column val sessionToken: String = "NotSet",
    @Column val expireDate: Date = Date.from(
        Instant.now().plus(1, ChronoUnit.HOURS)
    )  // Could also be  set to invalid date
) {}