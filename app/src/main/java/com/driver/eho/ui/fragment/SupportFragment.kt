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
import com.driver.eho.model.Login.DriverSignInResponse
import com.driver.eho.ui.activity.MainActivity
import com.driver.eho.ui.viewModel.viewModelFactory.SupportViewModelProviderFactory
import com.driver.eho.ui.viewModels.SupportViewModel
import com.driver.eho.utils.Constants
import com.driver.eho.utils.Constants.DRIVERSDATA
import com.driver.eho.utils.Constants.snackbarError
import com.driver.eho.utils.Constants.snackbarSuccess
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
        supportViewModel.getDriverDetails(
            prefs.getToken().toString()
        )
        profileData()

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
                    setEnabled()
                    snackbarSuccess(binding.root, resources.message.toString())
                    sendToMainActivity(prefs.getData())
                }

                is Resources.Error -> {
                    hideLoading()
                    setEnabled()
                    snackbarError(binding.root, resources.message.toString())
                }

                is Resources.Loading -> {
                    showLoading()
                    removeEnabled()
                }
            }
        }
    }

    private fun setEnabled() {
        binding.apply {
            edtFullName.isEnabled = true
            edtEmail.isEnabled = true
            edtMobileNumber.isEnabled = true
            edtMessage.isEnabled = true
        }
    }


    private fun removeEnabled() {
        binding.apply {
            edtFullName.isEnabled = false
            edtEmail.isEnabled = false
            edtMobileNumber.isEnabled = false
            edtMessage.isEnabled = false
        }
    }

    private fun profileData() {
        supportViewModel.driverMutableLiveData.observe(viewLifecycleOwner) { resources ->
            when (resources) {
                is Resources.Success -> {
                    hideLoading()
                    binding.edtFullName.setText(resources.data?.data?.name)
                    binding.edtEmail.setText(resources.data?.data?.email)
                    binding.edtMobileNumber.setText(resources.data?.data?.mobile.toString())
                }

                is Resources.Error -> {
                    hideLoading()
                    snackbarError(binding.root, resources.message.toString())
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
        val fullName = binding.edtFullName.text.toString()
        val email = binding.edtEmail.text.toString()
        val mobileNo = binding.edtMobileNumber.text.toString()
        val message = binding.edtMessage.text.toString()

        // Name
        if (TextUtils.isEmpty(fullName)) {
            binding.edtFullName.error = "Enter your Full Name"
            snackbarError(binding.root, "Enter Your Full Name")
            return false
        }
        // Email
        if (TextUtils.isEmpty(email)) {
            binding.edtEmail.error = "Enter your Email"
            snackbarError(binding.root, "Enter Your Email")
            return false
        }

        // MobileNumber
        if (TextUtils.isEmpty(mobileNo) && !Patterns.PHONE.matcher(mobileNo).matches()) {
            binding.edtMobileNumber.error = "Enter your correct mobile Number"
            snackbarError(binding.root, "Enter your correct mobile Number")
            return false
        }

        // Message
        if (TextUtils.isEmpty(message)) {
            binding.edtMessage.error = "Enter your message"
            snackbarError(binding.root, "Enter Your Message")
            return false
        }

        return true
    }

    private fun sendToMainActivity(data: DriverSignInResponse?) {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.putExtra(DRIVERSDATA, data)
        startActivity(intent)
    }
}