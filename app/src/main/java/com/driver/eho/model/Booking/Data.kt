package com.driver.eho.model.Booking


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("bookingDate")
    val bookingDate: String?,
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("date")
    val date: String?,
    @SerializedName("distance")
    val distance: String?,
    @SerializedName("driverId")
    val driverId: String?,
    @SerializedName("dropAddress")
    val dropAddress: String?,
    @SerializedName("dropLatitude")
    val dropLatitude: Double?,
    @SerializedName("dropLongitude")
    val dropLongitude: Double?,
    @SerializedName("dropTime")
    val dropTime: String?,
    @SerializedName("hospitalAddress")
    val hospitalAddress: String?,
    @SerializedName("hospitalName")
    val hospitalName: String?,
    @SerializedName("hospitalPhone")
    val hospitalPhone: String?,
    @SerializedName("_id")
    val id: String?,
    @SerializedName("payment_status")
    val paymentStatus: Int?,
    @SerializedName("payment_status_string")
    val paymentStatusString: String?,
    @SerializedName("pickupAddress")
    val pickupAddress: String?,
    @SerializedName("pickupLatitude")
    val pickupLatitude: Double?,
    @SerializedName("pickupLongitude")
    val pickupLongitude: Double?,
    @SerializedName("pickupTime")
    val pickupTime: String?,
    @SerializedName("price")
    val price: Double?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("updatedAt")
    val updatedAt: String?,
    @SerializedName("userId")
    val userId: String?,
    @SerializedName("userImage")
    val userImage: String?,
    @SerializedName("userName")
    val userName: String?,
    @SerializedName("__v")
    val v: Int?
)