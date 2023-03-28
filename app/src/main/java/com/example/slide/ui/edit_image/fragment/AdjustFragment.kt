package com.example.slide.ui.edit_image.fragment

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import com.example.slide.R
import com.example.slide.base.BaseFragment
import com.example.slide.base.InitViewTools
import com.example.slide.databinding.FragmentAdjustBinding
import com.example.slide.ui.edit_image.EditImageActivity
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar
import java.text.MessageFormat
import kotlin.math.abs

class AdjustFragment : BaseFragment<FragmentAdjustBinding>(), View.OnClickListener {
    
    override fun bindingView(): FragmentAdjustBinding {
        return FragmentAdjustBinding.inflate(layoutInflater)
    }

    override fun initViewTools() = InitViewTools({ R.layout.fragment_adjust })

    companion object {

        /*brightness*/
        const val BRIGHTNESS_IDENTIFICATION = 0

        const val BRIGHTNESS_MIN = -1.0f

        const val BRIGHTNESS_MAX = 1.0f

        /*sharpen*/
        const val SHARPEN_IDENTIFICATION = 2

        const val SHARPEN_DELTA_MIN = 0.0f

        const val SHARPEN_DELTA_MAX = 10.0f

        /*contrast*/
        const val CONTRAST_IDENTIFICATION = 1

        const val CONTRAST_MIN = 0.5f

        const val CONTRAST_MAX = 1.5f

        const val FILTER_CONFIG_TEMPLATE =
            "@adjust brightness {0} @adjust contrast {1} @adjust sharpen {2} @adjust saturation {3}"

        /*saturation*/
        const val SATURATION_IDENTIFICATION = 3

        const val SATURATION_MIN = 0.0f

        const val SATURATION_MAX = 2.0f

        var brightnessOriginValue = 0.0f

        var contrastOriginValue = 1.0f

        var sharpenOriginValue = 0.0f

        var saturationOriginValue = 1.0f

        var currentProcessBrightness = 50

        var currentProcessContrast = 50

        var currentProcessSharpen = 50

        var currentProcessSaturation = 50
    }

    private var identification = BRIGHTNESS_IDENTIFICATION

    private val editImageActivity: EditImageActivity by lazy {
        activity as EditImageActivity
    }

    override fun initListener() {
        super.initListener()
        binding.btnClose.setOnClickListener(this)
        binding.btnContrast.setOnClickListener(this)
        binding.btnBrightness.setOnClickListener(this)
        binding.btnSharpen.setOnClickListener(this)
        binding.btnCheck.setOnClickListener(this)
        binding.btnSaturation.setOnClickListener(this)
        setOnSeekBarChangeListener()
    }

    override fun initConfiguration() {
        super.initConfiguration()
        openFirstTimeConfig()
    }

    private fun openFirstTimeConfig() {
        activeTab(R.id.btn_brightness)
    }

    private fun setOnSeekBarChangeListener() {
        binding.sbAdjustLevel.setOnProgressChangeListener(object :
            DiscreteSeekBar.OnProgressChangeListener {
            override fun onProgressChanged(
                seekBar: DiscreteSeekBar?,
                currentProcess: Int,
                fromUser: Boolean
            ) {
                if (fromUser) {
                    getCurrentProcess(identification, currentProcess)

                    val delta: Float =
                        (getMaxValue(identification) - (getMaxValue(identification) + getMinValue(
                            identification
                        )) / 2) / 50

                    Log.d("tags", "delta: $delta")

                    val originValue =
                        abs(currentProcess) * delta + getMinValue(identification)

                    Log.d("tags", "originValue: $originValue")
                    val configs = getFilterConfig(identification, originValue)
                    Log.d("configs", "configs: $configs")
                    editImageActivity.setAdjustFilter(configs)
                }
            }

            override fun onStartTrackingTouch(seekBar: DiscreteSeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: DiscreteSeekBar?) {
            }

        })
    }

    private fun getCurrentProcess(identification: Int, process: Int) {
        return when (identification) {
            BRIGHTNESS_IDENTIFICATION -> {
                currentProcessBrightness = process
            }
            CONTRAST_IDENTIFICATION -> {
                currentProcessContrast = process
            }
            SHARPEN_IDENTIFICATION -> {
                currentProcessSharpen = process
            }
            SATURATION_IDENTIFICATION -> {
                currentProcessSaturation = process
            }
            else -> {
                currentProcessBrightness = process
            }
        }
    }

