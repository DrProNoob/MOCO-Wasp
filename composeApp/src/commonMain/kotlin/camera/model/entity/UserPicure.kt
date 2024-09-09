package camera.model.entity

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class UserPicure(
    val id: Long,
    val imageUrl: String
)
