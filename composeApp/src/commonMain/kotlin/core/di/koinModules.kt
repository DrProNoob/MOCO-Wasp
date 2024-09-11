package core.di

import camera.view.CameraViewModel
import feed.view.FeedViewModel
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val sharedModule = module {

    viewModelOf(::CameraViewModel)
    viewModelOf(::FeedViewModel)
}


