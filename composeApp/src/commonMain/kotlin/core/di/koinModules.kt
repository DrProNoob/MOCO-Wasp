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
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf

import org.koin.dsl.module

val sharedModule = module {
    val local = "10.0.2.2"
    val host = "192.168.178.20"

    single {
        Firebase.storage.apply {
            useEmulator(host = local, port = 9199)
        }
    }
    single {
        Firebase.database.apply {
            useEmulator(host = local, port = 9000)
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



