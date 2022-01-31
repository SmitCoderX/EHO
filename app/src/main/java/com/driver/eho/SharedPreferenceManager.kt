package com.driver.eho

import android.content.Context
import android.content.SharedPreferences
import com.driver.eho.model.DriverSignInResponse
import com.driver.eho.utils.Constants.DATA
import com.driver.eho.utils.Constants.LOGGEDIN
import com.driver.eho.utils.Constants.SP_NAME
import com.driver.eho.utils.Constants.TOKEN
import com.google.gson.Gson

/**
 * Created by Pintoo  on 07/01/2022.
 */
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

    fun logoutUser() {
        sharedPreferences.edit().clear().apply()
    }
}