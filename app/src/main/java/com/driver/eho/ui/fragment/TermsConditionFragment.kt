package com.driver.eho.ui.fragment

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.driver.eho.R
import com.driver.eho.databinding.FragmentTermsConditionBinding


class TermsConditionFragment : Fragment(R.layout.fragment_terms_condition) {

    private lateinit var binding: FragmentTermsConditionBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTermsConditionBinding.bind(view)

        binding.webview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url.toString())
                return true
            }
        }
        binding.webview.loadUrl("https://www.ehohealthcare.com/terms-and-conditions/")
    }


}