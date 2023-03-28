package com.example.slide.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.slide.MyApplication
import org.greenrobot.eventbus.EventBus

abstract class BaseFragment<T : ViewBinding> : Fragment() {

    protected lateinit var binding: T

    private val initViewTools by lazy { initViewTools() }

    val myApplication by lazy { requireActivity().application as MyApplication }

    var isBind = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        extractData(savedInstanceState ?: arguments)
        if (initViewTools.hasEventBus.invoke())
            EventBus.getDefault().register(this)
        binding = bindingView()
        initTheme(binding.root)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isBind = true
        initConfiguration()
        initListener()
        initTask()
    }

    override fun onDestroyView() {
        isBind = false
        if (initViewTools.hasEventBus.invoke())
            EventBus.getDefault().unregister(this)
        releaseData()
        super.onDestroyView()
    }

    abstract fun bindingView(): T

    override fun onDestroy() {
        super.onDestroy()
    }

    abstract fun initViewTools(): InitViewTools

    open fun extractData(bundle: Bundle?) {}

    open fun initTheme(view: View) {}

    open fun initConfiguration() {}

    open fun initTask() {}

    open fun initListener() {}

    abstract fun releaseData()

}