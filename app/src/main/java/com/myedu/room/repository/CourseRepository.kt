package com.myedu.room.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.myedu.model.Course
import com.myedu.room.RoomDb
import com.myedu.room.dao.CourseDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

class CourseRepository(application: Application) {
    private val courseDao: CourseDao
    val courses: LiveData<List<Course>?>
    val favouriteCourses: LiveData<List<Course>?>
    val myCourses: LiveData<List<Course>?>

    fun insert(course: Course) {
        RoomDb.databaseWriteExecutor.execute { courseDao.insert(course) }
    }

    fun update(course: Course) {
        RoomDb.databaseWriteExecutor.execute { courseDao.update(course) }
    }

    fun delete(course: Course) {
        RoomDb.databaseWriteExecutor.execute { courseDao.delete(course) }
    }

    fun deleteAll() {
        RoomDb.databaseWriteExecutor.execute { courseDao.deleteAll() }
    }

    suspend fun course(id: Int): Course {
        var course: Course
        withContext(coroutineContext + Dispatchers.IO) {
            course = courseDao.course(id)
        }
        return course
    }

    init {
        val db: RoomDb = RoomDb.getDatabase(application)!!
        courseDao = db.courseDao()
        courses = courseDao.courses
        favouriteCourses = courseDao.favouriteCourses
        myCourses = courseDao.myCourses
    }
}