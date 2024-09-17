package feed.model.entity

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class Post @OptIn(ExperimentalUuidApi::class) constructor(
    val userid: Int,
    val userName: String,
    val title: String,
    val description: String?,
    val content: AbstractContent,
    val contentKey:String = Uuid.random().toString()
)