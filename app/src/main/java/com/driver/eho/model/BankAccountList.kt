package com.driver.eho.model


import com.google.gson.annotations.SerializedName

data class BankAccountList(
    @SerializedName("code")
    val code: Int?,
    @SerializedName("data")
    val data: List<AccountList>?,
    @SerializedName("message")
    val message: String?
)