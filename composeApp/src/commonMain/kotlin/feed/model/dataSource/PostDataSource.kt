package feed.model.dataSource

import dev.gitlive.firebase.database.FirebaseDatabase
import feed.model.entity.Post
import kotlinx.serialization.modules.SerializersModule

class PostDataSource(
    private val firebaseDatabase: FirebaseDatabase
) {
    suspend fun putPost(post: Post, serializersModule: SerializersModule) {
        val dbRef = firebaseDatabase.reference()
            dbRef.child("posts").push().setValue(post) {
                encodeDefaults = true
                this.serializersModule = serializersModule
            }
    }

}