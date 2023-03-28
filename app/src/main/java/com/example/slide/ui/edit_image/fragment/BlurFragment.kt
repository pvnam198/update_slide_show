package com.example.slide.ui.edit_image.fragment

import android.media.ThumbnailUtils
import android.view.View
import android.widget.SeekBar
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.slide.R
import com.example.slide.base.BaseFragment
import com.example.slide.base.InitViewTools
import com.example.slide.databinding.FragmentBlurBinding
import com.example.slide.event.EditImageLoadedEvent
import com.example.slide.ui.edit_image.BlurProvider
import com.example.slide.ui.edit_image.EditImageActivity
import com.example.slide.ui.edit_image.adapter.BlurAdapter
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class BlurFragment : BaseFragment<FragmentBlurBinding>(), View.OnClickListener {

    private var processChangeFromUser = 0

    private var currentBlurMask: BlurProvider.BlurMask? = null

    private val editImageActivity by lazy {
        activity as EditImageActivity
    }

    override fun bindingView(): FragmentBlurBinding {
        return FragmentBlurBinding.inflate(layoutInflater)
    }

    override fun initViewTools() = InitViewTools({ R.layout.fragment_blur }, { true })

    override fun initConfiguration() {
        super.initConfiguration()
        binding.recyclerViewBlur.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        listItemConfig()
    }

    override fun initListener() {
        super.initListener()
        binding.btnCheck.setOnClickListener(this)
        binding.btnClose.setOnClickListener(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onImageLoaded(event: EditImageLoadedEvent) {
        listItemConfig()
    }

    private fun listItemConfig() {
        editImageActivity.getCurrentBitmap()?.let {
            val bitmapThumb = ThumbnailUtils.extractThumbnail(it, 150, 150)
            val blurAdapter =
                BlurAdapter(
                    bitmapThumb,
                    requireContext(),
                    splashItemOnclickCallback = { blurMask ->
                        currentBlurMask = blurMask
                        editImageActivity.addBlurSticker(currentBlurMask)
                    })
            binding.recyclerViewBlur.adapter = blurAdapter
            binding.recyclerViewBlur.setHasFixedSize(true)
        }

    }

    override fun initTask() {
        super.initTask()
        setBlurChange()
    }

    private fun setBlurChange() {
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, process: Int, fromUser: Boolean) {
                if (fromUser) {
                    processChangeFromUser = process
                    binding.seekBar.progress = process
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                editImageActivity.changeValueBlur(processChangeFromUser.toFloat())
            }
        })
    }

    override fun releaseData() {}
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_check -> {
                if (currentBlurMask == null) {
                    editImageActivity.cancelBlurImageEdit()
                } else {
                    editImageActivity.saveImageBlur()
                }
            }
            R.id.btn_close -> {
                editImageActivity.cancelBlurImageEdit()
            }
        }
    }
}
