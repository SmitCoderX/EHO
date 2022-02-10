package com.driver.eho.network

import android.util.Log
import com.driver.eho.network.ApiConstant.BASEURL
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import java.util.concurrent.TimeUnit

object ApiClient {

    private var HttpClient: OkHttpClient? = null

    private fun getHttpClient(): OkHttpClient {

        val networkMange = HttpLoggingInterceptor()
        networkMange.setLevel(HttpLoggingInterceptor.Level.BODY)
        HttpClient = OkHttpClient().newBuilder()
            .connectTimeout(10,TimeUnit.SECONDS)
            .readTimeout(10,TimeUnit.SECONDS)
            .writeTimeout(10,TimeUnit.SECONDS)
            .addNetworkInterceptor(Interceptor { chain ->
                val response = chain.proceed(chain.request())
                val body = response.body
                val bodyString = body!!.string()
                val contentType = body.contentType()
                Log.d("Response", bodyString)
                response.newBuilder().body(bodyString.toResponseBody(contentType)).build()
            }).build()
        return HttpClient as OkHttpClient
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASEURL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(getHttpClient())
        .build()
        .create(ApiInterface::class.java)


    fun getInstance(): ApiInterface{
        return retrofit
    }
}