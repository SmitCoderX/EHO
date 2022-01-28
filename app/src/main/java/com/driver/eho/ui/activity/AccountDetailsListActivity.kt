package com.driver.eho.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.driver.eho.databinding.ActivityAccountDetailsListBinding

class AccountDetailsListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAccountDetailsListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountDetailsListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvAddAccountDetails.setOnClickListener {
            startActivity(Intent(this, AddAccountDetailsActivity::class.java))
            finish()
        }

        binding.ivBack.setOnClickListener {
            startActivity(Intent(this, EhoMoneyActivity::class.java))
            finish()
        }
    }
}