package com.driver.eho.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.activity.viewModels
import com.driver.eho.databinding.ActivityLoginBinding
import com.driver.eho.ui.viewModel.DriverSignInViewModel
import com.driver.eho.ui.viewModel.viewModelFactory.DriverSignInViewModelProviderFactory
import com.driver.eho.utils.EHOApplication
import com.driver.eho.utils.Resources
import com.google.android.material.snackbar.Snackbar

class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val driverSignInViewModel: DriverSignInViewModel by viewModels {
        DriverSignInViewModelProviderFactory(
            application,
            (application as EHOApplication).repository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        driverSignInViewModel = ViewModelProvider(this)[DriverSignInViewModel::class.java]

        binding.ivBack.setOnClickListener {
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
        }

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }


        binding.btnLogin.setOnClickListener {
            if (validate()) {
                driverSignInViewModel.getLoginCredentials(
                    email = binding.edtEmailOrNumber.text.toString(),
                    password = binding.edtPassword.text.toString()
                )
            } else {
                Snackbar.make(binding.root, "Fields Cannot be Empty", Snackbar.LENGTH_SHORT).show()
            }
        }

        getLoginData()
    }

    private fun getLoginData() {
        driverSignInViewModel.loginLiveData.observe(this) { resources ->
            when (resources) {
                is Resources.Success -> {
                    hideLoadingView()
                    Snackbar.make(
                        binding.root,
                        resources.message.toString(),
                        Snackbar.LENGTH_SHORT
                    ).show()
                    startActivity(Intent(this, MainActivity::class.java))

                }

                is Resources.Error -> {
                    hideLoadingView()
                    Snackbar.make(
                        binding.root,
                        resources.message.toString(),
                        Snackbar.LENGTH_SHORT
                    )
                        .show()
                }
                is Resources.Loading -> {
                    showLoadingView()
                }
            }
        }
    }

    private fun validate(): Boolean {
        var valid = true
        val emailOrNumber = binding.edtEmailOrNumber.text.toString()
        val password = binding.edtPassword.text.toString()
        // emailOrNumber
        if (TextUtils.isEmpty(emailOrNumber)) {
            binding.edtEmailOrNumber.error = "Enter your Email"
            valid = false
        } else {
            binding.edtEmailOrNumber.error = null
        }
        // password
        if (TextUtils.isEmpty(password)) {
            binding.edtPassword.error = "Enter your password"
            valid = false
        } else {
            binding.edtPassword.error = null
        }
        return valid
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, WelcomeActivity::class.java))
        finish()
    }

    private fun showLoadingView() {
        binding.viewLoader.visibility = View.VISIBLE
    }

    private fun hideLoadingView() {
        binding.viewLoader.visibility = View.GONE
    }
}