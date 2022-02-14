package com.driver.eho.repository

import com.driver.eho.network.ApiClient
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody

class EHORepository {

    suspend fun loginDriver(parameter: JsonObject) =
        ApiClient.getInstance().getDataFromDriverSignIn(parameter)

    suspend fun registerDriver(
        userName: RequestBody,
        email: RequestBody,
        password: RequestBody,
        profileImage: MultipartBody.Part,
        documents: Array<MultipartBody.Part?>,
        mobile: RequestBody,
        name: RequestBody,
        hospitalName: RequestBody,
        hospitalAddress: RequestBody,
        state: RequestBody,
        city: RequestBody,
        country: RequestBody,
        driverExperience: RequestBody,
        driverLicenseNumber: RequestBody,
        ambulanceVehicleNumber: RequestBody,
        latitude: RequestBody,
        longitude: RequestBody
    ) = ApiClient.getInstance().getDataFromDriverRegister(
        userName = userName,
        email = email,
        password = password,
        profileImage = profileImage,
        documents = documents,
        mobile = mobile,
        name = name,
        hospitalName = hospitalName,
        hospitalAddress = hospitalAddress,
        state = state,
        city = city,
        country = country,
        driverExperience = driverExperience,
        driverLicenseNumber = driverLicenseNumber,
        ambulanceVehicleNumber = ambulanceVehicleNumber,
        latitude = latitude,
        longitude = longitude
    )

    suspend fun updateProfile(
        token: String,
        userName: RequestBody,
        email: RequestBody,
        profileImage: MultipartBody.Part?,
        documents: Array<MultipartBody.Part?>?,
        mobile: RequestBody,
        name: RequestBody,
        hospitalAddress: RequestBody,
        state: RequestBody,
        city: RequestBody,
        country: RequestBody,
        driverExperience: RequestBody,
        driverLicenseNumber: RequestBody,
        ambulanceVehicleNumber: RequestBody,
        latitude: RequestBody,
        longitude: RequestBody,
        isAttached: RequestBody
    ) = ApiClient.getInstance().updateProfile(
        token = token,
        userName = userName,
        email = email,
        profileImage = profileImage,
        documents = documents,
        mobile = mobile,
        name = name,
        hospitalAddress = hospitalAddress,
        state = state,
        city = city,
        country = country,
        driverExperience = driverExperience,
        driverLicenseNumber = driverLicenseNumber,
        ambulanceVehicleNumber = ambulanceVehicleNumber,
        latitude = latitude,
        longitude = longitude,
        isAttached = isAttached
    )

    suspend fun getBookingHistoryList(
        token: String,
        start: Int,
        items: Int
    ) = ApiClient.getInstance().getBookingHistoryList(
        token = token,
        start = start,
        items = items
    )

    suspend fun createSupport(token: String, parameter: JsonObject) =
        ApiClient.getInstance().createSupport(token, parameter)

    suspend fun addBankAccount(token: String, parameter: JsonObject) =
        ApiClient.getInstance().addBankAccount(token, parameter)

    suspend fun getBankAccountList(token: String) =
        ApiClient.getInstance().getBankAccountList(token)

    suspend fun getWithdrawlList(
        token: String,
        start: Int,
        items: Int
    ) = ApiClient.getInstance().getWithdrawList(
        token = token,
        start = start,
        items = items
    )

    suspend fun getReceiptData(token: String, bookingID: String) =
        ApiClient.getInstance().getReceiptData(token, bookingID)

    suspend fun logoutUser(token: String) = ApiClient.getInstance().logoutUser(token)

    suspend fun getNotification(
        token: String,
        start: Int,
        items: Int
    ) = ApiClient.getInstance().getNotificationList(token, start, items)

    suspend fun deactivateAccount(
        token: String,
        parameter: JsonObject
    ) = ApiClient.getInstance().deactivateAccount(token, parameter)

    suspend fun getDriverDetails(
        token: String
    ) = ApiClient.getInstance().getDriverDetails(token)
}