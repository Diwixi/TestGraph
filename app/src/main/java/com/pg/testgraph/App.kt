package com.pg.testgraph

import android.app.Application
import com.pg.testgraph.core.data.network.networkModule
import com.pg.testgraph.features.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(appModule, networkModule)
        }
    }
}