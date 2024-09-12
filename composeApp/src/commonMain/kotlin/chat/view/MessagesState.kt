package chat.view

import chat.model.Message

data class MessagesState(
    val messages : List<Message> = emptyList()
)
