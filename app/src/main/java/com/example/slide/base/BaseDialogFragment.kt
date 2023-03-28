package com.example.slide.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.example.slide.R

abstract class BaseDialogFragment : DialogFragment() {

    abstract val layoutId: Int

    protected var saveInstanceStateCalled: Boolean = false

    open val isClearFlag = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layoutId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        saveInstanceStateCalled = false
        val bundle = savedInstanceState ?: arguments
        bundle?.let { extractData(it) }
        initConfiguration()
        initListener()
        initTask()
    }

    override fun onStart() {
        super.onStart()
        saveInstanceStateCalled = false
        if (dialog == null || dialog!!.window == null)
            return
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )

        if (isClearFlag){
            dialog!!.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            dialog!!.window!!.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }
        dialog!!.window!!.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.md_pink_A400)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        saveInstanceStateCalled = true
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        releaseView()
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseData()
    }

    fun canChangeFragmentManagerState(): Boolean {
        return !(saveInstanceStateCalled)
    }

    override fun show(manager: FragmentManager, tag: String?) {
        manager.findFragmentByTag(tag).let { fragment ->
            fragment ?: let {
                manager.beginTransaction().let { transition ->
                    this.show(transition, tag)
                }
            }
        }
    }

    open fun extractData(it: Bundle) {}

    open fun initConfiguration() {}

    open fun initListener() {}

    open fun initTask() {}

    open fun releaseView() {}

    open fun releaseData() {}
}