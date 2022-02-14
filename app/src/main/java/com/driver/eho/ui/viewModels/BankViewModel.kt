package com.driver.eho.ui.viewModels

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
import com.driver.eho.utils.Constants.TAG
import com.driver.eho.utils.EHOApplication
import com.driver.eho.utils.Resources
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException

class BankViewModel(
    application: Application,
    private val repository: EHORepository
) : AndroidViewModel(application) {

    val bankAccountLiveData = MutableLiveData<Resources<DriverSignUpResponse>>()

    fun addBank(token: String, name: String, accountNumber: String, ifscCode: String) =
        viewModelScope.launch {
            safeHandleAccount(token, name, accountNumber, ifscCode)
        }

    private fun handleAccount(response: Response<DriverSignUpResponse>): Resources<DriverSignUpResponse> {
        if (response.isSuccessful) {
            response.body().let { resultResponse ->
                return Resources.Success(resultResponse)
            }
        }
        return Resources.Error(response.errorBody()?.string().toString())
    }

    private suspend fun safeHandleAccount(
        token: String,
        name: String, accountNumber: String, ifscCode: String
    ) {
        bankAccountLiveData.postValue(Resources.Loading())
        try {
            if (hasInternetConnection()) {
                val params = JSONObject()
                params.put("name", name)
                params.put("accountNumber", accountNumber)
                params.put("ifscCode", ifscCode)
                val jsonParser = JsonParser()
                val parameter = jsonParser.parse(params.toString()) as JsonObject
                val response = repository.addBankAccount(token, parameter)
                bankAccountLiveData.postValue(handleAccount(response))
            } else {
                bankAccountLiveData.postValue(Resources.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> bankAccountLiveData.postValue(Resources.Error("Network Failure"))
                else -> {
                    bankAccountLiveData.postValue(Resources.Error(t.message.toString()))
                    Log.d(TAG, "safeHandleHistoryList: ${t.message}")
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