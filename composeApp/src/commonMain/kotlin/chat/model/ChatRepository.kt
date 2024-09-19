package chat.model


import core.entity.User
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
    suspend fun setupUsers(){
        dbRef.child("users").child("daniel").setValue(User(1,"Daniel"))
        dbRef.child("users").child("leon").setValue(User(2,"Leon"))
        dbRef.child("users").child("ramon").setValue(User(3,"Ramon"))
    }

    fun getAllMessagesFromChatRoomId(chatRoomId : String): Flow<List<Message>> {
        val result = dbRef.child("chatRooms").child(chatRoomId)
            .child("messages").valueEvents.mapNotNull{
                    dataSnapshot->
                if(dataSnapshot.exists){
                    log.i { "DataSnapshot1 = ${dataSnapshot.value}" }
                    dataSnapshot.children.map {
                        log.i { "DataSnapshot 3 = ${it.value}" }
                        it.value(Message.serializer()) }
                }else{
                    log.i { "No messages found" }
                    emptyList<Message>()
                }
            }
        return result
    }
    suspend fun getChatRoomByUserId(remoteUserId: Int, ownUserId :Int):ChatRoom?{
        val result = dbRef.child("chatRooms").valueEvents.mapNotNull { dataSnapshot ->
            dataSnapshot.children.map { chatRoom ->
                chatRoom.value(ChatRoom.serializer())
            }
        }.first().find { chatRoom ->
            chatRoom.ownUser == remoteUserId && chatRoom.remoteUser == ownUserId
        }
        log.i { "ChatRoom = $result" }
        return result
    }

//    fun getCurrentChatRoomId1(chatRoom: ChatRoom):String?{
//        var chatRoomId: String? = ""
//        val path = dbRef.child("chatRooms").valueEvents.mapNotNull { dataSnapshot ->
//            dataSnapshot.children.map { cchatRoom ->
//                val fetchedChatRoom = cchatRoom.value(ChatRoom.serializer())
//                if (fetchedChatRoom == chatRoom) {
//                    chatRoomId = cchatRoom.key
//                }
//            }
//        }
//        return chatRoomId
//    }

    suspend fun getCurrentChatRoomId(chatRoom: ChatRoom): String? {
        return dbRef.child("chatRooms").valueEvents.mapNotNull { dataSnapshot ->
            dataSnapshot.children.find { child ->
                val fetchedChatRoom = child.value(ChatRoom.serializer())
                fetchedChatRoom == chatRoom
            }?.key
        }.first() // Collect the first matching result
    }
}