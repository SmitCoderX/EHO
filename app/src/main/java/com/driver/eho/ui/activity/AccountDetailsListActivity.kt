package com.driver.eho.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.driver.eho.SharedPreferenceManager
import com.driver.eho.adapter.BankDetailsListAdapter
import com.driver.eho.databinding.ActivityAccountDetailsListBinding
import com.driver.eho.model.AccountList
import com.driver.eho.ui.viewModel.viewModelFactory.BankAccountDetailsViewModelProviderFactory
import com.driver.eho.ui.viewModels.BankAccountDetailsViewModel
import com.driver.eho.utils.Constants
import com.driver.eho.utils.EHOApplication
import com.driver.eho.utils.Resources

class AccountDetailsListActivity : AppCompatActivity(),
    BankDetailsListAdapter.OnAccountDetailsClick {
    private lateinit var binding: ActivityAccountDetailsListBinding
    private val bankAccountDetailsViewModel: BankAccountDetailsViewModel by viewModels {
        BankAccountDetailsViewModelProviderFactory(
            application,
            (application as EHOApplication).repository
        )
    }
    private lateinit var detailsListAdapter: BankDetailsListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountDetailsListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = SharedPreferenceManager(this)
        bankAccountDetailsViewModel.detailsList(prefs.getToken().toString())

        detailsListAdapter = BankDetailsListAdapter(this)

        bankAccountDetailsViewModel.bankAccountDetailsLiveData.observe(this) { resources ->
            when (resources) {
                is Resources.Success -> {
                    hideLoadingView()
                    detailsListAdapter.differ.submitList(resources.data?.data)
                }
                is Resources.Error -> {
                    hideLoadingView()
                    Constants.snackbarError(binding.root, resources.message.toString())
                }
                is Resources.Loading -> {
                    showLoadingView()
                }
            }
        }

        binding.rvAccountDetails.apply {
            setHasFixedSize(true)
            adapter = detailsListAdapter
        }

        binding.tvAddAccountDetails.setOnClickListener {
            startActivity(Intent(this, AddAccountDetailsActivity::class.java))
            finish()
        }

        binding.ivBack.setOnClickListener {
            startActivity(Intent(this, EhoMoneyActivity::class.java))
            finish()
        }
    }

    private fun showLoadingView() {
        binding.viewLoader.visibility = View.VISIBLE
    }

    private fun hideLoadingView() {
        binding.viewLoader.visibility = View.GONE
    }

    override fun onDeleteClick(data: AccountList) {

    }
}