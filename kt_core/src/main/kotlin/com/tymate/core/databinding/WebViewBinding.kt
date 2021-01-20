package com.tymate.core.databinding

import android.webkit.WebView
import androidx.databinding.BindingAdapter


/**
 * Created by Aurélien Cocq
 * aurelien@tymate.com
 */
@BindingAdapter(value = ["url", "headers"], requireAll = false)
fun WebView.loadUrl(url: String, headers: Map<String, String>?) {
    if (headers == null) {
        loadUrl(url)
    } else {
        loadUrl(url, headers)
    }
}