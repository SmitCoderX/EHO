package com.driver.eho.model.Withdraw


import com.google.gson.annotations.SerializedName

data class WithdrawModel(
    @SerializedName("code")
    val code: Int?,
    @SerializedName("data")
    val data: List<WithdrawData>?,
    @SerializedName("message")
    val message: String?
)