package com.unwur.tabiatmu

import android.app.Application
import di.letsKoinStart

class MyApp: Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
    }

    private fun initKoin() {
        letsKoinStart()
    }
}