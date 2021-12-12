package com.myedu.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.myedu.R
import com.myedu.databinding.ItemCourseBinding
import com.myedu.event.CourseListener
import com.myedu.model.Course
import com.myedu.utils.CourseDiffUtil
import java.util.*

class CourseAdapter(private val listener: CourseListener) :
    RecyclerView.Adapter<CourseAdapter.ViewHolder?>() {
    private val courses: MutableList<Course> = ArrayList<Course>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_course, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setCourse(courses[position])
    }

    override fun getItemCount(): Int {
        return courses.size
    }

    var data: List<Course>?
        get() = courses
        set(courses) {
            val diffCallback = CourseDiffUtil(this.courses, courses)
            val diffResult: DiffUtil.DiffResult = DiffUtil.calculateDiff(diffCallback)
            this.courses.clear()
            this.courses.addAll(courses!!)
            diffResult.dispatchUpdatesTo(this)
        }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemCourseBinding.bind(itemView)

        fun setCourse(course: Course) {
            binding.poster.load(course.image125H) {
                placeholder(R.drawable.ic_illustration_b_one)
                error(R.drawable.ic_illustration_b_one)
            }
            binding.title.text = course.title
            if (course.instructors != null && course.instructors.isNotEmpty())
                binding.instructor.text = course.instructors[0].displayName
            binding.root.setOnClickListener { listener.onCourseSelected(course) }
        }
    }
}