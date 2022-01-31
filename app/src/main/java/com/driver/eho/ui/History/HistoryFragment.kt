package com.driver.eho.ui.History

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.driver.eho.R
import com.driver.eho.SharedPreferenceManager
import com.driver.eho.adapter.BookingHistoryListAdapter
import com.driver.eho.databinding.FragmentHistoryBinding
import com.driver.eho.model.Booking.Data
import com.driver.eho.ui.viewModel.viewModelFactory.BookingHistoryViewModelProviderFactory
import com.driver.eho.ui.viewModels.BookingHistoryViewModel
import com.driver.eho.utils.Constants
import com.driver.eho.utils.EHOApplication
import com.driver.eho.utils.Resources


class HistoryFragment : Fragment(R.layout.fragment_history),
    BookingHistoryListAdapter.OnBookingClick {
    private lateinit var binding: FragmentHistoryBinding
    private val bookingHistoryViewModel: BookingHistoryViewModel by viewModels {
        BookingHistoryViewModelProviderFactory(
            requireActivity().application,
            (requireActivity().application as EHOApplication).repository
        )
    }
    private lateinit var bookingAdapter: BookingHistoryListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHistoryBinding.bind(view)

        bookingAdapter = BookingHistoryListAdapter(this)

        val prefs = SharedPreferenceManager(requireContext())

        bookingHistoryViewModel.getBookingHistoryList(
            prefs.getToken().toString(),
            0, 3
        )

        bookingHistoryViewModel.bookingHistoryLiveData.observe(viewLifecycleOwner) { resources ->
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

    }

    private fun showLoadingView() {
        binding.viewLoader.visibility = View.VISIBLE
    }

    private fun hideLoadingView() {
        binding.viewLoader.visibility = View.GONE
    }

    override fun onClick(data: Data) {
        Constants.snackbarSuccess(binding.root, data.paymentStatusString.toString())
    }

}