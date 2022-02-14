package com.driver.eho.utils

import android.graphics.Color
import android.view.View
import com.google.android.material.snackbar.Snackbar

object Constants {
    const val TAG = "EHO"
    const val DRIVERSDATA = "DriverData"
    const val SP_NAME = "EHO_DRIVER"
    const val LOGGEDIN = "LoggedIn"
    const val TOKEN = "tokenValue"
    const val DATA = "BookingData"
    const val SPLASH_DELAY = 3000L
    const val BOOKING_ID = ""
    const val IMAGE_URL = "https://test-eho.s3.ap-south-1.amazonaws.com/"
    const val LAT = "Lat"
    const val LONG = "Long"
    const val TOGGLE_STATE = "ToggleState"
    const val REQUEST = "Request"


    fun snackbarError(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).setBackgroundTint(Color.RED)
            .setTextColor(Color.WHITE).show()
    }

    fun snackbarSuccess(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).setBackgroundTint(Color.GREEN)
            .setTextColor(Color.BLACK).show()
    }
}