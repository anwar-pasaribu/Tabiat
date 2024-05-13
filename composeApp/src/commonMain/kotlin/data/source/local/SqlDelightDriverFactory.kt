package data.source.local

import app.cash.sqldelight.db.SqlDriver

expect object SqlDelightDriverFactory {
    fun createDriver(dbName: String): SqlDriver
}
