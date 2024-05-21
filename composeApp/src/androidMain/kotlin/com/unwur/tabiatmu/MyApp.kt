package com.unwur.tabiatmu

import android.app.Application
import data.source.local.SqlDelightDriverFactory
import data.source.preferences.DataStoreFactory
import di.letsKoinStart

class MyApp: Application() {

    override fun onCreate() {
        super.onCreate()
        SqlDelightDriverFactory.initAndroid(this)
        DataStoreFactory.initAndroid(this)
        initKoin()
    }

    private fun initKoin() {
        letsKoinStart()
    }
}