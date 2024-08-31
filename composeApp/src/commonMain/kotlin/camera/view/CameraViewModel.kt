package camera.view

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import camera.Util.byteToData
import camera.Util.compressImage
import camera.Util.toImageBitmap
import camera.view.events.CameraEvent
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class CameraViewModel ():ViewModel() {

    val host = "192.168.178.20"
    val port = 9199
    val local = "10.0.2.2"

    val imageDbStorage = Firebase.storage.apply {
        useEmulator(host = host, port = 9199)
    }
    val storeRef = imageDbStorage.reference
    val imageRef = storeRef.child("images")
    val uploadRef = imageRef.child("${Random.nextLong()}.jpg")





    private val _imageStateBitmap = MutableStateFlow<ImageBitmap?>(null)
    val imageStateBitmap: StateFlow<ImageBitmap?> = _imageStateBitmap
    private val _imageStateByteArray = MutableStateFlow<ByteArray?>(null)
    val imageStateByteArray: StateFlow<ByteArray?> = _imageStateByteArray

    fun onCapture(image: ByteArray?) {
        viewModelScope.launch {
            image?.let {
                _imageStateBitmap.value = it.toImageBitmap()
                _imageStateByteArray.value = it

            }
        }
    }

    fun resetImage() {
        _imageStateBitmap.value = null
        _imageStateByteArray.value = null
    }



   private fun uploadImage(image: ByteArray) {
        viewModelScope.launch {
            val cImage = compressImage(image, 50)
            val data = byteToData(cImage)
            uploadRef.putData(data)
        }
    }

    fun handleCameraEvent(event:CameraEvent) {
        when (event) {
            CameraEvent.UploadImage -> {
                uploadImage(imageStateByteArray.value!!)
            }
        }
    }



    fun triggerCapture(state: MocoCameraState) {
        state.capture()
    }

    fun onInverseCamera(state: MocoCameraState) {
        state.toggleCamera()
    }

}
