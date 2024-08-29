package camera.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable


@Composable
expect fun rememberMocoCameraState(
    initialCameraMode:CameraMode = CameraMode.Back,
    onFrame: ((frame: ByteArray) -> Unit)? = null,
    onCapture: (ByteArray?) -> Unit,
): MocoCameraState

@Stable
expect class MocoCameraState {
    var isCameraReady: Boolean
        internal set

    var isCapturing: Boolean
        internal set

    var cameraMode: CameraMode
        internal set

    fun toggleCamera()

    fun capture()
}