package org.example.project

import android.app.Application
import core.di.initKoin
import org.koin.android.ext.koin.androidContext
import steps.domain.view.AppContext


class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        AppContext.apply{ set(applicationContext) }
        initKoin {
            androidContext(this@MyApplication)
        }
    }
}