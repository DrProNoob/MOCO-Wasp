package camera.model.entity

import dev.gitlive.firebase.database.ServerValue
import kotlinx.serialization.Serializable

@Serializable
data class UserPicure(
    val userId: Long,
    val imageUrl: String,
    val postDate: ServerValue = ServerValue.TIMESTAMP,
)