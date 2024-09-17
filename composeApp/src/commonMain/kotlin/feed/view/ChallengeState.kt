package feed.view

import core.entity.Challenge

data class ChallengeState(
    val challenge: Challenge? = null,
    val showDialog: Boolean = false
)
