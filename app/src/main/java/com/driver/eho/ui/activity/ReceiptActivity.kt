package com.driver.eho.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.driver.eho.R
import com.driver.eho.SharedPreferenceManager
import com.driver.eho.databinding.ActivityReceiptBinding
import com.driver.eho.ui.viewModel.viewModelFactory.ReceiptDataViewModelProviderFactory
import com.driver.eho.ui.viewModels.ReceiptDataViewModel
import com.driver.eho.utils.Constants
import com.driver.eho.utils.Constants.BOOKING_ID
import com.driver.eho.utils.EHOApplication
import com.driver.eho.utils.Resources

class ReceiptActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReceiptBinding
    private val receiptViewModel: ReceiptDataViewModel by viewModels {
        ReceiptDataViewModelProviderFactory(
            application,
            (application as EHOApplication).repository
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiptBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bookingID = intent.getStringExtra(BOOKING_ID)

        val prefs = SharedPreferenceManager(this)

        receiptViewModel.getReceiptData(prefs.getToken().toString(), bookingID.toString())

        receiptViewModel.receiptDataLiveData.observe(this) { resources ->
            when (resources) {
                is Resources.Success -> {
                    hideLoadingView()
                    val data = resources.data?.data
                    binding.apply {
                        tvAmbulanceCharge.text =
                            getString(R.string.Rs) + data?.ambulanceCharge.toString()
                        tvAmbulanceBookingFee.text =
                            getString(R.string.Rs) + data?.bookingFee.toString()
                        tvTotal.text = getString(R.string.Rs) + data?.total.toString()
                        tvMode.text = data?.paymentMode
                    }
                }

                is Resources.Loading -> {
                    showLoadingView()
                }

                is Resources.Error -> {
                    hideLoadingView()
                    Constants.snackbarError(binding.root, resources.message.toString())
                }
            }
        }

        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun showLoadingView() {
        binding.viewLoader.visibility = View.VISIBLE
    }

    private fun hideLoadingView() {
        binding.viewLoader.visibility = View.GONE
    }

}