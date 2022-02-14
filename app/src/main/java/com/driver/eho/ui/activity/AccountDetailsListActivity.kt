package com.driver.eho.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import com.driver.eho.SharedPreferenceManager
import com.driver.eho.adapter.BankDetailsListAdapter
import com.driver.eho.databinding.ActivityAccountDetailsListBinding
import com.driver.eho.model.AccountList
import com.driver.eho.ui.viewModel.viewModelFactory.BankAccountDetailsViewModelProviderFactory
import com.driver.eho.ui.viewModels.BankAccountDetailsViewModel
import com.driver.eho.utils.Constants
import com.driver.eho.utils.Constants.snackbarError
import com.driver.eho.utils.Constants.snackbarSuccess
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
    private lateinit var prefs: SharedPreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountDetailsListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = SharedPreferenceManager(this)
        bankAccountDetailsViewModel.detailsList(prefs.getToken().toString())

        detailsListAdapter = BankDetailsListAdapter(arrayListOf(), this)
        binding.rvAccountDetails.apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = detailsListAdapter
        }
        bankAccountList()

        binding.tvAddAccountDetails.setOnClickListener {
            startActivity(Intent(this, AddAccountDetailsActivity::class.java))
            finish()
        }

        binding.ivBack.setOnClickListener {
            startActivity(Intent(this, EhoMoneyActivity::class.java))
            finish()
        }
    }


    private fun bankAccountList() {
        bankAccountDetailsViewModel.bankAccountDetailsLiveData.observe(this) { resources ->
            when (resources) {
                is Resources.Success -> {
                    hideLoadingView()
                    detailsListAdapter.updateAdapter(resources.data?.data as ArrayList<AccountList>)

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

    override fun onResume() {
        super.onResume()
        bankAccountList()
    }

    private fun hideLoadingView() {
        binding.viewLoader.visibility = View.GONE
    }

    override fun onDeleteClick(data: AccountList, position: Int) {
        bankAccountDetailsViewModel.deleteBankAccount(
            prefs.getToken().toString(),
            data.id.toString()
        )
        bankAccountDetailsViewModel.deleteBankDetailsLiveData.observe(this) { resources ->
            when (resources) {
                is Resources.Success -> {
                    hideLoadingView()
                    snackbarSuccess(binding.root, resources.data?.message.toString())
                    detailsListAdapter.notifyItemRemoved(position)
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

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }


}