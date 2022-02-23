package com.driver.eho.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.driver.eho.R
import com.driver.eho.databinding.BottomFragmentForgotPasswordBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ForgotPasswordBottomFragment : BottomSheetDialogFragment() {

    private lateinit var binding: BottomFragmentForgotPasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_fragment_forgot_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = BottomFragmentForgotPasswordBinding.bind(view)



    }

}