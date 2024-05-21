package di

import data.source.preferences.DataStoreFactory
import data.source.preferences.IPreferencesDataSource
import data.source.preferences.PreferencesDataSourceImpl
import org.koin.dsl.module

fun dataStoreModule() = module {
    single {
        DataStoreFactory.createDataStore()
    }

    single<IPreferencesDataSource> {
        PreferencesDataSourceImpl(dataStore = get())
    }
}