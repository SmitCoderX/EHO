package com.driver.eho.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.driver.eho.R
import com.driver.eho.databinding.ActivityMainBinding
import com.driver.eho.ui.fragment.*
import com.google.android.material.navigation.NavigationView

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var actionBarToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        actionBarToggle = ActionBarDrawerToggle(this, binding.sideDrawer, 0, 0)
        binding.sideDrawer.addDrawerListener(actionBarToggle)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        /*loadingHeaderData()*/
        ProfileActivity()
        actionBarToggle.syncState()

        // side Drawer
        naviSideDrawer()
        setCurrentFragment(HomeFragment())

    }

    private fun ProfileActivity() {
        val header = binding.navigationView.getHeaderView(0)
        val edtProfile = header.findViewById<CardView>(R.id.cv1)
        val ehoMoneyActivity = header.findViewById<CardView>(R.id.cv2)

        edtProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }

        ehoMoneyActivity.setOnClickListener {
            startActivity(Intent(this, EhoMoneyActivity::class.java))
            finish()
        }
    }

    private fun naviSideDrawer() {
        binding.navigationView.setNavigationItemSelectedListener(this)
        binding.navigationView.setCheckedItem(R.id.home)

        binding.menuIcon.setOnClickListener {
            if (binding.sideDrawer.isDrawerVisible(GravityCompat.START)) {
                binding.sideDrawer.closeDrawer(GravityCompat.START)
                supportActionBar?.show()
            } else {
                binding.sideDrawer.openDrawer(GravityCompat.START)
                supportActionBar?.hide()
            }

        }
    }

    // on menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarToggle.onOptionsItemSelected(item)) {
            /* supportActionBar!!.hide() */// hide the toolbar
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
            super.onBackPressed()
        }
    }

    // set Fragment
    private fun setCurrentFragment(fragment: Fragment) {
        val frag = supportFragmentManager.beginTransaction()
        frag.replace(R.id.flFragment, fragment)
        frag.commit()
    }

    // Load Fragment
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.flFragment, fragment).commit()
    }

    @SuppressLint("ResourceAsColor", "SetTextI18n")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navHome -> {
                binding.tvTabNames.text = "Home"
                setCurrentFragment(HomeFragment())
            }
            R.id.navHistory -> {
                binding.tvTabNames.text = "Booking History"
                setCurrentFragment(HistoryFragment())
            }
            R.id.navSupport -> {
                binding.tvTabNames.text = "EHO Support"
                setCurrentFragment(SupportFragment())
            }
            R.id.navTerms -> {
                binding.tvTabNames.text = "Terms & Condition"
                setCurrentFragment(TermsConditionFragment())
            }
            R.id.navPrivacyPolicy -> {
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

    // AlertDialog For Logout
    private fun getLogoutAlertDialog() {
        val builder1 = AlertDialog.Builder(this)
        builder1.setMessage("Are you sure you want to logout ?")
        builder1.setCancelable(true)
        builder1.setPositiveButton(
            "Yes"
        ) { dialog, _ ->
//            callLogoutAPI()
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

    /* private fun callLogoutAPI() {
         val params = JSONObject()
         try {
             params.put("email", sp.get_Email().toString())
             params.put("_id", sp.get_Id().toString())

             Log.e("event request --->", params.toString())

             val jsonParser = JsonParser()
             val parameter = jsonParser.parse(params.toString()) as JsonObject

             val call: Call<DriverSignInResponse> = ApiClient.getInstance().getDataFromDriverSignIn(parameter)

             call.enqueue(object : Callback<DriverSignInResponse>{
                 override fun onResponse(call: Call<DriverSignInResponse>, response: Response<DriverSignInResponse>) {
                     val generalResponse : DriverSignInResponse?
                     if (response.isSuccessful){
                         generalResponse = response.body()
                         if (generalResponse?.code == 200){
                             Toast.makeText(this@MainActivity, generalResponse.message, Toast.LENGTH_SHORT).show()
                             getCallLogout()
                         }
                     }
                 }
                 override fun onFailure(call: Call<DriverSignInResponse>, t: Throwable) {
                     Toast.makeText(this@MainActivity, "Failed", Toast.LENGTH_SHORT).show()

                 }
             })
         }catch (e: JSONException){
             e.printStackTrace()
             Toast.makeText(this@MainActivity, "sever error", Toast.LENGTH_SHORT).show()
         }
     }*/

}