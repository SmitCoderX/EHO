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
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.driver.eho.adapter.HorizontalRecyclerView
import com.driver.eho.databinding.ActivityRegisterBinding
import com.driver.eho.ui.viewModel.viewModelFactory.DriverSignUpViewModelProviderFactory
import com.driver.eho.utils.Constants.TAG
import com.driver.eho.utils.Constants.snackbarError
import com.driver.eho.utils.EHOApplication
import com.driver.eho.utils.Resources
import com.driver.eho.utils.UploadRequestBody
import com.driver.eho.utils.getFileName
import com.driver.eho.ui.viewModels.DriverSignUpViewModel
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

class RegisterActivity : BaseActivity(), UploadRequestBody.UploadCallback {

    private lateinit var binding: ActivityRegisterBinding
    private var uri: ArrayList<Uri> = ArrayList()
    var recyclerView: RecyclerView? = null
    private var adapter: HorizontalRecyclerView? = null
    private var pictureDoc: Uri? = null
    private var currentPhotoPath: String = ""
    private var selectedImage: Uri? = null
    private val driverSignUpViewModel: DriverSignUpViewModel by viewModels {
        DriverSignUpViewModelProviderFactory(
            application,
            (application as EHOApplication).repository
        )
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.ivCam.setOnClickListener {
            checkPermissionForProfileImage()
        }

        // adapter fot upload document
        recyclerView = binding.rvDocument
        adapter = HorizontalRecyclerView(uri)
        recyclerView!!.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView!!.adapter = adapter

        // add upload document
        binding.ivAddDocument.setOnClickListener {
            addDocumentFromgelley()
        }

        binding.btnSubmit.setOnClickListener {
            if (validate()) {
                binding.apply {
                    driverSignUpApiCall(
                        edtUserName.text.toString().trim(),
                        edtHospitalName.text.toString().trim(),
                        edtMobileNumber.text.toString().trim(),
                        edtEmail.text.toString().trim(),
                        edtDriverName.text.toString().trim(),
                        edtDriverExperience.text.toString().trim().toInt(),
                        edtLicenceNumber.text.toString().trim().toInt(),
                        edtAmbulanceVehicleNumber.text.toString().trim(),
                        edtHospitalAddress.text.toString().trim(),
                        edtState.text.toString().trim(),
                        edtCity.text.toString().trim(),
                        edtCountry.text.toString().trim(),
                        edtPassword.text.toString().trim()
                    )
                }
            }
        }

        getRegisterData()
    }

