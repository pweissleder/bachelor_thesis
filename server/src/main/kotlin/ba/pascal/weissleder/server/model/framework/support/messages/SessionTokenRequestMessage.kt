package ba.pascal.weissleder.server.model.framework.support.messages

import java.util.*

data class SessionTokenRequestMessage(
    val id: UUID,
    val authToken: String
)