package com.myedu.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.myedu.model.Course

@Dao
interface CourseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(course: Course)

    @Delete
    fun delete(course: Course)

    @Query("DELETE FROM Course")
    fun deleteAll()

    @get:Query("SELECT * FROM Course ORDER BY id DESC LIMIT 100")
    val courses: LiveData<List<Course>?>

    @Query("SELECT * FROM Course WHERE id = :id")
    fun course(id: Int): Course
}