package feed.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import camera.model.entity.CameraImageContent
import camera.view.CameraButton
import core.model.repo.ChallengeEvent
import feed.model.entity.AbstractContent
import feed.model.entity.Post
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import mocowasp.composeapp.generated.resources.Res
import mocowasp.composeapp.generated.resources.shuffle
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import kotlin.random.Random

@OptIn(KoinExperimentalAPI::class, ExperimentalMaterial3Api::class)
@Composable
fun MainFeedScreen(viewModel: FeedViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val stateChallenge by viewModel.challengeState.collectAsStateWithLifecycle()
    val onChallengeEvent = viewModel::onChallengeEvent
    val navController = viewModel.navController
    val pullToRefreshState = rememberPullToRefreshState()
    val isLoading = state.isLoading
    val onRefresh = viewModel::reloadPosts


    if (stateChallenge.showDialog) {
        ChallengeDialog(stateChallenge, onChallengeEvent)
    }

    Scaffold(
        modifier = Modifier.pullToRefresh(
            state = pullToRefreshState,
            onRefresh = onRefresh,
            isRefreshing = isLoading
        ),
        floatingActionButton = { FloatingActionButtonRow(navController = navController, onChallengeEvent) },
        floatingActionButtonPosition = FabPosition.Center,
    ) { paddingValues ->
        FeedContent(paddingValues, feedList = state.posts, isLoading = isLoading, pullToRefreshState = pullToRefreshState)
    }
}

@Composable
private fun FloatingActionButtonRow(navController: NavController, onChallengeEvent: (ChallengeEvent) -> Unit) {
    Row {
        CameraButton(navController = navController)
        RandomChallengeButton(onChallengeEvent)
    }
}

@Composable
private fun RandomChallengeButton(onChallengeEvent: (ChallengeEvent) -> Unit) {
    FloatingActionButton(
        onClick = {
            onChallengeEvent(ChallengeEvent.SetChallenge)
            onChallengeEvent(ChallengeEvent.ShowDialog)
                  },
        content = { Icon(painter = painterResource(Res.drawable.shuffle), null) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeedContent(paddingValues: PaddingValues, feedList: List<Post>, isLoading: Boolean, pullToRefreshState: PullToRefreshState) {
    Column(
        modifier = Modifier.fillMaxSize().padding(paddingValues)
    ) {
        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(feedList, key = { it.contentKey }) { item: Post ->
                    key(item.title) {
                        PostCard(
                            userName = item.userName,
                            title = item.title,
                            description = item.description,
                            content = {
                                checkContent(item.content, item)
                            }
                        )
                    }
                }
            }
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                progress = {pullToRefreshState.distanceFraction}
            )
        }
    }
}

@Composable
fun ImageFeedContent(content: CameraImageContent) {
    val asyncPainter = asyncPainterResource(content.imageUrl)
    KamelImage(
        resource = asyncPainter,
        contentDescription = "Image",
        onLoading = {progress ->
            CircularProgressIndicator(
                progress = { progress },
            )
        }
    )
}

@Composable
fun checkContent(content: AbstractContent, post: Post) {
    if (content is CameraImageContent) {
        ImageFeedContent(post.content as CameraImageContent)
    }
}

@Composable
private fun PostCard(
    userName: String,
    title: String,
    description: String?,
    content:@Composable () -> Unit = {}
) {
    Card(
        modifier = Modifier.padding(16.dp).fillMaxSize(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
            ) {
                UserNameCircle(userName)
                Text(modifier = Modifier.padding(start = 27.dp), text = userName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }

            Text(text = title, style = MaterialTheme.typography.headlineMedium)
            description?.let {
                Text(text = it, style = MaterialTheme.typography.bodyLarge)
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                content()
            }

        }
    }
}
@Composable
private fun UserNameCircle(userName: String) {
    val colorState = remember { mutableStateOf(Color(Random.nextInt())) }
        Text(
            text = userName.first().toString(),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
                .drawBehind {
                    drawCircle(
                        color = colorState.value,
                        radius = this.size.maxDimension
                    )
                }
        )
}