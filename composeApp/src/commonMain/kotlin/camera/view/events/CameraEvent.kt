package camera.view.events

sealed interface CameraEvent{
    object UploadImage : CameraEvent
}