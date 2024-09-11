package chat.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatRoom(
    val participansIds: List<Int>,
    val lastMessageId: Int?
)
