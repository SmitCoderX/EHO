package com.driver.eho.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.driver.eho.SharedPreferenceManager
import com.driver.eho.databinding.ActivitySplashBinding
import com.driver.eho.utils.Constants.SPLASH_DELAY
import com.driver.eho.utils.Constants.TAG
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        startService(Intent(this, MyFirebaseMessagingService::class.java))


        val prefs = SharedPreferenceManager(this)

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            Log.d(TAG, "FCM TOKEN: $token")
            prefs.setFCMToken(token)
        })

        Handler(Looper.myLooper()!!).postDelayed({
            if (prefs.getLoggedIn() && prefs.getToken().toString().isNotEmpty()) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                startActivity(Intent(this@SplashActivity, WelcomeActivity::class.java))
                finish()
            }
        }, SPLASH_DELAY)
    }
}