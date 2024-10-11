package steps.domain.model

import dev.gitlive.firebase.database.ServerValue
import feed.model.entity.AbstractContent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic


@Serializable
@SerialName("StepChallengeContent")
data class StepChallengeContent(
    val steps: Int,
): AbstractContent()

val stepModule = SerializersModule {
    polymorphic(AbstractContent::class, AbstractContent.serializer()) {
        subclass(StepChallengeContent::class, StepChallengeContent.serializer())
    }
}
