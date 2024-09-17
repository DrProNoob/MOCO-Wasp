package core.di

import camera.view.CameraViewModel
import chat.view.ChatViewModel
import auth.view.AuthViewModel
import chat.model.ChatRepository
import core.model.repo.ChallengeRepo
import core.model.repo.UserRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.database.database
import dev.gitlive.firebase.storage.storage
import feed.view.FeedViewModel
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val sharedModule = module {
    single {
        Firebase.storage.apply {
            useEmulator(host = "192.168.178.30", port = 9000)
        }
    }
    single {
        Firebase.database.apply {
            useEmulator(host = "192.168.178.30", port = 9000)
        }
    }
    singleOf(::ChallengeRepo)
    singleOf(::UserRepository)
    singleOf(::ChatRepository)
    viewModelOf(::AuthViewModel)
    viewModelOf(::ChatViewModel)
    viewModelOf(::CameraViewModel)
    viewModelOf(::FeedViewModel)
}



