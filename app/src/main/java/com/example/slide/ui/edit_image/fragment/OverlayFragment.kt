package com.example.slide.ui.edit_image.fragment

import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.view.View
import android.widget.SeekBar
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.slide.R
import com.example.slide.base.BaseFragment
import com.example.slide.base.InitViewTools
import com.example.slide.event.EditImageLoadedEvent
import com.example.slide.ui.edit_image.EditImageActivity
import com.example.slide.ui.edit_image.adapter.OverlayAdapter
import com.example.slide.ui.edit_image.utils.FilterUtils
import kotlinx.android.synthetic.main.fragment_overlay.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class OverlayFragment : BaseFragment(), View.OnClickListener {

    private val ioScope = CoroutineScope(Dispatchers.IO)

    private val editImageActivity by lazy {
        activity as EditImageActivity
    }

    override fun initViewTools() = InitViewTools({
        R.layout.fragment_overlay
    },{true})

    override fun initConfiguration() {
        super.initConfiguration()
            progress_bar.visibility = View.VISIBLE
        loadOverlayBitmap()
        seekBarConfig()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onImageLoaded(event: EditImageLoadedEvent) {
        loadOverlayBitmap()
    }

    private fun seekBarConfig() {
        seek_bar.max = 100
        seek_bar.progress = 100
        seek_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
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
        btn_close.setOnClickListener(this)
        btn_check.setOnClickListener(this)
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
                recycler_view_blur?.let {
                    recycler_view_blur.layoutManager =
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
                    recycler_view_blur.adapter = overlayAdapter
                    overlayAdapter.notifyDataSetChanged()
                    progress_bar.visibility = View.GONE
                }
            }
        }
    }

    private fun toggleSeekBar(visible: Boolean) {
        if (visible) {
            seek_bar.visibility = View.VISIBLE
        } else {
            seek_bar.visibility = View.INVISIBLE
        }
    }
}