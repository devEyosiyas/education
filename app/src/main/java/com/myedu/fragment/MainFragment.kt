package com.myedu.fragment

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
import java.util.jar.Manifest


class MainFragment : Fragment(), CourseListener {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var pref: PrefManager
    private lateinit var adapter: CourseAdapter
    private lateinit var categoryAdapter: CourseAdapter
    private lateinit var viewModel: CourseViewModel

    private var uploadedImage : ImageView? = null
    private var image : Uri? = null

    private lateinit var request: ServerRequest

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        uploadedImage = binding.userPicture
        binding.userPicture.setOnClickListener {
            showDialog()
        }
        return binding.root
    }

    private fun setPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            useCamera()

        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android
                .Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = context?.let { PrefManager(it) }!!
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
        getLatestCourses()
        getCourseByCategory(categories[0])
    }

    private fun getLatestCourses() {
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
        private const val CAMERA_PERMISSION_CODE = 5
        private const val CAMERA = 10
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
                bundleOf("courseId" to course.id)
            )
            .onClick(view)
    }

    /**Select an image**/
    fun selectImage(){
        val intent = Intent()
        intent.type = "image/"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select image"), 30)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == 30 && resultCode == Activity.RESULT_OK){

            if (data == null || data.data == null) return
            image = data.data

            /**Retrieve image**/
            uploadedImage?.setImageURI(image)

        }

        //For camera
        if (requestCode == CAMERA_PERMISSION_CODE){
            val image : Bitmap = data!!.extras!!.get("data") as Bitmap
            binding.userPicture.setImageBitmap(image)

        }

    }

    //Show a dialog
    private fun showDialog(){
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.title_message))
            .setMessage(getString(R.string.message))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.response_yes)) { _, _ -> setPermission()}
            .setPositiveButton(getString(R.string.response_no)) { _, _ -> selectImage()}.show()
    }

    //Use camera
    private fun useCamera(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)

    }

    //Request permission for camera
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION_CODE){

            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, CAMERA)

            }else{
                Toast.makeText(requireContext(), "You denied permission for camera",
                    Toast.LENGTH_LONG).show()
            }
        }
    }
}