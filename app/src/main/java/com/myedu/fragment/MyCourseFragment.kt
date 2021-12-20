package com.myedu.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.myedu.R
import com.myedu.adapter.CourseAdapter
import com.myedu.databinding.FragmentMyCourseBinding
import com.myedu.event.CourseListener
import com.myedu.model.Course
import com.myedu.room.viewmodel.CourseViewModel


class MyCourseFragment : Fragment(), CourseListener {
    private var _binding: FragmentMyCourseBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CourseViewModel
    private lateinit var adapter: CourseAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyCourseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[CourseViewModel::class.java]
        adapter = CourseAdapter(this)
        binding.rvMyCourse.adapter = adapter
        viewModel.myCourses.observe(viewLifecycleOwner) { courses ->
            adapter.data = courses
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCourseSelected(course: Course) {
        Navigation
            .createNavigateOnClickListener(
                R.id.action_myCourseFragment_to_courseDetailFragment,
                bundleOf("courseId" to course.id, "source" to "course")
            )
            .onClick(view)
    }

    companion object {
        private const val TAG = "MyCourseFragment"
    }
}