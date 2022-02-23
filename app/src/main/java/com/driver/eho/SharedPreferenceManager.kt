package com.driver.eho

import android.content.Context
import android.content.SharedPreferences
import com.driver.eho.model.Login.DriverSignInResponse
import com.driver.eho.utils.Constants.BALANCE
import com.driver.eho.utils.Constants.DATA
import com.driver.eho.utils.Constants.FCM_TOKEN
import com.driver.eho.utils.Constants.LAT
import com.driver.eho.utils.Constants.LOGGEDIN
import com.driver.eho.utils.Constants.LONG
import com.driver.eho.utils.Constants.SP_NAME
import com.driver.eho.utils.Constants.TOGGLE_STATE
import com.driver.eho.utils.Constants.TOKEN
import com.google.gson.Gson

class SharedPreferenceManager(val context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)

    fun setLoggedIn(loggedIn: Boolean) {
        sharedPreferences.edit().putBoolean(LOGGEDIN, loggedIn).apply()
    }

    fun getLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(LOGGEDIN, false)
    }

    fun setToken(token: String) {
        sharedPreferences.edit().putString(TOKEN, token).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString(TOKEN, "")
    }

    fun setData(data: DriverSignInResponse) {
        val parcedData = Gson().toJson(data)
        sharedPreferences.edit().putString(DATA, parcedData).apply()
    }

    fun getData(): DriverSignInResponse? {
        val parcedData = sharedPreferences.getString(DATA, "")
        return Gson().fromJson(parcedData, DriverSignInResponse::class.java)
    }

    fun setLat(latitude: String) {
        sharedPreferences.edit().putString(LAT, latitude).apply()
    }

    fun getLat(): String? {
        return sharedPreferences.getString(LAT, "")
    }

    fun setLong(longitude: String) {
        sharedPreferences.edit().putString(LONG, longitude).apply()
    }

    fun getLong(): String? {
        return sharedPreferences.getString(LONG, "")
    }

    fun logoutUser() {
        sharedPreferences.edit().clear().apply()
    }

    fun setToggleState(toggleState: Boolean) {
        sharedPreferences.edit().putBoolean(TOGGLE_STATE, toggleState).apply()
    }

    fun getToggleState(): Boolean {
        return sharedPreferences.getBoolean(TOGGLE_STATE, false)
    }

    fun setLatestBalance(balance: String) {
        sharedPreferences.edit().putString(BALANCE, balance).apply()
    }

    fun getLatestBalance(): String? {
        return sharedPreferences.getString(BALANCE, "")
    }

    fun setFCMToken(fcm_token: String) {
        sharedPreferences.edit().putString(FCM_TOKEN, fcm_token).apply()
    }

    fun getFCMToken(): String? {
        return sharedPreferences.getString(FCM_TOKEN, "")
    }
}