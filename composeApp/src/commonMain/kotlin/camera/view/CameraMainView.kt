package camera.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

import com.preat.peekaboo.ui.camera.PeekabooCamera
import com.preat.peekaboo.ui.camera.rememberPeekabooCameraState
import mocowasp.composeapp.generated.resources.Res
import mocowasp.composeapp.generated.resources.bolt
import mocowasp.composeapp.generated.resources.camera_circle
import mocowasp.composeapp.generated.resources.change_camera
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@Composable
fun CameraMainView(navController: NavController, viewModel: CameraViewModel) {

    val state = rememberMocoCameraState(onCapture = viewModel::onCapture)

    val image by viewModel.imageState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        MocoCamera(
            state = state,
            modifier = Modifier.fillMaxSize()
        )
        Row(modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
            ) {
            CameraCircleContentBottomRow(
                onCapture = {viewModel.triggerCapture(state)},
                onInverseCamera = {viewModel.onInverseCamera(state)} )
        }

    }
}



@OptIn(ExperimentalResourceApi::class)
@Composable
private fun CameraCircleContentBottomRow(onCapture: () -> Unit, onInverseCamera: () -> Unit) {
        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                modifier = Modifier.padding(top = 16.dp),
                painter = painterResource(Res.drawable.bolt),
                contentDescription = null
            )
        }
        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            CameraCircleButton(onCapture)
        }
        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                modifier = Modifier.padding(top = 16.dp)
                    .clickable {
                        onInverseCamera()
                    },
                painter = painterResource(Res.drawable.change_camera),
                contentDescription = null
            )
        }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun CameraCircleButton(onClick : () -> Unit) {
    Image(
        modifier = Modifier.clickable {
            onClick()
        }
            .padding(bottom = 26.dp),
        painter = painterResource(Res.drawable.camera_circle),
        contentDescription = null
    )
}

