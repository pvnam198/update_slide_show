package com.example.slide.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

abstract class BaseBindingDialog<T : ViewBinding> : BaseDialogFragment() {

    override val layoutId: Int
        get() = -1

    open val binding by lazy { bindingView() }

    abstract fun bindingView(): T

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }
}