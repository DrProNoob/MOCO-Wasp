package chat.model

sealed interface ChatEvent {
    data class SaveMessage(val messageText:String):ChatEvent
    data class SaveChatRoom(val chatRoom: ChatRoom):ChatEvent
}