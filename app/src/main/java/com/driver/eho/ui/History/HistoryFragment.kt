package com.driver.eho.ui.History

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.driver.eho.R
import com.driver.eho.SharedPreferenceManager
import com.driver.eho.adapter.BookingHistoryListAdapter
import com.driver.eho.databinding.FragmentHistoryBinding
import com.driver.eho.model.Booking.BookingData
import com.driver.eho.ui.viewModel.viewModelFactory.BookingHistoryViewModelProviderFactory
import com.driver.eho.ui.viewModels.BookingHistoryViewModel
import com.driver.eho.utils.Constants
import com.driver.eho.utils.EHOApplication
import com.driver.eho.utils.Resources
import com.driver.eho.utils.pagination.EndlessRecyclerOnScrollListener


class HistoryFragment : Fragment(R.layout.fragment_history),
    BookingHistoryListAdapter.OnBookingClick {
    private lateinit var binding: FragmentHistoryBinding
    private val bookingHistoryViewModel: BookingHistoryViewModel by viewModels {
        BookingHistoryViewModelProviderFactory(
            requireActivity().application,
            (requireActivity().application as EHOApplication).repository
        )
    }
    val start = 0
    val itemCount = 10
    var item = 10
    var allDone = false
    private lateinit var bookingAdapter: BookingHistoryListAdapter
    private lateinit var prefs: SharedPreferenceManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHistoryBinding.bind(view)

        prefs = SharedPreferenceManager(requireContext())

        bookingAdapter = BookingHistoryListAdapter(listOf(), this)

        bookingHistoryData()
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
        bookingHistoryViewModel.bookingHistoryLiveData.observe(viewLifecycleOwner) { resources ->
            when (resources) {
                is Resources.Success -> {
                    hideLoadingView()
                    if (resources.data?.data?.size!! < itemCount) allDone = true
                    bookingAdapter.updateData(resources.data.data)
                    Log.d(Constants.TAG, "getBookingHistoryList: ${resources.data.data}")
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
        Constants.snackbarSuccess(binding.root, bookingData.paymentStatusString.toString())
    }

}