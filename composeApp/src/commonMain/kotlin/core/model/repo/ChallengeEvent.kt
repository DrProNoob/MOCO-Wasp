package core.model.repo

import core.entity.Challenge

sealed interface ChallengeEvent {
    object ShowDialog : ChallengeEvent
    object HideDialog : ChallengeEvent
    data class StartChallenge(val challenge: Challenge) : ChallengeEvent
    object SetChallenge: ChallengeEvent

}