package com.driver.eho.model.Withdraw


import com.google.gson.annotations.SerializedName

data class WithdrawData(
    @SerializedName("amount")
    val amount: Int?,
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("date")
    val date: String?,
    @SerializedName("driverId")
    val driverId: String?,
    @SerializedName("_id")
    val id: String?,
    @SerializedName("paymentMode")
    val paymentMode: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("time")
    val time: String?,
    @SerializedName("transactionId")
    val transactionId: String?,
    @SerializedName("updatedAt")
    val updatedAt: String?,
    @SerializedName("__v")
    val v: Int?
)