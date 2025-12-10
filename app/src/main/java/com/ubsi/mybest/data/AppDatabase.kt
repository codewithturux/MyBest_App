package com.ubsi.mybest.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ubsi.mybest.data.dao.NotificationDao
import com.ubsi.mybest.data.dao.ScheduleDao
import com.ubsi.mybest.data.dao.UserDao
import com.ubsi.mybest.data.entity.NotificationEntity
import com.ubsi.mybest.data.entity.ScheduleEntity
import com.ubsi.mybest.data.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        ScheduleEntity::class,
        NotificationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun userDao(): UserDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun notificationDao(): NotificationDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mybest_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
