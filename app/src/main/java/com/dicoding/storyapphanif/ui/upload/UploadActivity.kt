package com.dicoding.storyapphanif.ui.upload

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dicoding.storyapphanif.R
import com.dicoding.storyapphanif.data.Result
import com.dicoding.storyapphanif.databinding.ActivityUploadBinding
import com.dicoding.storyapphanif.ui.ViewModelFactory
import com.dicoding.storyapphanif.ui.getImageUri
import com.dicoding.storyapphanif.ui.main.MainActivity
import com.dicoding.storyapphanif.ui.reduceImage
import com.dicoding.storyapphanif.ui.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class UploadActivity : AppCompatActivity() {

    private var currentLocation: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding : ActivityUploadBinding
    private lateinit var uploadViewModel: UploadViewModel
    private var currentImageUri : Uri? = null

    private val reqPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                else -> {
                    binding.switchLocation.isChecked = false
                }
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.upload_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val factory :  ViewModelFactory = ViewModelFactory.getInstance(this)
        uploadViewModel = ViewModelProvider(this , factory)[UploadViewModel::class.java]

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
        binding.switchLocation.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                if (!areGPSEnabled()) {
                    gpsDialogShow()
                }
                lifecycleScope.launch {
                    getMyLastLocation()
                }
            } else {
                currentLocation = null
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

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

    private fun areGPSEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            currentLocation = locationResult.lastLocation
        }
    }

    private fun getNewLocation(){
        Toast.makeText(this.baseContext,"Get New Location", Toast.LENGTH_SHORT).show()
        val locationRequest =  LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = TimeUnit.SECONDS.toMillis(1)
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 1
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        )
            Looper.myLooper()?.let {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,locationCallback, it
                )
            }
    }
    @SuppressLint("MissingPermission")
    private fun getMyLastLocation() {
        if (permCheck(Manifest.permission.ACCESS_FINE_LOCATION) &&
            permCheck(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    currentLocation = location
                } else {
                    Toast.makeText(
                        this,
                    R.string.no_location,
                        Toast.LENGTH_SHORT
                    ).show()
                    getNewLocation()
                }
            }
        } else {
            reqPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun gpsDialogShow() {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.gps_tittle))
            setMessage(getString(R.string.gps_message))
            setPositiveButton(getString(R.string.dialog_positive_button)) { _, _ ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
            create()
            show()
        }
    }

}




