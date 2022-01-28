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

}