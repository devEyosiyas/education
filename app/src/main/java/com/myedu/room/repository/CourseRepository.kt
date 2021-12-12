package com.myedu.room.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.myedu.model.Course
import com.myedu.room.RoomDb
import com.myedu.room.dao.CourseDao

class CourseRepository(application: Application) {
    private val courseDao: CourseDao
    val courses: LiveData<List<Course>?>

    fun insert(course: Course) {
        RoomDb.databaseWriteExecutor.execute { courseDao.insert(course) }
    }

    fun delete(course: Course) {
        RoomDb.databaseWriteExecutor.execute { courseDao.delete(course) }
    }

    fun deleteAll() {
        RoomDb.databaseWriteExecutor.execute { courseDao.deleteAll() }
    }

    init {
        val db: RoomDb = RoomDb.getDatabase(application)!!
        courseDao = db.courseDao()
        courses = courseDao.courses
    }
}