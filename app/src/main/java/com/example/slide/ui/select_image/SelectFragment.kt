package com.example.slide.ui.select_image

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.slide.R
import com.example.slide.base.BaseFragment
import com.example.slide.base.InitViewTools
import com.example.slide.databinding.FragmentSelectBinding
import com.example.slide.event.ImageLoadStateChangedEvent
import com.example.slide.event.ImageSelectedChangedEvent
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class SelectFragment : BaseFragment<FragmentSelectBinding>(), View.OnClickListener {
    override fun bindingView(): FragmentSelectBinding {
        return FragmentSelectBinding.inflate(layoutInflater)
    }

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
        binding.ivCheck.setOnClickListener(this)
        binding.btnRemoveAllImage.setOnClickListener(this)
        binding.btnBack.setOnClickListener(this)
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
        binding.rvColors.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvColors.adapter = selectedImagesAdapter
    }

    private fun initImagesAdapter() {
        imagesAdapter = ImageAdapter(requireActivity() as SelectActivity)
        binding.recyclerviewImage.layoutManager =
            GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)
        binding.recyclerviewImage.adapter = imagesAdapter
    }

    private fun initAlbumsAdapter() {
        folderAdapter = FolderAdapter(selectActivity.albums, requireContext(), this)
        binding.recyclerviewAlbum.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerviewAlbum.adapter = folderAdapter
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
        binding.tvRequiteImages.visibility = if (amount == 0) View.VISIBLE else View.INVISIBLE
        binding.tvAmount.text = resources.getQuantityString(
            R.plurals.format_selected_images,
            amount, amount
        )
    }

    override fun releaseData() {}

    private fun imagesLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun imagesLoaded() {
        binding.progressBar.visibility = View.INVISIBLE
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
            binding.ivCheck -> {
                selectActivity.doneSelectImages()
            }
            binding.btnRemoveAllImage -> {
                if (selectActivity.selectedImages.size > 0)
                    RemoveAllImageDialogFragment.getInstance().show(parentFragmentManager, TAG)
            }
            binding.btnBack -> {
                requireActivity().onBackPressed()
            }
        }
    }

    fun removeAllSelectedImage() {
        selectedImagesAdapter.removeAllData()
        imagesAdapter.updateSelectedData(selectActivity.selectedImages)
        setAmountSelectedImage(0)
        binding.tvRequiteImages.visibility = View.VISIBLE
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