package data.source.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.unwur.tabiatmu.database.TabiatDatabase

actual object SqlDelightDriverFactory {
    actual fun createDriver(dbName: String): SqlDriver {
        return NativeSqliteDriver(TabiatDatabase.Schema, dbName)
    }
}