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
        dbRef.child("chatRooms").child(message.chatRoomId).child("messages").push().setValue(message)
    }
    suspend fun saveChatRoom(chatRoom: ChatRoom) :String? {
        val chatRef = dbRef.child("chatRooms").push()
        val id = chatRef.key
        chatRef.setValue(chatRoom)
        return id
    }

    fun getAllMessagesFromChatRoomId(chatRoomId : String): Flow<List<Message>> {
        return dbRef.child("chatRooms").child(chatRoomId).child("messages").valueEvents.map {
            dataSnapshot ->
            dataSnapshot.children.mapNotNull { it.toMessage() }
        }.scan(emptyList()) { acc, messages -> acc + messages }
    }
    fun getCurrentChatRoom(userId:Int):Flow<String>{
        return dbRef.child("chatRooms").child(userId.toString()).valueEvents.map{
                dataSnapshot ->
            dataSnapshot.value as String
        }
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