package com.myedu.fragment

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import coil.load
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.myedu.R
import com.myedu.databinding.FragmentProfileBinding
import com.myedu.utils.Constant
import com.myedu.utils.Constant.IMAGE_NAME
import com.myedu.utils.PrefManager
import com.myedu.utils.Validator
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException


class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var pref: PrefManager
    private lateinit var storageReference: StorageReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        storageReference = FirebaseStorage.getInstance().reference
        pref = PrefManager(requireContext())
        auth = Firebase.auth
        binding.userPicture.apply {
            setOnClickListener { showPictureDialog() }
            load(requireContext().getFileStreamPath(IMAGE_NAME)) {
                placeholder(R.drawable.ic_person)
                error(R.drawable.ic_person)
            }
        }
        binding.btnUpdate.setOnClickListener {
            validateFields()
            if (!Validator.validateName(binding.editName.text.toString()) && binding.editName.text.toString() == pref.name)
                binding.editName.requestFocus()
            else
                updateProfile()
        }
    }

    private fun validateFields() {
        if (!Validator.validateName(binding.editName.text.toString()))
            with(binding.nameInputLayout) {
                requestFocus()
                error = getString(R.string.helper_name)
            }
        else
            with(binding.nameInputLayout) {
                error = null
                isHelperTextEnabled = false
            }
    }

    private fun updateProfile() {
        binding.progress.visibility = View.VISIBLE
        activity?.let {
            val user = auth.currentUser
            user?.updateProfile(
                UserProfileChangeRequest.Builder()
                    .setDisplayName(binding.editName.text.toString())
                    .build()
            )
            if (user != null) {
                pref.name = binding.editName.text.toString()
                Toast.makeText(
                    requireContext(),
                    getString(R.string.profile_updated),
                    Toast.LENGTH_SHORT
                ).show()
                binding.editName.text?.clear()
            } else
                Toast.makeText(
                    requireContext(),
                    getString(R.string.profile_update_failed),
                    Toast.LENGTH_SHORT
                ).show()
            binding.progress.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun uploadImage(imageUri: Uri) {
        binding.progress.visibility = View.VISIBLE
        storageReference
            .child(Constant.STORAGE_PATH + System.currentTimeMillis())
            .putFile(imageUri)
            .addOnSuccessListener {
                Toast.makeText(context, "Image Uploaded!!", Toast.LENGTH_SHORT).show()
                binding.progress.visibility = View.GONE
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed " + e.message, Toast.LENGTH_SHORT).show()
                binding.progress.visibility = View.GONE
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
            Constant.IMAGE_PICKER_REQUEST_CODE
        )
    }

    private fun captureImage() {
        startActivityForResult(
            Intent(MediaStore.ACTION_IMAGE_CAPTURE),
            Constant.CAMERA_IMAGE_REQUEST_CODE
        )
    }

    private fun checkCameraPermission() {
        activity?.let {
            if (ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            )
                captureImage()
            else
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf(Manifest.permission.CAMERA),
                    Constant.CAMERA_PERMISSION_REQUEST_CODE
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
        val path =
            MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Edu", null)
        return Uri.parse(path)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.IMAGE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val imageUri: Uri? = data.data
            imageUri?.let {
                uploadImage(it)
                getBitmap(requireContext(), it)?.let { it1 -> saveImage(requireContext(), it1) }
            }
            binding.userPicture.load(requireContext().getFileStreamPath(IMAGE_NAME))
        }

        if (requestCode == Constant.CAMERA_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val imageBitmap = data.extras?.get("data") as Bitmap
            val imageUri: Uri? = getImageUri(requireContext(), imageBitmap)
            saveImage(requireContext(), imageBitmap)
            imageUri?.let {
                uploadImage(it)
            }
            binding.userPicture.load(requireContext().getFileStreamPath(IMAGE_NAME))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constant.CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
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
        private const val TAG = "ProfileFragment"
    }

    private fun saveImage(context: Context, finalBitmap: Bitmap) {
        try {
            val out = context.openFileOutput(IMAGE_NAME, Context.MODE_PRIVATE)
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getBitmap(context: Context, uri: Uri): Bitmap? {
        try {
            return BitmapFactory.decodeStream(
                context.contentResolver.openInputStream(uri) ?: return null
            )
        } catch (e: FileNotFoundException) {
            Log.e(TAG, "getContactBitmapFromURI: ", e)
        }
        return null
    }
}