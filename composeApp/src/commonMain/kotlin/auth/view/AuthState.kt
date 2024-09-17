package auth.view

import core.entity.User

data class AuthState(
    val ownUser: User? = null,
    val username: String = ""
)
