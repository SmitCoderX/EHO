package com.driver.eho.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.driver.eho.R
import com.driver.eho.adapter.WithdrawalIListAdapter
import com.driver.eho.databinding.ActivityEhoMoneyBinding
import com.driver.eho.model.WithdrawalListModel

class EhoMoneyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEhoMoneyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEhoMoneyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.menuIcon.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.ivAdd.setOnClickListener {
            startActivity(Intent(this, AccountDetailsListActivity::class.java))
            finish()
        }

        val WithdrawalList = arrayOf(
            WithdrawalListModel(
                "#8934777uihk/747bjjgjy", "Success", "10/01/2022, ", "10:00",
                "$250"
            ),
            WithdrawalListModel(
                "#8934777uihk/747bjjgjy", "Success", "10/01/2022, ", "10:00",
                "$250"
            ),
            WithdrawalListModel(
                "#8934777uihk/747bjjgjy", "Success", "10/01/2022, ", "10:00",
                "$250"
            ),
            WithdrawalListModel(
                "#8934777uihk/747bjjgjy", "Success", "10/01/2022, ", "10:00",
                "$250"
            ),
            WithdrawalListModel(
                "#8934777uihk/747bjjgjy", "Success", "10/01/2022, ", "10:00",
                "$250"
            ),
            WithdrawalListModel(
                "#8934777uihk/747bjjgjy", "Success", "10/01/2022, ", "10:00",
                "$250"
            ),
        )

        binding.rvEhoMoney.hasFixedSize()
        binding.rvEhoMoney.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvEhoMoney.adapter = WithdrawalIListAdapter(WithdrawalList)
    }
}