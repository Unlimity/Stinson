package com.alviere.stinson.sample

import android.app.Application
import com.alviere.stinson.sample.di.LoginModule
import org.koin.android.ext.android.startKoin

class SampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(LoginModule()))
    }
}