package core.model.repo
import core.entity.User
import dev.gitlive.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.toList

class UserRepository(database: FirebaseDatabase) {

    private val dbRef = database.reference()

    private var ownUser: User? = null

    suspend fun setOwnUser(username: String) {
        val dbUser = dbRef.child("users").child(username).valueEvents.first().value(User.serializer())
        this.ownUser = dbUser
    }
/*    suspend fun setOwnUser(username: String) {
        val dbUser = dbRef.child("users").child(username).setValue(User(3,username))
        this.ownUser = User(3,username)
    }*/

    fun getOwnUser(): User? {
        if (this.ownUser == null) {
            throw Exception("Own user not set")
        }
        return this.ownUser
    }

    suspend fun getAllUsers(): List<User> {
        return dbRef.child("users").valueEvents.mapNotNull { dataSnapshot ->
            dataSnapshot.value(User.serializer())
        }.toList()
    }
}