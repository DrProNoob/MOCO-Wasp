package camera.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import camera.view.events.CameraEvent
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun SendView(viewModel: CameraViewModel = koinViewModel()) {
    val image by viewModel.imageStateBitmap.collectAsStateWithLifecycle()
    val uploadImageEvent = viewModel::handleCameraEvent
    val resetImage = viewModel::resetImage

    Column(
        modifier = Modifier.fillMaxSize()
            .background(color = Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        image?.let { SendContent( it, onUploadImage = uploadImageEvent, onResetImage = resetImage) }
    }


}

@Composable
private fun SendContent(sendImage: ImageBitmap, onUploadImage : (CameraEvent) -> Unit, onResetImage: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(bitmap = sendImage, contentDescription = "Image to send")
        Spacer(modifier = Modifier.padding(top = 24.dp))
        Button(
            modifier = Modifier.size(width = 200.dp, height = 50.dp),
            onClick = {onUploadImage(CameraEvent.UploadImage)},
            colors = ButtonColors(
                containerColor = Color.White,
                contentColor = Color.Black,
                disabledContentColor = Color.Black,
                disabledContainerColor = Color.White
            )
        ) {
            Text("Send", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.padding(start = 8.dp))
            Icon(Icons.AutoMirrored.Outlined.Send, "Send")
        }
        Spacer(modifier = Modifier.padding(top = 15.dp))
        OutlinedButton(
            modifier = Modifier.size(width = 200.dp, height = 50.dp),
            onClick = onResetImage,
            colors = ButtonColors(
                containerColor = Color.White,
                contentColor = Color.Black,
                disabledContentColor = Color.Black,
                disabledContainerColor = Color.White
            )
        ) {
            Text("Go Back", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
    }
}