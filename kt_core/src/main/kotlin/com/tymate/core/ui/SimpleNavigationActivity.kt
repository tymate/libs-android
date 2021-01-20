package com.tymate.core.ui

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment

abstract class SimpleNavigationActivity<VB : ViewDataBinding> : BindingActivity<VB>() {

    open val navHostFragment
        get() = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment

    val navController: NavController?
        get() = navHostFragment?.navController

    override fun onViewCreated(savedInstanceState: Bundle?, binding: VB) {
        getToolbar()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        if (onNavUp()) {
            return
        }
        if (navController?.navigateUp() == true) {
            return
        }
        val fragment = supportFragmentManager.fragments.lastOrNull { it is HasNestedNavigation }
        if (fragment != null && fragment is HasNestedNavigation) {
            if (fragment.onBackPressed()) {
                return
            }
        }
        super.onBackPressed()
    }

    private fun onNavUp(): Boolean {
        val navHostFragment = navHostFragment ?: return false
        val fragments = navHostFragment
            .childFragmentManager
            .fragments
            .filter { it.userVisibleHint && it is HasNestedNavigation }
        return onNavigationUp(fragments)
    }

    private fun onNavigationUp(fragments: List<Fragment>): Boolean {
        val nested = fragments
            .filter { it is HasNestedNavigation }
            .map { it as HasNestedNavigation }
            .firstOrNull() ?: return false
        if (nested.onNavigateUp()) {
            return true
        }
        return onNavigationUp((nested as Fragment).childFragmentManager.fragments)
    }

//    override fun onSupportNavigateUp(): Boolean {
//        val currentHost = currentNestedNavigation()
//        if (currentHost == null || !currentHost.onNavigateUp()) {
//            return navController?.navigateUp() == true
//        }
//        return super.onSupportNavigateUp()
//    }
//
//    override fun onBackPressed() {
//        val currentHost = currentNestedNavigation()
//        if (currentHost == null || !currentHost.onBackPressed()) {
//            super.onBackPressed()
//        }
//    }
//
//    private fun currentNestedNavigation(): HasNestedNavigation? {
//        return navHostFragment?.childFragmentManager?.fragments
//                ?.asSequence()
//                ?.filter { it.userVisibleHint && it is HasNestedNavigation }
//                ?.map { it as HasNestedNavigation }
//                ?.firstOrNull()
//    }
}