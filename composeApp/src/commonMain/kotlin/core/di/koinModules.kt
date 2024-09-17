package core.di

import camera.view.CameraViewModel
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseApp
import dev.gitlive.firebase.database.database
import dev.gitlive.firebase.storage.FirebaseStorage
import dev.gitlive.firebase.storage.storage
import feed.view.FeedViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val sharedModule = module {
    single {
        Firebase.storage.apply {
            useEmulator(host = "192.168.178.20", port = 9199)
        }
    }
    single {
        Firebase.database.apply {
            useEmulator(host = "192.168.178.20", port = 9000)
        }
    }
    viewModelOf(::CameraViewModel)
    viewModelOf(::FeedViewModel)
}



