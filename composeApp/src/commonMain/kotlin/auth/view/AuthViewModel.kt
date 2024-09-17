package auth.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import core.model.repo.UserRepository
import dev.gitlive.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(userRepository: UserRepository) : ViewModel() {


    private val _state = MutableStateFlow(AuthState())
    val state = _state.asStateFlow()
    private val repository = userRepository

    fun handleEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.SetOwnUser -> {
                val userName = state.value.username
                viewModelScope.launch {
                    val ownUser = repository.setOwnUser(userName)
                    _state.update {
                        it.copy(ownUser = ownUser)
                    }
                }
            }
            is AuthEvent.SetUsername -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(username = event.username)
                    }
                    repository.getAllUsers()
                }

            }
        }
    }


}
