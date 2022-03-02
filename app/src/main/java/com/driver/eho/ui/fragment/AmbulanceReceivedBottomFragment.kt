package com.driver.eho.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.driver.eho.R
import com.driver.eho.SharedPreferenceManager
import com.driver.eho.databinding.BottomFragmentReceivedAmbulanceBinding
import com.driver.eho.model.BottomSheetModal
import com.driver.eho.model.Login.DriverSignInResponse
import com.driver.eho.ui.activity.BookingHistoryActivity
import com.driver.eho.ui.viewModel.viewModelFactory.HomeFragmentViewModelProviderFactory
import com.driver.eho.ui.viewModels.HomeViewModel
import com.driver.eho.utils.Constants
import com.driver.eho.utils.EHOApplication
import com.driver.eho.utils.Resources
import com.driver.eho.utils.SocketHandler
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AmbulanceReceivedBottomFragment : BottomSheetDialogFragment() {

    private lateinit var binding: BottomFragmentReceivedAmbulanceBinding
    private var requestDetails: BottomSheetModal? = BottomSheetModal()
    private var driverDetails: DriverSignInResponse? = DriverSignInResponse()
    private val homeViewModel: HomeViewModel by viewModels {
        HomeFragmentViewModelProviderFactory(
            requireActivity().application,
            (requireActivity().application as EHOApplication).repository
        )
    }
    private lateinit var prefs: SharedPreferenceManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_fragment_received_ambulance, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = BottomFragmentReceivedAmbulanceBinding.bind(view)

        prefs = SharedPreferenceManager(requireContext())
        requestDetails = arguments?.getParcelable(Constants.REQUEST)
        homeViewModel.getDriverDetails(
            prefs.getToken().toString()
        )
        profileData()

        binding.apply {
            Glide.with(requireContext())
                .load(Constants.IMAGE_URL + requestDetails?.userImage)
                .placeholder(R.drawable.profile)
                .error(R.drawable.profile)
                .centerCrop()
                .into(ivProfileReceived)

            if (requestDetails?.userName == "" || requestDetails?.userName == null) {
                if (requestDetails?.name == "" || requestDetails?.name == null) {
                    tvPatientName.text = "Guest"
                } else {
                    tvPatientName.text = requestDetails?.name
                }
            } else {
                tvPatientName.text = requestDetails?.userName
            }
            tvReceivedDistance.text = requestDetails?.distance.toString() + "Km"
            tvPaymentMode.text = requestDetails?.paymentMode
            tvAmount.text = getString(R.string.Rs) + requestDetails?.ambulanceCharge
        }

        binding.btnReceived.setOnClickListener {
            SocketHandler.emitWalletBalance(
                prefs.getData()?.data?.id.toString()
            )
            startActivity(Intent(requireContext(), BookingHistoryActivity::class.java))
            dismissAllowingStateLoss()
        }
    }

    private fun profileData() {
        homeViewModel.driverMutableLiveData.observe(viewLifecycleOwner) { resources ->
            when (resources) {
                is Resources.Success -> {
                    driverDetails = resources.data!!
                }

                is Resources.Error -> {
                    Constants.snackbarError(binding.root, resources.message.toString())
                }

                is Resources.Loading -> {
                }
            }
        }
    }


}