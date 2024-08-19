package camera.di

import camera.view.CameraViewModel
import dev.icerock.moko.permissions.PermissionsController
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val sharedModule = module {
    viewModelOf(::CameraViewModel)
}

