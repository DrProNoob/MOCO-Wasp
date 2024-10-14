package camera.view

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import camera.Util.byteToData
import camera.Util.compressImage
import camera.Util.toImageBitmap
import camera.model.entity.CameraImageContent
import camera.model.entity.UserPicure
import camera.model.entity.imageModule
import camera.view.events.CameraEvent
import camera.view.events.CameraPostEvent
import core.model.repo.UserRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.database.FirebaseDatabase
import dev.gitlive.firebase.database.ServerValue
import dev.gitlive.firebase.storage.FirebaseStorage
import dev.gitlive.firebase.storage.StorageReference
import dev.gitlive.firebase.storage.storage
import feed.model.dataSource.PostDataSource

import feed.model.dtos.PostDTO
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.lighthousegames.logging.logging
import kotlin.coroutines.cancellation.CancellationException
import kotlin.random.Random
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class CameraViewModel (val userRepository: UserRepository,
                       val navController: NavController,
                       imageDbStorage: FirebaseStorage,
                       firebaseDatabase: FirebaseDatabase):ViewModel() {


    val log = logging()

    val userImageDatabase = firebaseDatabase
    val realtimeDatabase = userImageDatabase.reference()



    val storeRef = imageDbStorage.reference
    val imageRef = storeRef.child("images")
    @OptIn(ExperimentalUuidApi::class)
    val uploadRef = imageRef.child("${Uuid.random()}.jpg")

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

                    _cameraPostState.update { it.copy(isLoading = true) }

                    val title = _cameraPostState.value.title
                    val description = _cameraPostState.value.description

                    // Check if both title and description are not null and not blank
                    if (title.isNullOrBlank() || description.isNullOrBlank()) {
                        _cameraPostState.update { it.copy(isLoading = false) }
                        return@launch
                    }

                    // Check if image is available before trying to upload
                    val imageByteArray = imageStateByteArray.value
                    if (imageByteArray == null) {
                        log.e { "Image data is null, cannot proceed with upload." }
                        _cameraPostState.update { it.copy(isLoading = false) }
                        return@launch
                    }

                    try {
                        withContext(NonCancellable) {
                            val url = uploadImage(imageByteArray)
                            val ownUser = userRepository.getOwnUser()

                            log.i { "UserID: $ownUser" }

                            val post = ownUser?.let {
                                PostDTO(
                                    userid = it.userId,
                                    title = title,
                                    description = description,
                                    content = CameraImageContent(imageUrl = url)
                                )
                            }

                            log.i { "Post: $post" }

                            if (post != null) {
                                postDataSource.putPost(post, imageModule)
                            }
                        }
                    } catch (e: CancellationException) {
                        log.w { "Operation was cancelled: ${e.message}" }
                        // Handle coroutine cancellation gracefully if needed
                    } catch (e: Exception) {
                        log.e { "Failed to save post: ${e.message}" }
                    } finally {
                        _cameraPostState.update { it.copy(isLoading = false) }
                    }
                }
            }

            is CameraPostEvent.SetDescription -> {
                _cameraPostState.update { it.copy(description = event.description) }
            }

            is CameraPostEvent.SetTitle -> {
                _cameraPostState.update { it.copy(title = event.title) }
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
