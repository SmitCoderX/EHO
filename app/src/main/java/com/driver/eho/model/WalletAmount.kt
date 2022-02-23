package com.driver.eho.model

import com.google.gson.annotations.SerializedName

data class WalletAmount(
    @SerializedName("amount")
    var amount: String? = "0"
)
