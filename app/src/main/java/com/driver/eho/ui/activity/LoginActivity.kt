package com.driver.eho.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.activity.viewModels
import com.driver.eho.SharedPreferenceManager
import com.driver.eho.databinding.ActivityLoginBinding
import com.driver.eho.ui.fragment.ForgotPasswordBottomFragment
import com.driver.eho.ui.viewModel.viewModelFactory.DriverSignInViewModelProviderFactory
import com.driver.eho.ui.viewModels.DriverSignInViewModel
import com.driver.eho.utils.Constants.DRIVERSDATA
import com.driver.eho.utils.Constants.snackbarError
import com.driver.eho.utils.EHOApplication
import com.driver.eho.utils.Resources

class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val driverSignInViewModel: DriverSignInViewModel by viewModels {
        DriverSignInViewModelProviderFactory(
            application,
            (application as EHOApplication).repository
        )
    }
    private lateinit var prefs: SharedPreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = SharedPreferenceManager(this)

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
                    email = binding.edtEmailOrNumber.text.toString().trim(),
                    password = binding.edtPassword.text.toString().trim(),
                    prefs.getFCMToken().toString()
                )
            }
        }

        binding.tvFpLogin.setOnClickListener {
            val forgotPasswordSheet = ForgotPasswordBottomFragment()
            forgotPasswordSheet.show(supportFragmentManager, forgotPasswordSheet.tag)
        }

        getLoginData()
    }

    private fun getLoginData() {
        val prefs = SharedPreferenceManager(this)
        driverSignInViewModel.loginLiveData.observe(this) { resources ->
            when (resources) {
                is Resources.Success -> {
                    hideLoadingView()
                    prefs.setToken(resources.data?.data?.token.toString())
                    prefs.setData(resources.data!!)
                    prefs.setLoggedIn(true)
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra(DRIVERSDATA, resources.data)
                    startActivity(intent)
                    finish()
                }

                is Resources.Error -> {
                    hideLoadingView()
                    snackbarError(binding.root, resources.message.toString())
                }
                is Resources.Loading -> {
                    showLoadingView()
                }
            }
        }
    }

    private fun validate(): Boolean {
        val emailOrNumber = binding.edtEmailOrNumber.text.toString()
        val password = binding.edtPassword.text.toString()
        // emailOrNumber
        if (TextUtils.isEmpty(emailOrNumber)) {
            binding.edtEmailOrNumber.error = "Enter your Email"
            snackbarError(binding.root, "Enter Your Email or Phone Number")
            return false
        }
        // password
        if (TextUtils.isEmpty(password)) {
            binding.edtPassword.error = "Enter your password"
            snackbarError(binding.root, "Enter Your Password")
            return false
        }
        return true
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