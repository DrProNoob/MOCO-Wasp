package core.model.repo

import core.entity.Challenge
import core.entity.ChallengeType

class ChallengeRepo(val userRepository: UserRepository) {
    private val photoChallenges = listOf(
        Challenge("Take a photo of a tree", ChallengeType.PHOTO),
        Challenge("Take a photo of a cat", ChallengeType.PHOTO),
        Challenge("Take a photo of a dog", ChallengeType.PHOTO)
    )

    private var chatChallenge = emptyList<Challenge>()




    fun getAllChallenges(): List<Challenge> {
        val allUsers = userRepository.getAllUsersWithoutOwnUser()
        allUsers.forEach { user ->
            chatChallenge += Challenge("Chat with ${user.userName}", ChallengeType.CHAT)
        }
        val allChallenges = photoChallenges + chatChallenge
            return allChallenges
    }

}