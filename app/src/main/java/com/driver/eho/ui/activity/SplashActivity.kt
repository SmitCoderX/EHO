package com.driver.eho.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.driver.eho.SharedPreferenceManager
import com.driver.eho.databinding.ActivitySplashBinding
import com.driver.eho.utils.Constants
import com.driver.eho.utils.Constants.SPLASH_DELAY
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = SharedPreferenceManager(this)

        Handler(Looper.myLooper()!!).postDelayed({
            if (prefs.getLoggedIn()) {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra(Constants.DRIVERSDATA, prefs.getData())
                startActivity(intent)
                finish()
            } else {
                startActivity(Intent(this@SplashActivity, WelcomeActivity::class.java))
                finish()
            }
        }, SPLASH_DELAY)
    }
}