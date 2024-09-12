package camera.view.events

import feed.model.entity.Post

sealed interface CameraPostEvent {
 object SavePost: CameraPostEvent
 data class SetTitle(val title: String): CameraPostEvent
 data class SetDescription(val description: String?): CameraPostEvent

}