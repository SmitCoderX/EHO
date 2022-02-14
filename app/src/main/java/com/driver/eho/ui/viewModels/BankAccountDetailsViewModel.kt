package com.driver.eho.ui.viewModels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.driver.eho.model.BankAccountList
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

class BankAccountDetailsViewModel(
    application: Application,
    private val repository: EHORepository
) : AndroidViewModel(application) {

    val bankAccountDetailsLiveData = MutableLiveData<Resources<BankAccountList>>()
    val deleteBankDetailsLiveData = MutableLiveData<Resources<DriverSignUpResponse>>()

    fun deleteBankAccount(
        token: String,
        accountID: String
    ) = viewModelScope.launch {
        safeHandleDeleteAccount(token, accountID)
    }

    private fun handleDeleteAccount(response: Response<DriverSignUpResponse>): Resources<DriverSignUpResponse> {
        if (response.isSuccessful) {
            response.body().let { resultResponse ->
                return Resources.Success(resultResponse)
            }
        }
        return Resources.Error(response.errorBody()?.string().toString())
    }

    fun detailsList(token: String) =
        viewModelScope.launch {
            safeHandleList(token)
        }

    private fun handleList(response: Response<BankAccountList>): Resources<BankAccountList> {
        if (response.isSuccessful) {
            response.body().let { resultResponse ->
                return Resources.Success(resultResponse)
            }
        }
        return Resources.Error(response.message().toString())
    }

    private suspend fun safeHandleDeleteAccount(token: String, accountID: String) {
        deleteBankDetailsLiveData.postValue(Resources.Loading())
        try {
            if (hasInternetConnection()) {
                val params = JSONObject()
                params.put("accountId", accountID)
                val jsonParser = JsonParser()
                val parameter = jsonParser.parse(params.toString()) as JsonObject
                val response = repository.deactivateAccount(token, parameter)
                deleteBankDetailsLiveData.postValue(handleDeleteAccount(response))
            } else {
                deleteBankDetailsLiveData.postValue(Resources.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> deleteBankDetailsLiveData.postValue(Resources.Error("Network Failure"))
                else -> {
                    deleteBankDetailsLiveData.postValue(Resources.Error(t.message.toString()))
                    Log.d(TAG, "safeHandleHistoryList: ${t.message}")
                }
            }
        }
    }

    private suspend fun safeHandleList(
        token: String
    ) {
        bankAccountDetailsLiveData.postValue(Resources.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.getBankAccountList(token)
                bankAccountDetailsLiveData.postValue(handleList(response))
            } else {
                bankAccountDetailsLiveData.postValue(Resources.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> bankAccountDetailsLiveData.postValue(Resources.Error("Network Failure"))
                else -> {
                    bankAccountDetailsLiveData.postValue(Resources.Error(t.message.toString()))
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