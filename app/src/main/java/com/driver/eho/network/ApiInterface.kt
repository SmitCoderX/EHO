package com.driver.eho.network

import com.driver.eho.model.BankAccountList
import com.driver.eho.model.Booking.BookingHistoryListModel
import com.driver.eho.model.DriverSignUpResponse
import com.driver.eho.model.Login.DriverSignInResponse
import com.driver.eho.model.Notification.Notification
import com.driver.eho.model.ReceiptModel
import com.driver.eho.model.Withdraw.WithdrawModel
import com.driver.eho.network.ApiConstant.DRIVERPROFILEUPDATE
import com.driver.eho.network.ApiConstant.DRIVERSIGNIN
import com.driver.eho.network.ApiConstant.DRIVERSIGNUP
import com.driver.eho.network.ApiConstant.GETBOOKINGLIST
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

    @Multipart
    @POST(DRIVERPROFILEUPDATE)
    suspend fun updateProfile(
        @Header("x-access-token") token: String,
        @Part("userName") userName: RequestBody,
        @Part("email") email: RequestBody,
        @Part profileImage: MultipartBody.Part,
        @Part documents: Array<MultipartBody.Part?>,
        @Part("mobile") mobile: RequestBody,
        @Part("name") name: RequestBody,
        @Part("hospitalAddress") hospitalAddress: RequestBody,
        @Part("state") state: RequestBody,
        @Part("city") city: RequestBody,
        @Part("country") country: RequestBody,
        @Part("driverExperience") driverExperience: RequestBody,
        @Part("driverLicenseNumber") driverLicenseNumber: RequestBody,
        @Part("ambulanceVehicleNumber") ambulanceVehicleNumber: RequestBody,
        @Part("latitude ") latitude: RequestBody,
        @Part("longitude ") longitude: RequestBody,
    ): Response<DriverSignInResponse>

    @GET(GETBOOKINGLIST)
    suspend fun getBookingHistoryList(
        @Header("x-access-token") token: String,
        @Query("start") start: Int,
        @Query("items") items: Int
    ): Response<BookingHistoryListModel>

    @POST("user/createSupport")
    suspend fun createSupport(
        @Header("x-access-token") token: String,
        @Body parameter: JsonObject
    ): Response<DriverSignUpResponse>

    @POST("user/addDriverBankAccount")
    suspend fun addBankAccount(
        @Header("x-access-token") token: String,
        @Body parameter: JsonObject
    ): Response<DriverSignUpResponse>

    @GET("user/getDriverBankAccount")
    suspend fun getBankAccountList(
        @Header("x-access-token") token: String,
    ): Response<BankAccountList>

    @GET("user/getWithdrawal")
    suspend fun getWithdrawList(
        @Header("x-access-token") token: String,
        @Query("start") start: Int,
        @Query("items") items: Int
    ): Response<WithdrawModel>

    @GET("user/getReceipt")
    suspend fun getReceiptData(
        @Header("x-access-token") token: String,
        @Query("bookingId") bookingId: String,
    ): Response<ReceiptModel>

    @POST("auth/driverLogOut")
    suspend fun logoutUser(
        @Header("x-access-token") token: String
    ): Response<DriverSignUpResponse>

    @GET("user/getNotification")
    suspend fun getNotificationList(
        @Header("x-access-token") token: String,
        @Query("start") start: Int,
        @Query("items") items: Int
    ): Response<Notification>

    @POST("user/deactivateDriverAccount")
    suspend fun deactivateAccount(
        @Header("x-access-token") token: String,
        @Body parameter: JsonObject
    ): Response<DriverSignUpResponse>

}