package feed.model.dataSource

import camera.model.entity.imageModule
import core.entity.User
import dev.gitlive.firebase.database.DataSnapshot
import dev.gitlive.firebase.database.FirebaseDatabase
import feed.model.dtos.PostDTO
import feed.model.entity.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.toList
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.lighthousegames.logging.logging
import kotlin.math.log

class PostDataSource(
    firebaseDatabase: FirebaseDatabase
) {

    private val dbRef = firebaseDatabase.reference()

    suspend fun putPost(post: PostDTO, serializersModule: SerializersModule) {
        try {
            dbRef.child("posts").push().setValue(post) {
                encodeDefaults = true
                this.serializersModule = serializersModule
            }
            println("Post added")
        } catch (e: Exception) {
            println("Error adding post: ${e.message}")
        }
    }

    fun getAllPosts(): Flow<List<Post>> =
        dbRef.child("posts").valueEvents.mapNotNull { dataSnapshot ->
            dataSnapshot.children.map { post ->
                val postDTO = post.value(PostDTO.serializer()) {
                    serializersModule = imageModule
                }
                postDTO.mapToPost()
            }.toList()
        }

    private suspend fun PostDTO.mapToPost(): Post {
        val userName = getUserNameById(this.userid)
        return Post(
            userid = this.userid,
            userName = userName,
            title = this.title,
            description = this.description,
            content = this.content
        )
    }

    private suspend fun getPostById(postid: Int): Post {
        return dbRef.child("posts").child(postid.toString()).valueEvents.mapNotNull { dataSnapshot ->
            dataSnapshot.value(PostDTO.serializer()) {
                serializersModule = imageModule
            }.mapToPost()
        }.toList().first()
    }

    private suspend fun getUserNameById(userid: Int): String {
/*        if (userid == 1) {
            return "User1"
        } else {*/
        return dbRef.child("users").valueEvents.mapNotNull { dataSnapshot ->
            dataSnapshot.children.map { user ->
                user.value(User.serializer())
            }
        }.first().find { user -> userid == user.userId }?.userName ?: "Unknown"

       // }
    }
}