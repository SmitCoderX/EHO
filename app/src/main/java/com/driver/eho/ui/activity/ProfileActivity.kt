package com.driver.eho.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import com.driver.eho.R
import com.driver.eho.SharedPreferenceManager
import com.driver.eho.adapter.HorizontalRecyclerView
import com.driver.eho.databinding.ActivityProfileBinding
import com.driver.eho.model.Login.Data
import com.driver.eho.ui.fragment.AmbulanceTypeBottomSheet
import com.driver.eho.ui.viewModel.viewModelFactory.UpdateProfileViewModelProviderFactory
import com.driver.eho.ui.viewModels.UpdateProfileViewModel
import com.driver.eho.utils.Constants.IMAGE_URL
import com.driver.eho.utils.Constants.TAG
import com.driver.eho.utils.Constants.snackbarError
import com.driver.eho.utils.Constants.snackbarSuccess
import com.driver.eho.utils.EHOApplication
import com.driver.eho.utils.Resources
import com.driver.eho.utils.UploadRequestBody
import com.driver.eho.utils.getFileName
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class ProfileActivity : AppCompatActivity(), UploadRequestBody.UploadCallback,
    AmbulanceTypeBottomSheet.AmbulanceType {

    private lateinit var binding: ActivityProfileBinding
    private var uri: ArrayList<Uri> = ArrayList()
    var recyclerView: RecyclerView? = null
    private var adapter: HorizontalRecyclerView? = null
    private var pictureDoc: Uri? = null
    private var currentPhotoPath: String = ""
    private var selectedImage: Uri? = null
    private val updateProfileViewModel: UpdateProfileViewModel by viewModels {
        UpdateProfileViewModelProviderFactory(
            application,
            (application as EHOApplication).repository
        )
    }
    private lateinit var prefs: SharedPreferenceManager

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = SharedPreferenceManager(this)

        Log.d(TAG, "onCreate LAT LONG: ${prefs.getLat() + prefs.getLong()}")
        profileData()


        binding.menuIcon.setOnClickListener {
            sendToMainActivity()
        }

        binding.ivCam.setOnClickListener {
            checkPermissionForProfileImage()
        }

        binding.tvAmbulanceType.setOnClickListener {
            val typeFragment = AmbulanceTypeBottomSheet()
            typeFragment.show(supportFragmentManager, typeFragment.tag)
        }

        // adapter fot upload document
        recyclerView = binding.rvDocument
        adapter = HorizontalRecyclerView(listOf())
        recyclerView!!.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView!!.adapter = adapter

        // add upload document
        binding.ivAddDocument.setOnClickListener {
            addDocumentFromgelley()
        }

        binding.tvSave.setOnClickListener {
            submit()
        }

        binding.btnSubmit.setOnClickListener {
            submit()
        }
        getUpdatedResult()

        binding.tvDeactivate.setOnClickListener {
            handleDeactivateDriver()
        }
    }

    private fun getUpdatedResult() {
        updateProfileViewModel.updateProfileLiveData.observe(this) { resources ->
            when (resources) {
                is Resources.Success -> {
                    setEnabled()
                    hideLoading()
                    snackbarSuccess(binding.root, resources.message.toString())
                    sendToMainActivity()
                }

                is Resources.Error -> {
                    setEnabled()
                    hideLoading()
                    snackbarError(binding.root, resources.message.toString())
                }

                is Resources.Loading -> {
                    removeEnabled()
                    showLoading()
                }
            }

        }
    }

    private fun submit() {
        if (validate()) {
            binding.apply {
                driverUpdateProfile(
                    prefs.getToken().toString().trim(),
                    edtUserName.text.toString().trim(),
                    edtMobileNumber.text.toString().trim(),
                    edtEmail.text.toString().trim(),
                    edtDriverName.text.toString().trim(),
                    edtDriverExperience.text.toString().trim().toInt(),
                    edtLicenceNumber.text.toString().trim(),
                    edtAmbulanceVehicleNumber.text.toString().trim(),
                    edtHospitalAddress.text.toString().trim(),
                    edtState.text.toString().trim(),
                    edtCity.text.toString().trim(),
                    edtCountry.text.toString().trim(),
                    tvAmbulanceType.text.toString().trim(),
                    edtPriceFair.text.toString().trim(),
//                        edtPassword.text.toString().trim()
                )
            }
        }
    }

    private fun handleDeactivateDriver() {
        updateProfileViewModel.deactivateDriver(prefs.getToken().toString())
        updateProfileViewModel.deactivateMutableLiveData.observe(this) { resources ->
            when (resources) {
                is Resources.Success -> {
                    setEnabled()
                    prefs.logoutUser()
                    startActivity(Intent(this, WelcomeActivity::class.java))
                    finish()
                }

                is Resources.Error -> {
                    setEnabled()
                    hideLoading()
                    snackbarError(binding.root, resources.message.toString())
                }

                is Resources.Loading -> {
                    removeEnabled()
                    showLoading()
                }
            }
        }
    }


    // api call for driver SignUp
    private fun driverUpdateProfile(
        token: String,
        UserName: String,
        MobileNumber: String,
        Email: String,
        DriverName: String,
        DriverExperience: Int,
        LicenceNumber: String,
        AmbulanceVehicleNumber: String,
        HospitalAddress: String,
        State: String,
        City: String,
        Country: String,
        AmbulanceType: String,
        PriceFair: String,
    ) {

        val type = if (AmbulanceType == "Free") {
            "1"
        } else {
            "2"
        }

        if (selectedImage == null && uri.isNullOrEmpty()) {
            try {
                val userName: RequestBody =
                    UserName.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                val email: RequestBody =
                    Email.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                val mobile: RequestBody =
                    MobileNumber.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                val name: RequestBody =
                    DriverName.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                /*  val hospitalName: RequestBody =
                      HospitalName.toRequestBody("multipart/form-data".toMediaTypeOrNull())*/
                val hospitalAddress: RequestBody =
                    HospitalAddress.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                val state: RequestBody =
                    State.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                val city: RequestBody =
                    City.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                val country: RequestBody =
                    Country.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                val driverExperience: RequestBody =
                    DriverExperience.toString()
                        .toRequestBody("multipart/form-data".toMediaTypeOrNull())
                val driverLicenseNumber: RequestBody =
                    LicenceNumber
                        .toRequestBody("multipart/form-data".toMediaTypeOrNull())
                val ambulanceVehicleNumber: RequestBody =
                    AmbulanceVehicleNumber.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                val latitude: RequestBody =
                    prefs.getLat().toString()
                        .toRequestBody("multipart/form-data".toMediaTypeOrNull())
                val longitude: RequestBody =
                    prefs.getLong().toString()
                        .toRequestBody("multipart/form-data".toMediaTypeOrNull())
                val isAttached = setIsAttached().toString()
                    .toRequestBody("multipart/form-data".toMediaTypeOrNull())
                val ambulanceType: RequestBody =
                    type.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                val priceFair: RequestBody =
                    PriceFair.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                Log.d(TAG, "driverSignUpApiCall: $type")

                updateProfileViewModel.updateProfile(
                    token,
                    userName,
                    email,
                    null,
                    null,
                    mobile,
                    name,
                    hospitalAddress,
                    state,
                    city,
                    country,
                    ambulanceType,
                    priceFair,
                    driverExperience,
                    driverLicenseNumber,
                    ambulanceVehicleNumber,
                    latitude,
                    longitude,
                    isAttached
                )
            } catch (e: Exception) {
                Log.d(TAG, "driverUpdateProfile: $e")
            }
        } else if (selectedImage == null) {
            try {
                val listofImage: Array<MultipartBody.Part?> = arrayOfNulls(5)
                for (i in uri.indices) {
                    val parcelFileDescriptor1 =
                        contentResolver.openFileDescriptor(uri[i], "r", null) ?: return
                    val inputStream1 = FileInputStream(parcelFileDescriptor1.fileDescriptor)
                    val file1 = File(cacheDir, contentResolver.getFileName(uri[i]))
                    val outputStream1 = FileOutputStream(file1)
                    inputStream1.copyTo(outputStream1)
                    val documentBody = UploadRequestBody(file1, "file", this)
                    val document_Body =
                        MultipartBody.Part.createFormData("documents", file1.name, documentBody)
                    listofImage[i] = document_Body
                    val userName: RequestBody =
                        UserName.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                    val email: RequestBody =
                        Email.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                    val mobile: RequestBody =
                        MobileNumber.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                    val name: RequestBody =
                        DriverName.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                    val hospitalAddress: RequestBody =
                        HospitalAddress.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                    val state: RequestBody =
                        State.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                    val city: RequestBody =
                        City.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                    val country: RequestBody =
                        Country.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                    val driverExperience: RequestBody =
                        DriverExperience.toString()
                            .toRequestBody("multipart/form-data".toMediaTypeOrNull())
                    val driverLicenseNumber: RequestBody =
                        LicenceNumber
                            .toRequestBody("multipart/form-data".toMediaTypeOrNull())
                    val ambulanceVehicleNumber: RequestBody =
                        AmbulanceVehicleNumber.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                    val ambulanceType: RequestBody =
                        type.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                    val priceFair: RequestBody =
                        PriceFair.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                    val latitude: RequestBody =
                        prefs.getLat().toString()
                            .toRequestBody("multipart/form-data".toMediaTypeOrNull())
                    val longitude: RequestBody =
                        prefs.getLong().toString()
                            .toRequestBody("multipart/form-data".toMediaTypeOrNull())
                    val isAttached = setIsAttached().toString()
                        .toRequestBody("multipart/form-data".toMediaTypeOrNull())

                    Log.d(TAG, "driverSignUpApiCall: $type")
                    updateProfileViewModel.updateProfile(
                        token,
                        userName,
                        email,
                        null,
                        listofImage,
                        mobile,
                        name,
                        hospitalAddress,
                        state,
                        city,
                        country,
                        ambulanceType,
                        priceFair,
                        driverExperience,
                        driverLicenseNumber,
                        ambulanceVehicleNumber,
                        latitude,
                        longitude,
                        isAttached
                    )
                }
            } catch (e: Exception) {
                Log.d(TAG, "driverUpdateProfile: $e")
            }
        } else if (uri.isNullOrEmpty()) {
            val parcelFileDescriptor =
                contentResolver.openFileDescriptor(selectedImage!!, "r", null) ?: return
            val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
            val file = File(cacheDir, contentResolver.getFileName(selectedImage!!))
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)

            try {
                val imageBody = UploadRequestBody(file, "file", this)
                val image_body =
                    MultipartBody.Part.createFormData("profileImage", file.name, imageBody)
                val userName: RequestBody =
                    UserName.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                val email: RequestBody =
                    Email.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                val mobile: RequestBody =
                    MobileNumber.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                val name: RequestBody =
                    DriverName.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                /*  val hospitalName: RequestBody =
                      HospitalName.toRequestBody("multipart/form-data".toMediaTypeOrNull())*/
                val hospitalAddress: RequestBody =
                    HospitalAddress.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                val state: RequestBody =
                    State.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                val city: RequestBody =
                    City.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                val country: RequestBody =
                    Country.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                val ambulanceType: RequestBody =
                    type.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                val priceFair: RequestBody =
                    PriceFair.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                val driverExperience: RequestBody =
                    DriverExperience.toString()
                        .toRequestBody("multipart/form-data".toMediaTypeOrNull())
                val driverLicenseNumber: RequestBody =
                    LicenceNumber
                        .toRequestBody("multipart/form-data".toMediaTypeOrNull())
                val ambulanceVehicleNumber: RequestBody =
                    AmbulanceVehicleNumber.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                val latitude: RequestBody =
                    prefs.getLat().toString()
                        .toRequestBody("multipart/form-data".toMediaTypeOrNull())
                val longitude: RequestBody =
                    prefs.getLong().toString()
                        .toRequestBody("multipart/form-data".toMediaTypeOrNull())
                val isAttached = setIsAttached().toString()
                    .toRequestBody("multipart/form-data".toMediaTypeOrNull())

                Log.d(TAG, "driverSignUpApiCall: $type")
                updateProfileViewModel.updateProfile(
                    token,
                    userName,
                    email,
                    image_body,
                    null,
                    mobile,
                    name,
                    hospitalAddress,
                    state,
                    city,
                    country,
                    ambulanceType,
                    priceFair,
                    driverExperience,
                    driverLicenseNumber,
                    ambulanceVehicleNumber,
                    latitude,
                    longitude,
                    isAttached
                )
            } catch (e: Exception) {
                Log.d(TAG, "driverUpdateProfile: $e")
            }
        } else {
            val parcelFileDescriptor =
                contentResolver.openFileDescriptor(selectedImage!!, "r", null) ?: return
            val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
            val file = File(cacheDir, contentResolver.getFileName(selectedImage!!))
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)

            try {
                val listofImage: Array<MultipartBody.Part?> = arrayOfNulls(5)
                for (i in uri.indices) {
                    val parcelFileDescriptor1 =
                        contentResolver.openFileDescriptor(uri[i], "r", null) ?: return
                    val inputStream1 = FileInputStream(parcelFileDescriptor1.fileDescriptor)
                    val file1 = File(cacheDir, contentResolver.getFileName(uri[i]))
                    val outputStream1 = FileOutputStream(file1)
                    inputStream1.copyTo(outputStream1)
                    val documentBody = UploadRequestBody(file1, "file", this)
                    val document_Body =
                        MultipartBody.Part.createFormData("documents", file1.name, documentBody)
                    listofImage[i] = document_Body

                    val imageBody = UploadRequestBody(file, "file", this)
                    val image_body =
                        MultipartBody.Part.createFormData("profileImage", file.name, imageBody)
                    val userName: RequestBody =
                        UserName.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                    val email: RequestBody =
                        Email.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                    val mobile: RequestBody =
                        MobileNumber.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                    val name: RequestBody =
                        DriverName.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                    /*  val hospitalName: RequestBody =
                          HospitalName.toRequestBody("multipart/form-data".toMediaTypeOrNull())*/
                    val hospitalAddress: RequestBody =
                        HospitalAddress.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                    val state: RequestBody =
                        State.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                    val city: RequestBody =
                        City.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                    val country: RequestBody =
                        Country.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                    val ambulanceType: RequestBody =
                        type.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                    val priceFair: RequestBody =
                        PriceFair.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                    val driverExperience: RequestBody =
                        DriverExperience.toString()
                            .toRequestBody("multipart/form-data".toMediaTypeOrNull())
                    val driverLicenseNumber: RequestBody =
                        LicenceNumber
                            .toRequestBody("multipart/form-data".toMediaTypeOrNull())
                    val ambulanceVehicleNumber: RequestBody =
                        AmbulanceVehicleNumber.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                    val latitude: RequestBody =
                        prefs.getLat().toString()
                            .toRequestBody("multipart/form-data".toMediaTypeOrNull())
                    val longitude =
                        prefs.getLong().toString()
                            .toRequestBody("multipart/form-data".toMediaTypeOrNull())
                    val isAttached = setIsAttached().toString()
                        .toRequestBody("multipart/form-data".toMediaTypeOrNull())

                    Log.d(TAG, "driverSignUpApiCall: $type")
                    updateProfileViewModel.updateProfile(
                        token,
                        userName,
                        email,
                        image_body,
                        listofImage,
                        mobile,
                        name,
                        hospitalAddress,
                        state,
                        city,
                        country,
                        ambulanceType,
                        priceFair,
                        driverExperience,
                        driverLicenseNumber,
                        ambulanceVehicleNumber,
                        latitude,
                        longitude,
                        isAttached
                    )
                }
            } catch (e: Exception) {
                Log.d(TAG, "driverUpdateProfile: $e")
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun profileData() {
        updateProfileViewModel.getDriverDetails(prefs.getToken().toString())
        updateProfileViewModel.driverMutableLiveData.observe(this) { resources ->
            when (resources) {
                is Resources.Success -> {
                    setEnabled()
                    hideLoading()
                    setData(resources.data!!.data)
                    prefs.setData(resources.data)
                }

                is Resources.Error -> {
                    setEnabled()
                    snackbarError(binding.root, resources.message.toString())
                }

                is Resources.Loading -> {
                    removeEnabled()
                    showLoading()
                }
            }
        }
    }

    private fun setData(data: Data?) {
        val newUri = ArrayList<Uri>()
        binding.apply {
            Glide.with(this@ProfileActivity)
                .load(IMAGE_URL + data?.image)
                .placeholder(R.drawable.profile)
                .error(R.drawable.profile)
                .into(ivProfile)

            if (data?.attachedWithHospital == true) {
                radioYes.isChecked = true
            } else {
                radioNo.isChecked = true
            }

            edtUserName.setText(data?.userName)
            edtEmail.setText(data?.email)
            edtMobileNumber.setText(data?.mobile.toString())
            edtDriverName.setText(data?.name)
            edtDriverExperience.setText(data?.driverExperience.toString())
            edtLicenceNumber.setText(data?.driverLicenseNumber)
            edtAmbulanceVehicleNumber.setText(data?.ambulenceVehicleNumber)
            edtHospitalAddress.setText(data?.hospitalAddress)
            edtState.setText(data?.state)
            edtCity.setText(data?.city)
            edtCountry.setText(data?.country)
            data?.documents?.forEach {
                val myUri = Uri.parse(IMAGE_URL + it)
                newUri.add(myUri)
            }
            adapter?.updateData(newUri)
            if (data?.priceFair == 0) {
                binding.tvAmbulanceType.text = "Free"
                binding.edtPriceFair.visibility = View.GONE
            } else {
                binding.tvAmbulanceType.text = "Paid"
                binding.edtPriceFair.setText(data?.priceFair.toString())
                binding.edtPriceFair.visibility = View.VISIBLE
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getProfileAlertDialog() {
        // setup the alert builder
        MaterialAlertDialogBuilder(this).apply {
            setTitle("Choose a image")
            val images = arrayOf("Camera", "Gallery")
            setItems(images) { _, which ->
                when (which) {
                    0 -> {
                        getImageFromCamera()
                    }
                    1 -> {
                        getImageFromGallery()
                    }
                }
            }
            create()
            show()
        }
    }


    private fun getImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 1)
    }

    private fun addDocumentFromgelley() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(intent, 2)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getImageFromCamera() {
        dispatchTakePictureIntent()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun checkPermissionForProfileImage() {
        if ((checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            && (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            && (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)
        ) {
            val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            val permissionWrite = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val cameraPermission = arrayOf(Manifest.permission.CAMERA)

            requestPermissions(permission, 2001)
            requestPermissions(permissionWrite, 2021)
            requestPermissions(cameraPermission, 0)
        } else {
            getProfileAlertDialog()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            uri.clear()
            when (requestCode) {
                0 -> if (resultCode == RESULT_OK) {
                    Glide.with(this).load(currentPhotoPath).into(binding.ivProfile)
                }
                1 -> if (resultCode == RESULT_OK) {
                    selectedImage = data!!.data
                    Glide.with(this).load(selectedImage).into(binding.ivProfile)
                }
                2 -> {
                    if (resultCode == RESULT_OK) {
                        if (data!!.clipData != null) {
                            val count: Int = data.clipData!!.itemCount
                            for (i in 0 until count) {
                                uri.add(data.clipData!!.getItemAt(i).uri)
                                adapter?.updateData(uri)
                            }
                        } else {
                            val imageUri = data.data
                            uri.add(imageUri!!)
                            adapter?.updateData(uri)
                        }
                    } else if (data!!.data != null) {
                        pictureDoc = data.data
                    }
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "onActivityResult: $e")
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("QueryPermissionsNeeded")
    private fun dispatchTakePictureIntent() {
        val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(this.packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (e: IOException) {
                    Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
                    null
                }
                photoFile?.also {
                    selectedImage = FileProvider.getUriForFile(
                        this,
                        "com.example.android.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImage)
                    startActivityForResult(takePictureIntent, 0)
                }
            }
        }
    }

    // all type validations
    private fun validate(): Boolean {
        val userName = binding.edtUserName.text.toString().trim()
        val mobileNumber = binding.edtMobileNumber.text.toString().trim()
        val email = binding.edtEmail.text.toString().trim()
        val driverName = binding.edtDriverName.text.toString().trim()
        val driverExperience = binding.edtDriverExperience.text.toString().trim()
        val licenceNumber = binding.edtLicenceNumber.text.toString().trim()
        val anbVehicleNumber = binding.edtAmbulanceVehicleNumber.text.toString().trim()
        val state = binding.edtState.text.toString().trim()
        val city = binding.edtCity.text.toString().trim()
        val country = binding.edtCountry.text.toString().trim()
        val ambulancePrice = binding.edtPriceFair.text.toString().trim()
        val hospitalAddress = binding.edtHospitalAddress.text.toString().trim()

        // UserName
        if (TextUtils.isEmpty(userName)) {
            binding.edtUserName.error = "Enter your Username"
            return false
        }
        // mobileNumber
        if (TextUtils.isEmpty(mobileNumber) && !Patterns.PHONE.matcher(mobileNumber).matches()) {
            binding.edtMobileNumber.error = "Enter your correct mobile Number"
            snackbarError(binding.root, "Enter your correct mobile Number")
            return false
        }

        if (mobileNumber.length < 10) {
            binding.edtMobileNumber.error = "Please Check your Mobile Number Again"
            snackbarError(binding.root, "Please Check your Mobile Number Again")
            return false
        }
        // driver name
        if (TextUtils.isEmpty(driverName)) {
            binding.edtDriverName.error = "Enter driver name"
            snackbarError(binding.root, "Enter driver name")
            return false
        }
        // email
        if (TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.edtEmail.error = "Enter your correct email"
            snackbarError(binding.root, "Enter your correct email")
            return false
        }
        // driverExperience
        if (TextUtils.isEmpty(driverExperience)) {
            binding.edtDriverExperience.error = "Enter your driver experience"
            snackbarError(binding.root, "Enter your driver Experience")
            return false
        }
        // licenceNumber
        if (TextUtils.isEmpty(licenceNumber)) {
            binding.edtLicenceNumber.error = "Enter your licence number"
            snackbarError(binding.root, "Enter your Licence Number")
            return false
        }
        // anbVehicleNumber
        if (TextUtils.isEmpty(anbVehicleNumber)) {
            binding.edtAmbulanceVehicleNumber.error = "Enter your ambulance vehicle number"
            snackbarError(binding.root, "Enter your ambulance vehicle number")
            return false
        }
        // state
        if (TextUtils.isEmpty(state)) {
            binding.edtState.error = "Enter your state"
            snackbarError(binding.root, "Enter your state")
            return false
        }
        // city
        if (TextUtils.isEmpty(city)) {
            binding.edtCity.error = "Enter your city"
            snackbarError(binding.root, "Enter your city")
            return false
        }
        // country
        if (TextUtils.isEmpty(country)) {
            binding.edtCountry.error = "Enter your Country"
            snackbarError(binding.root, "Enter your country")
            return false
        }
        // address
        if (TextUtils.isEmpty(hospitalAddress)) {
            binding.edtHospitalAddress.error = "Enter hospital address"
            snackbarError(binding.root, "Enter Hospital Address")
            return false
        }

        // Price Fair
        if (TextUtils.isEmpty(ambulancePrice)) {
            binding.edtPriceFair.error = "Enter Price Fair"
            snackbarError(binding.root, "Enter Price Fair")
            return false
        }

        return true
    }

    // camera image path
    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SimpleDateFormat")
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? =
            this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        sendToMainActivity()
    }

    override fun onProgressUpdate(percentage: Int) {
        Log.d("TAG", "Upload Image $percentage")
    }

    private fun sendToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showLoading() {
        binding.viewLoader.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.viewLoader.visibility = View.GONE
    }

    private fun setIsAttached(): Boolean {
        return binding.radioYes.isChecked
    }

    private fun setEnabled() {
        binding.apply {
            ivProfile.isEnabled = true
            tvSave.isEnabled = true
            ivCam.isEnabled = true
            ivAddDocument.isEnabled = true
            edtUserName.isEnabled = true
            edtMobileNumber.isEnabled = true
            edtEmail.isEnabled = true
            edtDriverName.isEnabled = true
            edtDriverExperience.isEnabled = true
            edtLicenceNumber.isEnabled = true
            edtAmbulanceVehicleNumber.isEnabled = true
            radioGrpd.isEnabled = true
            edtHospitalAddress.isEnabled = true
            edtState.isEnabled = true
            edtCity.isEnabled = true
            edtCountry.isEnabled = true
            btnSubmit.isEnabled = true
            tvDeactivate.isEnabled = true
        }
    }

    private fun removeEnabled() {
        binding.apply {
            ivProfile.isEnabled = false
            tvSave.isEnabled = false
            ivCam.isEnabled = false
            ivAddDocument.isEnabled = false
            edtUserName.isEnabled = false
            edtMobileNumber.isEnabled = false
            edtEmail.isEnabled = false
            edtDriverName.isEnabled = false
            edtDriverExperience.isEnabled = false
            edtLicenceNumber.isEnabled = false
            edtAmbulanceVehicleNumber.isEnabled = false
            radioGrpd.isEnabled = false
            edtHospitalAddress.isEnabled = false
            edtState.isEnabled = false
            edtCity.isEnabled = false
            edtCountry.isEnabled = false
            btnSubmit.isEnabled = false
            tvDeactivate.isEnabled = false
        }
    }

    override fun typeData(type: String) {
        if (type == "Free") {
            binding.tvAmbulanceType.text = type
            TransitionManager.beginDelayedTransition(
                binding.root,
                AutoTransition()
            )
            binding.edtPriceFair.visibility = View.GONE
            binding.edtPriceFair.setText("0")
        } else {
            binding.tvAmbulanceType.text = type
            TransitionManager.beginDelayedTransition(
                binding.root,
                AutoTransition()
            )
            binding.edtPriceFair.visibility = View.VISIBLE
        }
    }

}