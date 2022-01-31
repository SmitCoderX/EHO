package com.driver.eho.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.driver.eho.SharedPreferenceManager
import com.driver.eho.adapter.WithdrawlHistoryListAdapter
import com.driver.eho.databinding.ActivityEhoMoneyBinding
import com.driver.eho.model.DriverSignInResponse
import com.driver.eho.ui.viewModel.viewModelFactory.WithdrawlHistoryViewModelProviderFactory
import com.driver.eho.ui.viewModels.WithdrawalHistoryViewModel
import com.driver.eho.utils.Constants
import com.driver.eho.utils.Constants.TAG
import com.driver.eho.utils.Constants.snackbarError
import com.driver.eho.utils.EHOApplication
import com.driver.eho.utils.Resources

class EhoMoneyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEhoMoneyBinding
    private val withdrawHistoryViewModel: WithdrawalHistoryViewModel by viewModels {
        WithdrawlHistoryViewModelProviderFactory(
            application,
            (application as EHOApplication).repository
        )
    }
    private lateinit var withdrawlAdapter: WithdrawlHistoryListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEhoMoneyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = SharedPreferenceManager(this)

        withdrawlAdapter = WithdrawlHistoryListAdapter()

        withdrawHistoryViewModel.getwithdrawlHistoryList(
            prefs.getToken().toString(),
            0,
            1
        )

        getWithdrawlHistoryList()
        binding.rvEhoMoney.apply {
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(this@EhoMoneyActivity, LinearLayoutManager.VERTICAL, false)
            adapter = withdrawlAdapter
        }

        binding.menuIcon.setOnClickListener {
            sendToMainActivity(prefs.getData())
        }

        binding.ivAdd.setOnClickListener {
            startActivity(Intent(this, AccountDetailsListActivity::class.java))
            finish()
        }
    }

    private fun getWithdrawlHistoryList() {
        withdrawHistoryViewModel.withdrawlHistoryLiveData.observe(this) { resources ->
            when (resources) {
                is Resources.Success -> {
                    hideLoadingView()
                    withdrawlAdapter.differ.submitList(resources.data?.data)
                    Log.d(TAG, "getWithdrawlHistoryList: ${resources.data?.data}")
                }

                is Resources.Loading -> {
                    showLoadingView()
                }

                is Resources.Error -> {
                    hideLoadingView()
                    snackbarError(binding.root, resources.message.toString())
                }
            }
        }
    }

    private fun showLoadingView() {
        binding.viewLoader.visibility = View.VISIBLE
    }

    private fun hideLoadingView() {
        binding.viewLoader.visibility = View.GONE
    }


    private fun sendToMainActivity(data: DriverSignInResponse?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(Constants.DRIVERSDATA, data)
        startActivity(intent)
        finish()
    }

}