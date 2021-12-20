package com.myedu.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import coil.load
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
import com.myedu.utils.Constant.IMAGE_NAME
import com.myedu.utils.Constant.PAGE
import com.myedu.utils.Constant.PAGE_SIZE
import com.myedu.utils.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class MainFragment : Fragment(), CourseListener {
    private lateinit var binding: FragmentMainBinding
    private lateinit var pref: PrefManager
    private lateinit var adapter: CourseAdapter
    private lateinit var categoryAdapter: CourseAdapter
    private lateinit var viewModel: CourseViewModel
    private lateinit var request: ServerRequest

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = PrefManager(requireContext())
        binding.userName.text = "Hi, ${pref.name.split(" ")[0]}"
        viewModel = ViewModelProvider(this)[CourseViewModel::class.java]
        request = Client.getClient().create(ServerRequest::class.java)
        adapter = CourseAdapter(this@MainFragment)
        categoryAdapter = CourseAdapter(this@MainFragment)
        binding.rvPopularCourse.adapter = adapter
        binding.rvCourseCategory.adapter = categoryAdapter

        viewModel.courses.observe(viewLifecycleOwner) { courses ->
            adapter.data = courses
        }

        val categories = resources.getStringArray(R.array.courseCategory)
        for (category in categories) {
            with(Chip(context))
            {
                text = category
                binding.chipGroup.addView(this)
                setTextColor(ResourcesCompat.getColor(resources, R.color.white, null))
                setChipBackgroundColorResource(R.color.brand_dark)
                setOnClickListener { getCourseByCategory(text.toString()) }
            }
        }
        getLatestCourses("")
        getCourseByCategory(categories[0])
        binding.userPicture.apply {
            setOnClickListener {
                Navigation
                    .createNavigateOnClickListener(R.id.action_mainFragment_to_profileFragment2)
                    .onClick(view)
            }
            load(context.getFileStreamPath(IMAGE_NAME)) {
                placeholder(R.drawable.ic_person)
                error(R.drawable.ic_person)
            }
        }
        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                getLatestCourses(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.toString().isEmpty())
                    adapter.data = viewModel.courses.value
                else
                    getLatestCourses(newText)
                return true
            }
        })
    }

    private fun getLatestCourses(search: String?) {
        binding.progress.visibility = View.VISIBLE
        val call: Call<CourseResponse?> = if (search.isNullOrEmpty())
            request.getCourses(PAGE, PAGE_SIZE)
        else
            request.getCourses(PAGE, PAGE_SIZE, search)

        call.enqueue(object : Callback<CourseResponse?> {
            override fun onResponse(
                call: Call<CourseResponse?>,
                response: Response<CourseResponse?>
            ) {
                val courseResponse = response.body()
                if (response.isSuccessful && courseResponse != null && courseResponse.courses.isNotEmpty()) {
                    if (search.isNullOrEmpty())
                        viewModel.insert(courseResponse.courses)
                    else
                        adapter.data = courseResponse.courses
                }
                binding.progress.visibility = View.GONE
            }

            override fun onFailure(call: Call<CourseResponse?>, t: Throwable) {
                Log.e(TAG, "onFailure: ", t)
                binding.progress.visibility = View.GONE
            }
        })
    }

    companion object {
        private const val TAG = "MainFragment"
    }

    private fun getCourseByCategory(category: String) {
        request.getCourseByCategory(PAGE, PAGE_SIZE, category)
            .enqueue(object : Callback<CourseResponse?> {
                override fun onResponse(
                    call: Call<CourseResponse?>,
                    response: Response<CourseResponse?>
                ) {
                    val courseResponse = response.body()
                    if (response.isSuccessful && courseResponse != null && courseResponse.courses.isNotEmpty()) {
                        categoryAdapter.data = courseResponse.courses
                    }
                }

                override fun onFailure(call: Call<CourseResponse?>, t: Throwable) {
                    Log.e(TAG, "onFailure: ", t)
                }
            })
    }

    override fun onCourseSelected(course: Course) {
        Log.i(TAG, "onCourseSelected: $course")
        Navigation
            .createNavigateOnClickListener(
                R.id.action_mainFragment_to_courseDetailFragment,
                bundleOf("courseId" to course.id, "source" to "main")
            )
            .onClick(view)
    }
}