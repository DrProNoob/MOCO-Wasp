package camera.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import com.preat.peekaboo.ui.camera.PeekabooCameraState

@Composable
expect fun MocoCamera(
    modifier: Modifier,
    cameraMode: CameraMode = CameraMode.Back,
    captureIcon: @Composable (onClick: () -> Unit) -> Unit,
    convertIcon: @Composable (onClick: () -> Unit) -> Unit = {},
    progressIndicator: @Composable () -> Unit = {},
    onCapture: (byteArray: ByteArray?) -> Unit,
    onFrame: ((frame: ByteArray) -> Unit)? = null
)

@Composable
expect fun MocoCamera(
    state: MocoCameraState,
    modifier: Modifier
)