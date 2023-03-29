package com.example.slide.ui.select_image

import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.slide.R
import com.example.slide.base.BaseFragment
import com.example.slide.base.InitViewTools
import com.example.slide.databinding.FragmentSwapsBinding
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager

class SwapsFragment : BaseFragment<FragmentSwapsBinding>(), View.OnClickListener {
    override fun bindingView(): FragmentSwapsBinding {
        return FragmentSwapsBinding.inflate(layoutInflater)
    }

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
        binding.btnBack.setOnClickListener(this)
        binding.btnSubmit.setOnClickListener(this)
    }

    fun editImageDone() {
        isImageEdit = true
    }

    private fun initSelectedImages() {
        val dragDropManager = RecyclerViewDragDropManager()
        binding.recyclerSelectedImages.layoutManager = LinearLayoutManager(requireContext())
        swapsAdapter?.let {
            adapter = dragDropManager.createWrappedAdapter(it)
        }
        binding.recyclerSelectedImages.adapter = adapter
        dragDropManager.attachRecyclerView(binding.recyclerSelectedImages)
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
            binding.btnBack -> {
                (requireActivity() as SelectActivity).onBackPressed()
            }
            binding.btnSubmit -> {
                (requireActivity() as SelectActivity).doneSwapImages(isImageEdit)
            }
        }
    }

}