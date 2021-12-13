package com.myedu.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import coil.load
import com.myedu.R
import com.myedu.databinding.FragmentCourseDetailBinding
import com.myedu.event.ServerRequest
import com.myedu.model.response.CourseDetailResponse
import com.myedu.room.viewmodel.CourseViewModel
import com.myedu.utils.Client
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CourseDetailFragment : Fragment() {
    private var _binding: FragmentCourseDetailBinding? = null
    private val binding get() = _binding!!
    private val args: CourseDetailFragmentArgs by navArgs()
    private lateinit var viewModel: CourseViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCourseDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[CourseViewModel::class.java]

        loadCourseDetail(args.courseId)
        viewModel.viewModelScope.launch {
            with(viewModel.course(args.courseId)) {
                binding.courseHeadline.text = headline
            }
        }

        binding.imgBack.setOnClickListener {
            Navigation
                .createNavigateOnClickListener(R.id.action_courseDetailFragment_to_mainFragment)
                .onClick(it)
        }
    }

    private fun loadCourseDetail(id: Int) {
        binding.progress.visibility = View.VISIBLE
        val request: ServerRequest = Client.getClient().create(ServerRequest::class.java)
        request.getCourseDetail(id).enqueue(object : Callback<CourseDetailResponse?> {
            override fun onResponse(
                call: Call<CourseDetailResponse?>,
                response: Response<CourseDetailResponse?>
            ) {
                val courseDetailResponse = response.body()
                if (response.isSuccessful && courseDetailResponse != null) {
                    with(binding) {
                        courseBanner.load(courseDetailResponse.image480x270)
                        courseTitle.text = courseDetailResponse.title
                        instructorName.text = courseDetailResponse.instructors[0].displayName
                    }
                }
                Log.i(TAG, "onResponse: detail raw ${response.body()}")
                binding.progress.visibility = View.GONE
            }

            override fun onFailure(call: Call<CourseDetailResponse?>, t: Throwable) {
                Log.e(TAG, "onFailure: ", t)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "CourseDetailFragment"
    }
}