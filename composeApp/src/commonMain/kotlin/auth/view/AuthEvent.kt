package auth.view

interface AuthEvent {
    object SetOwnUser : AuthEvent
    data class SetUsername(val username: String) : AuthEvent
}