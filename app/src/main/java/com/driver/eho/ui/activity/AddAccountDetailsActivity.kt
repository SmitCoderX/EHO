package com.driver.eho.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.driver.eho.databinding.ActivityAddAccountDetailsBinding

class AddAccountDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddAccountDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAccountDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSaved.setOnClickListener {
            startActivity(Intent(this, AccountDetailsListActivity::class.java))
            finish()
        }

        binding.ivBack.setOnClickListener {
            startActivity(Intent(this, AccountDetailsListActivity::class.java))
            finish()
        }

        binding.btnDelete.setOnClickListener {
            Toast.makeText(this, "deleted", Toast.LENGTH_SHORT).show()
        }

    }
}