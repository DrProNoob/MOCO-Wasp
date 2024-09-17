package feed.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import camera.model.entity.CameraImageContent
import camera.view.CameraButton
import feed.model.entity.AbstractContent
import feed.model.entity.Post
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import kotlin.random.Random

@OptIn(KoinExperimentalAPI::class)
@Composable
fun MainFeedScreen(navController: NavController,viewModel: FeedViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = { CameraButton(navController = navController) },
        floatingActionButtonPosition = FabPosition.Center,
    ) { paddingValues ->
        FeedContent(paddingValues, feedList = state.posts )
    }
}

@Composable
private fun FeedContent(paddingValues: PaddingValues, feedList: List<Post>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(paddingValues),
    ) {
        items(feedList, key = {it.contentKey}) { item: Post ->
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