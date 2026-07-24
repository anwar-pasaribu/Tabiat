package com.unwur.tabiatmu.platform

class AppTrackerImpl : AppTracker {
    override fun setUserId(dataMap: HashMap<String, String>) {
        // no op
    }

    override fun trackEvent(
        eventName: String,
        dataMap: HashMap<String, String>
    ) {
        // no op
    }

    override fun trackException(
        throwable: Throwable,
        eventName: String,
        dataMap: HashMap<String, String>
    ) {
        // no op
    }
}

actual fun getAppTracker(): AppTracker {
    return AppTrackerImpl()
}