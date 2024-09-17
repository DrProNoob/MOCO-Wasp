package chat.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatRoom(
    val ownUser: Int,
    val remoteUser: Int,
    val lastMessageId: Int?
)
