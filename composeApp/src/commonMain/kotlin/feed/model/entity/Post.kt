package feed.model.entity

import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val userid: Long,
    val title: String,
    val description: String?,
    val contentId:AbstractContent
)
