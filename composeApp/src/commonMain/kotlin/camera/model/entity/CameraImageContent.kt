package camera.model.entity

import dev.gitlive.firebase.database.ServerValue
import feed.model.entity.AbstractContent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Serializable
@SerialName("CameraImageContent")
data class CameraImageContent(
    val imageUrl: String,
    val captureDate: ServerValue = ServerValue.TIMESTAMP,
):AbstractContent()

val imageModule = SerializersModule {
    polymorphic(AbstractContent::class, AbstractContent.serializer()) {
        subclass(CameraImageContent::class, CameraImageContent.serializer())
    }
}
