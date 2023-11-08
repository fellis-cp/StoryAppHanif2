package com.dicoding.storyapphanif.ui.upload

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapphanif.R
import com.dicoding.storyapphanif.data.Result
import com.dicoding.storyapphanif.databinding.ActivityUploadBinding
import com.dicoding.storyapphanif.ui.ViewModelFactory
import com.dicoding.storyapphanif.ui.getImageUri
import com.dicoding.storyapphanif.ui.main.MainActivity
import com.dicoding.storyapphanif.ui.reduceImage
import com.dicoding.storyapphanif.ui.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class UploadActivity : AppCompatActivity() {

    private var currentLocation: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding : ActivityUploadBinding
    private lateinit var uploadViewModel: UploadViewModel
    private var currentImageUri : Uri? = null


    private val reqPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermGranted () = ContextCompat.checkSelfPermission(this ,REQUIRED_PERMISSION) ==
        PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.upload_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val factory :  ViewModelFactory = ViewModelFactory.getInstance(this)
        uploadViewModel = ViewModelProvider(this , factory)[UploadViewModel::class.java]

        if (!allPermGranted()){
            reqPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        uploadViewModel.responseUploadStory.observe(this) {
            when (it) {
                is Result.Loading -> {
                    loadingVisible(true)
                }
                is Result.Success -> {
                    loadingVisible(false)
                    AlertDialog.Builder(this).apply {
                        setTitle("Success")
                        setMessage(getString(R.string.upload_message))
                        setCancelable(false)
                        setPositiveButton(getString(R.string.dialog_positive_button)) { _, _ ->
                            val intent = Intent(context, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        }
                        create()
                        show()
                    }
                }
                is Result.Error -> {
                    loadingVisible(false)
                }
            }
        }

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.uploadButton.setOnClickListener { uploadStart() }

    }

    @Suppress("DEPRECATION")
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            imageShow()
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }


    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            imageShow()
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }


    private fun loadingVisible (isLoading : Boolean) {
        binding.progressIndicator.visibility =  if (isLoading) View.VISIBLE else View.GONE
    }

    private fun imageShow() {
        currentImageUri?.let {
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun toastShow (message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    private fun uploadStart() {
        var token: String
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceImage()
            val description = binding.edtDescription.text.toString()
            loadingVisible(true)
            if (description.isEmpty()) {
                toastShow(getString(R.string.desc_empty))
            }
            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )

            uploadViewModel.getSession().observe(this) { user ->
                token = user.token
                uploadViewModel.uploadStory(token, multipartBody, requestBody , currentLocation)
            }

        } ?: toastShow(getString(R.string.empty_warning))
    }

    private fun permCheck(permission : String) : Boolean {
        return  ContextCompat.checkSelfPermission(this , permission) ==
            PackageManager.PERMISSION_GRANTED
    }



    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}




