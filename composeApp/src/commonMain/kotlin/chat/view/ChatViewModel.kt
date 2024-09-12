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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.lighthousegames.logging.logging

class ChatViewModel(): ViewModel() {
    companion object {
        val log = logging()
    }
    val remoteDatabase = Firebase.database.apply {
        useEmulator("192.168.178.30",9000)
    }
    val dataRef = remoteDatabase.reference()
    val chatRepository = ChatRepository(remoteDatabase)
    //val chat = ChatRoom(listOf(1,2),1)
    val _chatRoomState = MutableStateFlow(ChatRoomState())
    val chatRoomState: StateFlow<ChatRoomState> = _chatRoomState.asStateFlow()

    init{
        viewModelScope.launch {
            _chatRoomState.update { it.copy(chatRoom = ChatRoom(2,2,3)) }
        }
    }
    var chatRoomId :String ="-O6_emmeFfd3WEQHaosf"



    private val _messages = chatRepository.getAllMessagesFromChatRoomId(chatRoomId).stateIn(viewModelScope, SharingStarted.WhileSubscribed(),emptyList())
    val messages: StateFlow<List<Message>> = _messages

    fun onEvent(event: ChatEvent){
        when(event){
            is ChatEvent.SaveMessage ->{
                saveMessage(Message( event.messageText, "",1,1L))
            }
            is ChatEvent.SaveChatRoom ->{
                saveChatRoom(event.chatRoom)
            }
            is ChatEvent.GetChatRoom ->{
                getChatRoomId(chatRoomId)
            }

        }
    }

    fun saveChatRoom(chatRoom: ChatRoom) {
        viewModelScope.launch {
            chatRoomId = chatRepository.saveChatRoom(chatRoom)
            log.i { "chatRoomId = $chatRoomId" }
        }
    }

    fun saveMessage(message: Message) {
        viewModelScope.launch {
            chatRepository.saveMessage(message)
        }
    }
    fun getChatRoomId(chatRoomId: String) {
        viewModelScope.launch{
            //chatRepository.getCurrentChatRoom("-O6WljW9dG74i-BhFaGz")
            _chatRoomState.update { it.copy(chatRoom = chatRepository.getCurrentChatRoom(chatRoomId)) }
        }
    }

}
