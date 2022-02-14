package com.driver.eho.ui.fragment

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.driver.eho.R
import com.driver.eho.SharedPreferenceManager
import com.driver.eho.databinding.FragmentAmbulancRequestBottomBinding
import com.driver.eho.model.BottomSheetModal
import com.driver.eho.model.Login.DriverSignInResponse
import com.driver.eho.ui.viewModel.viewModelFactory.HomeFragmentViewModelProviderFactory
import com.driver.eho.ui.viewModels.HomeViewModel
import com.driver.eho.utils.Constants
import com.driver.eho.utils.Constants.IMAGE_URL
import com.driver.eho.utils.Constants.REQUEST
import com.driver.eho.utils.EHOApplication
import com.driver.eho.utils.Resources
import com.driver.eho.utils.SocketHandler
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class AmbulancRequestBottomFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentAmbulancRequestBottomBinding
    private lateinit var mediaPlayer: MediaPlayer
    private val handler = Handler(Looper.myLooper()!!)
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
        return inflater.inflate(R.layout.fragment_ambulanc_request_bottom, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAmbulancRequestBottomBinding.bind(view)

        prefs = SharedPreferenceManager(requireContext())
        requestDetails = arguments?.getParcelable(REQUEST)
        homeViewModel.getDriverDetails(
            prefs.getToken().toString()
        )
        profileData()

        binding.apply {
            Glide.with(requireContext())
                .load(IMAGE_URL + requestDetails?.userImage)
                .placeholder(R.drawable.profile)
                .error(R.drawable.profile)
                .centerCrop()
                .into(ivProfile)

            tvPatientName.text = requestDetails?.userName.toString()
            tvKM.text = requestDetails?.distance.toString()
            tvPickupAddress.text = requestDetails?.pickupLocation.toString()
            tvAmmount.text = getString(R.string.Rs) + requestDetails?.price

//            tvDropAddress.text = requestDetails?.dropLocation.toString()
        }

        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.siren)
        mediaPlayer.start()

        binding.btnAccept.setOnClickListener {
            SocketHandler.emitAcceptRequest(
                requestDetails?.userId.toString(),
                driverDetails?.data?.id.toString(),
                requestDetails?.bookingId.toString()
            )

            performButtonAccept()
            mediaPlayer.stop()
            handler.removeCallbacksAndMessages(null)
        }



        binding.btnDecline.setOnClickListener {
            SocketHandler.emitRejectRequest(
                requestDetails?.userId.toString(),
                driverDetails?.data?.id.toString(),
                requestDetails?.bookingId.toString()
            )
            dismiss()
            mediaPlayer.stop()
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

    // click on Accept button onclick listener fun
    private fun performButtonAccept() {
//        binding.llDrop.visibility = View.VISIBLE
        binding.btnAccept.visibility = View.GONE
        binding.btnDecline.visibility = View.GONE
    }


    override fun onResume() {
        super.onResume()
        handler.postDelayed({
            dismiss()
            mediaPlayer.stop()
        }, 10000)
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
    }

}