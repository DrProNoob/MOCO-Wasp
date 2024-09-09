package feed.model.dataSource

import dev.gitlive.firebase.database.DataSnapshot
import dev.gitlive.firebase.database.FirebaseDatabase
import feed.model.entity.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.toList
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

class PostDataSource(
    private val firebaseDatabase: FirebaseDatabase
) {

    private val dbRef = firebaseDatabase.reference()

    suspend fun putPost(post: Post, serializersModule: SerializersModule) {
            dbRef.child("posts").push().setValue(post) {
                encodeDefaults = true
                this.serializersModule = serializersModule
            }
    }

   fun getAllPosts():Flow<List<Post>> {
       return dbRef.child("posts").valueEvents.map { dataSnapshot ->
           dataSnapshot.children.mapNotNull { it.toPost() }
       }.scan(emptyList()) { acc, posts -> acc + posts }
   }

    private fun DataSnapshot.toPost(): Post? {
        return try {
            val postJson = value as String
            Json.decodeFromString(Post.serializer(), postJson)
        } catch (e: Exception) {
            null
        }
    }

}