package auth.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import chat.model.ChatRepository
import core.entity.User
import core.model.repo.UserRepository
import dev.gitlive.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(val navController: NavController,userRepository: UserRepository) : ViewModel() {


    private val _state = MutableStateFlow(AuthState())
    val state = _state.asStateFlow()
    private val repository = userRepository
    private var ownUser = User(0,"")

    fun handleEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.SetOwnUser -> {
                val userName = state.value.username
                viewModelScope.launch {
                    ownUser = repository.setOwnUser(userName)
                    repository.getAllUsers()
                    }
                    _state.update {
                        it.copy(ownUser = ownUser)
                }
            }
            is AuthEvent.SetUsername -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(username = event.username)
                    }
                }
            }
        }
    }


}
