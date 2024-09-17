package core.model.repo

import core.entity.Challenge
import core.entity.ChallengeType

class ChallengeRepo(userRepository: UserRepository) {

    private val allUsers = userRepository.getAllUsersWithoutOwnUser()

    private val photoChallenges = listOf(
        Challenge("Take a photo of a tree", ChallengeType.PHOTO),
        Challenge("Take a photo of a cat", ChallengeType.PHOTO),
        Challenge("Take a photo of a dog", ChallengeType.PHOTO)
    )

    private var chatChallenge = emptyList<Challenge>()

    init {
        allUsers.forEach { user ->
            chatChallenge += Challenge("Chat with ${user.userName}", ChallengeType.CHAT)
        }
    }

    private val allChallenges = photoChallenges + chatChallenge

    fun getAllChallenges(): List<Challenge> {
        return allChallenges
    }

}