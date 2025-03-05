package com.tps.challenge.dagger

import android.app.Application
import com.tps.challenge.TCApplication
import com.tps.challenge.database.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val application: TCApplication) {
    @Provides
    @Singleton
    fun getApplication(): Application {
        return application
    }

    /**
     * Provides an instance of [AppDatabase].
     */
    @Provides
    @Singleton
    fun provideAppDatabase(): AppDatabase {
        return AppDatabase.getDatabase(application)
    }
}
