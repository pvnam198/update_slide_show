package com.example.slide.ui.edit_image.fragment

import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.view.View
import android.widget.SeekBar
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.slide.R
import com.example.slide.base.BaseFragment
import com.example.slide.base.InitViewTools
import com.example.slide.databinding.FragmentOverlayBinding
import com.example.slide.event.EditImageLoadedEvent
import com.example.slide.ui.edit_image.EditImageActivity
import com.example.slide.ui.edit_image.adapter.OverlayAdapter
import com.example.slide.ui.edit_image.utils.FilterUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class OverlayFragment : BaseFragment<FragmentOverlayBinding>(), View.OnClickListener {

    private val ioScope = CoroutineScope(Dispatchers.IO)

    private val editImageActivity by lazy {
        activity as EditImageActivity
    }

    override fun bindingView(): FragmentOverlayBinding {
        return FragmentOverlayBinding.inflate(layoutInflater)
    }

    override fun initViewTools() = InitViewTools({
        R.layout.fragment_overlay
    },{true})

    override fun initConfiguration() {
        super.initConfiguration()
            binding.progressBar.visibility = View.VISIBLE
        loadOverlayBitmap()
        seekBarConfig()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onImageLoaded(event: EditImageLoadedEvent) {
        loadOverlayBitmap()
    }

    private fun seekBarConfig() {
        binding.seekBar.max = 100
        binding.seekBar.progress = 100
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                currentProcess: Int,
                fromUser: Boolean
            ) {
                if (fromUser) {
                    val intensity = currentProcess / 100.0f
                    editImageActivity.setFilterIntensity(intensity)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
    }

    override fun initListener() {
        super.initListener()
        binding.btnClose.setOnClickListener(this)
        binding.btnCheck.setOnClickListener(this)
    }

    override fun releaseData() {
        editImageActivity.popFragment(OverlayFragment::class.java.name)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_close -> {
                editImageActivity.cancelOverlay()
            }
            R.id.btn_check -> {
                editImageActivity.saveImageOverLay()
            }
        }
    }

    private fun loadOverlayBitmap() = ioScope.launch {
        editImageActivity.getCurrentBitmap()?.let {
            val bitmapOverlays: ArrayList<Bitmap> = ArrayList()
            val bitmap = ThumbnailUtils.extractThumbnail(it, 150, 150)
            bitmapOverlays.addAll(FilterUtils.getLstBitmapWithOverlay(bitmap))
            CoroutineScope(Dispatchers.Main).launch {
                binding.recyclerViewBlur.let {
                    binding.recyclerViewBlur.layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                    val overlayAdapter =
                        OverlayAdapter(
                            bitmapOverlays,
                            FilterUtils.OVERLAY_CONFIG,
                            overlayCallback = { config: String, position: Int ->
                                if (position == 0) {
                                    toggleSeekBar(false)
                                } else {
                                    toggleSeekBar(true)
                                }
                                editImageActivity.setImageOverlay(config)
                            })
                    binding.recyclerViewBlur.adapter = overlayAdapter
                    overlayAdapter.notifyDataSetChanged()
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun toggleSeekBar(visible: Boolean) {
        if (visible) {
            binding.seekBar.visibility = View.VISIBLE
        } else {
            binding.seekBar.visibility = View.INVISIBLE
        }
    }
}