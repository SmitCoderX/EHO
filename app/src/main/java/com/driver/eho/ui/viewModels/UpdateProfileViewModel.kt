package com.driver.eho.ui.viewModels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.driver.eho.model.DriverSignInResponse
import com.driver.eho.repository.EHORepository
import com.driver.eho.utils.Constants
import com.driver.eho.utils.EHOApplication
import com.driver.eho.utils.Resources
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.IOException

class UpdateProfileViewModel(
    app: Application,
    private val repositroy: EHORepository
) : AndroidViewModel(app) {

    val updateProfileLiveData = MutableLiveData<Resources<DriverSignInResponse>>()

    fun updateProfile(
        token: String,
        userName: RequestBody,
        email: RequestBody,
        profileImage: MultipartBody.Part,
        documents: Array<MultipartBody.Part?>,
        mobile: RequestBody,
        name: RequestBody,
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
        safeHandleResponse(
            token,
            userName,
            email,
            profileImage,
            documents,
            mobile,
            name,
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

    private fun handleResponse(response: Response<DriverSignInResponse>): Resources<DriverSignInResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resources.Success(resultResponse)
            }
        }
        return Resources.Error(response.message())
    }

    private suspend fun safeHandleResponse(
        token: String,
        userName: RequestBody,
        email: RequestBody,
        profileImage: MultipartBody.Part,
        documents: Array<MultipartBody.Part?>,
        mobile: RequestBody,
        name: RequestBody,
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
        updateProfileLiveData.postValue(Resources.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repositroy.updateProfile(
                    token,
                    userName,
                    email,
                    profileImage,
                    documents,
                    mobile,
                    name,
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
                updateProfileLiveData.postValue(handleResponse(response))
            } else {
                updateProfileLiveData.postValue(Resources.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> updateProfileLiveData.postValue(Resources.Error("Network Failure"))
                else -> {
                    updateProfileLiveData.postValue(Resources.Error(t.message.toString()))
                    Log.d(Constants.TAG, "safeUpdateProfileCall: ${t.message}")
                }
            }
        }
    }


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