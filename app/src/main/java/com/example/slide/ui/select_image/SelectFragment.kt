package com.example.slide.ui.select_image

import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.slide.R
import com.example.slide.base.BaseFragment
import com.example.slide.base.InitViewTools
import com.example.slide.event.ImageLoadStateChangedEvent
import com.example.slide.event.ImageSelectedChangedEvent
import kotlinx.android.synthetic.main.fragment_select.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class SelectFragment : BaseFragment(), View.OnClickListener {

    override fun initViewTools() = InitViewTools({
        R.layout.fragment_select
    }, { true })

    private val selectActivity by lazy { requireActivity() as SelectActivity }

    private lateinit var folderAdapter: FolderAdapter

    private lateinit var imagesAdapter: ImageAdapter

    private val selectedImagesAdapter: ImageSelectedAdapter by lazy {
        ImageSelectedAdapter(selectActivity.selectedImages, requireContext(), this)
    }

    companion object {

        const val TAG: String = "SelectFragment"

        fun getInstance(): SelectFragment {
            return SelectFragment()
        }
    }

    override fun initConfiguration() {
        super.initConfiguration()
        setAmountSelectedImage(selectedImagesAdapter.itemCount)
        initAlbumsAdapter()
        initImagesAdapter()
        initSelectedImages()
        initImageState()
    }

    override fun initListener() {
        super.initListener()
        iv_check.setOnClickListener(this)
        btn_remove_all_image.setOnClickListener(this)
        btn_back.setOnClickListener(this)
    }

    override fun initTask() {
        super.initTask()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onImagesStateChanged(event: ImageLoadStateChangedEvent) {
        initImageState()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onImagesSelectedChanged(event: ImageSelectedChangedEvent) {
        imagesAdapter.notifyItemChanged(event.position)
        onSelectedImageChanged()
    }

    private fun initImageState() {
        if (selectActivity.state == SelectActivity.STATE_LOADING) {
            imagesLoading()
        } else {
            imagesLoaded()
        }
    }

    private fun initSelectedImages() {
        rv_colors.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rv_colors.adapter = selectedImagesAdapter
    }

    private fun initImagesAdapter() {
        imagesAdapter = ImageAdapter(requireActivity() as SelectActivity)
        recyclerview_image.layoutManager =
                GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)
        recyclerview_image.adapter = imagesAdapter
    }

    private fun initAlbumsAdapter() {
        folderAdapter = FolderAdapter(selectActivity.albums, requireContext(), this)
        recyclerview_album.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerview_album.adapter = folderAdapter
    }

    fun onSelectedImageChanged() {
        initSelectedImagesState()
    }

    private fun setImagesNotifyDataSetChanged() {
        imagesAdapter.notifyDataSetChanged()
    }

    private fun initSelectedImagesState() {
        selectedImagesAdapter.notifyDataSetChanged()
        setAmountSelectedImage(selectedImagesAdapter.itemCount)
    }

    private fun setAmountSelectedImage(amount: Int) {
        tv_requite_images.visibility = if (amount == 0) View.VISIBLE else View.INVISIBLE
        tv_amount.text = resources.getQuantityString(
                R.plurals.format_selected_images,
                amount, amount
        )
    }

    override fun releaseData() {}

    private fun imagesLoading() {
        progress_bar.visibility = View.VISIBLE
    }

    private fun imagesLoaded() {
        progress_bar.visibility = View.INVISIBLE
        folderAdapter.updateData(selectActivity.albums)
        if (selectActivity.albums.isNotEmpty()) {
            imagesAdapter.updateData(
                    selectActivity.albums[0].imageList,
                    selectActivity.selectedImages
            )
        }
    }

    override fun onClick(view: View) {
        when (view) {
            iv_check -> {
                selectActivity.doneSelectImages()
            }
            btn_remove_all_image -> {
                if (selectActivity.selectedImages.size > 0)
                    RemoveAllImageDialogFragment.getInstance().show(parentFragmentManager, TAG)
            }
            btn_back -> {
                requireActivity().onBackPressed()
            }
        }
    }

    fun removeAllSelectedImage() {
        selectedImagesAdapter.removeAllData()
        imagesAdapter.updateSelectedData(selectActivity.selectedImages)
        setAmountSelectedImage(0)
        tv_requite_images.visibility = View.VISIBLE
    }

    fun removeSelectedImageAt(position: Int) {
        if (position >= 0 && position < selectActivity.selectedImages.size) {
            selectActivity.selectedImages.removeAt(position)
        }
        initSelectedImagesState()
        imagesAdapter.updateSelectedData(selectActivity.selectedImages)
    }

    fun onAlbumSelected(position: Int) {
        imagesAdapter.updateData(
                selectActivity.albums[position].imageList,
                selectActivity.selectedImages
        )
    }

}