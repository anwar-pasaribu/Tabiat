package com.unwur.tabiatmu.platform

object TrackerKey {
    const val USER_ID = "user_id"
    const val EVENT_NAME = "event_name"
}

interface AppTracker {
    fun setUserId(dataMap: HashMap<String, String>)
    fun trackEvent(eventName: String, dataMap: HashMap<String, String>)
    fun trackException(
        throwable: Throwable,
        eventName: String = "",
        dataMap: HashMap<String, String> = hashMapOf()
    )
}

expect fun getAppTracker(): AppTracker