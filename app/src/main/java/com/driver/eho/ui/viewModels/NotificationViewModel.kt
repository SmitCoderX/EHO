package com.driver.eho.ui.viewModels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.driver.eho.model.Notification.Notification
import com.driver.eho.repository.EHORepository
import com.driver.eho.utils.Constants
import com.driver.eho.utils.EHOApplication
import com.driver.eho.utils.Resources
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NotificationViewModel(
    app: Application,
    val repository: EHORepository
) : AndroidViewModel(app) {

    val notificationMutableLiveData = MutableLiveData<Resources<Notification>>()

    fun getNotifcationList(token: String, items: Int, start: Int) = viewModelScope.launch {
        safeHandleNotification(token, items, start)
    }

    private fun handleNotification(response: Response<Notification>): Resources<Notification> {
        if (response.isSuccessful) {
            response.body().let { resultResponse ->
                return Resources.Success(resultResponse)
            }
        }
        return Resources.Error(response.errorBody()?.string().toString())
    }

    private suspend fun safeHandleNotification(token: String, items: Int, start: Int) {
        notificationMutableLiveData.postValue(Resources.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.getNotification(token, start, items)
                notificationMutableLiveData.postValue(handleNotification(response))
            } else {
                notificationMutableLiveData.postValue(Resources.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> notificationMutableLiveData.postValue(Resources.Error("Network Failure"))
                else -> {
                    notificationMutableLiveData.postValue(Resources.Error(t.message.toString()))
                    Log.d(Constants.TAG, "safeLoginCall: ${t.message}")
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