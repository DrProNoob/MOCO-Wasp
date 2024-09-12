package chat.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatRoom(
    val chatRoomId: Int,
    val ownUser: Int,
    val remoteUser: Int,
    val lastMessageId: Int?
)
