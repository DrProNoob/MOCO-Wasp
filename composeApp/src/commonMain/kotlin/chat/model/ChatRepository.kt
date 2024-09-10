package chat.model

import dev.gitlive.firebase.database.DataSnapshot
import dev.gitlive.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

class ChatRepository(
    private val firebaseDatabase: FirebaseDatabase
) {

    private val dbRef = firebaseDatabase.reference()

    suspend fun saveMessage(message: Message) {
        val messageRef = dbRef.child("messages").child(message.chatRoomId.toString()).push()
        messageRef.setValue(message)
    }
    suspend fun saveChatRoom(chatRoom: ChatRoom) {
        dbRef.child("chatRooms").push().setValue(chatRoom)
    }

    fun getAllMessages(): Flow<List<Message>> {
        return dbRef.child("messages").valueEvents.map { dataSnapshot ->
            dataSnapshot.children.mapNotNull { it.toMessage() }
        }.scan(emptyList()) { acc, messages -> acc + messages }
    }

    private fun DataSnapshot.toMessage(): Message? {
        return try {
            val messageJson = value as String
            Json.decodeFromString(Message.serializer(), messageJson)
        } catch (e: Exception) {
            null
        }
    }

}