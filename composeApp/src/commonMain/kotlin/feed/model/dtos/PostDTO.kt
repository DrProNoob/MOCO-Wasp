package feed.model.dtos

import feed.model.entity.AbstractContent
import kotlinx.serialization.Serializable


@Serializable
data class PostDTO(
    val userid: Int,
    val title: String,
    val description: String?,
    val content: AbstractContent
)
