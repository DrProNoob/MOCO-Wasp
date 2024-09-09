package core.di

import camera.view.CameraViewModel
import dev.gitlive.firebase.database.FirebaseDatabase
import feed.view.FeedViewModel
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val sharedModule = module {

    viewModelOf(::CameraViewModel)
    viewModelOf(::FeedViewModel)
}