    // api call for driver SignUp
    private fun driverSignUpApiCall(
        UserName: String,
        HospitalName: String,
        MobileNumber: String,
        Email: String,
        DriverName: String,
        DriverExperience: Int,
        LicenceNumber: Int,
        AmbulanceVehicleNumber: String,
        HospitalAddress: String,
        State: String,
        City: String,
        Country: String,
        Password: String
    ) {

        if (selectedImage == null) {
            Toast.makeText(this, "Select An Image First", Toast.LENGTH_SHORT).show()
            return
        }
        if (uri.isNullOrEmpty()) {
            Toast.makeText(this, "Select An document First", Toast.LENGTH_SHORT).show()
            return
        }

        val parcelFileDescriptor =
            contentResolver.openFileDescriptor(selectedImage!!, "r", null) ?: return
        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
        val file = File(cacheDir, contentResolver.getFileName(selectedImage!!))
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)

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
        }

        val imageBody = UploadRequestBody(file, "file", this)
        val image_body = MultipartBody.Part.createFormData("profileImage", file.name, imageBody)
        val userName: RequestBody =
            UserName.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val email: RequestBody =
            Email.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val password: RequestBody =
            Password.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val mobile: RequestBody =
            MobileNumber.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val name: RequestBody =
            DriverName.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val hospitalName: RequestBody =
            HospitalName.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val hospitalAddress: RequestBody =
            HospitalAddress.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val state: RequestBody =
            State.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val city: RequestBody =
            City.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val country: RequestBody =
            Country.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val driverExperience: RequestBody =
            DriverExperience.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val driverLicenseNumber: RequestBody =
            LicenceNumber.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val ambulanceVehicleNumber: RequestBody =
            AmbulanceVehicleNumber.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val latitude: RequestBody =
            "latitude".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val longitude: RequestBody =
            "longitude".toRequestBody("multipart/form-data".toMediaTypeOrNull())

        driverSignUpViewModel.getRegisterCredentails(
            userName,
            email,
            password,
            image_body,
            listofImage,
            mobile,
            name,
            hospitalName,
            hospitalAddress,
            state,
            city,
            country,
            driverExperience,
            driverLicenseNumber,
            ambulanceVehicleNumber,
            latitude,
            longitude
        )
    }

    // all type validations
    private fun validate(): Boolean {
        var valid = true

        val userName = binding.edtUserName.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()
        val fullName = binding.edtHospitalName.text.toString().trim()
        val mobileNumber = binding.edtMobileNumber.text.toString().trim()
        val email = binding.edtEmail.text.toString().trim()
        val driverName = binding.edtDriverName.text.toString().trim()
        val driverExperience = binding.edtDriverExperience.text.toString().trim()
        val licenceNumber = binding.edtLicenceNumber.text.toString().trim()
        val anbVehicleNumber = binding.edtAmbulanceVehicleNumber.text.toString().trim()
        val state = binding.edtState.text.toString().trim()
        val city = binding.edtCity.text.toString().trim()
        val country = binding.edtCountry.text.toString().trim()
        val hospitalAddress = binding.edtHospitalAddress.text.toString().trim()

        // UserName
        if (TextUtils.isEmpty(userName)) {
            binding.edtUserName.error = "Enter your Username"
            valid = false
        } else {
            binding.edtUserName.error = null
        }

        // Password
        if (TextUtils.isEmpty(password)) {
            binding.edtPassword.error = "Enter your Password"
            valid = false
        } else {
            binding.edtPassword.error = null
        }

        // fullName
        if (TextUtils.isEmpty(fullName) || fullName < 3.toString()) {
            binding.edtHospitalName.error = "Enter your Hospital Name"
            snackbarError(binding.root, "Enter your Hospital Name")
            valid = false
        } else {
            binding.edtHospitalName.error = null
        }
        // mobileNumber
        if (TextUtils.isEmpty(mobileNumber) && !Patterns.PHONE.matcher(mobileNumber).matches()) {
            binding.edtMobileNumber.error = "Enter your correct mobile Number"
            snackbarError(binding.root, "Enter your correct mobile Number")
            valid = false
        } else {
            binding.edtMobileNumber.error = null
        }
        // driver name
        if (TextUtils.isEmpty(driverName)) {
            binding.edtDriverName.error = "Enter driver name"
            snackbarError(binding.root, "Enter driver name")
            valid = false
        } else {
            binding.edtDriverName.error = null
        }
        // email
        if (TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.edtEmail.error = "Enter your correct email"
            snackbarError(binding.root, "Enter your correct email")
            valid = false
        } else {
            binding.edtEmail.error = null
        }
        // driverExperience
        if (TextUtils.isEmpty(driverExperience)) {
            binding.edtDriverExperience.error = "Enter your driver experience"
            snackbarError(binding.root, "Enter your driver Experience")
            valid = false
        } else {
            binding.edtDriverExperience.error = null
        }
        // licenceNumber
        if (TextUtils.isEmpty(licenceNumber)) {
            binding.edtLicenceNumber.error = "Enter your licence number"
            snackbarError(binding.root, "Enter your Licence Number")
            valid = false
        } else {
            binding.edtLicenceNumber.error = null
        }
        // anbVehicleNumber
        if (TextUtils.isEmpty(anbVehicleNumber)) {
            binding.edtAmbulanceVehicleNumber.error = "Enter your ambulance vehicle number"
            snackbarError(binding.root, "Enter your ambulance vehicle number")
            valid = false
        } else {
            binding.edtAmbulanceVehicleNumber.error = null
        }
        // state
        if (TextUtils.isEmpty(state)) {
            binding.edtState.error = "Enter your state"
            snackbarError(binding.root, "Enter your state")
            valid = false
        } else {
            binding.edtState.error = null
        }
        // city
        if (TextUtils.isEmpty(city)) {
            binding.edtCity.error = "Enter your city"
            snackbarError(binding.root, "Enter your city")
            valid = false
        } else {
            binding.edtCity.error = null
        }
        // country
        if (TextUtils.isEmpty(country)) {
            binding.edtCountry.error = "Enter your Country"
            snackbarError(binding.root, "Enter your country")
            valid = false
        } else {
            binding.edtCountry.error = null
        }
        // address
        if (TextUtils.isEmpty(hospitalAddress)) {
            binding.edtHospitalAddress.error = "Enter hospital address"
            snackbarError(binding.root, "Enter Hospital Address")
            valid = false
        } else {
            binding.edtHospitalAddress.error = null
        }
        return valid
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
        intent.type = "image/* "
        startActivityForResult(intent, 2)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getImageFromCamera() {
        dispatchTakePictureIntent()
        /*val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePicture, 0)*/ //zero can be replaced with any action code (called requestCode)
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
            when (requestCode) {
                0 -> if (resultCode == RESULT_OK) {
                    Glide.with(this).load(currentPhotoPath).into(binding.ivProfile)
                }
                1 -> if (resultCode == RESULT_OK) {
                    selectedImage = data!!.data
                    binding.ivProfile.setImageURI(selectedImage)
                }
                2 -> {
                    if (resultCode == RESULT_OK) {
                        if (data!!.clipData != null) {
                            val count: Int = data.clipData!!.itemCount
                            for (i in 0 until count) {
                                uri.add(data.clipData!!.getItemAt(i).uri)
                            }
                            adapter!!.notifyDataSetChanged()
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
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onProgressUpdate(percentage: Int) {
        Log.d("TAG", "Upload Image $percentage")
    }

    private fun getRegisterData() {
        driverSignUpViewModel.registerMutableLiveData.observe(this) { resources ->
            when (resources) {
                is Resources.Success -> {
                    hideLoadingView()
                    startActivity(Intent(this, LoginActivity::class.java))
                }

                is Resources.Error -> {
                    hideLoadingView()
                    snackbarError(binding.root, resources.message.toString())

                    Log.d(TAG, "getRegisterData: ${resources.message}")
                }
                is Resources.Loading -> {
                    showLoadingView()
                }
            }
        }
    }

    private fun showLoadingView() {
        binding.viewLoader.visibility = View.VISIBLE
    }

    private fun hideLoadingView() {
        binding.viewLoader.visibility = View.GONE
    }

}