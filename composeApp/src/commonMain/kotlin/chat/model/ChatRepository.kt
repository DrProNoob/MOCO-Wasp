package chat.model


import dev.gitlive.firebase.database.DataSnapshot
import dev.gitlive.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.toList
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import mocowasp.composeapp.generated.resources.Res
import org.lighthousegames.logging.logging

class ChatRepository(
      firebaseDatabase: FirebaseDatabase
) {
    companion object {
        val log = logging()
    }
    private val dbRef = firebaseDatabase.reference()

    suspend fun saveMessage(message: Message) {
        dbRef.child("chatRooms").child(message.chatRoomId).child("messages").push().setValue(message)
    }
    suspend fun saveChatRoom(chatRoom: ChatRoom) :String {
        val chatRef = dbRef.child("chatRooms").push()
        val id = chatRef.key
        chatRef.setValue(chatRoom)
        if(id== null) {
            return ""
        }else return id
    }
    suspend fun setupChatRoom(){
        dbRef.child("chatRooms").child("-O6u0bvgT2ByGFleJIch").setValue(ChatRoom(1,2,2))
    }

    fun getAllMessagesFromChatRoomId(chatRoomId : String): Flow<List<Message>> {
        val result = dbRef.child("chatRooms").child(chatRoomId)
            .child("messages").valueEvents.mapNotNull{
                    dataSnapshot->
                log.i { "DataSnapshot1 = ${dataSnapshot.value}" }
                dataSnapshot.children.map {
                    log.i { "DataSnapshot 3 = ${it.value}" }
                    it.value(Message.serializer()) }
            }
        return result
    }
    suspend fun getCurrentChatRoom(chatRoomId: String):ChatRoom?{
        val path = dbRef.child("chatRooms").child(chatRoomId).key
        log.i { "path = $path" }
        val result = dbRef.child("chatRooms").child(chatRoomId)
            .valueEvents
            .onEach { dataSnapshot ->
                log.i { "DataSnapshot = ${dataSnapshot.value}" }
            }
            .mapNotNull { dataSnapshot ->
                dataSnapshot.value(ChatRoom.serializer())
            }
            .firstOrNull()
        log.i { "teilErgebnis = $result" }

        return result
    }

}