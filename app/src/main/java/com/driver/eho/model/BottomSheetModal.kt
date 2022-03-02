package com.driver.eho.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class BottomSheetModal(
    @SerializedName("bookingId")
    var bookingId: String? = "",
    @SerializedName("distance")
    var distance: String? = "",
    @SerializedName("dropLatitude")
    var dropLatitude: Double? = null,
    @SerializedName("dropLocation")
    var dropLocation: String? = "",
    @SerializedName("dropLongitude")
    var dropLongitude: Double? = null,
    @SerializedName("pickupLocation")
    var pickupLocation: String? = "",
    @SerializedName("pickupLongitude")
    var pickupLongitude: Double? = null,
    @SerializedName("price")
    var price: Int? = null,
    @SerializedName("userId")
    var userId: String? = "",
    @SerializedName("userImage")
    var userImage: String? = "",
    @SerializedName("userMobile")
    var userMobile: String? = "",
    @SerializedName("userName")
    var userName: String? = "",
    @SerializedName("paymentMode")
    var paymentMode: String? = "",
    @SerializedName("ambulanceCharge")
    var ambulanceCharge: String? = "",
    @SerializedName("bookingFee")
    var bookingFee: String? = "",
    @SerializedName("name")
    var name: String? = ""

    /*paymentMode,
        ambulanceCharge,
        bookingFee*/
) : Parcelable