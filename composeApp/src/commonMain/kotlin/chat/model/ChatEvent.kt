package chat.model

sealed interface ChatEvent {
    object SaveMessage:ChatEvent
}