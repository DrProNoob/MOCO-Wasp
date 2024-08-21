package org.example.project

import android.app.Application
import camera.di.initAuthKoin
import org.koin.android.ext.koin.androidContext


class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        initAuthKoin {
            androidContext(this@MyApplication)
        }
    }
}