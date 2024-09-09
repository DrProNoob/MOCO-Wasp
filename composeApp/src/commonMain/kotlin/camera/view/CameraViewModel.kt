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
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.database.ServerValue
import dev.gitlive.firebase.database.database
import dev.gitlive.firebase.storage.StorageReference
import dev.gitlive.firebase.storage.storage
import feed.model.dataSource.PostDataSource
import feed.model.entity.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.serializer
import kotlin.random.Random

class CameraViewModel ():ViewModel() {

    val host = "192.168.178.20"
    val port = 9199
    val local = "10.0.2.2"

    val userImageDatabase = Firebase.database.apply {
        useEmulator(host = host, port = 9000)
    }
    val realtimeDatabase = userImageDatabase.reference()



    val imageDbStorage = Firebase.storage.apply {
        useEmulator(host = host, port = 9199)
    }
    val storeRef = imageDbStorage.reference
    val imageRef = storeRef.child("images")
    val uploadRef = imageRef.child("${Random.nextLong()}.jpg")

    val postDataSource = PostDataSource(userImageDatabase)





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

    private fun mapImageToUser(uploadRef:StorageReference) {
        viewModelScope.launch {
            val imagePath = uploadRef.getDownloadUrl()
            val userPicure = UserPicure(id = 1, imageUrl = imagePath, postDate = ServerValue.TIMESTAMP)
            realtimeDatabase.child("usersImage").child(userPicure.id.toString()).setValue(userPicure.imageUrl)
            //SO NUTZT MAN DAS
            val imageContent = CameraImageContent(contentId = "1", imageUrl = imagePath)
            val post = Post(id = 1, userid = 1, title = "title", description = "description", contentId = imageContent)
            postDataSource.putPost(post, imageModule)
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
            mapImageToUser(uploadRef)
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
