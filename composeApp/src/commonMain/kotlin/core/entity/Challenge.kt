package core.entity


data class Challenge(
    val challengeAction: String,
    val challengeType: ChallengeType
)

enum class ChallengeType {
    CHAT,
    PHOTO,
    STEPS
}
