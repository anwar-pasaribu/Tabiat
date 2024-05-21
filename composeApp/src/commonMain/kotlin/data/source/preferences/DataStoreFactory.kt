package data.source.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

expect object DataStoreFactory {
    fun createDataStore(): DataStore<Preferences>
}
