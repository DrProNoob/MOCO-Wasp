package core.model.repo
import core.entity.User
import dev.gitlive.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.toList

class UserRepository(database: FirebaseDatabase) {

    private val dbRef = database.reference()

    private var ownUser: User? = null
    private var remoteUser: User? = null

    private var allUsersWithoutOwnUser = emptyList<User>()

    fun setRemoteUser(username: String) {
        this.remoteUser = allUsersWithoutOwnUser.find { user ->
            username == user.userName
        }
    }

    fun getRemoteUser(): User? {
        return this.remoteUser
    }

    fun getAllUsersWithoutOwnUser(): List<User> {
        return allUsersWithoutOwnUser
    }

    suspend fun setOwnUser(username: String): User {
        val dbUser = dbRef.child("users").child(username).valueEvents.first().value(User.serializer())
        this.ownUser = dbUser
        return dbUser
    }

    fun getOwnUser(): User? {
        if (this.ownUser == null) {
            throw Exception("Own user not set")
        }
        return this.ownUser
    }

    suspend fun getAllUsers(): List<User> {
        val allUsers = dbRef.child("users").valueEvents.mapNotNull { dataSnapshot ->
            dataSnapshot.children.map { user ->
                user.value(User.serializer())
            }
        }.first()
        allUsersWithoutOwnUser = allUsers.filter { user ->
            user.userName != (this.ownUser?.userName ?: "")
        }
        return allUsers
    }
}