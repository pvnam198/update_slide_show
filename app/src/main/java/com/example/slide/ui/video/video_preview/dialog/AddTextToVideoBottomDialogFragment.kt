package com.example.slide.ui.video.video_preview.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.slide.MyApplication
import com.example.slide.R
import com.example.slide.base.BaseBottomBindingDialog
import com.example.slide.databinding.DialogVideoAddTextBinding
import com.example.slide.framework.cutter.myrangeseekbar.AudioCutterView
import com.example.slide.framework.texttovideo.VideoTextFloatingItem
import com.example.slide.lib.keyboardheightprovider.KeyboardHeightProvider
import com.example.slide.ui.edit_image.adapter.ColorPickerAdapter
import com.example.slide.ui.edit_image.adapter.FontPickerAdapter
import com.example.slide.ui.edit_image.framework.AddTextProperties
import com.example.slide.ui.edit_image.utils.SystemUtil
import com.example.slide.ui.video.video_preview.VideoCreateActivity
import com.example.slide.util.FontProvider
import com.example.slide.util.StringUtils
import com.example.slide.util.Utils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar

class AddTextToVideoBottomDialogFragment : BaseBottomBindingDialog<DialogVideoAddTextBinding>(), View.OnClickListener {

    private var heightKeyboard = 0

    private var topHeightKeyboard = 0

    private var keyboardHeightTest: KeyboardHeightProvider? = null

    private var behavior: BottomSheetBehavior<View>? = null

    private var startSubtitleTime = 0

    private var endSubtitleTime = 0

    private var isFullTime = true

    var duration = 0

    private var videoTextSticker: VideoTextFloatingItem? = null

    private lateinit var colorPickerAdapter: ColorPickerAdapter

    private lateinit var fontPickerAdapter: FontPickerAdapter

    private var textSizePreView = 30

    private var currentTextFont = ""

    private val defaultFont = ""

    private val handler by lazy {
        Handler(Looper.getMainLooper())
    }

    companion object {

        const val TAG = "AddTextToVideoBt"

        const val ARG_SUB_TITLE = "sub_title"

        const val ARG_EDIT_MODE = "edit_mode"

        fun getInstance(): AddTextToVideoBottomDialogFragment {
            return AddTextToVideoBottomDialogFragment().apply {
                arguments = bundleOf(ARG_EDIT_MODE to false)
            }
        }

        fun getInstance(
            videoTextSticker: VideoTextFloatingItem
        ): AddTextToVideoBottomDialogFragment {
            return AddTextToVideoBottomDialogFragment().apply {
                arguments = bundleOf(ARG_SUB_TITLE to videoTextSticker, ARG_EDIT_MODE to true)
            }
        }
    }

    private var isEditMode = false

