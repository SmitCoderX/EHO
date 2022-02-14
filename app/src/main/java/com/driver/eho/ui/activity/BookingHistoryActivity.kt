package com.driver.eho.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.driver.eho.SharedPreferenceManager
import com.driver.eho.adapter.BookingHistoryListAdapter
import com.driver.eho.databinding.ActivityBookingHistoryBinding
import com.driver.eho.model.Booking.BookingData
import com.driver.eho.model.Login.DriverSignInResponse
import com.driver.eho.ui.viewModel.viewModelFactory.BookingHistoryViewModelProviderFactory
import com.driver.eho.ui.viewModels.BookingHistoryViewModel
import com.driver.eho.utils.Constants
import com.driver.eho.utils.Constants.BOOKING_ID
import com.driver.eho.utils.Constants.TAG
import com.driver.eho.utils.EHOApplication
import com.driver.eho.utils.Resources
import com.driver.eho.utils.pagination.EndlessRecyclerOnScrollListener

class BookingHistoryActivity : AppCompatActivity(), BookingHistoryListAdapter.OnBookingClick {
    private lateinit var binding: ActivityBookingHistoryBinding
    private val bookingHistoryViewModel: BookingHistoryViewModel by viewModels {
        BookingHistoryViewModelProviderFactory(
            application,
            (application as EHOApplication).repository
        )
    }
    private lateinit var bookingAdapter: BookingHistoryListAdapter
    val start = 0
    val itemCount = 10
    var item = 10
    var allDone = false
    private lateinit var prefs: SharedPreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = SharedPreferenceManager(this)
        bookingAdapter = BookingHistoryListAdapter(listOf(), this)
        bookingHistoryData()


        binding.ivBack.setOnClickListener {
            sendToMainActivity()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        sendToMainActivity()
    }

    private fun bookingHistoryData() {
        binding.rvBookingHy.apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = bookingAdapter
            val linearLayout = layoutManager as LinearLayoutManager
            val endlessRecyclerOnScrollListener =
                object : EndlessRecyclerOnScrollListener(linearLayout) {
                    override fun onLoadMore(current_page: Int) {
                        if (!allDone) {
                            item += itemCount
                            bookingHistoryViewModel.getBookingHistoryList(
                                prefs.getToken().toString(),
                                start,
                                item
                            )
                        }
                    }
                }
            addOnScrollListener(endlessRecyclerOnScrollListener as EndlessRecyclerOnScrollListener)
        }
        bookingHistoryViewModel.bookingHistoryLiveData.observe(this) { resources ->
            when (resources) {
                is Resources.Success -> {
                    hideLoadingView()
                    if (resources.data?.data?.size!! < itemCount) allDone = true
                    bookingAdapter.updateData(resources.data.data)
                    Log.d(TAG, "getBookingHistoryList: ${resources.data.data}")
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
        bookingHistoryViewModel.getBookingHistoryList(
            prefs.getToken().toString(),
            start,
            item
        )
    }


    private fun showLoadingView() {
        binding.viewLoader.visibility = View.VISIBLE
    }

    private fun hideLoadingView() {
        binding.viewLoader.visibility = View.GONE
    }

    override fun onClick(bookingData: BookingData) {
        val intent = Intent(this, ReceiptActivity::class.java)
        intent.putExtra(BOOKING_ID, bookingData.id)
        startActivity(intent)
    }

    private fun sendToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}