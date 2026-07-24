package com.unwur.tabiatmu.platform

import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.google.firebase.crashlytics.setCustomKeys


class AppTrackerImpl : AppTracker {
    override fun setUserId(dataMap: HashMap<String, String>) {
        if (!dataMap.containsKey(TrackerKey.USER_ID)) return
        Firebase.crashlytics.setUserId(
            dataMap.get(TrackerKey.USER_ID).orEmpty()
        )
    }

    override fun trackEvent(
        eventName: String,
        dataMap: HashMap<String, String>
    ) {
        val crashlytics = Firebase.crashlytics
        crashlytics.log(eventName)
        crashlytics.setCustomKeys {
            dataMap.forEach { (key, value) ->
                key(key, value)
            }
        }
    }

    override fun trackException(
        throwable: Throwable,
        eventName: String,
        dataMap: HashMap<String, String>
    ) {
        val crashlytics = Firebase.crashlytics
        crashlytics.log("exception during: $eventName")
        crashlytics.setCustomKeys {
            dataMap.forEach { (key, value) ->
                key(key, value)
            }
        }
        crashlytics.recordException(throwable)
    }
}

actual fun getAppTracker(): AppTracker {
    return AppTrackerImpl()
}