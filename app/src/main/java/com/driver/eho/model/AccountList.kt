package com.driver.eho.model


import com.google.gson.annotations.SerializedName

data class AccountList(
    @SerializedName("accountNumber")
    val accountNumber: String?,
    @SerializedName("bankName")
    val bankName: String?,
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("driverId")
    val driverId: String?,
    @SerializedName("_id")
    val id: String?,
    @SerializedName("ifscCode")
    val ifscCode: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("updatedAt")
    val updatedAt: String?,
    @SerializedName("__v")
    val v: Int?
)