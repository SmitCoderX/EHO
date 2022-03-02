package com.driver.eho.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.driver.eho.R
import com.driver.eho.SharedPreferenceManager
import com.driver.eho.databinding.FragmentAmbulanceAcceptRequestBottomBinding
import com.driver.eho.model.BottomSheetModal
import com.driver.eho.model.Login.DriverSignInResponse
import com.driver.eho.ui.viewModel.viewModelFactory.HomeFragmentViewModelProviderFactory
import com.driver.eho.ui.viewModels.HomeViewModel
import com.driver.eho.utils.Constants
import com.driver.eho.utils.Constants.REQUEST
import com.driver.eho.utils.Constants.TAG
import com.driver.eho.utils.EHOApplication
import com.driver.eho.utils.Resources
import com.driver.eho.utils.SocketHandler
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class AmbulanceAcceptBottomFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentAmbulanceAcceptRequestBottomBinding
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
        return inflater.inflate(R.layout.fragment_ambulance_accept_request_bottom, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAmbulanceAcceptRequestBottomBinding.bind(view)

        prefs = SharedPreferenceManager(requireContext())
        requestDetails = arguments?.getParcelable(REQUEST)!!

        Log.d(TAG, "onViewCreated: ")
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
                .into(ivProfileAccept)

            if (requestDetails?.userName == "" || requestDetails?.userName == null) {
                if (requestDetails?.name == "" || requestDetails?.name == null) {
                    tvPatientName.text = "Guest"
                } else {
                    tvPatientName.text = requestDetails?.name
                }
            } else {
                tvPatientName.text = requestDetails?.userName
            }
            tvAcceptDistance.text = requestDetails?.distance.toString() + "Km"
            tvPickup.text = requestDetails?.pickupLocation.toString()
            tvPaymentMode.text = requestDetails?.paymentMode.toString()
            if (requestDetails?.paymentMode == "Free") {
                tvAmount.visibility = View.GONE
            } else {
                tvAmount.visibility = View.VISIBLE
                tvAmount.text = getString(R.string.Rs) + requestDetails?.ambulanceCharge
            }
            tvDrop.text = requestDetails?.dropLocation.toString()
        }

        binding.btnDropOff.setOnClickListener {
            SocketHandler.emitDropOffRequest(
                requestDetails?.userId.toString(),
                prefs.getData()?.data?.id.toString(),
                requestDetails?.bookingId.toString(),
                requestDetails?.dropLatitude.toString(),
                requestDetails?.dropLongitude.toString()
            )
            dismissAllowingStateLoss()
        }

        binding.btnCancel.setOnClickListener {
            SocketHandler.emitRejectRequest(
                requestDetails?.userId.toString(),
                prefs.getData()?.data?.id.toString(),
                requestDetails?.bookingId.toString()
            )

            dismissAllowingStateLoss()
        }
        binding.btnCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${requestDetails?.userMobile}")
            startActivity(intent)
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