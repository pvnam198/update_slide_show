package com.example.slide.ui.edit_image.crop

import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import android.view.View
import com.example.slide.R
import com.example.slide.base.BaseFragment
import com.example.slide.base.InitViewTools
import com.example.slide.ui.edit_image.EditImageActivity
import com.example.slide.util.BitmapUtil
import kotlinx.android.synthetic.main.fragment_rotate.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CropImageFragment : BaseFragment(), View.OnClickListener {

    override fun initViewTools() = InitViewTools({ R.layout.fragment_rotate })

    private var cropBitmap: Bitmap? = null

    private val editImageActivity by lazy {
        activity as EditImageActivity
    }

    companion object {
        const val TAG = "tags"
    }

    override fun initConfiguration() {
        super.initConfiguration()
        val bitmap = editImageActivity.originBitmap
        if (bitmap != null) {
            Log.d(TAG, "initConfiguration: ${bitmap.width}")
            Log.d(TAG, "initConfiguration: ${bitmap.height}")
            cropBitmap = bitmap.copy(bitmap.config, true)
            crop_view.setImageBitmap(bitmap)
            ll_loading.visibility = View.GONE
        } else
            editImageActivity.onBackPressed()
    }

    override fun initListener() {
        super.initListener()

        btn_check.setOnClickListener(this)
        btn_flip_vertical.setOnClickListener(this)
        btn_flip_horizontal.setOnClickListener(this)
        btn_rotate.setOnClickListener(this)
        btn_room_default.setOnClickListener(this)
        btn_close.setOnClickListener(this)
        btn_back.setOnClickListener(this)
    }

    override fun releaseData() {}

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_back -> {
                editImageActivity.onBackPressed()
            }
            R.id.btn_close -> {
                editImageActivity.onBackPressed()
            }
            R.id.btn_room_default -> {
                crop_view.setImageBitmap(editImageActivity.getCurrentBitmap())
            }
            R.id.btn_rotate -> {
                ll_loading.visibility = View.VISIBLE
                CoroutineScope(Dispatchers.IO).launch {
                    cropBitmap = BitmapUtil.rotateBitmap(cropBitmap, 90.0f)
                    CoroutineScope(Dispatchers.Main).launch {
                        cropBitmap?.let {
                            crop_view.setImageBitmap(it)
                            ll_loading.visibility = View.GONE
                        }
                    }
                }
            }
            R.id.btn_flip_horizontal -> {
                ll_loading.visibility = View.VISIBLE
                CoroutineScope(Dispatchers.IO).launch {
                    cropBitmap = BitmapUtil.flipHBitmap(cropBitmap)
                    CoroutineScope(Dispatchers.Main).launch {
                        cropBitmap?.let {
                            crop_view.setImageBitmap(it)
                            ll_loading.visibility = View.GONE
                        }
                    }
                }
            }
            R.id.btn_flip_vertical -> {
                ll_loading.visibility = View.VISIBLE
                CoroutineScope(Dispatchers.IO).launch {
                    cropBitmap = BitmapUtil.flipVBitmap(cropBitmap)
                    CoroutineScope(Dispatchers.Main).launch {
                        cropBitmap?.let {
                            crop_view.setImageBitmap(it)
                            ll_loading.visibility = View.GONE
                        }
                    }
                }
            }
            R.id.btn_check -> {
                crop_view.hideGridLine()
                val bitmap = Bitmap.createBitmap(
                    layout_crop_view_parent.width,
                    layout_crop_view_parent.height,
                    Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(bitmap)
                layout_crop_view_parent.draw(canvas)
                editImageActivity.saveImageCrop(bitmap)
            }
            else -> {
            }
        }
    }

}