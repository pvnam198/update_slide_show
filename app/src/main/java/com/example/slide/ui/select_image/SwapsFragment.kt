package com.example.slide.ui.select_image

import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.slide.R
import com.example.slide.base.BaseFragment
import com.example.slide.base.InitViewTools
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager
import kotlinx.android.synthetic.main.activity_swaps.btn_back
import kotlinx.android.synthetic.main.activity_swaps.btn_submit
import kotlinx.android.synthetic.main.fragment_swaps.*

class SwapsFragment : BaseFragment(), View.OnClickListener {

    override fun initViewTools() = InitViewTools({
        R.layout.fragment_swaps
    })

    private var swapsAdapter: SwapsAdapter? = null

    private var adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>? = null

    private var isImageEdit = false

    companion object {

        const val TAG = "SwapsFragment"

        fun newInstance(): SwapsFragment {
            return SwapsFragment()
        }
    }

    override fun initConfiguration() {
        super.initConfiguration()
        swapsAdapter = SwapsAdapter(
                requireActivity() as SelectActivity,
                (requireActivity() as SelectActivity).selectedImages
        )
        initSelectedImages()
    }

    override fun initListener() {
        super.initListener()
        btn_back.setOnClickListener(this)
        btn_submit.setOnClickListener(this)
    }

    fun editImageDone() {
        isImageEdit = true
    }

    private fun initSelectedImages() {
        val dragDropManager = RecyclerViewDragDropManager()
        recycler_selected_images.layoutManager = LinearLayoutManager(requireContext())
        swapsAdapter?.let {
            adapter = dragDropManager.createWrappedAdapter(it)
        }
        recycler_selected_images.adapter = adapter
        dragDropManager.attachRecyclerView(recycler_selected_images)
        swapsAdapter?.notifyDataSetChanged()
    }

    fun editImageDone(position: Int) {
        swapsAdapter?.notifyItemChanged(position)
        lifecycleScope.launchWhenResumed { (requireActivity() as SelectActivity).saveDraft() }
    }

    fun dataChanged() {
        swapsAdapter?.notifyDataSetChanged()
        editImageDone()
        lifecycleScope.launchWhenResumed { (requireActivity() as SelectActivity).saveDraft() }
    }

    fun zoomImage(url: String) {
        ZoomImageDialogFragment.getInstance(url).show(parentFragmentManager, ZoomImageDialogFragment.TAG)
    }

    override fun releaseData() {

    }

    override fun onClick(view: View) {
        when (view) {
            btn_back -> {
                (requireActivity() as SelectActivity).onBackPressed()
            }
            btn_submit -> {
                (requireActivity() as SelectActivity).doneSwapImages(isImageEdit)
            }
        }
    }

}