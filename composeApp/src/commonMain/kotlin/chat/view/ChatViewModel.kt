package chat.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import chat.model.ChatEvent
import chat.model.ChatRoom
import chat.model.Message
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.database.database
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel( ): ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    val remoteDatabase = Firebase.database.apply {
        useEmulator("192.168.178.35",9000)
    }
    val dataRef = remoteDatabase.reference()
    val message= Message(1,"l",1,1,0L)
    fun onEvent(event: ChatEvent){
        when(event){
            ChatEvent.SaveMessage ->{

            }
        }
    }
    fun saveChatRoom(chatRoom: ChatRoom) {
        viewModelScope.launch {
            dataRef.child("chatRooms").child(chatRoom.chatRoomId.toString()).setValue(chatRoom)
        }
    }

    fun saveMessage(message: Message) {
        viewModelScope.launch {
            dataRef.child("messages").child(message.id.toString()).setValue(message)
        }
    }

}
