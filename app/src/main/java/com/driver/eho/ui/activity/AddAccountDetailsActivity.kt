package com.driver.eho.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.driver.eho.SharedPreferenceManager
import com.driver.eho.databinding.ActivityAddAccountDetailsBinding
import com.driver.eho.ui.viewModel.viewModelFactory.BankAccountViewModelProviderFactory
import com.driver.eho.ui.viewModels.BankViewModel
import com.driver.eho.utils.Constants
import com.driver.eho.utils.EHOApplication
import com.driver.eho.utils.Resources

class AddAccountDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddAccountDetailsBinding
    private val bankAccountViewModel: BankViewModel by viewModels {
        BankAccountViewModelProviderFactory(
            application,
            (application as EHOApplication).repository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAccountDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = SharedPreferenceManager(this)

        binding.btnSaved.setOnClickListener {
            if (validate()) {
                bankAccountViewModel.addBank(
                    prefs.getToken().toString(),
                    binding.edtAccountHolderName.text.toString(),
                    binding.edtAccountNumber.text.toString(),
                    binding.edtAccountIFSC.text.toString()
                )
            }
        }

        bankAccountViewModel.bankAccountLiveData.observe(this) { resources ->
            when (resources) {
                is Resources.Success -> {
                    hideLoading()
                    Constants.snackbarSuccess(binding.root, resources.message.toString())
                    startActivity(Intent(this, AccountDetailsListActivity::class.java))
                    finish()
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

        binding.ivBack.setOnClickListener {
            startActivity(Intent(this, AccountDetailsListActivity::class.java))
            finish()
        }
    }

    private fun validate(): Boolean {
        var valid = true
        val name = binding.edtAccountHolderName.text.toString()
        val accountNo = binding.edtAccountNumber.text.toString()
        val ifsc = binding.edtAccountIFSC.text.toString()

        // name
        if (TextUtils.isEmpty(name)) {
            binding.edtAccountHolderName.error = "Enter your Name"
            Constants.snackbarError(binding.root, "Enter Your Name")
            valid = false
        } else {
            binding.edtAccountHolderName.error = null
        }
        // account Number
        if (TextUtils.isEmpty(accountNo)) {
            binding.edtAccountNumber.error = "Enter your Account Number"
            Constants.snackbarError(binding.root, "Enter Your Account Number")
            valid = false
        } else {
            binding.edtAccountNumber.error = null
        }

        // IFSC
        if (TextUtils.isEmpty(ifsc)) {
            binding.edtAccountIFSC.error = "Enter your Bank IFSC Code"
            Constants.snackbarError(binding.root, "Enter Your Bank IFSC Code")
            valid = false
        } else {
            binding.edtAccountIFSC.error = null
        }
        return valid
    }

    private fun showLoading() {
        binding.viewLoader.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.viewLoader.visibility = View.GONE
    }
}