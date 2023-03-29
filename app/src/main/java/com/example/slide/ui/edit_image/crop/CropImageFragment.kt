package com.example.slide.ui.edit_image.crop

import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import android.view.View
import com.example.slide.R
import com.example.slide.base.BaseFragment
import com.example.slide.base.InitViewTools
import com.example.slide.databinding.FragmentRotateBinding
import com.example.slide.ui.edit_image.EditImageActivity
import com.example.slide.util.BitmapUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CropImageFragment : BaseFragment<FragmentRotateBinding>(), View.OnClickListener {
    override fun bindingView(): FragmentRotateBinding {
        return FragmentRotateBinding.inflate(layoutInflater)
    }

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
            cropBitmap = bitmap.copy(bitmap.config, true)
            binding.cropView.setImageBitmap(bitmap)
            binding.llLoading.visibility = View.GONE
        } else
            editImageActivity.onBackPressed()
    }

    override fun initListener() {
        super.initListener()

        binding.btnCheck.setOnClickListener(this)
        binding.btnFlipVertical.setOnClickListener(this)
        binding.btnFlipHorizontal.setOnClickListener(this)
        binding.btnRotate.setOnClickListener(this)
        binding.btnRoomDefault.setOnClickListener(this)
        binding.btnClose.setOnClickListener(this)
        binding.btnBack.setOnClickListener(this)
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
                binding.cropView.setImageBitmap(editImageActivity.getCurrentBitmap())
            }
            R.id.btn_rotate -> {
                binding.llLoading.visibility = View.VISIBLE
                CoroutineScope(Dispatchers.IO).launch {
                    cropBitmap = BitmapUtil.rotateBitmap(cropBitmap, 90.0f)
                    CoroutineScope(Dispatchers.Main).launch {
                        cropBitmap?.let {
                            binding.cropView.setImageBitmap(it)
                            binding.llLoading.visibility = View.GONE
                        }
                    }
                }
            }
            R.id.btn_flip_horizontal -> {
                binding.llLoading.visibility = View.VISIBLE
                CoroutineScope(Dispatchers.IO).launch {
                    cropBitmap = BitmapUtil.flipHBitmap(cropBitmap)
                    CoroutineScope(Dispatchers.Main).launch {
                        cropBitmap?.let {
                            binding.cropView.setImageBitmap(it)
                            binding.llLoading.visibility = View.GONE
                        }
                    }
                }
            }
            R.id.btn_flip_vertical -> {
                binding.llLoading.visibility = View.VISIBLE
                CoroutineScope(Dispatchers.IO).launch {
                    cropBitmap = BitmapUtil.flipVBitmap(cropBitmap)
                    CoroutineScope(Dispatchers.Main).launch {
                        cropBitmap?.let {
                            binding.cropView.setImageBitmap(it)
                            binding.llLoading.visibility = View.GONE
                        }
                    }
                }
            }
            R.id.btn_check -> {
                binding.cropView.hideGridLine()
                val bitmap = Bitmap.createBitmap(
                    binding.layoutCropViewParent.width,
                    binding.layoutCropViewParent.height,
                    Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(bitmap)
                binding.layoutCropViewParent.draw(canvas)
                editImageActivity.saveImageCrop(bitmap)
            }
            else -> {
            }
        }
    }

}