package com.driver.eho.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class DriverSignInResponse(

    @field:SerializedName("code")
    val code: Int? = null,

    @field:SerializedName("data")
    val data: LoginData? = null,

    @field:SerializedName("message")
    val message: String? = null
) : Parcelable

@Parcelize
data class LoginData(

    @field:SerializedName("driverExperience")
    val driverExperience: Int? = null,

    @field:SerializedName("image")
    val image: String? = null,

    @field:SerializedName("city")
    val city: String? = null,

    @field:SerializedName("documents")
    val documents: List<String?>? = null,

    @field:SerializedName("mobile")
    val mobile: Long? = null,

    @field:SerializedName("hospitalAddress")
    val hospitalAddress: String? = null,

    @field:SerializedName("hospitalName")
    val hospitalName: String? = null,

    @field:SerializedName("userName")
    val userName: String? = null,

    @field:SerializedName("token")
    val token: String? = "",

    @field:SerializedName("createdAt")
    val createdAt: String? = null,

    @field:SerializedName("password")
    val password: String? = null,

    @field:SerializedName("driverLicenseNumber")
    val driverLicenseNumber: String? = null,

    @field:SerializedName("__v")
    val V: Int? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("_id")
    val id: String? = null,

    @field:SerializedName("state")
    val state: String? = null,

    @field:SerializedName("email")
    val email: String? = null,

    @field:SerializedName("updatedAt")
    val updatedAt: String? = null
) : Parcelable
