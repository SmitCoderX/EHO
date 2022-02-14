package com.driver.eho.model


import com.google.gson.annotations.SerializedName

data class ReceiptModel(
    @SerializedName("code")
    val code: Int?,
    @SerializedName("data")
    val `data`: Data?,
    @SerializedName("message")
    val message: String?
)