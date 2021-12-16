package com.myedu.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import coil.load
import com.google.android.material.chip.Chip
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.myedu.R
import com.myedu.adapter.CourseAdapter
import com.myedu.databinding.FragmentMainBinding
import com.myedu.event.CourseListener
import com.myedu.event.ServerRequest
import com.myedu.model.Course
import com.myedu.model.response.CourseResponse
import com.myedu.room.viewmodel.CourseViewModel
import com.myedu.utils.Client
import com.myedu.utils.Constant.IMAGE_PICKER_REQUEST_CODE
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
    private lateinit var categoryAdapter: CourseAdapter
    private lateinit var viewModel: CourseViewModel
    private lateinit var request: ServerRequest
    private lateinit var storageReference: StorageReference
    private var imageUri: Uri? = null

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
        request = Client.getClient().create(ServerRequest::class.java)
        adapter = CourseAdapter(this@MainFragment)
        categoryAdapter = CourseAdapter(this@MainFragment)
        binding.rvPopularCourse.adapter = adapter
        binding.rvCourseCategory.adapter = categoryAdapter

        storageReference = FirebaseStorage.getInstance().reference

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
            if (pref.profilePicture != "null" || pref.profilePicture != "")
                binding.userPicture.load(Uri.parse(pref.profilePicture))
        Log.i(TAG, "onViewCreated: profile pic ${pref.profilePicture}")
        binding.userPicture.apply {
            setOnClickListener { pickImage() }
        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICKER_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            pref.profilePicture = imageUri.toString()
            Log.i(TAG, "onActivityResult: $imageUri")
//            binding.userPicture.load(imageUri)
        }
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
                bundleOf("courseId" to course.id)
            )
            .onClick(view)
    }

    private fun uploadImage() {
//        if (filePath != null) {
//
//            // Code for showing progressDialog while uploading
//            val progressDialog = ProgressDialog(this)
//            progressDialog.setTitle("Uploading...")
//            progressDialog.show()
//
//            // Defining the child of storageReference
//            val ref: StorageReference = storageReference
//                .child(
//                    "images/"
//                            + UUID.randomUUID().toString()
//                )
//
//            // adding listeners on upload
//            // or failure of image
//            ref.putFile(filePath)
//                .addOnSuccessListener(
//                    object : OnSuccessListener<UploadTask.TaskSnapshot?> {
//                        fun onSuccess(
//                            taskSnapshot: UploadTask.TaskSnapshot?
//                        ) {
//
//                            // Image uploaded successfully
//                            // Dismiss dialog
//                            progressDialog.dismiss()
//                            Toast
//                                .makeText(
//                                    this@MainActivity,
//                                    "Image Uploaded!!",
//                                    Toast.LENGTH_SHORT
//                                )
//                                .show()
//                        }
//                    })
//                .addOnFailureListener { e -> // Error, Image not uploaded
//                    progressDialog.dismiss()
//                    Toast
//                        .makeText(
//                            this@MainActivity,
//                            "Failed " + e.message,
//                            Toast.LENGTH_SHORT
//                        )
//                        .show()
//                }
//                .addOnProgressListener { taskSnapshot ->
//
//                    // Progress Listener for loading
//                    // percentage on the dialog box
//                    val progress = (100.0
//                            * taskSnapshot.bytesTransferred
//                            / taskSnapshot.totalByteCount)
//                    progressDialog.setMessage(
//                        "Uploaded "
//                                + progress.toInt() + "%"
//                    )
//                }
//        }
    }

    private fun pickImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(
               intent,
                getString(R.string.image_pick_title)
            ),
            IMAGE_PICKER_REQUEST_CODE
        )
    }

}