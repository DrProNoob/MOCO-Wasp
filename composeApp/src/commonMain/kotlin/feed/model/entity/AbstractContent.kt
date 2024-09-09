package feed.model.entity

import kotlinx.serialization.Serializable

@Serializable
abstract class AbstractContent {
    abstract val contentId: String
}