    private val keyboardListener = object : KeyboardHeightProvider.KeyboardListener {
        override fun onHeightChanged(height: Int) {
            if (height < 0) { // height of notch
                topHeightKeyboard = -height
            } else if ((height + topHeightKeyboard) > 250 && (height + topHeightKeyboard) != heightKeyboard) {
                heightKeyboard = height + topHeightKeyboard
                handler.postDelayed({
                    val layoutParams = binding.layoutBottom.layoutParams
                    layoutParams.height = heightKeyboard
                    binding.layoutBottom.layoutParams = layoutParams
                }, 100)

            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(ARG_EDIT_MODE, isEditMode)
    }

    override fun bindingView(): DialogVideoAddTextBinding {
        return DialogVideoAddTextBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_video_add_text, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = requireArguments()
        videoTextSticker = bundle.getSerializable(ARG_SUB_TITLE) as VideoTextFloatingItem?
        Log.d("kimkaka1", "onViewCreated: " + videoTextSticker)
        isEditMode = bundle.getBoolean(ARG_EDIT_MODE)
        if (isEditMode && videoTextSticker == null) {
            dismiss()
            return
        }
        initConfig()
        initListener()
        if (!isEditMode) {
            showQueryTab()
        } else {
            binding.edtText.requestFocus()
            showTextSubTitleTimeTab()
        }
    }

    private fun initConfig() {
        initPreviewText()
        initTextSize()
        initTextDuration()
        initTextColors()
        initTextFonts()

        videoTextSticker?.let {
            binding.edtText.setText(it.addTextProperties.text)
            colorPickerAdapter.updateSelectedColor(it.addTextProperties.textColor)
            isFullTime = it.isFullTime
            if (isFullTime) {
                startSubtitleTime = 0
                endSubtitleTime = duration
            } else {
                startSubtitleTime = it.startTime
                endSubtitleTime = it.endTime
            }
            binding.seekBarSubtitle.minProgress = startSubtitleTime
            binding.seekBarSubtitle.maxProgress = endSubtitleTime
            binding.tvTotalTime.text =
                StringUtils.getDurationDisplayFromSeconds(endSubtitleTime - startSubtitleTime)
            binding.tvStartTime.text = StringUtils.getDurationDisplayFromSeconds(startSubtitleTime)
            binding.tvEndTime.text = StringUtils.getDurationDisplayFromSeconds(endSubtitleTime)
            currentTextFont = it.addTextProperties.fontName
            fontPickerAdapter.setCurrentTextFont(currentTextFont)
        }
    }

    private fun initTextDuration() {
        duration =
            (requireActivity() as VideoCreateActivity).myApplication.videoDataState.totalSecond
        binding.seekBarSubtitle.setIsHideActiveLinePaint(true)
        binding.seekBarSubtitle.count = duration
        binding.seekBarSubtitle.maxProgress = duration
        endSubtitleTime = duration
        binding.tvTotalTime.text = StringUtils.getDurationDisplayFromSeconds(endSubtitleTime)
        binding.tvStartTime.text = StringUtils.getDurationDisplayFromSeconds(startSubtitleTime)
        binding.tvEndTime.text = StringUtils.getDurationDisplayFromSeconds(endSubtitleTime)
    }

    private fun initTextSize() {
        binding.seekBarTextSize.progress = textSizePreView
        binding.seekBarTextSize.max = 100
    }

    @SuppressLint("WrongConstant")
    private fun initPreviewText() {
        val addTextProperties =
            this.videoTextSticker?.addTextProperties ?: AddTextProperties.getDefaultProperties()

        Utils.setTypeface(requireContext(), binding.tvPreview, defaultFont)
        binding.tvPreview.setTextColor(Color.parseColor("#000000"))

        binding.tvPreview.text = addTextProperties.text
        binding.tvPreview.typeface = addTextProperties.getTypeface(requireContext())
        binding.tvPreview.setTextColor(addTextProperties.textColor)

        if (addTextProperties.paddingHeight > 0) {
            binding.tvPreview.setPadding(
                binding.tvPreview.paddingLeft,
                addTextProperties.paddingHeight,
                binding.tvPreview.paddingRight,
                addTextProperties.paddingHeight
            )
        }
        if (addTextProperties.paddingWidth > 0) {
            binding.tvPreview.setPadding(
                addTextProperties.paddingWidth,
                binding.tvPreview.paddingTop,
                addTextProperties.paddingWidth,
                binding.tvPreview.paddingBottom
            )
        }
        if (addTextProperties.textShader != null) {
            binding.tvPreview.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            binding.tvPreview.paint.shader = addTextProperties.textShader
        }

        binding.tvPreview.setPadding(
            SystemUtil.dpToPx(requireContext(), addTextProperties.paddingWidth),
            binding.tvPreview.paddingTop,
            SystemUtil.dpToPx(requireContext(), addTextProperties.paddingWidth),
            binding.tvPreview.paddingBottom
        )
        //binding.tvPreview.setTextColor(addTextProperties.textColor)
        binding.tvPreview.textAlignment = addTextProperties.textAlign
        binding.tvPreview.textSize = addTextProperties.textSize.toFloat()
        textSizePreView = addTextProperties.textSize
        binding.tvPreview.invalidate()
    }

    private fun initTextFonts() {
        binding.tvSize.text = textSizePreView.toString()
        binding.recyclerViewFont.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        fontPickerAdapter = FontPickerAdapter(FontProvider.fonts(), requireContext()) { font ->
            setTextFont(font)
        }
        binding.recyclerViewFont.adapter = fontPickerAdapter
    }

    private fun initTextColors() {
        binding.recyclerViewColor.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        colorPickerAdapter = ColorPickerAdapter(onColorSelected = { color ->
            setTextColor(color)
        })
        binding.recyclerViewColor.adapter = colorPickerAdapter
    }

    override fun onResume() {
        super.onResume()
        keyboardHeightTest?.onResume()
    }

    override fun onPause() {
        super.onPause()
        keyboardHeightTest?.onPause()
    }

    private fun initListener() {
        binding.btnDecrease.setOnClickListener(this)
        binding.btnIncrease.setOnClickListener(this)
        keyboardHeightTest = KeyboardHeightProvider(requireActivity())
        keyboardHeightTest!!.addKeyboardListener(keyboardListener)
        binding.edtText.addTextChangedListener(textPreviewListener)
        binding.btnSubmit.setOnClickListener(this)
        binding.btnClose.setOnClickListener(this)
        binding.btnColor.setOnClickListener(this)
        binding.btnFont.setOnClickListener(this)
        binding.btnSubtitle.setOnClickListener(this)
        binding.btnIncreaseStart.setOnClickListener(this)
        binding.btnTextDecreaseStart.setOnClickListener(this)
        binding.btnIncreaseEnd.setOnClickListener(this)
        binding.btnTextDecreaseEnd.setOnClickListener(this)
        binding.btnQwerty.setOnClickListener(this)
        timeSubtitleListener()
        onSeekerListener()
        binding.edtText.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) showQueryTab()
        }
    }

