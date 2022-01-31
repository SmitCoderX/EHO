package com.driver.eho.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.driver.eho.R
import com.driver.eho.SharedPreferenceManager
import com.driver.eho.databinding.FragmentSupportBinding
import com.driver.eho.model.DriverSignInResponse
import com.driver.eho.model.LoginData
import com.driver.eho.ui.activity.MainActivity
import com.driver.eho.ui.viewModel.viewModelFactory.SupportViewModelProviderFactory
import com.driver.eho.ui.viewModels.SupportViewModel
import com.driver.eho.utils.Constants
import com.driver.eho.utils.Constants.DRIVERSDATA
import com.driver.eho.utils.EHOApplication
import com.driver.eho.utils.Resources


class SupportFragment : Fragment(R.layout.fragment_support) {

    private lateinit var binding: FragmentSupportBinding
    private val supportViewModel: SupportViewModel by viewModels {
        SupportViewModelProviderFactory(
            requireActivity().application,
            (requireActivity().application as EHOApplication).repository
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSupportBinding.bind(view)

        val prefs = SharedPreferenceManager(requireContext())
        val data: LoginData? = prefs.getData()?.data

        if (data != null) {
            binding.edtFullName.setText(data.name)
            binding.edtEmail.setText(data.email)
            binding.edtMobileNumber.setText(data.mobile.toString())
        }

        binding.tvOfficeAddress.append(getString(R.string.support_address))

        binding.btnSubmit.setOnClickListener {
            if (validate()) {
                supportViewModel.createSupport(
                    prefs.getToken().toString(),
                    binding.edtFullName.text.toString(),
                    binding.edtEmail.text.toString(),
                    binding.edtMobileNumber.text.toString(),
                    binding.edtMessage.text.toString()
                )
            }
        }

        supportViewModel.supportLiveData.observe(viewLifecycleOwner) { resources ->
            when (resources) {
                is Resources.Success -> {
                    hideLoading()
                    Constants.snackbarSuccess(binding.root, resources.message.toString())
                    sendToMainActivity(prefs.getData())
                }

                is Resources.Error -> {
                    hideLoading()
                    Constants.snackbarError(binding.root, resources.message.toString())
                }

                is Resources.Loading -> {
                    showLoading()
                }
            }
        }
    }

    private fun showLoading() {
        binding.viewLoader.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.viewLoader.visibility = View.GONE
    }

    private fun validate(): Boolean {
        var valid = true
        val fullName = binding.edtFullName.text.toString()
        val email = binding.edtEmail.text.toString()
        val mobileNo = binding.edtMobileNumber.text.toString()
        val message = binding.edtMessage.text.toString()

        // Name
        if (TextUtils.isEmpty(fullName)) {
            binding.edtFullName.error = "Enter your Full Name"
            Constants.snackbarError(binding.root, "Enter Your Full Name")
            valid = false
        } else {
            binding.edtFullName.error = null
        }
        // Email
        if (TextUtils.isEmpty(email)) {
            binding.edtEmail.error = "Enter your Email"
            Constants.snackbarError(binding.root, "Enter Your Email")
            valid = false
        } else {
            binding.edtEmail.error = null
        }

        // MobileNumber
        if (TextUtils.isEmpty(mobileNo) && !Patterns.PHONE.matcher(mobileNo).matches()) {
            binding.edtMobileNumber.error = "Enter your correct mobile Number"
            Constants.snackbarError(binding.root, "Enter your correct mobile Number")
            valid = false
        } else {
            binding.edtMobileNumber.error = null
        }

        // Message
        if (TextUtils.isEmpty(email)) {
            binding.edtMessage.error = "Enter your message"
            Constants.snackbarError(binding.root, "Enter Your Message")
            valid = false
        } else {
            binding.edtMessage.error = null
        }
        return valid
    }

    private fun sendToMainActivity(data: DriverSignInResponse?) {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.putExtra(DRIVERSDATA, data)
        startActivity(intent)
    }
}