package chat.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import chat.model.ChatEvent
import chat.model.ChatRepository
import chat.model.ChatRoom
import chat.model.Message
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.database.database
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.modules.serializersModuleOf

class ChatViewModel( ): ViewModel() {
    val remoteDatabase = Firebase.database.apply {
        useEmulator("192.168.178.35",9000)
    }
    val dataRef = remoteDatabase.reference()
    val chatRepository = ChatRepository(remoteDatabase)

    private val _messages = chatRepository.getAllMessages().stateIn(viewModelScope, SharingStarted.WhileSubscribed(),emptyList())
    val messages: StateFlow<List<Message>> = _messages

    fun onEvent(event: ChatEvent){
        when(event){
            is ChatEvent.SaveMessage ->{
                saveMessage(Message(1, event.messageText, 1,1,1L))
            }
            is ChatEvent.SaveChatRoom ->{
                saveChatRoom(event.chatRoom)
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
            chatRepository.saveMessage(message)
        }
    }

}
