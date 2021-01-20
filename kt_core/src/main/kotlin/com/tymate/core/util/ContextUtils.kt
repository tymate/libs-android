package com.tymate.core.util

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

fun Context.openUrl(string: String) {
    CustomTabsIntent.Builder().build().launchUrl(this, Uri.parse(string))
}