package com.driver.eho.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.driver.eho.R
import com.driver.eho.SharedPreferenceManager
import com.driver.eho.databinding.ActivityMainBinding
import com.driver.eho.model.DriverSignInResponse
import com.driver.eho.ui.Home.HomeFragment
import com.driver.eho.ui.fragment.PrivacyPolicyFragment
import com.driver.eho.ui.fragment.SupportFragment
import com.driver.eho.ui.fragment.TermsConditionFragment
import com.driver.eho.utils.Constants.DRIVERSDATA
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var actionBarToggle: ActionBarDrawerToggle

    //    private lateinit var navHostFragment: NavHostFragment
//    private lateinit var navController: NavController
//    private lateinit var appBarConfiguration: AppBarConfiguration
    private var driverData: DriverSignInResponse? = DriverSignInResponse()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        actionBarToggle = ActionBarDrawerToggle(this, binding.sideDrawer, 0, 0)
        binding.sideDrawer.addDrawerListener(actionBarToggle)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        /*loadingHeaderData()*/

        val prefs = SharedPreferenceManager(this)

        driverData = if (driverData != null) {
            intent.getParcelableExtra(DRIVERSDATA)
        } else {
            prefs.getData()
        }


        /*  navHostFragment =
              supportFragmentManager.findFragmentById(R.id.fragment_container_view_tag) as NavHostFragment
          navController = navHostFragment.navController
          appBarConfiguration = AppBarConfiguration(navController.graph)*/

//        setUpDrawerLayout()
        profileData()
        actionBarToggle.syncState()

        naviSideDrawer()

        // side Drawer
        setCurrentFragment(HomeFragment())
    }

    /* override fun onSupportNavigateUp(): Boolean {
         return NavigationUI.navigateUp(navController, binding.sideDrawer)
     }*/

    /* private fun setUpDrawerLayout() {
         binding.toolbar.setupWithNavController(navController)
         NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
     }*/

    private fun profileData() {
        val header = binding.navigationView.getHeaderView(0)
        val edtProfile = header.findViewById<CardView>(R.id.cv1)
        val ehoMoneyActivity = header.findViewById<CardView>(R.id.cv2)

        val profileImage = header.findViewById<ShapeableImageView>(R.id.iv_profile)
        val userName = header.findViewById<TextView>(R.id.tvUsername)
        val mobileNo = header.findViewById<TextView>(R.id.tvMobileNumber)
        val amount = header.findViewById<TextView>(R.id.tvAmount)

        Glide.with(this)
            .load(driverData?.data?.image)
            .placeholder(R.drawable.profile)
            .error(R.drawable.profile)
            .into(profileImage)

        userName.text = driverData?.data?.userName
        mobileNo.text = driverData?.data?.mobile.toString()

        edtProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra(DRIVERSDATA, driverData)
            startActivity(intent)
            finish()
        }

        ehoMoneyActivity.setOnClickListener {
            startActivity(Intent(this, EhoMoneyActivity::class.java))
            finish()
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
        val prefs = SharedPreferenceManager(this)
        prefs.logoutUser()
        startActivity(Intent(this, WelcomeActivity::class.java))
        finish()
    }

}