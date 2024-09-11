package camera.view

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import camera.Util.byteToData
import camera.Util.compressImage
import camera.Util.toImageBitmap
import camera.model.entity.CameraImageContent
import camera.model.entity.UserPicure
import camera.model.entity.imageModule
import camera.view.events.CameraEvent
import camera.view.events.CameraPostEvent
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.database.FirebaseDatabase
import dev.gitlive.firebase.database.ServerValue
import dev.gitlive.firebase.storage.FirebaseStorage
import dev.gitlive.firebase.storage.StorageReference
import dev.gitlive.firebase.storage.storage
import feed.model.dataSource.PostDataSource

import feed.model.dtos.PostDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class CameraViewModel (imageDbStorage: FirebaseStorage,firebaseDatabase: FirebaseDatabase):ViewModel() {

    val host = "192.168.178.20"
    val port = 9199
    val local = "10.0.2.2"

    val userImageDatabase = firebaseDatabase
    val realtimeDatabase = userImageDatabase.reference()



    val storeRef = imageDbStorage.reference
    val imageRef = storeRef.child("images")
    val uploadRef = imageRef.child("${Random.nextLong()}.jpg")

    val postDataSource = PostDataSource(userImageDatabase)





    private val _cameraPostState = MutableStateFlow(CameraPostState())
    val cameraPostState: StateFlow<CameraPostState> = _cameraPostState
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



  private suspend fun uploadImage(image: ByteArray): String {
      val cImage = compressImage(image, 50)
      val data = byteToData(cImage)
      uploadRef.putData(data)
      return uploadRef.getDownloadUrl()
  }





    fun handleCameraPostEvent(event: CameraPostEvent) {
        when (event) {
            CameraPostEvent.SavePost -> {
                viewModelScope.launch {
                    if (cameraPostState.value.isLoading) return@launch
                    _cameraPostState.update {
                        it.copy(isLoading = true)
                    }
                    val title = _cameraPostState.value.title
                    val description = _cameraPostState.value.description

                    if (description != null) {
                        if (title.isBlank() or description.isBlank()) {
                            _cameraPostState.update {
                                it.copy(isLoading = false)
                            }
                            return@launch
                        }
                    }
                    val url = uploadImage(imageStateByteArray.value!!)
                    val post = PostDTO(userid = 1, title = title, description = description, content = CameraImageContent(imageUrl = url))
                    postDataSource.putPost(post, imageModule)
                    _cameraPostState.update {
                        it.copy(isLoading = false)
                    }

                }
            }
            is CameraPostEvent.SetDescription -> {
                _cameraPostState.update {
                    it.copy(description = event.description)
                }
            }
            is CameraPostEvent.SetTitle -> {
                _cameraPostState.update {
                    it.copy(title = event.title)
                }
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
