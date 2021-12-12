package com.myedu.event

import com.myedu.model.Course

interface CourseListener {
    fun onCourseSelected(course: Course)
}