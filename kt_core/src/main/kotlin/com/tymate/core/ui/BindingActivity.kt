package com.tymate.core.ui

import android.content.Context
import android.content.SharedPreferences
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import com.tymate.core.util.ContextWrapper
import java.util.*

/**
 * Created by Aurélien Cocq
 * aurelien@tymate.com
 */
abstract class BindingActivity<VB : ViewDataBinding> : BaseActivity() {

    lateinit var binding: VB
        private set

    var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutId)
//        binding.setLifecycleOwner(this)
        initAppBarLayout()
        onViewCreated(savedInstanceState, binding)
    }

    override fun attachBaseContext(newBase: Context?) {
        sharedPreferences =
            newBase?.getSharedPreferences(
                "PREFERENCES_SETTINGS",
                Context.MODE_PRIVATE
            )
        val lang = sharedPreferences?.getString("LANG", "fr") ?: "fr"
        val locale = Locale(lang)

        val context: Context = ContextWrapper.wrap(newBase, locale)
        super.attachBaseContext(context)
    }

    protected abstract val layoutId: Int

    protected abstract fun onViewCreated(savedInstanceState: Bundle?, binding: VB)
}