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
import com.driver.eho.adapter.NotificationAdapter
import com.driver.eho.databinding.ActivityNotificationBinding
import com.driver.eho.ui.viewModel.viewModelFactory.NotificationViewModelProviderFactory
import com.driver.eho.ui.viewModels.NotificationViewModel
import com.driver.eho.utils.Constants
import com.driver.eho.utils.EHOApplication
import com.driver.eho.utils.Resources
import com.driver.eho.utils.pagination.EndlessRecyclerOnScrollListener

class NotificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationBinding
    private lateinit var notificationAdapter: NotificationAdapter
    private lateinit var prefs: SharedPreferenceManager
    private val notificationViewModel by viewModels<NotificationViewModel> {
        NotificationViewModelProviderFactory(
            application,
            (application as EHOApplication).repository
        )
    }
    val start = 0
    val itemCount = 10
    var item = 10
    var allDone = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = SharedPreferenceManager(this)
        notificationAdapter = NotificationAdapter(listOf())

        binding.ivBack.setOnClickListener {
            sendToMainActivity()
        }

        binding.rvNotification.apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = notificationAdapter
            val linearLayout = layoutManager as LinearLayoutManager
            val endlessRecyclerOnScrollListener =
                object : EndlessRecyclerOnScrollListener(linearLayout) {
                    override fun onLoadMore(current_page: Int) {
                        if (!allDone) {
                            item += itemCount
                            notificationViewModel.getNotifcationList(
                                prefs.getToken().toString(),
                                item,
                                start
                            )
                        }
                    }
                }
            addOnScrollListener(endlessRecyclerOnScrollListener as EndlessRecyclerOnScrollListener)
        }
        notificationViewModel.notificationMutableLiveData.observe(this) { resources ->
            when (resources) {
                is Resources.Success -> {
                    hideLoadingView()
                    if (resources.data?.notificationData?.size!! < itemCount) allDone = true
                    notificationAdapter.updateList(resources.data.notificationData)
                    Log.d(
                        Constants.TAG,
                        "getNotificationList: ${resources.data.notificationData}"
                    )
                }

                is Resources.Loading -> {
                    showLoadingView()
                }

                is Resources.Error -> {
                    hideLoadingView()
                    Constants.snackbarError(binding.root, resources.message.toString())
                }
            }
        }
        notificationViewModel.getNotifcationList(
            prefs.getToken().toString(),
            item,
            start
        )


    }

    override fun onBackPressed() {
        super.onBackPressed()
        sendToMainActivity()
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
}