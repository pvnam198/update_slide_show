package com.example.slide.base

import android.os.Bundle
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import com.example.slide.MyApplication
import org.greenrobot.eventbus.EventBus

abstract class BaseActivity : AppCompatActivity() {

    val myApplication by lazy { application as MyApplication }

    protected var saveInstanceStateCalled: Boolean = false

    private val initViewTools by lazy { initViewTools() }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = savedInstanceState ?: intent.extras
        bundle?.let { extractData(it) }
        saveInstanceStateCalled = false
        val layoutRes = initViewTools.layoutRes.invoke()
        if (layoutRes != 0)
            setContentView(layoutRes)
        if (initViewTools.hasEventBus.invoke())
            EventBus.getDefault().register(this)
        initConfiguration(savedInstanceState)
        initListener()
        initTask()
    }

    open fun extractData(bundle: Bundle) {
    }

    override fun onDestroy() {
        if (initViewTools.hasEventBus.invoke())
            EventBus.getDefault().unregister(this)
        releaseData()
        super.onDestroy()
    }

    abstract fun initViewTools(): InitViewTools

    open fun initConfiguration(savedInstanceState: Bundle?) {}

    open fun initListener() {}

    open fun initTask() {}

    abstract fun releaseData()

    @CallSuper
    override fun onStart() {
        super.onStart()
        saveInstanceStateCalled = false
    }

    @CallSuper
    override fun onResume() {
        super.onResume()
        saveInstanceStateCalled = false
    }

    fun canChangeFragmentManagerState(): Boolean {
        return !(saveInstanceStateCalled || isFinishing)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveInstanceStateCalled = true
    }

    fun showToast(stringResource: Int) {
        try {
            showToast(getString(stringResource))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showToast(content: String) {
        if (canChangeFragmentManagerState())
            Toast.makeText(this, content, Toast.LENGTH_SHORT).show()
    }

}
