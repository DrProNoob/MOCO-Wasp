package feed.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import core.entity.Challenge
import core.entity.ChallengeType
import core.model.repo.ChallengeEvent
import core.model.repo.ChallengeRepo
import core.model.repo.UserRepository
import dev.gitlive.firebase.database.FirebaseDatabase
import feed.model.dataSource.PostDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FeedViewModel(val userRepository: UserRepository,val navController: NavController,val challengeRepo: ChallengeRepo, firebaseDatabase: FirebaseDatabase) : ViewModel() {


    val host = "192.168.178.20"
    val port = 9199
    val local = "10.0.2.2"

    val database = firebaseDatabase
    val realtimeDatabase = database.reference()

    private val postDataSource = PostDataSource(database)

    private val _state = MutableStateFlow(FeedState())

    private val _challengeState = MutableStateFlow(ChallengeState())
    val challengeState = _challengeState.asStateFlow()

    private val _posts = postDataSource.getAllPosts()

    fun reloadPosts() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val posts = postDataSource.getAllPosts().first()
            _state.value = _state.value.copy(posts = posts, isLoading = false)
        }
    }



    fun onChallengeEvent(event: ChallengeEvent) {
        when (event) {
            ChallengeEvent.HideDialog -> {
                _challengeState.update { it.copy(showDialog = false) }
            }
            ChallengeEvent.SetChallenge ->
                setRandomChallenge()

            ChallengeEvent.ShowDialog -> {
                    _challengeState.update { it.copy(showDialog = true) }
            }
            is ChallengeEvent.StartChallenge -> {
                startChallenge(navController = navController, challengeType = event.challenge.challengeType, challenge = event.challenge, userRepository = userRepository )
            }
        }
    }

    private fun startChallenge(navController: NavController,challengeType: ChallengeType, challenge: Challenge, userRepository: UserRepository) {
        when (challengeType) {
            ChallengeType.CHAT -> startChatChallenge(navController, challenge, userRepository)
            ChallengeType.PHOTO -> navController.navigate("camera")
        }
    }

    private fun startChatChallenge(navController: NavController, challenge: Challenge, userRepository: UserRepository) {
        val remoteUser = challenge.challengeAction.replace("Chat with ", "")
        userRepository.setRemoteUser(remoteUser)
        navController.navigate("chat")
    }




    private fun setRandomChallenge() {
        val allChallenges = challengeRepo.getAllChallenges()
        val randomChallenge = allChallenges.random()
        viewModelScope.launch {
            _challengeState.update { it.copy(challenge = randomChallenge) }
        }
    }



    val state = combine(_state, _posts ) { state, posts ->
        state.copy(
            posts = posts
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), FeedState())


}