package com.driver.eho.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.driver.eho.R
import com.driver.eho.databinding.FragmentBottomSheetAmbulanceTypeBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AmbulanceTypeBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentBottomSheetAmbulanceTypeBinding
    private lateinit var listener: AmbulanceType

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bottom_sheet_ambulance_type, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBottomSheetAmbulanceTypeBinding.bind(view)

        binding.tvFree.setOnClickListener {
            listener.typeData(binding.tvFree.text.toString())
            dismiss()
        }

        binding.tvPaid.setOnClickListener {
            listener.typeData(binding.tvPaid.text.toString())
            dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    interface AmbulanceType {
        fun typeData(type: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            listener = context as AmbulanceType
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement AmbulanceType")
        }
    }

}