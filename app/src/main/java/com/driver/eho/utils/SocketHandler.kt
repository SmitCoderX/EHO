package com.driver.eho.utils

import android.util.Log
import com.driver.eho.model.BottomSheetModal
import com.driver.eho.utils.Constants.TAG
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.engineio.client.transports.Polling
import io.socket.engineio.client.transports.WebSocket
import org.json.JSONObject
import java.net.URISyntaxException

object SocketHandler {

    lateinit var mSocket: Socket

    @Synchronized
    fun setSocket() {
        try {
            val options = IO.Options.builder()
                .setForceNew(false)
                .setMultiplex(true)
                .setTransports(arrayOf(Polling.NAME, WebSocket.NAME))
                .setUpgrade(true)
                .setRememberUpgrade(false)
                .setPath("/socket.io/")
                .setQuery(null)
                .setExtraHeaders(null)
                .setReconnection(true)
                .setReconnectionAttempts(Int.MAX_VALUE)
                .setReconnectionDelay(1000)
                .setReconnectionDelayMax(5000)
                .setRandomizationFactor(0.5)
                .setTimeout(2000)
                .setAuth(null)
                .build()
            mSocket = IO.socket("https://test-app-eho.herokuapp.com", options)
        } catch (e: URISyntaxException) {
            Log.d(TAG, "setSocket: ErroConnection ${e.message.toString()}")
        }
    }

    @Synchronized
    fun getSocket(): Socket = mSocket

    @Synchronized
    fun establishConnection() {
        mSocket.connect()
    }

    @Synchronized
    fun closeConnection() {
        mSocket.disconnect()
    }

    fun emitSubscribe(userid: String) {
        val obj1 = JSONObject()
        obj1.put("userId", userid)
        mSocket.emit("subscribe", obj1.toString())
    }

    fun emitSentLocation(latitude: String, longitude: String, driverId: String) {
        val obj2 = JSONObject()
        obj2.put("latitude", latitude)
        obj2.put("longitude", longitude)
        obj2.put("driverId", driverId)
        mSocket.emit("sentLocation", obj2.toString())
    }

    fun emitAcceptRequest(userId: String, driverId: String, bookingId: String) {
        val obj3 = JSONObject()
        obj3.put("userId", userId)
        obj3.put("driverId", driverId)
        obj3.put("bookingId", bookingId)
        mSocket.emit("acceptRequest", obj3.toString())
    }

    fun emitIsAccepted(driverId: String) {
        val obj4 = JSONObject()
//        obj4.put("userId", userid)
        obj4.put("driverId", driverId)
        mSocket.emit("isAccepted", obj4.toString())
    }

    fun emitRejectRequest(userid: String, driverId: String, bookingId: String) {
        val obj5 = JSONObject()
        obj5.put("userId", userid)
        obj5.put("bookingId", bookingId)
        obj5.put("driverId", driverId)
        mSocket.emit("rejectRequest", obj5.toString())
    }

    fun emitDropOffRequest(
        userid: String,
        driverId: String,
        bookingId: String,
        dropLatitude: String,
        dropLongitude: String
    ) {
        val obj6 = JSONObject()
        obj6.put(
            "userId",
            userid
        ) // userId is equivalent to senderID that we will get from sendRequestDriverListener down below
        obj6.put("driverId", driverId)
        obj6.put("bookingId", bookingId)
        obj6.put("dropLatitude", dropLatitude)
        obj6.put("dropLongitude", dropLongitude)
        mSocket.emit("dropOffRequest", obj6.toString())
    }

    fun sendRequestDriverListener(callback: (callbackValue: BottomSheetModal) -> Unit) {
        mSocket.on("sendRequestDriverListener") {
            Log.d(TAG, "Accept Request sendRequestDriverListener: $it")
            val data = it[0] as BottomSheetModal
            callback.invoke(data)
        }
    }

    fun closeSendRequestDriverListner(callback: (callbackValue: String) -> Unit) {
        mSocket.off("sendRequestDriverListener") {
            Log.d(TAG, "closeSendRequestDriverListner: $it")

        }
    }

    fun acceptRequestDriverListener(callback: (callbackValue: BottomSheetModal) -> Unit) {
        mSocket.on("acceptRequestDriverListener") {
            Log.d(TAG, "acceptRequestDriverListener: $it")
            val data = it[0] as BottomSheetModal
            callback.invoke(data)
        }
    }

    fun closeAcceptRequestDriverListener(callback: (callbackValue: String) -> Unit) {
        mSocket.off("acceptRequestDriverListener") {
            Log.d(TAG, "closeAcceptRequestDriverListener: $it")
        }
    }

    fun dropOffRequestDriverListener(callback: (callbackValue: BottomSheetModal) -> Unit) {
        mSocket.on("dropOffRequestDriverListener") {
            Log.d(TAG, "dropOffRequestDriverListener: $it")
            val data = it[0] as BottomSheetModal
            callback.invoke(data)
        }
    }

    fun closeDropOffRequestDriverListener(callback: (callbackValue: String) -> Unit) {
        mSocket.off("dropOffRequestDriverListener") {
            Log.d(TAG, "closeDropOffRequestDriverListener: $it")
        }
    }

    fun rejectRequestDriverListener(callback: (callbackValue: String) -> Unit) {
        mSocket.on("rejectRequestDriverListener") {
            Log.d(TAG, "rejectRequestDriverListener: $it")
            val data = it[0] as String
            callback.invoke(data)
        }
    }

    fun closeRejectRequestDriverListener(callback: (callbackValue: String) -> Unit) {
        mSocket.off("rejectRequestDriverListener") {
            Log.d(TAG, "closeRejectRequestDriverListener: $it")
        }
    }

    fun cancelRequestDriverListener(callback: (callbackValue: String) -> Unit) {
        mSocket.on("cancelRequestDriverListener") {
            Log.d(TAG, "cancelRequestDriverListener: $it")
        }
    }

    fun closeCancelRequestDriverListener(callback: (callbackValue: String) -> Unit) {
        mSocket.off("cancelRequestDriverListener") {
            Log.d(TAG, "closeCancelRequestDriverListener: $it")
        }
    }


//    dropOffRequestDriverListener  HIDE BOTTOM IN THESE THREE  Same Modal for Drop
//rejectRequestDriverListener
//cancelRequestDriverListener


}