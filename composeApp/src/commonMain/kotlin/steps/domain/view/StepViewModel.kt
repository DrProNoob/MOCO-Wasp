package steps.domain.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import core.model.repo.UserRepository
import dev.gitlive.firebase.database.FirebaseDatabase
import feed.model.dataSource.PostDataSource
import feed.model.dtos.PostDTO
import feed.model.entity.Post
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.lighthousegames.logging.logging
import steps.domain.model.StepChallengeContent
import steps.domain.model.StepEvent
import steps.domain.model.stepModule

class StepViewModel(private val stepCounter: StepCounter, val navController: NavController,firebaseDatabase: FirebaseDatabase,val userRepository: UserRepository) : ViewModel() {
    companion object {
        val log = logging()
    }
    private var job: Job? = null

    val stepGoal = navController.currentBackStackEntry?.arguments?.getString("goal")?.toIntOrNull() ?: 0
    val postDataSource = PostDataSource(firebaseDatabase)


    private val _counterState = MutableStateFlow(stepCounter.getStepCount())
    val counterState = _counterState.asStateFlow()
    var startValueOfCountedSteps = 0

    init {
        viewModelScope.launch {
            stepCounter.startCounting()
            delay(2000)
            startValueOfCountedSteps = stepCounter.getStepCount()

        }
    }

    fun onEvent(event: StepEvent) {
        when (event) {
            is StepEvent.StartCounting -> startStepCounting(event.stepGoal)
            is StepEvent.StopCounting -> stopStepCounting()
            is StepEvent.StepsCounted -> {
                _counterState.value = stepCounter.getStepCount()
                _counterState.update { stepCounter.getStepCount() }
            }
        }
    }

    fun startStepCounting(stepGoal: Int) {
        job = viewModelScope.launch {
            stepCounter.startCounting()
            while (true) {
                _counterState.update { stepCounter.getStepCount()-startValueOfCountedSteps }
                if(stepCounter.getStepCount()-startValueOfCountedSteps >= stepGoal){
                    val content = StepChallengeContent(stepGoal)
                    val post = Post(userRepository.getOwnUser()!!.userId, userRepository.getOwnUser()!!.userName, "Step Challenge completed", "You have reached your goal of $stepGoal steps!", content)
                    val postDto = PostDTO(post.userid, post.title, post.description, post.content)
                    postDataSource.putPost(postDto, stepModule)
                    navController.navigate("feed")
                    break
                }
                delay(2000)  // Aktualisiere alle 2 Sekunden
            }
        }
    }

    fun stopStepCounting() {
        stepCounter.stopCounting()
        job?.cancel()
    }

    fun getStepCount(): Int {
        return stepCounter.getStepCount()
    }


}
