package chat.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer

@Serializable
data class Message(
    val id: Int,
    val messageText: String,
    val chatRoomId: Int,
    val userId: Int,
    val timeSend: Long
)