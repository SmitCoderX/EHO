package com.driver.eho.model


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("ambulanceCharge")
    val ambulanceCharge: Int?,
    @SerializedName("bookingFee")
    val bookingFee: Int?,
    @SerializedName("bookingId")
    val bookingId: String?,
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("driverId")
    val driverId: String?,
    @SerializedName("_id")
    val id: String?,
    @SerializedName("paymentMode")
    val paymentMode: String?,
    @SerializedName("paymentStatus")
    val paymentStatus: String?,
    @SerializedName("total")
    val total: Int?,
    @SerializedName("transactionId")
    val transactionId: String?,
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