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
import com.driver.eho.utils.Constants.TAG
import com.driver.eho.utils.EHOApplication
import com.driver.eho.utils.Resources
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException

class DriverSignInViewModel(
    application: Application,
    private val repository: EHORepository
) : AndroidViewModel(application) {

    val loginLiveData = MutableLiveData<Resources<DriverSignInResponse>>()

    fun getLoginCredentials(email: String, password: String) = viewModelScope.launch {
        safeLoginCall(email, password)
    }

    private fun handleLogin(response: Response<DriverSignInResponse>): Resources<DriverSignInResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resources.Success(resultResponse)
            }
        }
        return Resources.Error(response.message())
    }

    private suspend fun safeLoginCall(email: String, password: String) {
        loginLiveData.postValue(Resources.Loading())
        try {
            if (hasInternetConnection()) {
                val params = JSONObject()
                params.put("email", email)
                params.put("password", password)
                val jsonParser = JsonParser()
                val parameter = jsonParser.parse(params.toString()) as JsonObject
                val response = repository.loginDriver(parameter)
                loginLiveData.postValue(handleLogin(response))
            } else {
                loginLiveData.postValue(Resources.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> loginLiveData.postValue(Resources.Error("Network Failure"))
                else -> {
                    loginLiveData.postValue(Resources.Error(t.message.toString()))
                    Log.d(TAG, "safeLoginCall: ${t.message}")
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