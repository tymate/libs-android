package com.tymate.core.ui

import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


/**
 * Created by Aurélien Cocq
 * aurelien@tymate.com
 */
abstract class BindingDialogFragment<VB : ViewDataBinding> : BaseDialogFragment() {

    lateinit var binding: VB
        private set

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
//        binding.setLifecycleOwner(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onViewCreated(binding, savedInstanceState)
    }

    protected abstract val layoutId: Int

    protected abstract fun onViewCreated(binding: VB, savedInstanceState: Bundle?)
}