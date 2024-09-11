package feed.model.entity

data class Post(
    val userid: Int,
    val userName: String,
    val title: String,
    val description: String?,
    val content: AbstractContent
)