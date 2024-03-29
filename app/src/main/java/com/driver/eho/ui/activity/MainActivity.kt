package com.driver.eho.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.driver.eho.BuildConfig
import com.driver.eho.R
import com.driver.eho.SharedPreferenceManager
import com.driver.eho.databinding.ActivityMainBinding
import com.driver.eho.model.Login.DriverSignInResponse
import com.driver.eho.ui.Home.HomeFragment
import com.driver.eho.ui.fragment.PrivacyPolicyFragment
import com.driver.eho.ui.fragment.SupportFragment
import com.driver.eho.ui.fragment.TermsConditionFragment
import com.driver.eho.ui.viewModel.viewModelFactory.MainActivityViewModelProviderFactory
import com.driver.eho.ui.viewModels.MainActivityViewModel
import com.driver.eho.utils.Constants.IMAGE_URL
import com.driver.eho.utils.Constants.TAG
import com.driver.eho.utils.Constants.snackbarError
import com.driver.eho.utils.EHOApplication
import com.driver.eho.utils.Resources
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var actionBarToggle: ActionBarDrawerToggle
    private val mainViewModel by viewModels<MainActivityViewModel> {
        MainActivityViewModelProviderFactory(
            application,
            (application as EHOApplication).repository
        )
    }
    private lateinit var prefs: SharedPreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        actionBarToggle = ActionBarDrawerToggle(this, binding.sideDrawer, 0, 0)
        binding.sideDrawer.addDrawerListener(actionBarToggle)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        /*loadingHeaderData()*/

        prefs = SharedPreferenceManager(this)

        mainViewModel.getDriverDetails(
            prefs.getToken().toString()
        )
        profileData()
        actionBarToggle.syncState()

        naviSideDrawer()

        // side Drawer
        setCurrentFragment(HomeFragment())
    }

    @SuppressLint("SetTextI18n")
    private fun profileData() {
        mainViewModel.driverMutableLiveData.observe(this) { resources ->
            when (resources) {
                is Resources.Success -> {
                    setData(resources.data!!)
                }

                is Resources.Error -> {
                }

                is Resources.Loading -> {

                }
            }
        }
    }

    // set Fragment
    private fun setCurrentFragment(fragment: Fragment) {
        val frag = supportFragmentManager.beginTransaction()
        frag.replace(R.id.fragment_container_view_tag, fragment)
        frag.commit()
    }

    @SuppressLint("ResourceAsColor", "SetTextI18n")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.homeFragment -> {
                binding.tvTabNames.text = "Home"
                setCurrentFragment(HomeFragment())
            }
            R.id.historyFragment -> {
                binding.tvTabNames.text = "Booking History"
                startActivity(Intent(this, BookingHistoryActivity::class.java))
                finish()
            }
            R.id.supportFragment -> {
                binding.tvTabNames.text = "EHO Support"
                setCurrentFragment(SupportFragment())
            }
            R.id.termsConditionFragment -> {
                binding.tvTabNames.text = "Terms & Condition"
                setCurrentFragment(TermsConditionFragment())
            }
            R.id.privacyPolicyFragment -> {
                binding.tvTabNames.text = "Privacy Policy"
                setCurrentFragment(PrivacyPolicyFragment())
            }
            R.id.navLogout -> {
                getLogoutAlertDialog()
            }
        }
        binding.sideDrawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun naviSideDrawer() {
        binding.navigationView.setNavigationItemSelectedListener(this)
        binding.navigationView.setCheckedItem(R.id.home)

        binding.menuIcon.setOnClickListener {
            if (binding.sideDrawer.isDrawerVisible(GravityCompat.START)) {
                binding.sideDrawer.closeDrawer(GravityCompat.START)
            } else {
                binding.sideDrawer.openDrawer(GravityCompat.START)
            }
        }

        binding.ivNotification.setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
            finish()
        }

        binding.cv1.setOnClickListener {
            try {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
                var shareMessage = "\nLet me recommend you this application\n\n"
                shareMessage =
                    """${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}""".trimIndent()
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                startActivity(Intent.createChooser(shareIntent, "choose one"))
            } catch (e: Exception) {
                Log.d(TAG, "Share App Error: $e")
            }
        }
    }

    // on menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarToggle.onOptionsItemSelected(item)) {
            true
        } else
            super.onOptionsItemSelected(item)
    }

    // on back
    override fun onBackPressed() {
        if (binding.sideDrawer.isDrawerOpen(GravityCompat.START)) {
            binding.sideDrawer.closeDrawer(GravityCompat.START)
            supportActionBar!!.show() // show toolbar
        } else {
            binding.sideDrawer.isDrawerOpen(GravityCompat.START)
            super.onBackPressed()
        }
    }

    // AlertDialog For Logout
    private fun getLogoutAlertDialog() {
        val builder1 = AlertDialog.Builder(this)
        builder1.setMessage("Are you sure you want to logout ?")
        builder1.setCancelable(true)
        builder1.setPositiveButton(
            "Yes"
        ) { dialog, _ ->
            callLogoutAPI()
            dialog.cancel()
        }
        builder1.setNegativeButton(
            "No"
        ) { dialog, _ ->
            Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show()
            dialog.cancel()
        }
        val alert11 = builder1.create()
        alert11.show()
    }

    private fun callLogoutAPI() {
        mainViewModel.logoutUser(prefs.getToken().toString())
        mainViewModel.logoutMutableLiveData.observe(this) { resources ->
            when (resources) {
                is Resources.Success -> {
                    prefs.logoutUser()
                    startActivity(Intent(this, WelcomeActivity::class.java))
                    finish()
                }

                is Resources.Error -> {
                    snackbarError(binding.root, resources.message.toString())
                }

                is Resources.Loading -> {

                }
            }

        }
    }

    @SuppressLint("SetTextI18n")
    private fun setData(details: DriverSignInResponse) {

        val header = binding.navigationView.getHeaderView(0)
        val edtProfile = header.findViewById<CardView>(R.id.cv1)
        val ehoMoneyActivity = header.findViewById<CardView>(R.id.cv2)

        val profileImage = header.findViewById<ShapeableImageView>(R.id.iv_profile)
        val userName = header.findViewById<TextView>(R.id.tvUsername)
        val mobileNo = header.findViewById<TextView>(R.id.tvMobileNumber)
        val amount = header.findViewById<TextView>(R.id.tvAmount)

        Glide.with(this)
            .load(IMAGE_URL + details.data?.image)
            .placeholder(R.drawable.profile)
            .error(R.drawable.profile)
            .into(profileImage)

        userName.text = details.data?.userName
        mobileNo.text = details.data?.mobile.toString()
        if (prefs.getLatestBalance().isNullOrEmpty()) {
            amount.text = getString(R.string.Rs) + details.data?.amount.toString()
        } else {
            amount.text = getString(R.string.Rs) + prefs.getLatestBalance()
        }
        edtProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }

        ehoMoneyActivity.setOnClickListener {
            startActivity(Intent(this, EhoMoneyActivity::class.java))
            finish()
        }
    }

}