package camera.model.entity

import feed.model.entity.AbstractContent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Serializable
@SerialName("CameraImageContent")
data class CameraImageContent(
    override val contentId: String,
    val imageUrl: String
):AbstractContent()

val imageModule = SerializersModule {
    polymorphic(AbstractContent::class, AbstractContent.serializer()) {
        subclass(CameraImageContent::class, CameraImageContent.serializer())
    }
}
