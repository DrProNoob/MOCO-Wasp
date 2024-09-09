package feed.model.entity

import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val id: Long,
    val userid: Long,
    val title: String,
    val description: String?,
    val contentId:AbstractContent
)
