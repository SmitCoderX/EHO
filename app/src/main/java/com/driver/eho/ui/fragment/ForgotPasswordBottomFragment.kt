package com.driver.eho.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.driver.eho.R
import com.driver.eho.databinding.BottomFragmentForgotPasswordBinding
import com.driver.eho.ui.viewModel.viewModelFactory.DriverSignInViewModelProviderFactory
import com.driver.eho.ui.viewModels.DriverSignInViewModel
import com.driver.eho.utils.Constants
import com.driver.eho.utils.EHOApplication
import com.driver.eho.utils.Resources
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ForgotPasswordBottomFragment : BottomSheetDialogFragment() {

    private lateinit var binding: BottomFragmentForgotPasswordBinding
    private val loginViewModel: DriverSignInViewModel by viewModels {
        DriverSignInViewModelProviderFactory(
            requireActivity().application,
            (requireActivity().application as EHOApplication).repository
        )
    }

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

        binding.btnSubmit.setOnClickListener {
            loginViewModel.forgotPassword(binding.edtEmail.text.toString().trim())
        }

        loginViewModel.fpLiveData.observe(viewLifecycleOwner) { resources ->
            when (resources) {
                is Resources.Success -> {
                    Toast.makeText(requireContext(), resources.message.toString(), Toast.LENGTH_SHORT).show()
                    dismissAllowingStateLoss()
                }

                is Resources.Error -> {
                    Toast.makeText(requireContext(), resources.message.toString(), Toast.LENGTH_SHORT).show()
                }

                is Resources.Loading -> {

                }
            }

        }


    }

}