    private fun onSeekerListener() {
        binding.seekBarTextSize.setOnProgressChangeListener(object :
            DiscreteSeekBar.OnProgressChangeListener {

            override fun onProgressChanged(
                seekBar: DiscreteSeekBar?,
                value: Int,
                fromUser: Boolean
            ) {
                if (fromUser) {
                    updateViewsControl(value.toString())
                    updatePreviewTextSize(value)
                }
            }

            override fun onStartTrackingTouch(seekBar: DiscreteSeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: DiscreteSeekBar?) {
            }

        })
    }

    private fun timeSubtitleListener() {
        binding.seekBarSubtitle.setOnValueChangedListener(object :
            AudioCutterView.OnValueChangedListener() {
            override fun onValueChanged(progress: Int, fromUser: Boolean) {
            }

            override fun onStartChanged(minProgress: Int, fromUser: Boolean) {
                super.onStartChanged(minProgress, fromUser)
                if (fromUser) {
                    startSubtitleTime = minProgress
                    updateIsFullTime()
                    binding.tvTotalTime.text =
                        StringUtils.getDurationDisplayFromSeconds(endSubtitleTime - startSubtitleTime)
                    binding.tvStartTime.text = StringUtils.getDurationDisplayFromSeconds(minProgress)
                    isFullTime =
                        startSubtitleTime == 0 && endSubtitleTime == MyApplication.getInstance().videoDataState.totalSecond
                }
            }

            override fun onEndChanged(maxProgress: Int, fromUser: Boolean) {
                super.onEndChanged(maxProgress, fromUser)
                if (fromUser) {
                    endSubtitleTime = maxProgress
                    updateIsFullTime()
                    binding.tvTotalTime.text =
                        StringUtils.getDurationDisplayFromSeconds(endSubtitleTime - startSubtitleTime)
                    binding.tvEndTime.text = StringUtils.getDurationDisplayFromSeconds(maxProgress)
                    isFullTime =
                        startSubtitleTime == 0 && endSubtitleTime == MyApplication.getInstance().videoDataState.totalSecond
                }
            }

        })
    }

