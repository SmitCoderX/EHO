package com.driver.eho.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.driver.eho.SharedPreferenceManager
import com.driver.eho.adapter.BookingHistoryListAdapter
import com.driver.eho.databinding.ActivityBookingHistoryBinding
import com.driver.eho.model.Booking.Data
import com.driver.eho.model.DriverSignInResponse
import com.driver.eho.ui.viewModel.viewModelFactory.BookingHistoryViewModelProviderFactory
import com.driver.eho.ui.viewModels.BookingHistoryViewModel
import com.driver.eho.utils.Constants
import com.driver.eho.utils.Constants.BOOKING_ID
import com.driver.eho.utils.EHOApplication
import com.driver.eho.utils.Resources

class BookingHistoryActivity : AppCompatActivity(), BookingHistoryListAdapter.OnBookingClick {
    private lateinit var binding: ActivityBookingHistoryBinding
    private val bookingHistoryViewModel: BookingHistoryViewModel by viewModels {
        BookingHistoryViewModelProviderFactory(
            application,
            (application as EHOApplication).repository
        )
    }
    private lateinit var bookingAdapter: BookingHistoryListAdapter
    private lateinit var prefs: SharedPreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bookingAdapter = BookingHistoryListAdapter(this)

        prefs = SharedPreferenceManager(this)

        bookingHistoryViewModel.getBookingHistoryList(
            prefs.getToken().toString(),
            0, 3
        )

        bookingHistoryViewModel.bookingHistoryLiveData.observe(this) { resources ->
            when (resources) {
                is Resources.Success -> {
                    hideLoadingView()
                    bookingAdapter.differ.submitList(resources.data?.data)
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

        binding.rvBookingHy.apply {
            setHasFixedSize(true)
            adapter = bookingAdapter
        }


        binding.ivBack.setOnClickListener {
            sendToMainActivity(prefs.getData())
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        sendToMainActivity(prefs.getData())
    }


    private fun showLoadingView() {
        binding.viewLoader.visibility = View.VISIBLE
    }

    private fun hideLoadingView() {
        binding.viewLoader.visibility = View.GONE
    }

    override fun onClick(data: Data) {
        val intent = Intent(this, ReceiptActivity::class.java)
        intent.putExtra(BOOKING_ID, data.id)
        startActivity(intent)
    }

    private fun sendToMainActivity(data: DriverSignInResponse?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(Constants.DRIVERSDATA, data)
        startActivity(intent)
        finish()
    }

}