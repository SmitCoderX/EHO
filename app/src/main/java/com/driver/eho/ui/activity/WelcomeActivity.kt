package com.driver.eho.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.driver.eho.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        binding.btnAmbulance.setOnClickListener {

        }

        binding.btnHospital.setOnClickListener {
            Toast.makeText(this, "Hospital Comping soon", Toast.LENGTH_SHORT).show()
        }

        binding.btnPharmacy.setOnClickListener {
            Toast.makeText(this, "Pharmacy Comping soon", Toast.LENGTH_SHORT).show()
        }
    }
}