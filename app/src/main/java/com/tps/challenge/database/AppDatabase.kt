package com.tps.challenge.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tps.challenge.database.entities.PlaceholderEntity

/**
 * The [Room] database for the TPS app.
 */
@Database(entities = [PlaceholderEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        private const val DATABASE_NAME = "app_db"

        @Volatile
        private var instance: AppDatabase? = null

        /**
         * Gets a singleton instance of the database.
         *
         * @param context The context to create/open the database under
         * @return [AppDatabase] instance
         */
        fun getDatabase(context: Context): AppDatabase {
            if (instance == null) {
                synchronized(AppDatabase::class.java) {
                    if (instance == null) {
                        instance = Room.databaseBuilder(
                            context, AppDatabase::class.java, DATABASE_NAME
                        )
                            .fallbackToDestructiveMigration()
                            .enableMultiInstanceInvalidation()
                            .build()
                    }
                }
            }

            return instance!!
        }
    }
}