    private fun updateIsFullTime() = startSubtitleTime == 0 && endSubtitleTime == duration

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
        keyboardHeightTest?.removeKeyboardListener(keyboardListener)
        binding.edtText.removeTextChangedListener(textPreviewListener)
    }

    override fun onClick(view: View) {
        when (view) {
            binding.btnSubmit -> {
                closeKeyboard(view)
                if (isEditMode)
                    updateText()
                else
                    addText()
                dismiss()
            }
            binding.btnQwerty -> {
                toggleQueryTab()
            }
            binding.btnSubtitle -> {
                showTextSubTitleTimeTab()
            }
            binding.btnColor -> {
                showTextColorTab(view)
                Handler(Looper.myLooper()!!).postDelayed({
                    binding.recyclerViewColor?.smoothScrollToPosition(colorPickerAdapter.getSelectedPos())
                }, 300)
            }
            binding.btnFont -> {
                showTextFontTab(view)
                Handler(Looper.myLooper()!!).postDelayed({
                    binding.recyclerViewFont?.smoothScrollToPosition(fontPickerAdapter.getSelectedPos())
                }, 300)
            }
            binding.btnClose -> {
                closeKeyboard(view)
                videoTextSticker?.isShow = true
                dismiss()
            }
            binding.btnIncreaseStart -> {
                if (endSubtitleTime - startSubtitleTime > 1)
                    binding.seekBarSubtitle.minProgress = ++startSubtitleTime
                binding.tvTotalTime.text =
                    StringUtils.getDurationDisplayFromSeconds(endSubtitleTime - startSubtitleTime)
                binding.tvStartTime.text = StringUtils.getDurationDisplayFromSeconds(startSubtitleTime)
                updateIsFullTime()
            }
            binding.btnTextDecreaseStart -> {
                if (startSubtitleTime > 0)
                    binding.seekBarSubtitle.minProgress = --startSubtitleTime
                binding.tvTotalTime.text =
                    StringUtils.getDurationDisplayFromSeconds(endSubtitleTime - startSubtitleTime)
                binding.tvStartTime.text = StringUtils.getDurationDisplayFromSeconds(startSubtitleTime)
                updateIsFullTime()
            }
            binding.btnIncreaseEnd -> {
                if (endSubtitleTime < duration)
                    binding.seekBarSubtitle.maxProgress = ++endSubtitleTime
                binding.tvTotalTime.text =
                    StringUtils.getDurationDisplayFromSeconds(endSubtitleTime - startSubtitleTime)
                binding.tvEndTime.text = StringUtils.getDurationDisplayFromSeconds(endSubtitleTime)
                updateIsFullTime()
            }
            binding.btnTextDecreaseEnd -> {
                if (endSubtitleTime - startSubtitleTime > 1)
                    binding.seekBarSubtitle.maxProgress = --endSubtitleTime
                binding.tvTotalTime.text =
                    StringUtils.getDurationDisplayFromSeconds(endSubtitleTime - startSubtitleTime)
                binding.tvEndTime.text = StringUtils.getDurationDisplayFromSeconds(endSubtitleTime)
                updateIsFullTime()
            }
            binding.btnDecrease -> {
                updateViewsControl((--textSizePreView).toString())
                updatePreviewTextSize(binding.tvSize.text.toString().toInt())
            }
            binding.btnIncrease -> {
                updateViewsControl((++textSizePreView).toString())
                updatePreviewTextSize(binding.tvSize.text.toString().toInt())
            }
        }
    }

    private fun updateViewsControl(previewSize: String) {
        binding.tvSize.text = previewSize
        binding.seekBarTextSize.progress = previewSize.toInt()
    }

    private fun addText() {
        val previewText = binding.tvPreview.text.toString()
        if (TextUtils.isEmpty(previewText)) return
        val addTextProperties = AddTextProperties.getDefaultProperties()
        addTextProperties.textSize = textSizePreView
        addTextProperties.text = previewText
        addTextProperties.textWidth = binding.tvPreview.width
        addTextProperties.textHeight = binding.tvPreview.height
        addTextProperties.textColor = binding.tvPreview.currentTextColor
        addTextProperties.fontName = currentTextFont

        val videoTextSticker =
            VideoTextFloatingItem(
                requireContext(),
                addTextProperties
            )
        videoTextSticker.isFullTime = isFullTime
        videoTextSticker.startTime = startSubtitleTime
        videoTextSticker.endTime = endSubtitleTime

        (requireActivity() as VideoCreateActivity).addTextToVideo(videoTextSticker)
    }

    private fun updateText() {
        videoTextSticker?.let {
            it.isFullTime = isFullTime
            it.startTime = startSubtitleTime
            it.endTime = endSubtitleTime
            it.addTextProperties.fontName = currentTextFont
            it.addTextProperties.textColor =
                binding.tvPreview.currentTextColor
            it.addTextProperties.textSize = textSizePreView
            it.isShow = true
            it.setHandling(true)
            it.updateConfig(
                binding.tvPreview.text.toString(),
                binding.tvPreview.width,
                binding.tvPreview.height
            )
            (requireActivity() as VideoCreateActivity).updateSub(it)
        }
    }

    private fun toggleQueryTab() {
        activeTab(R.id.btn_qwerty)
        toggleKeyboard()
        binding.edtText.requestFocus()
        binding.subtitleLayout.visibility = View.GONE
        binding.recyclerViewColor.visibility = View.GONE
        binding.fontLayout.visibility = View.GONE
    }

    private fun showQueryTab() {
        Log.d(TAG, "showQueryTab: ")
        activeTab(R.id.btn_qwerty)
        showKeyboard()
        binding.edtText.requestFocus()
        binding.subtitleLayout.visibility = View.GONE
        binding.recyclerViewColor.visibility = View.GONE
        binding.fontLayout.visibility = View.GONE
    }

    private fun showTextSubTitleTimeTab() {
        activeTab(R.id.btn_subtitle)
        closeKeyboard(binding.btnSubtitle)
        binding.subtitleLayout.visibility = View.VISIBLE
        binding.recyclerViewColor.visibility = View.GONE
        binding.fontLayout.visibility = View.GONE
    }

    private fun showTextFontTab(view: View) {
        binding.edtText.clearFocus()
        activeTab(R.id.btn_font)
        closeKeyboard(view)
        binding.subtitleLayout.visibility = View.GONE
        binding.recyclerViewColor.visibility = View.GONE
        binding.fontLayout.visibility = View.VISIBLE
    }

    private fun showTextColorTab(view: View) {
        binding.edtText.clearFocus()
        activeTab(R.id.btn_color)
        closeKeyboard(view)
        binding.subtitleLayout.visibility = View.GONE
        binding.recyclerViewColor.visibility = View.VISIBLE
        binding.fontLayout.visibility = View.GONE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog.setOnShowListener {
            val bottomSheet =
                (it as BottomSheetDialog).findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
            behavior = BottomSheetBehavior.from(bottomSheet!!)
            behavior?.state = BottomSheetBehavior.STATE_EXPANDED
            behavior?.addBottomSheetCallback(callback)
        }
        return dialog
    }

    private val textPreviewListener = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(charSequence: CharSequence, p1: Int, p2: Int, p3: Int) {
            binding.tvPreview.text = charSequence
        }

        override fun afterTextChanged(p0: Editable?) {

        }

    }

    private var callback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                behavior?.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    private fun toggleKeyboard() {
        val inputMethodManager: InputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    private fun showKeyboard() {
        val inputMethodManager: InputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(binding.edtText, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun closeKeyboard(v: View) {
        val inputMethodManager: InputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
    }

    private fun activeTab(id: Int) {
        binding.btnQwerty.isSelected = R.id.btn_qwerty == id
        binding.btnSubtitle.isSelected = R.id.btn_subtitle== id
        binding.btnColor.isSelected = R.id.btn_color == id
        binding.btnFont.isSelected = R.id.btn_font == id
    }

    fun setTextFont(font: String) {
        Utils.setTypeface(requireContext(), binding.tvPreview, font)
        currentTextFont = font
    }

    fun setTextColor(color: Int) {
        binding.tvPreview.setTextColor(color)
    }

    fun updatePreviewTextSize(textSize: Int) {
        binding.tvPreview.textSize = textSize.toFloat()
        textSizePreView = textSize
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        videoTextSticker?.isShow = true
    }

}