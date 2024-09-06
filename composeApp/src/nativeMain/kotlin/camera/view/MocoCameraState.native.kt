package camera.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue



@Composable
actual fun rememberMocoCameraState(
    initialCameraMode: CameraMode,
    onFrame: ((frame: ByteArray) -> Unit)?,
    onCapture: (ByteArray?) -> Unit
): MocoCameraState {
    return rememberSaveable(
        saver = MocoCameraState.saver(onFrame, onCapture),
    ) { MocoCameraState(initialCameraMode, onFrame, onCapture) }.apply {
        this.onFrame = onFrame
        this.onCapture = onCapture
    }
}

@Stable
actual class MocoCameraState(
    cameraMode: CameraMode,
    internal var onFrame: ((frame: ByteArray) -> Unit)?,
    internal var onCapture: (ByteArray?) -> Unit,
) {
    actual var isCameraReady: Boolean by mutableStateOf(false)

    internal var triggerCaptureAnchor: (() -> Unit)? = null

    actual var isCapturing: Boolean by mutableStateOf(false)

    actual var cameraMode: CameraMode by mutableStateOf(cameraMode)

    actual fun toggleCamera() {
        cameraMode = cameraMode.inverse()
    }

    actual fun capture() {
        isCapturing = true
        triggerCaptureAnchor?.invoke()
    }

    internal fun stopCapturing() {
        isCapturing = false
    }

    internal fun onCapture(image: ByteArray?) {
        onCapture.invoke(image)
    }

    fun onCameraReady() {
        isCameraReady = true
    }

    companion object {
        fun saver(
            onFrame: ((frame: ByteArray) -> Unit)?,
            onCapture: (ByteArray?) -> Unit,
        ): Saver<MocoCameraState, Int> {
            return Saver(
                save = {
                    it.cameraMode.id()
                },
                restore = {
                    MocoCameraState(
                        cameraMode = cameraModeFromId(it),
                        onFrame = onFrame,
                        onCapture = onCapture,
                    )
                },
            )
        }
    }

    actual var isTorchOn: Boolean
        get() = TODO("Not yet implemented")
        set(value) {}

    actual fun toggleTorch() {
    }
}