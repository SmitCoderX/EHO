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
import com.driver.eho.repository.EHORepository
import com.driver.eho.utils.Constants
import com.driver.eho.utils.EHOApplication
import com.driver.eho.utils.Resources
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class HomeViewModel(
    app: Application,
    private val repository: EHORepository
) : AndroidViewModel(app) {

    val driverMutableLiveData = MutableLiveData<Resources<DriverSignInResponse>>()

    fun getDriverDetails(token: String) = viewModelScope.launch {
        safeHandleDriverDetails(token)
    }

    private fun handleDriverDetails(response: Response<DriverSignInResponse>): Resources<DriverSignInResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resources.Success(resultResponse)
            }
        }
        val gson = Gson()
        val type = object : TypeToken<DriverSignInResponse>() {}.type
        val errorResponse: DriverSignInResponse? =
            gson.fromJson(response.errorBody()!!.charStream(), type)
        return Resources.Error(errorResponse?.message.toString())
    }

    private suspend fun safeHandleDriverDetails(token: String) {
        driverMutableLiveData.postValue(Resources.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.getDriverDetails(token)
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