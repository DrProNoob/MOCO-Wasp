package feed.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.gitlive.firebase.database.FirebaseDatabase
import feed.model.dataSource.PostDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class FeedViewModel(firebaseDatabase: FirebaseDatabase) : ViewModel() {

    val host = "192.168.178.20"
    val port = 9199
    val local = "10.0.2.2"

    val database = firebaseDatabase
    val realtimeDatabase = database.reference()

    private val postDataSource = PostDataSource(database)

    private val _state = MutableStateFlow(FeedState())

    private val _posts = postDataSource.getAllPosts()

/*    private val _posts = flow {
        emit(postDataSource.getAllPosts())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())*/



    val state = combine(_state, _posts ) { state, posts ->
        state.copy(
            posts = posts
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), FeedState())


}