package camera.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import mocowasp.composeapp.generated.resources.Res
import mocowasp.composeapp.generated.resources.camera_circle
import mocowasp.composeapp.generated.resources.change_camera
import mocowasp.composeapp.generated.resources.flash_off
import mocowasp.composeapp.generated.resources.flash_on
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun CameraMainView(navController: NavController, viewModel: CameraViewModel = koinViewModel()) {

    val state = rememberMocoCameraState(onCapture = viewModel::onCapture)
    val image by viewModel.imageStateBitmap.collectAsState()

    val isTorchOn = state.isTorchOn

    if (image != null) {
        SendView( viewModel = viewModel)
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            MocoCamera(
                state = state,
                modifier = Modifier.fillMaxSize()
            )
            Column(modifier = Modifier.fillMaxSize()) {
                image?.let {
                    Image(
                        bitmap = it,
                        contentDescription = "Captured Image",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Row(modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                CameraCircleContentBottomRow(
                    onCapture = {
                        viewModel.triggerCapture(state)

                    },
                    onInverseCamera = {viewModel.onInverseCamera(state)}, onToggleTorch = {state.toggleTorch()}, isTorchOn = isTorchOn )
            }

        }
    }
}




@OptIn(ExperimentalResourceApi::class)
@Composable
private fun CameraCircleContentBottomRow(onCapture: () -> Unit, onInverseCamera: () -> Unit, onToggleTorch: () -> Unit, isTorchOn:Boolean) {
        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                modifier = Modifier.padding(top = 16.dp)
                    .clickable {
                        onToggleTorch()
                    },
                painter = if (isTorchOn) painterResource(Res.drawable.flash_on) else painterResource(Res.drawable.flash_off),
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

