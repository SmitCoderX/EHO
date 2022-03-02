package com.driver.eho.ui.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.driver.eho.R
import com.driver.eho.SharedPreferenceManager
import com.driver.eho.dialog.CustomProgressDialog
import com.google.android.material.snackbar.Snackbar

@Suppress("DEPRECATION")
abstract class BaseActivity : AppCompatActivity() {
    lateinit var sp: SharedPreferenceManager
    lateinit var progressDialog: ProgressDialog
    lateinit var customProgressDialog: CustomProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sp = SharedPreferenceManager(getActivity())

    }

    // Snackbar main
    open fun showSnackBar(context: Context, message: String?) {
        var view =
            (findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0) as ViewGroup
        val snackbar = Snackbar.make(view, message.toString(), Snackbar.LENGTH_LONG)
            .setActionTextColor(Color.WHITE)
        val viewGroup = snackbar.view as ViewGroup
        viewGroup.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        val viewTv = snackbar.view
        val tv = viewTv.findViewById<TextView>(R.id.snackbar_text)
        tv.setTextColor(ContextCompat.getColor(context, R.color.blue))
        tv.maxLines = 5
        snackbar.show()
    }

    // simple Dialog Progress
    open fun showProgressDialog(context: Context) {
        try {
            progressDialog = ProgressDialog.show(context, "", "Please wait...", true, false)
            progressDialog.setProgressStyle(R.style.AppCompatAlertDialogStyle)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*Show Progress Dialog*/
    open fun showProgressDialog(context: Context, canCancel: Boolean) {
        progressDialog = ProgressDialog.show(context, "", "Please wait...", true, canCancel)
    }

    /*Hide Progress Dialog*/
    open fun hideProgressDialog() {
        try {
            if (progressDialog.isShowing) {
                progressDialog.hide()
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*Logout*/
    open fun getCallLogout() {
        val i = Intent(getActivity(), WelcomeActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
        finish()
    }


    open fun getActivity(): Activity {
        return this
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
}