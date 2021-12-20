package com.myedu.room.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.myedu.model.Course
import com.myedu.room.repository.CourseRepository

class CourseViewModel(application: Application) : AndroidViewModel(application) {
    private val courseRepository: CourseRepository = CourseRepository(application)
    val courses: LiveData<List<Course>?> = courseRepository.courses
    val favouriteCourses: LiveData<List<Course>?> = courseRepository.favouriteCourses
    val myCourses: LiveData<List<Course>?> = courseRepository.myCourses
    var coursesList = MediatorLiveData<List<Course>>()

    fun insert(course: Course) {
        courseRepository.insert(course)
    }

    fun update(course: Course) {
        courseRepository.update(course)
    }

    fun insert(courses: List<Course>) {
        for (course in courses) {
            if (!exists(course.id))
                courseRepository.insert(course)
        }
    }

    fun delete(course: Course) {
        courseRepository.delete(course)
    }

    fun deleteAll() {
        courseRepository.deleteAll()
    }

    suspend fun course(id: Int): Course {
        return courseRepository.course(id)
    }

    val isEmpty: Boolean
        get() = if (courseRepository.courses.value != null) courseRepository.courses.value!!.isNotEmpty() else true

    fun exists(id: Int): Boolean {
        var exist = false
        if (courses.value != null)
            for (course in courses.value!!) {
                if (course.id == id) {
                    exist = true
                    break
                }
            }
        return exist
    }

    companion object {
        private const val TAG = "CourseViewModel"
    }
}