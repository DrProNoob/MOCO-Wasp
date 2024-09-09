package org.example.project

import android.app.Application
import camera.di.initCameraKoin
import org.koin.android.ext.koin.androidContext


class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        initCameraKoin {
            androidContext(this@MyApplication)
        }
    }
}