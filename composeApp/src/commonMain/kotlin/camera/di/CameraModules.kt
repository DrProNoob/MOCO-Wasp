package camera.di

import camera.view.CameraViewModel
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val sharedModule = module {
    viewModelOf(::CameraViewModel)
}


