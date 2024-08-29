package camera.view.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import mocowasp.composeapp.generated.resources.Res
import mocowasp.composeapp.generated.resources.bolt
import mocowasp.composeapp.generated.resources.camera_circle
import mocowasp.composeapp.generated.resources.change_camera
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun CameraCircle(navController: NavHostController) {


}



@OptIn(ExperimentalResourceApi::class)
@Composable
private fun CameraCircleContentBottomRow() {
    Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            modifier = Modifier.padding(top = 16.dp),
            painter = painterResource(Res.drawable.bolt),
            contentDescription = null
        )
    }
    Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        CameraCircleButton()
    }
    Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            modifier = Modifier.padding(top = 16.dp),
            painter = painterResource(Res.drawable.change_camera),
            contentDescription = null
        )
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun CameraCircleButton() {
    Image(
        modifier = Modifier.clickable {
            TODO()
        }
            .padding(bottom = 26.dp),
        painter = painterResource(Res.drawable.camera_circle),
        contentDescription = null
    )
}



