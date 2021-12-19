package com.myedu.fragment

import android.Manifest.permission.CAMERA
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import coil.load
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
import com.myedu.utils.Constant.CAMERA_IMAGE_REQUEST_CODE
import com.myedu.utils.Constant.CAMERA_PERMISSION_REQUEST_CODE
import com.myedu.utils.Constant.IMAGE_PICKER_REQUEST_CODE
import com.myedu.utils.Constant.PAGE
import com.myedu.utils.Constant.PAGE_SIZE
import com.myedu.utils.Constant.STORAGE_PATH
import com.myedu.utils.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.util.*


class MainFragment : Fragment(), CourseListener {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var pref: PrefManager
    private lateinit var adapter: CourseAdapter
    private lateinit var categoryAdapter: CourseAdapter
    private lateinit var viewModel: CourseViewModel
    private lateinit var request: ServerRequest
    private lateinit var storageReference: StorageReference

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
        getLatestCourses("")
        getCourseByCategory(categories[0])
        Log.i(TAG, "onViewCreated: profile pic ${Uri.decode(pref.profilePicture)}")
        binding.userPicture.apply {
            setOnClickListener { showPictureDialog() }
            load(Uri.parse(pref.profilePicture)) {
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
                getLatestCourses(newText)
                return true
            }
        })
    }

    private fun getLatestCourses(search: String?) {
        binding.progress.visibility = View.VISIBLE
        if (search.isNullOrEmpty())
            request.getCourses(PAGE, PAGE_SIZE)
        else
            request.getCourses(PAGE, PAGE_SIZE, search)
                .enqueue(object : Callback<CourseResponse?> {
                    override fun onResponse(
                        call: Call<CourseResponse?>,
                        response: Response<CourseResponse?>
                    ) {
                        val courseResponse = response.body()
                        if (response.isSuccessful && courseResponse != null && courseResponse.courses.isNotEmpty()) {
                            viewModel.insert(courseResponse.courses)
                        }
                        binding.progress.visibility = View.GONE
                    }

                    override fun onFailure(call: Call<CourseResponse?>, t: Throwable) {
                        Log.e(TAG, "onFailure: ", t)
                        binding.progress.visibility = View.GONE
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
            val imageUri: Uri? = data.data
            imageUri?.let {
                uploadImage(it)
                pref.profilePicture = it.toString()
            }
            binding.userPicture.load(pref.profilePicture)
        }

        if (requestCode == CAMERA_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val imageBitmap = data.extras?.get("data") as Bitmap
            val imageUri: Uri? = context?.let {
                getImageUri(it, imageBitmap)
            }
            imageUri?.let {
                uploadImage(it)
                pref.profilePicture = it.toString()
            }
            binding.userPicture.load(pref.profilePicture)
        }
        Log.i(TAG, "onActivityResult: profile pic ${pref.profilePicture}")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED)
                captureImage()
            else
                Toast.makeText(
                    context,
                    getString(R.string.camera_permission_denied),
                    Toast.LENGTH_LONG
                ).show()
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

    private fun uploadImage(imageUri: Uri) {
        storageReference
            .child(STORAGE_PATH + System.currentTimeMillis())
            .putFile(imageUri)
            .addOnSuccessListener {
                Toast.makeText(context, "Image Uploaded!!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed " + e.message, Toast.LENGTH_SHORT).show()
            }
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

    private fun captureImage() {
        startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE), CAMERA_IMAGE_REQUEST_CODE)
    }

    private fun checkCameraPermission() {
        activity?.let {
            if (ContextCompat.checkSelfPermission(it, CAMERA) == PERMISSION_GRANTED
            )
                captureImage()
            else
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf(CAMERA),
                    CAMERA_PERMISSION_REQUEST_CODE
                )
        }
    }

    private fun showPictureDialog() {
        context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle(getString(R.string.camera_title))
                .setPositiveButton(getString(R.string.camera)) { _, _ -> checkCameraPermission() }
                .setNegativeButton(getString(R.string.gallery)) { _, _ -> pickImage() }
                .show()
        }
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = Images.Media.insertImage(inContext.contentResolver, inImage, "Edu", null)
        return Uri.parse(path)
    }
}