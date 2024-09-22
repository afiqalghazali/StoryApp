package com.scifi.storyapp.view.upload

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.scifi.storyapp.R
import com.scifi.storyapp.databinding.ActivityUploadBinding
import com.scifi.storyapp.view.MainViewModelFactory
import com.scifi.storyapp.view.main.MainActivity
import com.scifi.storyapp.view.utils.InterfaceUtils
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class UploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel: UploadViewModel by viewModels {
        MainViewModelFactory.getInstance(this)
    }
    private var currentImageUri: Uri? = null
    private var isUploading = false
    private var latLng: LatLng? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setupObserver()
        setupAction()
    }

    private fun setupAction() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnCamera.setOnClickListener { startCamera() }
        binding.switchLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                getMyLocation()
            }
        }
        binding.btnUpload.setOnClickListener {
            uploadImage(latLng)
        }
    }

    private fun startGallery() {
        launcherGallery.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherCamera.launch(currentImageUri!!)
    }

    private val launcherCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            binding.ivPreview.setImageURI(it)
        }
    }

    private fun setupObserver() {
        viewModel.uploadResponse.observe(this) { uploadResponse ->
            if (uploadResponse.error == true) {
                InterfaceUtils.showAlert(
                    context = this,
                    message = uploadResponse.message,
                )
            } else {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            InterfaceUtils.showLoading(binding.root, false)
            isUploading = false
        }
    }

    private fun uploadImage(latLng: LatLng?) {
        if (!isUploading) {
            isUploading = true
            currentImageUri?.let { uri ->
                val imageFile = uriToFile(uri, this)
                val reduceImageFile = imageFile.reduceFileImage()
                val description = binding.etUploadDescription.text.toString()
                val requestBody = description.toRequestBody("text/plain".toMediaType())
                val imageFileRequestBody = reduceImageFile.asRequestBody("image/jpeg".toMediaType())
                val imageMultipart = MultipartBody.Part.createFormData(
                    "photo",
                    reduceImageFile.name,
                    imageFileRequestBody
                )
                val latitude = latLng?.latitude
                val longitude = latLng?.longitude
                InterfaceUtils.showLoading(binding.root, true)
                viewModel.addStory(imageMultipart, requestBody, latitude, longitude)
            } ?: run {
                InterfaceUtils.showAlert(
                    this,
                    getString(R.string.upload_message)
                )
                isUploading = false
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLocation()
                }

                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLocation()
                }

                else -> {
                    InterfaceUtils.showAlert(
                        this,
                        "Location permission denied"
                    )
                }
            }

        }

    private fun checkPermission(): Boolean {
        val fineLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineLocationPermission && coarseLocationPermission
    }

    private fun getMyLocation() {
        if (checkPermission()
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    latLng = LatLng(location.latitude, location.longitude)
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

}
