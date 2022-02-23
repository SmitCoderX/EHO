package com.driver.eho.model.Login


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Data(
    @SerializedName("ambulenceVehicleNumber")
    val ambulenceVehicleNumber: String? = null,
    @SerializedName("amount")
    val amount: Int? = null,
    @SerializedName("attachedWithHospital")
    val attachedWithHospital: Boolean? = null,
    @SerializedName("city")
    val city: String? = null,
    @SerializedName("country")
    val country: String? = null,
    @SerializedName("ambulanceType")
    val ambulanceType: String? = null,
    @SerializedName("ambulancePrice")
    val priceFair: Int? = null,
    @SerializedName("countryCode")
    val countryCode: String? = null,
    @SerializedName("createdAt")
    val createdAt: String? = null,
    @SerializedName("deviceName")
    val deviceName: String? = null,
    @SerializedName("deviceToken")
    val deviceToken: String? = null,
    @SerializedName("documents")
    val documents: List<String>? = null,
    @SerializedName("driverExperience")
    val driverExperience: String? = null,
    @SerializedName("driverLicenseNumber")
    val driverLicenseNumber: String? = null,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("firstName")
    val firstName: String? = null,
    @SerializedName("gender")
    val gender: String? = null,
    @SerializedName("hospitalAddress")
    val hospitalAddress: String? = null,
    @SerializedName("hospitalName")
    val hospitalName: String? = null,
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("image")
    val image: String? = null,
    @SerializedName("isActive")
    val isActive: Boolean? = null,
    @SerializedName("lastName")
    val lastName: String? = null,
    @SerializedName("latitude")
    val latitude: Float? = null,
    @SerializedName("location")
    val location: String? = null,
    @SerializedName("longitude")
    val longitude: Float? = null,
    @SerializedName("mobile")
    val mobile: String? = null,
    @SerializedName("mobileType")
    val mobileType: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("osVersion")
    val osVersion: String? = null,
    @SerializedName("password")
    val password: String? = null,
    @SerializedName("platform")
    val platform: String? = null,
    @SerializedName("state")
    val state: String? = null,
    @SerializedName("status")
    val status: String? = null,
    @SerializedName("token")
    val token: String? = null,
    @SerializedName("updatedAt")
    val updatedAt: String? = null,
    @SerializedName("userName")
    val userName: String? = null,
    @SerializedName("uuid")
    val uuid: String? = null,
    @SerializedName("__v")
    val v: Int? = null
) : Parcelable