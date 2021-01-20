package com.tymate.core.ui

import android.animation.LayoutTransition
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.appbar.AppBarLayout
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import timber.log.Timber


/**
 * Created by Aurélien Cocq
 * aurelien@tymate.com
 */
abstract class BaseActivity : AppCompatActivity() {

    internal val coldDisposables: CompositeDisposable = CompositeDisposable()
    internal val hotDisposables: CompositeDisposable = CompositeDisposable()

    private var appBarLayout: AppBarLayout? = null
    private var toolbar: Toolbar? = null

    var savedInstanceState: Bundle? = null
        private set
    var isFirstStart = true
        private set
    var isStarted = false
        private set
    var isStopped = false
        private set

    val backstackCount: Int get() = supportFragmentManager.backStackEntryCount

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.savedInstanceState = savedInstanceState
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        initAppBarLayout()
    }

    open fun initAppBarLayout() {
        toolbar = getToolbar()
        appBarLayout = getAppBarLayout()
    }

    open fun hasSavedState(): Boolean {
        return savedInstanceState != null
    }

    open fun getToolbar(): Toolbar? {
        if (toolbar == null) {
            toolbar = findViewById(R.id.toolbar)
            initToolbar(toolbar)
        }
        return toolbar
    }

    open fun getAppBarLayout(): AppBarLayout? {
        if (appBarLayout == null) {
            appBarLayout = findViewById(R.id.app_bar_layout)
            appBarLayout?.run {
                val layoutTransition = LayoutTransition()
                layoutTransition.setDuration(150)
                this.layoutTransition = layoutTransition
            }
        }
        return appBarLayout
    }

    open fun initToolbar(toolbar: Toolbar?) {
        if (toolbar == null) {
            return
        }
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            initActionBar(actionBar)
            if (BuildConfig.DEBUG) {
                onDebugActionBar(actionBar)
            }
        }
    }
//
    open fun initActionBar(actionBar: ActionBar) {
        actionBar.setDisplayShowTitleEnabled(false)
//        actionBar.setDisplayShowHomeEnabled(false)
//        actionBar.setDisplayHomeAsUpEnabled(false)
    }

    open fun onDebugActionBar(actionBar: ActionBar) {

    }

    open fun setTitle(title: String) {
        val toolbar = getToolbar()
        if (toolbar != null) {
            toolbar.title = title
        }
    }

    override fun setTitle(title: CharSequence?) {
        Timber.i("setTitle $title")
        setTitle(title.toString())
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if (item.itemId == android.R.id.home) {
//            onHomeClicked()
//            return true
//        }
//        return false
//    }

//    open fun onHomeClicked() {
//        if (popBackStack() <= 0) {
//            finish()
//        }
//    }

//    open fun popBackStack(): Int {
//        val backStackCount = backstackCount
//        if (backStackCount > 0) {
//            supportFragmentManager.popBackStack()
//            return backStackCount
//        }
//        return 0
//    }

    override fun onStart() {
        super.onStart()
        isStopped = false
        isStarted = true
    }

    override fun onStop() {
        hotDisposables.clear()
        super.onStop()
        isFirstStart = false
        isStopped = true
        isStarted = false
    }

    override fun onDestroy() {
        coldDisposables.clear()
        super.onDestroy()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        try {
            return super.dispatchTouchEvent(ev)
        } catch (e: Exception) {
            e.printStackTrace()
            return true
        }
    }
}

fun Disposable.addTo(activity: BaseActivity) {
    if (activity.isStarted) {
        addTo(activity.hotDisposables)
    } else {
        addTo(activity.coldDisposables)
    }
}