package com.myedu.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.myedu.model.Course
import com.myedu.room.RoomDb
import com.myedu.room.dao.CourseDao
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Database(entities = [Course::class], version = 1)
abstract class RoomDb : RoomDatabase() {
    abstract fun courseDao(): CourseDao

    companion object {
        @Volatile
        private var INSTANCE: RoomDb? = null
        private const val NUMBER_OF_THREADS = 4
        val databaseWriteExecutor: ExecutorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS)

        fun getDatabase(context: Context): RoomDb? {
            if (INSTANCE == null) {
                synchronized(RoomDb::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            RoomDb::class.java, "Cronus"
                        ).build()
                    }
                }
            }
            return INSTANCE
        }
    }
}