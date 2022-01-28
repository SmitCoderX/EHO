package com.driver.eho.network

import com.driver.eho.model.DriverSignInResponse
import com.driver.eho.model.DriverSignUpResponse
import com.driver.eho.network.ApiConstant.DRIVERSIGNIN
import com.driver.eho.network.ApiConstant.DRIVERSIGNUP
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*


interface ApiInterface {

    @POST(DRIVERSIGNIN)
    suspend fun getDataFromDriverSignIn(@Body parameter: JsonObject): Response<DriverSignInResponse>

    @Headers("Accept-Encoding: identity")
    @Multipart
    @POST(DRIVERSIGNUP)
    suspend fun getDataFromDriverRegister(
        @Part("userName") userName: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part profileImage: MultipartBody.Part,
        @Part documents: Array<MultipartBody.Part?>,
        @Part("mobile") mobile: RequestBody,
        @Part("name") name: RequestBody,
        @Part("hospitalName") hospitalName: RequestBody,
        @Part("hospitalAddress") hospitalAddress: RequestBody,
        @Part("state") state: RequestBody,
        @Part("city") city: RequestBody,
        @Part("country") country: RequestBody,
        @Part("driverExperience") driverExperience: RequestBody,
        @Part("driverLicenseNumber") driverLicenseNumber: RequestBody,
        @Part("ambulanceVehicleNumber") ambulanceVehicleNumber: RequestBody,
        @Part("latitude ") latitude: RequestBody,
        @Part("longitude ") longitude: RequestBody,
    ): Response<DriverSignUpResponse>

    /*@Headers("Accept-Encoding: identity")
    @Multipart
    @POST(DRIVERSIGNUP)
    fun getDataFromDriverRegister(@Part("userName") parameter: JsonObject,
                                  @Part("email") parameter: JsonObject,
                                  @Part("password")
                                  @Part BodyImg1: MultipartBody.Part): Response<DriverSignUpResponse>*/

}