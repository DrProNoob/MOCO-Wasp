package camera.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initCameraKoin(config:KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(sharedModule)
    }
}