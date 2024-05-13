package data.source.local

import com.unwur.tabiatmu.database.TabiatDatabase

const val TABIAT_DB_NAME = "TabiatDatabase.db"

fun createDatabase(): TabiatDatabase {
    return TabiatDatabase(
        SqlDelightDriverFactory.createDriver(TABIAT_DB_NAME)
    )
}