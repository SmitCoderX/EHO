package com.driver.eho.ui.viewModels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.driver.eho.model.Booking.BookingHistoryListModel
import com.driver.eho.repository.EHORepository
import com.driver.eho.utils.Constants.TAG
import com.driver.eho.utils.EHOApplication
import com.driver.eho.utils.Resources
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class BookingHistoryViewModel(
    application: Application,
    private val repository: EHORepository
) : AndroidViewModel(application) {

    val bookingHistoryLiveData = MutableLiveData<Resources<BookingHistoryListModel>>()

    fun getBookingHistoryList(token: String, start: Int, items: Int) = viewModelScope.launch {
        safeHandleHistoryList(token, start, items)
    }

    private fun handleHistoryList(response: Response<BookingHistoryListModel>): Resources<BookingHistoryListModel> {
        if (response.isSuccessful) {
            response.body().let { resultResponse ->
                return Resources.Success(resultResponse)
            }
        }
        return Resources.Error(response.message().toString())
    }

    private suspend fun safeHandleHistoryList(
        token: String,
        start: Int, items: Int
    ) {
        bookingHistoryLiveData.postValue(Resources.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.getBookingHistoryList(token, start, items)
                bookingHistoryLiveData.postValue(handleHistoryList(response))
            } else {
                bookingHistoryLiveData.postValue(Resources.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> bookingHistoryLiveData.postValue(Resources.Error("Network Failure"))
                else -> {
                    bookingHistoryLiveData.postValue(Resources.Error(t.message.toString()))
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