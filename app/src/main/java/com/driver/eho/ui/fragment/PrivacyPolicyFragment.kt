package com.driver.eho.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.driver.eho.R
import com.driver.eho.databinding.FragmentPrivacyPolicyBinding


class PrivacyPolicyFragment : Fragment(R.layout.fragment_privacy_policy) {

    private lateinit var binding: FragmentPrivacyPolicyBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPrivacyPolicyBinding.bind(view)

        binding.webview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url.toString())
                return true
            }
        }
        binding.webview.loadUrl("https://www.google.co.in/")


    }


}