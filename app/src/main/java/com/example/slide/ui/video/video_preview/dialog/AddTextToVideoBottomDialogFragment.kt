package com.example.slide.ui.video.video_preview.dialog

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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_video_add_text.*
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar

class AddTextToVideoBottomDialogFragment : BottomSheetDialogFragment(), View.OnClickListener {

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
                    val layoutParams = layout_bottom.layoutParams
                    layoutParams.height = heightKeyboard
                    layout_bottom.layoutParams = layoutParams
                }, 100)

            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(ARG_EDIT_MODE, isEditMode)
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
            edt_text.requestFocus()
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
            edt_text.setText(it.addTextProperties.text)
            colorPickerAdapter.updateSelectedColor(it.addTextProperties.textColor)
            isFullTime = it.isFullTime
            if (isFullTime) {
                startSubtitleTime = 0
                endSubtitleTime = duration
            } else {
                startSubtitleTime = it.startTime
                endSubtitleTime = it.endTime
            }
            seek_bar_subtitle.minProgress = startSubtitleTime
            seek_bar_subtitle.maxProgress = endSubtitleTime
            tv_total_time.text =
                StringUtils.getDurationDisplayFromSeconds(endSubtitleTime - startSubtitleTime)
            tv_start_time.text = StringUtils.getDurationDisplayFromSeconds(startSubtitleTime)
            tv_end_time.text = StringUtils.getDurationDisplayFromSeconds(endSubtitleTime)
            currentTextFont = it.addTextProperties.fontName
            fontPickerAdapter.setCurrentTextFont(currentTextFont)
        }
    }

    private fun initTextDuration() {
        duration =
            (requireActivity() as VideoCreateActivity).myApplication.videoDataState.totalSecond
        seek_bar_subtitle.setIsHideActiveLinePaint(true)
        seek_bar_subtitle.count = duration
        seek_bar_subtitle.maxProgress = duration
        endSubtitleTime = duration
        tv_total_time.text = StringUtils.getDurationDisplayFromSeconds(endSubtitleTime)
        tv_start_time.text = StringUtils.getDurationDisplayFromSeconds(startSubtitleTime)
        tv_end_time.text = StringUtils.getDurationDisplayFromSeconds(endSubtitleTime)
    }

    private fun initTextSize() {
        seek_bar_text_size.progress = textSizePreView
        seek_bar_text_size.max = 100
    }

    private fun initPreviewText() {
        val addTextProperties =
            this.videoTextSticker?.addTextProperties ?: AddTextProperties.getDefaultProperties()

        Utils.setTypeface(requireContext(), tv_preview, defaultFont)
        tv_preview.setTextColor(Color.parseColor("#000000"))

        tv_preview.text = addTextProperties.text
        tv_preview.typeface = addTextProperties.getTypeface(requireContext())
        tv_preview.setTextColor(addTextProperties.textColor)

        if (addTextProperties.paddingHeight > 0) {
            tv_preview.setPadding(
                tv_preview.paddingLeft,
                addTextProperties.paddingHeight,
                tv_preview.paddingRight,
                addTextProperties.paddingHeight
            )
        }
        if (addTextProperties.paddingWidth > 0) {
            tv_preview.setPadding(
                addTextProperties.paddingWidth,
                tv_preview.paddingTop,
                addTextProperties.paddingWidth,
                tv_preview.paddingBottom
            )
        }
        if (addTextProperties.textShader != null) {
            tv_preview.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            tv_preview.paint.shader = addTextProperties.textShader
        }

        tv_preview.setPadding(
            SystemUtil.dpToPx(requireContext(), addTextProperties.paddingWidth),
            tv_preview.paddingTop,
            SystemUtil.dpToPx(requireContext(), addTextProperties.paddingWidth),
            tv_preview.paddingBottom
        )
        //tv_preview.setTextColor(addTextProperties.textColor)
        tv_preview.textAlignment = addTextProperties.textAlign
        tv_preview.textSize = addTextProperties.textSize.toFloat()
        textSizePreView = addTextProperties.textSize
        tv_preview.invalidate()
    }

    private fun initTextFonts() {
        tv_size.text = textSizePreView.toString()
        recycler_view_font.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        fontPickerAdapter = FontPickerAdapter(FontProvider.fonts(), requireContext()) { font ->
            setTextFont(font)
        }
        recycler_view_font.adapter = fontPickerAdapter
    }

    private fun initTextColors() {
        recycler_view_color.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        colorPickerAdapter = ColorPickerAdapter(onColorSelected = { color ->
            setTextColor(color)
        })
        recycler_view_color.adapter = colorPickerAdapter
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
        btn_decrease.setOnClickListener(this)
        btn_increase.setOnClickListener(this)
        keyboardHeightTest = KeyboardHeightProvider(requireActivity())
        keyboardHeightTest!!.addKeyboardListener(keyboardListener)
        edt_text.addTextChangedListener(textPreviewListener)
        btn_submit.setOnClickListener(this)
        btn_close.setOnClickListener(this)
        btn_color.setOnClickListener(this)
        btn_font.setOnClickListener(this)
        btn_subtitle.setOnClickListener(this)
        btn_increase_start.setOnClickListener(this)
        btn_text_decrease_start.setOnClickListener(this)
        btn_increase_end.setOnClickListener(this)
        btn_text_decrease_end.setOnClickListener(this)
        btn_qwerty.setOnClickListener(this)
        timeSubtitleListener()
        onSeekerListener()
        edt_text.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) showQueryTab()
        }
    }

    private fun onSeekerListener() {
        seek_bar_text_size.setOnProgressChangeListener(object :
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
        seek_bar_subtitle.setOnValueChangedListener(object :
            AudioCutterView.OnValueChangedListener() {
            override fun onValueChanged(progress: Int, fromUser: Boolean) {
            }

            override fun onStartChanged(minProgress: Int, fromUser: Boolean) {
                super.onStartChanged(minProgress, fromUser)
                if (fromUser) {
                    startSubtitleTime = minProgress
                    updateIsFullTime()
                    tv_total_time.text =
                        StringUtils.getDurationDisplayFromSeconds(endSubtitleTime - startSubtitleTime)
                    tv_start_time.text = StringUtils.getDurationDisplayFromSeconds(minProgress)
                    isFullTime =
                        startSubtitleTime == 0 && endSubtitleTime == MyApplication.getInstance().videoDataState.totalSecond
                }
            }

            override fun onEndChanged(maxProgress: Int, fromUser: Boolean) {
                super.onEndChanged(maxProgress, fromUser)
                if (fromUser) {
                    endSubtitleTime = maxProgress
                    updateIsFullTime()
                    tv_total_time.text =
                        StringUtils.getDurationDisplayFromSeconds(endSubtitleTime - startSubtitleTime)
                    tv_end_time.text = StringUtils.getDurationDisplayFromSeconds(maxProgress)
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
        edt_text.removeTextChangedListener(textPreviewListener)
    }

    override fun onClick(view: View) {
        when (view) {
            btn_submit -> {
                closeKeyboard(view)
                if (isEditMode)
                    updateText()
                else
                    addText()
                dismiss()
            }
            btn_qwerty -> {
                toggleQueryTab()
            }
            btn_subtitle -> {
                showTextSubTitleTimeTab()
            }
            btn_color -> {
                showTextColorTab(view)
                Handler(Looper.myLooper()!!).postDelayed({
                    recycler_view_color?.smoothScrollToPosition(colorPickerAdapter.getSelectedPos())
                }, 300)
            }
            btn_font -> {
                showTextFontTab(view)
                Handler(Looper.myLooper()!!).postDelayed({
                    recycler_view_font?.smoothScrollToPosition(fontPickerAdapter.getSelectedPos())
                }, 300)
            }
            btn_close -> {
                closeKeyboard(view)
                videoTextSticker?.isShow = true
                dismiss()
            }
            btn_increase_start -> {
                if (endSubtitleTime - startSubtitleTime > 1)
                    seek_bar_subtitle.minProgress = ++startSubtitleTime
                tv_total_time.text =
                    StringUtils.getDurationDisplayFromSeconds(endSubtitleTime - startSubtitleTime)
                tv_start_time.text = StringUtils.getDurationDisplayFromSeconds(startSubtitleTime)
                updateIsFullTime()
            }
            btn_text_decrease_start -> {
                if (startSubtitleTime > 0)
                    seek_bar_subtitle.minProgress = --startSubtitleTime
                tv_total_time.text =
                    StringUtils.getDurationDisplayFromSeconds(endSubtitleTime - startSubtitleTime)
                tv_start_time.text = StringUtils.getDurationDisplayFromSeconds(startSubtitleTime)
                updateIsFullTime()
            }
            btn_increase_end -> {
                if (endSubtitleTime < duration)
                    seek_bar_subtitle.maxProgress = ++endSubtitleTime
                tv_total_time.text =
                    StringUtils.getDurationDisplayFromSeconds(endSubtitleTime - startSubtitleTime)
                tv_end_time.text = StringUtils.getDurationDisplayFromSeconds(endSubtitleTime)
                updateIsFullTime()
            }
            btn_text_decrease_end -> {
                if (endSubtitleTime - startSubtitleTime > 1)
                    seek_bar_subtitle.maxProgress = --endSubtitleTime
                tv_total_time.text =
                    StringUtils.getDurationDisplayFromSeconds(endSubtitleTime - startSubtitleTime)
                tv_end_time.text = StringUtils.getDurationDisplayFromSeconds(endSubtitleTime)
                updateIsFullTime()
            }
            btn_decrease -> {
                updateViewsControl((--textSizePreView).toString())
                updatePreviewTextSize(tv_size.text.toString().toInt())
            }
            btn_increase -> {
                updateViewsControl((++textSizePreView).toString())
                updatePreviewTextSize(tv_size.text.toString().toInt())
            }
        }
    }

    private fun updateViewsControl(previewSize: String) {
        tv_size.text = previewSize
        seek_bar_text_size.progress = previewSize.toInt()
    }

    private fun addText() {
        val previewText = tv_preview.text.toString()
        if (TextUtils.isEmpty(previewText)) return
        val addTextProperties = AddTextProperties.getDefaultProperties()
        addTextProperties.textSize = textSizePreView
        addTextProperties.text = previewText
        addTextProperties.textWidth = tv_preview.width
        addTextProperties.textHeight = tv_preview.height
        addTextProperties.textColor = tv_preview.currentTextColor
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
                tv_preview.currentTextColor
            it.addTextProperties.textSize = textSizePreView
            it.isShow = true
            it.setHandling(true)
            it.updateConfig(
                tv_preview.text.toString(),
                tv_preview.width,
                tv_preview.height
            )
            (requireActivity() as VideoCreateActivity).updateSub(it)
        }
    }

    private fun toggleQueryTab() {
        activeTab(R.id.btn_qwerty)
        toggleKeyboard()
        edt_text.requestFocus()
        subtitle_layout.visibility = View.GONE
        recycler_view_color.visibility = View.GONE
        font_layout.visibility = View.GONE
    }

    private fun showQueryTab() {
        Log.d(TAG, "showQueryTab: ")
        activeTab(R.id.btn_qwerty)
        showKeyboard()
        edt_text.requestFocus()
        subtitle_layout.visibility = View.GONE
        recycler_view_color.visibility = View.GONE
        font_layout.visibility = View.GONE
    }

    private fun showTextSubTitleTimeTab() {
        activeTab(R.id.btn_subtitle)
        closeKeyboard(btn_subtitle)
        subtitle_layout.visibility = View.VISIBLE
        recycler_view_color.visibility = View.GONE
        font_layout.visibility = View.GONE
    }

    private fun showTextFontTab(view: View) {
        edt_text.clearFocus()
        activeTab(R.id.btn_font)
        closeKeyboard(view)
        subtitle_layout.visibility = View.GONE
        recycler_view_color.visibility = View.GONE
        font_layout.visibility = View.VISIBLE
    }

    private fun showTextColorTab(view: View) {
        edt_text.clearFocus()
        activeTab(R.id.btn_color)
        closeKeyboard(view)
        subtitle_layout.visibility = View.GONE
        recycler_view_color.visibility = View.VISIBLE
        font_layout.visibility = View.GONE
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
            tv_preview.text = charSequence
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
        inputMethodManager.showSoftInput(edt_text, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun closeKeyboard(v: View) {
        val inputMethodManager: InputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
    }

    private fun activeTab(id: Int) {
        btn_qwerty.isSelected = R.id.btn_qwerty == id
        btn_subtitle.isSelected = R.id.btn_subtitle == id
        btn_color.isSelected = R.id.btn_color == id
        btn_font.isSelected = R.id.btn_font == id
    }

    fun setTextFont(font: String) {
        Utils.setTypeface(requireContext(), tv_preview, font)
        currentTextFont = font
    }

    fun setTextColor(color: Int) {
        tv_preview.setTextColor(color)
    }

    fun updatePreviewTextSize(textSize: Int) {
        tv_preview.textSize = textSize.toFloat()
        textSizePreView = textSize
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        videoTextSticker?.isShow = true
    }

}