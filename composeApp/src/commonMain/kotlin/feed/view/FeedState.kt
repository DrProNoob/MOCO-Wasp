package feed.view

import feed.model.entity.Post

data class FeedState(
    val posts: List<Post> = emptyList(),
    val hasChallengeOfTheDay:Boolean = false,
)
