package data.source.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import java.lang.ref.WeakReference

actual object DataStoreFactory {
    private var appContextRef: WeakReference<Context>? = null

    fun initAndroid(appContext: Context) {
        appContextRef = WeakReference(appContext)
    }

    actual fun createDataStore(): DataStore<Preferences> {
        val appContext = appContextRef?.get() ?: throw IllegalStateException("DataStore App Context not initialized or already released")
        return getDataStore(
            producePath = { appContext.filesDir.resolve(dataStoreFileName).absolutePath }
        )
    }
}