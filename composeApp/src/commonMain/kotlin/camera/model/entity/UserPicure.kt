package camera.model.entity

import dev.gitlive.firebase.database.ServerValue
import dev.gitlive.firebase.firestore.FieldValue
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class UserPicure(
    val id: Long,
    val imageUrl: String,
    val postDate: ServerValue = ServerValue.TIMESTAMP,
)