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
import com.driver.eho.repository.EHORepository
import com.driver.eho.utils.Constants.TAG
import com.driver.eho.utils.EHOApplication
import com.driver.eho.utils.Resources
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class BankAccountDetailsViewModel(
    application: Application,
    private val repository: EHORepository
) : AndroidViewModel(application) {

    val bankAccountDetailsLiveData = MutableLiveData<Resources<BankAccountList>>()

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