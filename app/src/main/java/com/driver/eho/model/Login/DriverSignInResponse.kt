package com.driver.eho.model.Login


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class DriverSignInResponse(
    @SerializedName("code")
    val code: Int? = null,
    @SerializedName("data")
    val data: Data? = null,
    @SerializedName("message")
    val message: String? = null
) : Parcelable