    private fun getFilterConfig(identification: Int, originValue: Float): String {
        when (identification) {
            BRIGHTNESS_IDENTIFICATION -> {
                brightnessOriginValue = originValue
            }
            CONTRAST_IDENTIFICATION -> {
                contrastOriginValue = originValue
            }
            SHARPEN_IDENTIFICATION -> {
                sharpenOriginValue = originValue
            }
            SATURATION_IDENTIFICATION -> {
                saturationOriginValue = originValue
            }
            else -> {
                brightnessOriginValue = originValue
            }
        }
        Log.d("tags", "saturationOriginValue: $saturationOriginValue")
        return MessageFormat.format(
            FILTER_CONFIG_TEMPLATE,
            "$brightnessOriginValue",
            "$contrastOriginValue",
            "$sharpenOriginValue",
            "$saturationOriginValue"
        )
    }

    private fun getMaxValue(identification: Int): Float {
        return when (identification) {
            BRIGHTNESS_IDENTIFICATION -> {
                BRIGHTNESS_MAX
            }
            CONTRAST_IDENTIFICATION -> {
                CONTRAST_MAX
            }
            SHARPEN_IDENTIFICATION -> {
                SHARPEN_DELTA_MAX
            }
            SATURATION_IDENTIFICATION -> {
                SATURATION_MAX
            }
            else -> {
                BRIGHTNESS_MAX
            }
        }
    }

    private fun getMinValue(identification: Int): Float {
        return when (identification) {
            BRIGHTNESS_IDENTIFICATION -> {
                BRIGHTNESS_MIN
            }
            CONTRAST_IDENTIFICATION -> {
                CONTRAST_MIN
            }
            SHARPEN_IDENTIFICATION -> {
                SHARPEN_DELTA_MIN
            }
            SATURATION_IDENTIFICATION -> {
                SATURATION_MIN
            }
            else -> {
                BRIGHTNESS_MIN
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_close -> {
                editImageActivity.closeAdjust()
                closeAction()
                resetCurrentProcess()
            }
            R.id.btn_brightness -> {
                activeTab(R.id.btn_brightness)
            }
            R.id.btn_contrast -> {
                activeTab(R.id.btn_contrast)
            }
            R.id.btn_sharpen -> {
                activeTab(R.id.btn_sharpen)
            }
            R.id.btn_saturation -> {
                activeTab(R.id.btn_saturation)
            }
            R.id.btn_check -> {
                resetCurrentProcess()
                editImageActivity.saveAdjustAsBitmap()
                editImageActivity.popFragment(AdjustFragment::class.java.name)
            }
        }
    }

    private fun setCurrentProcessSeekBar(process: Int) {
        binding.sbAdjustLevel.progress = process
    }

    private fun activeTab(id: Int) {
        binding.btnBrightness.isSelected = id == R.id.btn_brightness
        binding.btnSharpen.isSelected = id == R.id.btn_sharpen
        binding.btnContrast.isSelected = id == R.id.btn_contrast
        binding.btnSaturation.isSelected = id == R.id.btn_saturation

        binding.contraintSeekbarControl.visibility = View.VISIBLE
        when (id) {
            R.id.btn_brightness -> {
                identification = BRIGHTNESS_IDENTIFICATION
                binding.tvLabel.setText(R.string.brightness)
                binding.sbAdjustLevel.progress = currentProcessBrightness
            }
            R.id.btn_sharpen -> {
                identification = SHARPEN_IDENTIFICATION
                setCurrentProcessSeekBar(currentProcessSharpen)
                binding.tvLabel.setText(R.string.sharpen)
            }
            R.id.btn_contrast -> {
                identification = CONTRAST_IDENTIFICATION
                setCurrentProcessSeekBar(currentProcessContrast)
                binding.tvLabel.setText(R.string.contrast)
            }
            R.id.btn_saturation -> {
                identification = SATURATION_IDENTIFICATION
                setCurrentProcessSeekBar(currentProcessSaturation)
                binding.tvLabel.setText(R.string.saturation)
            }
        }
    }

    private fun closeAction() {
        val fragment =
            requireActivity().supportFragmentManager
                .findFragmentByTag(AdjustFragment::class.java.name)
        if (fragment != null) {
            requireActivity().supportFragmentManager
                .beginTransaction().remove(fragment)
                .commit()
        }
    }

    private fun resetCurrentProcess() {
        currentProcessBrightness = 50
        currentProcessContrast = 50
        currentProcessSharpen = 50
        currentProcessSaturation = 50
    }

    override fun releaseData() {}
}