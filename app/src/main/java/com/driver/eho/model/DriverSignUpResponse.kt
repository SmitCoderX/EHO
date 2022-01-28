package com.driver.eho.model

import com.google.gson.annotations.SerializedName

data class DriverSignUpResponse(

    @field:SerializedName("code")
    val code: Int? = null,

    @field:SerializedName("message")
    val message: String? = null
)