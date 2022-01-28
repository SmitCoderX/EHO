package com.driver.eho

import android.content.Context
import android.content.SharedPreferences
import com.driver.eho.network.ApiConstant

/**
 * Created by Pintoo  on 07/01/2022.
 */
class SharedPreferenceManager (val context: Context) {
     private val SP_NAME = "EHO_DRIVER"
     private val sharedPreferences: SharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)

     fun get_Id(): String? {
          return sharedPreferences.getString(ApiConstant._Id, "")
     }

     fun set_Id(_id: String?) {
          sharedPreferences.edit()!!.putString(ApiConstant._Id, _id).apply()
     }

     fun get_Email(): String? {
          return sharedPreferences.getString(ApiConstant._Id, "")
     }

     fun set_Email(email: String?) {
          sharedPreferences.edit()!!.putString(ApiConstant.EMAIL, email).apply()
     }
}