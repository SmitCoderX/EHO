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
import com.driver.eho.utils.Constants.TAG
import com.driver.eho.utils.EHOApplication
import com.driver.eho.utils.Resources
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException

class DriverSignInViewModel(
    application: Application,
    private val repository: EHORepository
) : AndroidViewModel(application) {

    val loginLiveData = MutableLiveData<Resources<DriverSignInResponse>>()
    val fpLiveData = MutableLiveData<Resources<MessageResponseModal>>()

    fun forgotPassword(email: String) = viewModelScope.launch {
        safeHandleFp(email)
    }

    private fun handleFp(response: Response<MessageResponseModal>): Resources<MessageResponseModal> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resources.Success(resultResponse)
            }
        }
        val gson = Gson()
        val type = object : TypeToken<MessageResponseModal>() {}.type
        val errorResponse: MessageResponseModal? =
            gson.fromJson(response.errorBody()!!.charStream(), type)
        return Resources.Error(errorResponse?.message.toString())
    }


    private suspend fun safeHandleFp(email: String) {
        fpLiveData.postValue(Resources.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.forgotPassword(email)
                fpLiveData.postValue(handleFp(response))
            } else {
                fpLiveData.postValue(Resources.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> fpLiveData.postValue(Resources.Error("Network Failure"))
                else -> {
                    fpLiveData.postValue(Resources.Error(t.message.toString()))
                    Log.d(TAG, "safeLoginCall: ${t.message}")
                }
            }
        }
    }

    fun getLoginCredentials(email: String, password: String, token: String) =
        viewModelScope.launch {
            safeLoginCall(email, password, token)
        }

    private fun handleLogin(response: Response<DriverSignInResponse>): Resources<DriverSignInResponse> {
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

    private suspend fun safeLoginCall(email: String, password: String, token: String) {
        loginLiveData.postValue(Resources.Loading())
        try {
            if (hasInternetConnection()) {
                val params = JSONObject()
                params.put("email", email)
                params.put("password", password)
                params.put("deviceToken", token)
                val jsonParser = JsonParser()
                val parameter = jsonParser.parse(params.toString()) as JsonObject
                val response = repository.loginDriver(parameter)
                Log.d(TAG, "safeLoginCall: $parameter")
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