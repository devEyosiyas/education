package com.myedu.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.myedu.model.Course

@Dao
interface CourseDao {
    @Insert(onConflict = REPLACE)
    fun insert(course: Course)

    @Update(onConflict = REPLACE)
    fun update(course: Course)

    @Delete
    fun delete(course: Course)

    @Query("DELETE FROM Course")
    fun deleteAll()

    @get:Query("SELECT * FROM Course ORDER BY id DESC LIMIT 100")
    val courses: LiveData<List<Course>?>

    @get:Query("SELECT * FROM Course WHERE favourite = 1 ORDER BY id DESC LIMIT 100")
    val favouriteCourses: LiveData<List<Course>?>

    @Query("SELECT * FROM Course WHERE id = :id")
    fun course(id: Int): Course
}