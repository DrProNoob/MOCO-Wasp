package chat.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import chat.model.ChatEvent
import chat.model.ChatRepository
import chat.model.ChatRoom
import chat.model.Message
import core.entity.User
import core.model.repo.UserRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.database.FirebaseDatabase
import dev.gitlive.firebase.database.database
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.lighthousegames.logging.logging
import kotlin.random.Random

class ChatViewModel(userRepository: UserRepository,remoteDatabase : FirebaseDatabase): ViewModel() {
    companion object {
        val log = logging()
    }
    val host = "192.168.178.30"
    val local = "10.0.2.2"
    val host2 = "10.3.227.20"
    val host3 = "0.0.0.0"
    val dataRef = remoteDatabase.reference()
    val userRepository = UserRepository(remoteDatabase)
    val chatRepository = ChatRepository(remoteDatabase)

    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()
    //val user:User = userRepository.getOwnUser()
    //val chat = ChatRoom(listOf(1,2),1)
    val _chatRoomState = MutableStateFlow(ChatRoomState())
    val chatRoomState: StateFlow<ChatRoomState> = _chatRoomState.asStateFlow()

    private val _messagesState = MutableStateFlow(MessagesState())
    val messagesState: StateFlow<MessagesState> = _messagesState.asStateFlow()

    var chatRoomId :String = ""
    init {
        viewModelScope.launch {
/*            //chatRepository.setupUsers()
           // chatRepository.setupChatRoom()
            val user = userRepository.getOwnUser()
            _user.update {user}
            _chatRoomState.update { it.copy(chatRoom = ChatRoom(2,2,3)) }
            chatRepository.getAllMessagesFromChatRoomId(chatRoomId).collect{ messageList ->
                _messagesState.update { it.copy(messages = messageList) }*/
            val user = userRepository.getOwnUser()
            _user.update { user }
            val chatRoom = user?.let { ChatRoom(it.userId, 0, 0) }
            saveChatRoom(chatRoom!!)
            chatRepository.getAllMessagesFromChatRoomId(chatRoomId).collect { messageList ->
                _messagesState.update { it.copy(messages = messageList) }
            }
        }
    }




    fun onEvent(event: ChatEvent){
        when(event){
            is ChatEvent.SaveMessage ->{
                saveMessage(Message( event.messageText, chatRoomId, user.value!!.userId,1L))
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
            _chatRoomState.update { it.copy(chatRoom = chatRepository.getCurrentChatRoom(chatRoomId)) }
        }
    }

}
