package com.driver.eho.ui.viewModel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.driver.eho.model.DriverSignUpResponse
import com.driver.eho.repository.EHORepository
import com.driver.eho.utils.Constants
import com.driver.eho.utils.EHOApplication
import com.driver.eho.utils.Resources
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.IOException

class DriverSignUpViewModel(
    app: Application,
    private val repository: EHORepository
) : AndroidViewModel(app) {

    val registerMutableLiveData = MutableLiveData<Resources<DriverSignUpResponse>>()

    fun getRegisterCredentails(
        userName: RequestBody,
        email: RequestBody,
        password: RequestBody,
        profileImage: MultipartBody.Part,
        documents: Array<MultipartBody.Part?>,
        mobile: RequestBody,
        name: RequestBody,
        hospitalName: RequestBody,
        hospitalAddress: RequestBody,
        state: RequestBody,
        city: RequestBody,
        country: RequestBody,
        driverExperience: RequestBody,
        driverLicenseNumber: RequestBody,
        ambulanceVehicleNumber: RequestBody,
        latitude: RequestBody,
        longitude: RequestBody
    ) = viewModelScope.launch {
        safeRegisterCall(
            userName = userName,
            email = email,
            password = password,
            profileImage = profileImage,
            documents = documents,
            mobile = mobile,
            name = name,
            hospitalName = hospitalName,
            hospitalAddress = hospitalAddress,
            state = state,
            city = city,
            country = country,
            driverExperience = driverExperience,
            driverLicenseNumber = driverLicenseNumber,
            ambulanceVehicleNumber = ambulanceVehicleNumber,
            latitude = latitude,
            longitude = longitude
        )
    }

    private suspend fun safeRegisterCall(
        userName: RequestBody,
        email: RequestBody,
        password: RequestBody,
        profileImage: MultipartBody.Part,
        documents: Array<MultipartBody.Part?>,
        mobile: RequestBody,
        name: RequestBody,
        hospitalName: RequestBody,
        hospitalAddress: RequestBody,
        state: RequestBody,
        city: RequestBody,
        country: RequestBody,
        driverExperience: RequestBody,
        driverLicenseNumber: RequestBody,
        ambulanceVehicleNumber: RequestBody,
        latitude: RequestBody,
        longitude: RequestBody
    ) {
        registerMutableLiveData.postValue(Resources.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.registerDriver(
                    userName = userName,
                    email = email,
                    password = password,
                    profileImage = profileImage,
                    documents = documents,
                    mobile = mobile,
                    name = name,
                    hospitalName = hospitalName,
                    hospitalAddress = hospitalAddress,
                    state = state,
                    city = city,
                    country = country,
                    driverExperience = driverExperience,
                    driverLicenseNumber = driverLicenseNumber,
                    ambulanceVehicleNumber = ambulanceVehicleNumber,
                    latitude = latitude,
                    longitude = longitude
                )
                registerMutableLiveData.postValue(handleRegister(response))
            } else {
                registerMutableLiveData.postValue(Resources.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> registerMutableLiveData.postValue(Resources.Error("Network Failure"))
                else -> {
                    registerMutableLiveData.postValue(Resources.Error(t.message.toString()))
                    Log.d(Constants.TAG, "safeLoginCall: ${t.message}")
                }
            }
        }
    }

    private fun handleRegister(response: Response<DriverSignUpResponse>): Resources<DriverSignUpResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resources.Success(resultResponse)
            }
        }
        return Resources.Error(response.message())
    }


    /*   var UserName = MutableLiveData<String>()
       var HospitalName = MutableLiveData<String>()
       var MobileNumber = MutableLiveData<String>()
       var Email = MutableLiveData<String>()
       var DriverName = MutableLiveData<String>()
       var DriverExperience = MutableLiveData<String>()
       var LicenceNumber = MutableLiveData<String>()
       var AmbulanceVehicleNumber = MutableLiveData<String>()
       var HospitalAddress = MutableLiveData<String>()
       var State = MutableLiveData<String>()
       var City = MutableLiveData<String>()
       var Country = MutableLiveData<String>()
       val Password = MutableLiveData<String>()

       var userMutableLiveData: MutableLiveData<DriverSignUpUser>? = null

       val user: MutableLiveData<DriverSignUpUser>
           get() {
               if (userMutableLiveData == null) {
                   userMutableLiveData = MutableLiveData()
               }
               return userMutableLiveData!!
           }

       fun onclick(view: View?) {
           val driverSignUpUser = DriverExperience.value?.let {
               DriverSignUpUser(
                   UserName.value.toString(),
                   HospitalName.value.toString(),
                   MobileNumber.value.toString(),
                   Email.value.toString(),
                   DriverName.value.toString(),
                   it.toInt(),
                   LicenceNumber.value!!.toInt(),
                   AmbulanceVehicleNumber.value.toString(),
                   HospitalAddress.value.toString(),
                   State.value.toString(),
                   City.value.toString(),
                   Country.value.toString(),
                   Password.value.toString()
               )
           }
           userMutableLiveData!!.value = driverSignUpUser!!
       }*/

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<EHOApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilites =
            connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilites.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilites.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilites.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
        return false
    }

}