package data.source.local

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.unwur.tabiatmu.database.TabiatDatabase
import java.lang.ref.WeakReference

actual object SqlDelightDriverFactory {
    private var appContextRef: WeakReference<Context>? = null

    fun initAndroid(appContext: Context) {
        appContextRef = WeakReference(appContext)
    }

    actual fun createDriver(dbName: String): SqlDriver {
        val appContext = appContextRef?.get() ?: throw IllegalStateException("App Context not initialized or already released")
        return AndroidSqliteDriver(
            TabiatDatabase.Schema,
            appContext,
            dbName
        )
    }
}