package core.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val userId :String,
    val userName: String,
    val profilPicturePath: String?
)
