package com.driver.eho.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.driver.eho.R
import com.driver.eho.databinding.ActivityTopupWalletBinding
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject

class TopupWalletActivity : AppCompatActivity(), PaymentResultListener {
    private lateinit var binding: ActivityTopupWalletBinding
    val TAG:String = TopupWalletActivity::class.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopupWalletBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*
     * To ensure faster loading of the Checkout form,
     * call this method as early as possible in your checkout flow
     * */
        Checkout.preload(applicationContext)

        binding.btnWalletToBank.setOnClickListener {
            makePayment()
        }
        addMoney()

    }

    private fun makePayment() {
            val checkout = Checkout()
            checkout.setKeyID("rzp_test_on6hHJ99jd97Ec")
            checkout.setImage(R.drawable.brand_logo)
            val activity: Activity = this

            try {
                val options = JSONObject()
                options.put("name","EHO Partner")
                options.put("description","Demoing Charges")
                //You can omit the image option to fetch the image from dashboard
                options.put("image","https://s3.amazonaws.com/rzp-mobile/images/rzp.png")
                options.put("theme.color", "#3399cc")
                options.put("currency","INR")
                /* options.put("order_id", "order_DBJOWzybf0sJbb")*/
                options.put("amount","50000")// 500 * 100

                val retryObj = JSONObject()
                retryObj.put("enabled", true)
                retryObj.put("max_count", 4)
                options.put("retry", retryObj)

                val prefill = JSONObject()
                prefill.put("email","niral@example.com")
                prefill.put("contact","6352734040")

                options.put("prefill",prefill)
                checkout.open(activity,options)
            }catch (e: Exception){
                Toast.makeText(activity,"Error in payment: "+ e.message, Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
    }


    // Add Money
    @SuppressLint("SetTextI18n")
    private fun addMoney() {
        binding.tvAdd1000.setOnClickListener {
            binding.edtAmount.setText("1000").toString()
        }
        binding.tvAdd2000.setOnClickListener {
            binding.edtAmount.setText("2000").toString()
        }
        binding.tvAdd3000.setOnClickListener {
            binding.edtAmount.setText("3000").toString()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, WelcomeActivity::class.java))
        finish()
    }

    override fun onPaymentSuccess(paymentId: String?) {
        Toast.makeText(this, "Successfully payment ID :$paymentId", Toast.LENGTH_SHORT).show()
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        Toast.makeText(this, "Failed and cause is :$p1", Toast.LENGTH_SHORT).show()
    }


}