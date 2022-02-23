package com.driver.eho.ui.viewModels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.driver.eho.model.Login.DriverSignInResponse
import com.driver.eho.model.MessageResponseModal
import com.driver.eho.repository.EHORepository
import com.driver.eho.utils.Constants
import com.driver.eho.utils.EHOApplication
import com.driver.eho.utils.Resources
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
    val driverMutableLiveData = MutableLiveData<Resources<DriverSignInResponse>>()
    val deactivateMutableLiveData = MutableLiveData<Resources<MessageResponseModal>>()

    fun getDriverDetails(token: String) = viewModelScope.launch {
        safeHandleDriverDetails(token)
    }

    private fun handleDriverDetails(response: Response<DriverSignInResponse>): Resources<DriverSignInResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resources.Success(resultResponse)
            }
        }
        return Resources.Error(response.body()?.message.toString())
    }

    private suspend fun safeHandleDriverDetails(token: String) {
        driverMutableLiveData.postValue(Resources.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repositroy.getDriverDetails(token)
                driverMutableLiveData.postValue(handleDriverDetails(response))
            } else {
                driverMutableLiveData.postValue(Resources.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> driverMutableLiveData.postValue(Resources.Error("Network Failure"))
                else -> {
                    driverMutableLiveData.postValue(Resources.Error(t.message.toString()))
                    Log.d(Constants.TAG, "safeDriverDetails: ${t.message}")
                }
            }
        }
    }


    fun deactivateDriver(token: String) = viewModelScope.launch {
        safeHandleDeactivateDriver(token)
    }

    private fun handleDeactivateDriver(response: Response<MessageResponseModal>): Resources<MessageResponseModal> {
        if (response.isSuccessful) {
            response.body().let { resultResponse ->
                return Resources.Success(resultResponse)
            }
        }
        if(response.code() == 500) {
            return Resources.Error("Something Went Wrong!!")
        }
        val gson = Gson()
        val type = object : TypeToken<MessageResponseModal>() {}.type
        val errorResponse: MessageResponseModal? =
            gson.fromJson(response.errorBody()!!.charStream(), type)
        return Resources.Error(errorResponse?.message.toString())
    }

    private suspend fun safeHandleDeactivateDriver(token: String) {
        deactivateMutableLiveData.postValue(Resources.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repositroy.deactivateDriver(token)
                deactivateMutableLiveData.postValue(handleDeactivateDriver(response))
            } else {
                deactivateMutableLiveData.postValue(Resources.Error("No Internet Connection!!"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> deactivateMutableLiveData.postValue(Resources.Error("Network Failure"))
                else -> {
                    deactivateMutableLiveData.postValue(Resources.Error(t.message.toString()))
                    Log.d(Constants.TAG, "deactivateMutableLiveData: ${t.message}")
                }
            }
        }
    }


    fun updateProfile(
        token: String,
        userName: RequestBody,
        email: RequestBody,
        profileImage: MultipartBody.Part? = null,
        documents: Array<MultipartBody.Part?>? = null,
        mobile: RequestBody,
        name: RequestBody,
        hospitalAddress: RequestBody,
        state: RequestBody,
        city: RequestBody,
        country: RequestBody,
        ambulanceType: RequestBody,
        ambulancePrice: RequestBody,
        driverExperience: RequestBody,
        driverLicenseNumber: RequestBody,
        ambulanceVehicleNumber: RequestBody,
        latitude: RequestBody,
        longitude: RequestBody,
        isAttached: RequestBody
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
            ambulanceType,
            ambulancePrice,
            driverExperience,
            driverLicenseNumber,
            ambulanceVehicleNumber,
            latitude,
            longitude,
            isAttached
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
        profileImage: MultipartBody.Part?,
        documents: Array<MultipartBody.Part?>?,
        mobile: RequestBody,
        name: RequestBody,
        hospitalAddress: RequestBody,
        state: RequestBody,
        city: RequestBody,
        country: RequestBody,
        ambulanceType: RequestBody,
        ambulancePrice: RequestBody,
        driverExperience: RequestBody,
        driverLicenseNumber: RequestBody,
        ambulanceVehicleNumber: RequestBody,
        latitude: RequestBody,
        longitude: RequestBody,
        isAttached: RequestBody
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
                    ambulanceType,
                    ambulancePrice,
                    driverExperience,
                    driverLicenseNumber,
                    ambulanceVehicleNumber,
                    latitude,
                    longitude,
                    isAttached
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