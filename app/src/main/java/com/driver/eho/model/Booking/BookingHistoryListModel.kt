package com.driver.eho.model.Booking


import com.google.gson.annotations.SerializedName

data class BookingHistoryListModel(
    @SerializedName("code")
    val code: Int?,
    @SerializedName("data")
    val data: List<Data>,
    @SerializedName("message")
    val message: String?
)