package com.driver.eho.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.driver.eho.SharedPreferenceManager
import com.driver.eho.adapter.WithdrawlHistoryListAdapter
import com.driver.eho.databinding.ActivityEhoMoneyBinding
import com.driver.eho.model.Login.DriverSignInResponse
import com.driver.eho.ui.viewModel.viewModelFactory.WithdrawlHistoryViewModelProviderFactory
import com.driver.eho.ui.viewModels.WithdrawalHistoryViewModel
import com.driver.eho.utils.Constants
import com.driver.eho.utils.Constants.TAG
import com.driver.eho.utils.Constants.snackbarError
import com.driver.eho.utils.EHOApplication
import com.driver.eho.utils.Resources
import com.driver.eho.utils.pagination.EndlessRecyclerOnScrollListener

class EhoMoneyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEhoMoneyBinding
    private val withdrawHistoryViewModel: WithdrawalHistoryViewModel by viewModels {
        WithdrawlHistoryViewModelProviderFactory(
            application,
            (application as EHOApplication).repository
        )
    }
    private lateinit var prefs: SharedPreferenceManager
    val start = 0
    val itemCount = 10
    var item = 10
    var allDone = false
    private lateinit var withdrawAdapter: WithdrawlHistoryListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEhoMoneyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = SharedPreferenceManager(this)

        withdrawHistoryViewModel.getDriverDetails(
            prefs.getToken().toString()
        )
        profileData()

        withdrawAdapter = WithdrawlHistoryListAdapter(listOf())
        getWithdrawlHistoryList()

        binding.menuIcon.setOnClickListener {
            sendToMainActivity()
        }

        binding.ivAdd.setOnClickListener {
            startActivity(Intent(this, AccountDetailsListActivity::class.java))
            finish()
        }
    }

    private fun getWithdrawlHistoryList() {
        binding.rvEhoMoney.apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = withdrawAdapter
            val linearLayout = layoutManager as LinearLayoutManager
            val endlessRecyclerOnScrollListener =
                object : EndlessRecyclerOnScrollListener(linearLayout) {
                    override fun onLoadMore(current_page: Int) {
                        if (!allDone) {
                            item += itemCount
                            withdrawHistoryViewModel.getwithdrawlHistoryList(
                                prefs.getToken().toString(),
                                start,
                                item
                            )
                        }
                    }
                }
            addOnScrollListener(endlessRecyclerOnScrollListener as EndlessRecyclerOnScrollListener)
        }
        withdrawHistoryViewModel.withdrawlHistoryLiveData.observe(this) { resources ->
            when (resources) {
                is Resources.Success -> {
                    hideLoadingView()
                    if (resources.data?.data?.size!! < itemCount) allDone = true
                    withdrawAdapter.updateData(resources.data.data)
                    Log.d(TAG, "getWithdrawlHistoryList: ${resources.data.data}")
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
        withdrawHistoryViewModel.getwithdrawlHistoryList(
            prefs.getToken().toString(),
            start, item
        )
    }

    private fun profileData() {
        withdrawHistoryViewModel.driverMutableLiveData.observe(this) { resources ->
            when (resources) {
                is Resources.Success -> {
                    hideLoadingView()
                    binding.tvEhoAmount.text = resources.data?.data?.amount.toString()
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


    private fun showLoadingView() {
        binding.viewLoader.visibility = View.VISIBLE
    }

    private fun hideLoadingView() {
        binding.viewLoader.visibility = View.GONE
    }


    private fun sendToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        sendToMainActivity()
    }
}