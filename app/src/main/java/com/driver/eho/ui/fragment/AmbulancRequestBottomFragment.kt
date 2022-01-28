package com.driver.eho.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.driver.eho.databinding.FragmentAmbulancRequestBottomBinding
import com.driver.eho.ui.activity.BookingHistoryActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AmbulancRequestBottomFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentAmbulancRequestBottomBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAmbulancRequestBottomBinding.inflate(layoutInflater, container, false)

        binding.btnAccept.setOnClickListener {
            performButtonAccept()
        }

        binding.btnDropOff.setOnClickListener {
            startActivity(Intent(activity, BookingHistoryActivity::class.java))
            requireActivity().finish()
        }
        return binding.root
    }

    // click on Accept button onclick listener fun
    private fun performButtonAccept() {
        binding.llDrop.visibility = View.VISIBLE
        binding.call.visibility = View.VISIBLE
        binding.btnDropOff.visibility = View.VISIBLE
        binding.btnAccept.visibility = View.GONE
        binding.btnDecline.visibility = View.GONE
    }

}