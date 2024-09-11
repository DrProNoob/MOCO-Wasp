package chat.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer

@Serializable
data class Message(
    val messageText: String,
    val chatRoomId: String,
    val userId: Int,
    val timeSend: Long
)