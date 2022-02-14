package com.driver.eho.model.Notification


import com.google.gson.annotations.SerializedName

data class Notification(
    @SerializedName("code")
    val code: Int?,
    @SerializedName("data")
    val notificationData: List<NotificationData>?,
    @SerializedName("message")
    val message: String?
)