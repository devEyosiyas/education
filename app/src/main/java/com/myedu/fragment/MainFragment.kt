package com.myedu.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.android.material.chip.Chip
import com.myedu.R
import com.myedu.adapter.CourseAdapter
import com.myedu.databinding.FragmentMainBinding
import com.myedu.event.CourseListener
import com.myedu.event.ServerRequest
import com.myedu.model.Course
import com.myedu.model.response.CourseResponse
import com.myedu.room.viewmodel.CourseViewModel
import com.myedu.utils.Client
import com.myedu.utils.Constant.PAGE
import com.myedu.utils.Constant.PAGE_SIZE
import com.myedu.utils.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainFragment : Fragment(), CourseListener {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var pref: PrefManager
    private lateinit var adapter: CourseAdapter
    private lateinit var viewModel: CourseViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = context?.let { PrefManager(it) }!!
        binding.userName.text = "Hi, ${pref.name.split(" ")[0]}"
        viewModel = ViewModelProvider(this)[CourseViewModel::class.java]
        adapter = CourseAdapter(this@MainFragment)
        binding.rvPopularCourse.adapter = adapter
        viewModel.courses.observe(viewLifecycleOwner) { courses ->
            adapter.data = courses
        }

        var demo = arrayOf<String>("Animation", "Editing")
        for (genre in demo) {
            val chip = Chip(context)
            chip.text = genre
//            chip.isCheckable = true
//            chip.isEnabled = false
//            chip.setTextColor(ResourcesCompat.getColor(resources, R.color.white,null))
//            chip.setChipBackgroundColorResource(R.color.brand_dark)
            binding.chipGroup.addView(chip)
            chip.setOnClickListener {
                Log.i(
                    TAG,
                    "onViewCreated: ${chip.text} checked ${chip.isChecked} checkable ${chip.isCheckable}"
                )
                chip.isCheckable = !chip.isCheckable
            }
        }
        val request: ServerRequest = Client.getClient().create(ServerRequest::class.java)
        request.getCourses(PAGE, PAGE_SIZE).enqueue(object : Callback<CourseResponse?> {
            override fun onResponse(
                call: Call<CourseResponse?>,
                response: Response<CourseResponse?>
            ) {
                val courseResponse = response.body()
                if (response.isSuccessful && courseResponse != null && courseResponse.courses.isNotEmpty()) {
                    viewModel.insert(courseResponse.courses)
                }
            }

            override fun onFailure(call: Call<CourseResponse?>, t: Throwable) {
                Log.e(TAG, "onFailure: ", t)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "MainFragment"
    }

    override fun onCourseSelected(course: Course) {
        Log.i(TAG, "onCourseSelected: $course")
//        val action = ViewPagerFragmentDirections.navigateToDetailFragment(message.sender, displayName)
//        Navigation.findNavController(binder.root).navigate(action)
        Navigation
            .createNavigateOnClickListener(R.id.action_mainFragment_to_courseDetailFragment,bundleOf("courseId" to course.id))
            .onClick(view)
    }
}