package com.driver.eho.utils

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.driver.eho.R
import com.driver.eho.model.BottomSheetModal
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody


open class AppUtils {
    open fun getRequestBody(value: String): RequestBody {
        return value.toRequestBody("multipart/form-data".toMediaTypeOrNull())
    }

    open fun overridePendingTransitionEnter(context: Context) {
        (context as Activity).overridePendingTransition(
            R.anim.slide_from_right,
            R.anim.slide_to_left
        )
    }

    open fun overridePendingTransitionExit(context: Context) {
        (context as Activity).overridePendingTransition(
            R.anim.trans_right_in,
            R.anim.trans_right_out
        )
    }

    open fun isConnectedToInternet(context: Context): Boolean {
        val cm: ConnectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo: NetworkInfo? = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnected && netInfo.isAvailable
    }
}