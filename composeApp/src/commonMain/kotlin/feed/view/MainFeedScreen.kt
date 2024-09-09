package feed.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
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
        items(feedList) { item: Post ->
            key(item.title) {
                PostCard(
                    title = item.title,
                    description = item.description,
                    content = { checkContent(item.contentId, item) }
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
    )
}

@Composable
fun checkContent(content: AbstractContent, post: Post) {
    if (content is CameraImageContent) {
        return ImageFeedContent(post.contentId as CameraImageContent)
    }
}

@Composable
private fun PostCard(
    title: String,
    description: String?,
    content:@Composable () -> Unit = {}
) {
    Card(
        modifier = Modifier.padding(16.dp).fillMaxSize(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.headlineMedium)
            description?.let {
                Text(text = it, style = MaterialTheme.typography.bodyLarge)
            }
            content()
        }
    }
}