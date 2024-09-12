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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.lighthousegames.logging.logging
import kotlin.random.Random

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

    private val _messagesState = MutableStateFlow(MessagesState())
    val messagesState: StateFlow<MessagesState> = _messagesState.asStateFlow()

    var chatRoomId :String ="-O6aEzHANfBWe3ZkFvQc"
    init{
        viewModelScope.launch {
            _chatRoomState.update { it.copy(chatRoom = ChatRoom(2,2,3)) }
            _messagesState.update { it.copy(messages = chatRepository.getAllMessagesFromChatRoomId(chatRoomId).first()) }
        }
    }



    //private val _messages = chatRepository.getAllMessagesFromChatRoomId(chatRoomId).stateIn(viewModelScope, SharingStarted.WhileSubscribed(),emptyList())
    //val messages: StateFlow<List<Message>> = _messages

    fun onEvent(event: ChatEvent){
        when(event){
            is ChatEvent.SaveMessage ->{
                saveMessage(Message( event.messageText, chatRoomId, Random.nextInt(1,3),1L))
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
            _messagesState.update {
                it.copy(messages = chatRepository.getAllMessagesFromChatRoomId(chatRoomId).first())
            }
        }
    }
    fun getChatRoomId(chatRoomId: String) {
        viewModelScope.launch{
            //chatRepository.getCurrentChatRoom("-O6WljW9dG74i-BhFaGz")
            _chatRoomState.update { it.copy(chatRoom = chatRepository.getCurrentChatRoom(chatRoomId)) }
        }
    }

}
