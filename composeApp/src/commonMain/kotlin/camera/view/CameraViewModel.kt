package camera.view

import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import camera.Util.toImageBitmap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CameraViewModel ():ViewModel() {

    private val _imageState = MutableStateFlow<ImageBitmap?>(null)
    val imageState: StateFlow<ImageBitmap?> = _imageState

    fun onCapture(image: ByteArray?) {
        image?.let {
            _imageState.value = it.toImageBitmap()
        }
    }

    fun triggerCapture(state: MocoCameraState) {
        state.capture()
    }

    fun onInverseCamera(state: MocoCameraState) {
        state.toggleCamera()
    }

}