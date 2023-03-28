package com.example.slide.ui.edit_image.fragment

import android.content.Context
import android.graphics.BitmapShader
import android.graphics.Color
import android.graphics.Shader
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SeekBar
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.slide.R
import com.example.slide.base.BaseFragment
import com.example.slide.base.InitViewTools
import com.example.slide.ui.edit_image.EditImageActivity
import com.example.slide.ui.edit_image.adapter.ColorTextAdapter
import com.example.slide.ui.edit_image.adapter.FontPickerAdapter
import com.example.slide.ui.edit_image.adapter.ShadowAdapter
import com.example.slide.ui.edit_image.adapter.TextureAdapter
import com.example.slide.ui.edit_image.framework.AddTextProperties
import com.example.slide.ui.edit_image.framework.TextFloatingItem
import com.example.slide.ui.edit_image.utils.SystemUtil
import com.example.slide.util.ColorProvider
import com.example.slide.util.FontProvider
import com.example.slide.util.Utils
import com.example.slide.lib.keyboardheightprovider.KeyboardHeightProvider
import kotlinx.android.synthetic.main.fragment_text.*
import kotlinx.android.synthetic.main.fragment_text.btn_close
import kotlinx.android.synthetic.main.fragment_text.btn_color
import kotlinx.android.synthetic.main.fragment_text.btn_font
import kotlinx.android.synthetic.main.fragment_text.btn_qwerty
import kotlinx.android.synthetic.main.fragment_text.btn_submit
import kotlinx.android.synthetic.main.fragment_text.edt_text
import kotlinx.android.synthetic.main.fragment_text.font_layout
import kotlinx.android.synthetic.main.fragment_text.tv_preview
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class TextFragment : BaseFragment(), View.OnClickListener {

    private lateinit var fontPickerAdapter: FontPickerAdapter

    private lateinit var colorTextAdapter: ColorTextAdapter

    private lateinit var colorTextBackgroundAdapter: ColorTextAdapter

    private lateinit var textureAdapter: TextureAdapter

    private lateinit var textShadowAdapter: ShadowAdapter

    private var heightKeyboard = 0

    private var topHeightKeyboard = 0

    private var keyboardHeightTest: KeyboardHeightProvider? = null

    private var textSizePreView = 30

    private var textSticker: TextFloatingItem? = null

    private var currentMode = ADD_MODE

    private val ioScope = CoroutineScope(Dispatchers.IO)

    private val uiScope = CoroutineScope(Dispatchers.Main)

    private var job: Job? = null

    private val activity by lazy {
        (requireActivity() as EditImageActivity)
    }

    private var shader: Shader? = null

    private var addTextProperties: AddTextProperties? = null

    private val handler by lazy {
        Handler(Looper.getMainLooper())
    }

    private var isShowKeyboard: Boolean = false

    companion object {

        const val TAG = "tags"

        private const val ADD_MODE = 0

        private const val EDIT_MODE = 1

        fun getInstance(textSticker: TextFloatingItem): TextFragment {
            return TextFragment().apply {
                this.textSticker = textSticker
                addTextProperties = textSticker.addTextProperties
            }
        }
    }

    override fun initViewTools() = InitViewTools({
        R.layout.fragment_text
    })

    override fun initConfiguration() {
        super.initConfiguration()
        if (addTextProperties == null) {
            currentMode = ADD_MODE
            addTextProperties = AddTextProperties.getDefaultProperties()
        } else {
            currentMode = EDIT_MODE
        }
        initPreviewText()
        openQueryTab()
        initTextColors()
        initTextFonts()
        initTextBackground()
        initTextShadow()
    }

    private fun initTextShadow() {
        addTextProperties?.let {
            textShadowAdapter = ShadowAdapter(requireContext(), it.textShadow)
            rvShadow.adapter = textShadowAdapter
            textShadowAdapter.setShadowItemClickListener { textShadow, textShadowIndex ->
                Log.d("hehe", "initTextShadow: $textShadowIndex")
                tv_preview.setShadowLayer(
                    textShadow.radius.toFloat(),
                    textShadow.dx.toFloat(),
                    textShadow.dy.toFloat(),
                    textShadow.colorShadow
                )
                tv_preview.invalidate()
                it.textShadow = textShadow
                it.setTextShadowIndex(textShadowIndex)
            }
        }
    }

    private fun initTextBackground() {
        colorTextBackgroundAdapter = ColorTextAdapter { color, position ->
            addTextProperties?.let {
                it.isShowBackground = true
                if (!switchBackground.isChecked) {
                    switchBackground.isChecked = true
                }
                updateBackgroundPreview(color)
                it.backgroundColor = color
                it.backgroundColorIndex = position
            }
        }
        rvTextBackground.adapter = colorTextBackgroundAdapter
    }

    private fun updateBackgroundPreview(color: Int = 0) {
        addTextProperties?.let {
            if (color != 0) {
                val red = Color.red(color)
                val green = Color.green(color)
                val blue = Color.blue(color)
                val shade = GradientDrawable()
                shade.setColor(Color.argb(it.backgroundAlpha, red, green, blue))
                shade.cornerRadius =
                    SystemUtil.dpToPx(requireContext(), it.backgroundBorder).toFloat()
                tv_preview.background = shade
            } else {
                tv_preview.background = null
                updatePaddingHeight(2)
                updatePaddingWidth(8)
            }
            updateFullScreenBackgroundPreview(switchFullScreenBackground.isChecked)
        }
    }

    private fun initTextColors() {

        colorTextAdapter = ColorTextAdapter { color, position ->
            tv_preview.setTextColor(color)
            tv_preview.paint.shader = null
            addTextProperties?.textShader = null
            addTextProperties?.textColor = color
            addTextProperties?.textColorIndex = position
            setTextColor()
            textureAdapter.updateSelectedColor(-1)
        }

        textureAdapter = TextureAdapter { color, position ->
            shader = BitmapShader(color, Shader.TileMode.MIRROR, Shader.TileMode.MIRROR)
            tv_preview.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            tv_preview.paint.shader = shader
            addTextProperties?.textShader = shader
            addTextProperties?.textShaderIndex = position
            setTextColor()
            colorTextAdapter.disableSelected()
        }

        rv_colors.adapter = colorTextAdapter

        rv_texture.adapter = textureAdapter

    }

    private fun initTextFonts() {
        rv_fonts.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        fontPickerAdapter = FontPickerAdapter(FontProvider.fonts(), requireContext()) { font ->
            addTextProperties?.fontName = font
            Utils.setTypeface(requireContext(), tv_preview, font)
        }
        rv_fonts.adapter = fontPickerAdapter
    }

    override fun initListener() {

        btn_close.setOnClickListener(this)
        btn_back.setOnClickListener(this)
        btn_qwerty.setOnClickListener(this)
        btn_color.setOnClickListener(this)
        btn_font.setOnClickListener(this)
        btn_submit.setOnClickListener(this)
        btn_add_new_text.setOnClickListener(this)
        btn_size_down.setOnClickListener(this)
        btn_size_up.setOnClickListener(this)

        keyboardHeightTest = KeyboardHeightProvider(requireActivity())
        keyboardHeightTest!!.addKeyboardListener(keyboardListener)

        edt_text.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val text = charSequence.toString()
                tv_preview.text = text
                addTextProperties?.text = text
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        edt_text.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) openQueryTab()
        }

        textTransparentListener()

        backgroundTextTransparentListener()

        paddingWidthListener()

        paddingHeightListener()

        switchBackground.setOnCheckedChangeListener { _, isChecked ->
            addTextProperties?.let {
                it.isShowBackground = isChecked
                if (!isChecked) {
                    updateBackgroundPreview()
                } else {
                    updateBackgroundPreview(it.backgroundColor)
                }
            }
        }

        switchFullScreenBackground.setOnCheckedChangeListener { _, isChecked ->
            updateFullScreenBackgroundPreview(isChecked)
        }

    }

    private fun paddingWidthListener() {
        sbPaddingWidth.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    updatePaddingWidth(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
    }


    private fun paddingHeightListener() {
        sbPaddingHeight.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    updatePaddingHeight(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
    }

    private fun updatePaddingWidth(progress: Int) {
        addTextProperties?.let {
            if (it.isShowBackground) {
                tv_preview.setPadding(
                    SystemUtil.dpToPx(requireContext(), progress),
                    tv_preview.paddingTop,
                    SystemUtil.dpToPx(requireContext(), progress),
                    tv_preview.paddingBottom
                )
                it.paddingWidth = progress
            }
        }
    }

    private fun updatePaddingHeight(progress: Int) {
        addTextProperties?.let {
            if (it.isShowBackground) {
                tv_preview.setPadding(
                    tv_preview.paddingLeft,
                    SystemUtil.dpToPx(requireContext(), progress),
                    tv_preview.paddingRight,
                    SystemUtil.dpToPx(requireContext(), progress)
                )
                it.paddingHeight = progress
            }
        }
    }

    private fun updateFullScreenBackgroundPreview(isFullScreen: Boolean) {
        addTextProperties?.let {
            if (it.isShowBackground) {
                it.isFullScreen = isFullScreen
                if (isFullScreen) {
                    tv_preview.layoutParams = LinearLayoutCompat.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                } else {
                    tv_preview.layoutParams = LinearLayoutCompat.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                }
            }
        }
    }

    private fun backgroundTextTransparentListener() {
        sbBackgroundTextTransparent.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    updateTransparentBackgroundText(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
    }

    private fun updateTransparentBackgroundText(progress: Int) {
        addTextProperties?.let {
            it.backgroundAlpha = 255 - progress
            if (it.isShowBackground) {
                val red = Color.red(it.backgroundColor)
                val green = Color.green(it.backgroundColor)
                val blue = Color.blue(it.backgroundColor)
                val shade = GradientDrawable()
                shade.setColor(Color.argb(it.backgroundAlpha, red, green, blue))
                shade.cornerRadius = SystemUtil.dpToPx(
                    requireContext(),
                    it.backgroundBorder
                )
                    .toFloat()
                tv_preview.background = shade
            }

        }
    }

    private fun textTransparentListener() {
        sbTextTransparent.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    addTextProperties?.let {
                        it.textAlpha = 255 - progress
                        setTextColor()
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
    }

    override fun initTask() {
        super.initTask()
        if (currentMode == EDIT_MODE) {
            addTextProperties?.let {
                textSizePreView = it.textSize
                tv_preview.textSize = textSizePreView.toFloat()
                tv_size_content.text = textSizePreView.toString()
                it.fontName?.let { fontName ->
                    fontPickerAdapter.setCurrentTextFont(fontName)
                }
                edt_text.setText(it.text)
                edt_text.text?.let { text ->
                    edt_text.setSelection(text.length)
                }
                tv_preview.typeface = it.getTypeface(requireContext())

                if (it.isColor) {
                    tv_preview.setTextColor(it.textColor)
                } else {
                    tv_preview.paint.shader = it.textShader
                }
                setTextColor()
                sbTextTransparent.progress = 255 - it.textAlpha
                sbBackgroundTextTransparent.progress = 255 - it.backgroundAlpha
                if (it.isShowBackground) {
                    switchFullScreenBackground.isSelected = it.isFullScreen
                    switchFullScreenBackground.isChecked = it.isFullScreen
                    switchBackground.isSelected = it.isShowBackground
                    switchBackground.isSelected = it.isShowBackground
                    updateBackgroundPreview(it.backgroundColor)
                    if (it.paddingWidth > 0) {
                        updatePaddingWidth(it.paddingWidth)
                    }
                    if (it.paddingHeight > 0) {
                        updatePaddingHeight(it.paddingHeight)
                    }
                }
                updateFullScreenBackgroundPreview(it.isFullScreen)
                sbPaddingWidth.progress = it.paddingWidth
                sbPaddingHeight.progress = it.paddingHeight
            }
        } else {
            addTextProperties?.textSize = textSizePreView
        }

        job = ioScope.launch {
            val bitmapTextures = ColorProvider.getTextureBitmaps(requireContext())
            uiScope.launch {
                textureAdapter.updateData(bitmapTextures)
                layoutLoading.visibility = View.GONE
            }
        }

    }

    private fun setTextColor() {
        addTextProperties?.let {
            val red = Color.red(it.textColor)
            val green = Color.green(it.textColor)
            val blue = Color.blue(it.textColor)
            tv_preview.setTextColor(Color.argb(it.textAlpha, red, green, blue))
        }
    }

    override fun releaseData() {
        textShadowAdapter.setShadowItemClickListener(null)
        handler.removeCallbacksAndMessages(null)
        job?.cancel()
        keyboardHeightTest?.removeKeyboardListener(keyboardListener)
    }

    override fun onResume() {
        super.onResume()
        keyboardHeightTest?.onResume()
    }

    override fun onPause() {
        super.onPause()
        keyboardHeightTest?.onPause()
    }

    private fun openColorTab() {
        edt_text.clearFocus()
        activeTab(btn_color)
        layoutColor.visibility = View.VISIBLE
        font_layout.visibility = View.GONE
        closeKeyboard(btn_color)

        addTextProperties?.let {
            handler.postDelayed({
                switchBackground.isSelected = it.isShowBackground
                switchBackground.isChecked = it.isShowBackground
                switchFullScreenBackground.isSelected = it.isFullScreen
                switchFullScreenBackground.isChecked = it.isFullScreen
            }, 100)
            if (it.isColor) {
                colorTextAdapter.updateSelectedColor(it.textColorIndex)
                textureAdapter.updateSelectedColor(-1)
                handler.postDelayed({
                    rv_colors.smoothScrollToPosition(it.textColorIndex)
                }, 100)
            } else {
                colorTextAdapter.disableSelected()
                textureAdapter.updateSelectedColor(it.textShaderIndex)
                handler.postDelayed({
                    rv_texture.smoothScrollToPosition(it.textShaderIndex)
                }, 100)
            }
            if (it.isShowBackground) {
                colorTextBackgroundAdapter.updateSelectedColor(it.backgroundColorIndex)
                handler.postDelayed({
                    rvTextBackground.smoothScrollToPosition(it.backgroundColorIndex)
                }, 100)
            }
        }
    }

    private fun addText() {
        activeTab(btn_submit)
        closeKeyboard(btn_submit)
        text_properties_layout.visibility = View.INVISIBLE
        btn_add_new_text.visibility = View.VISIBLE
        rv_fonts.visibility = View.INVISIBLE
        layoutColor.visibility = View.INVISIBLE

        val previewText = tv_preview.text.toString()
        if (TextUtils.isEmpty(previewText)) return

        addTextProperties?.let {
            it.textWidth = tv_preview.width
            it.textHeight = tv_preview.height
            if (currentMode == ADD_MODE)
                activity.addText(
                    TextFloatingItem(
                        requireContext(),
                        it
                    )
                )
            else {
                textSticker?.let { textSticker ->
                    textSticker.updateConfig(it)
                    activity.updateText(textSticker)
                }
            }
        }
        addTextProperties = null
        edt_text.setText("")
        edt_text.visibility = View.INVISIBLE
        activity.onBackPressed()
    }

    private val keyboardListener = object : KeyboardHeightProvider.KeyboardListener {
        override fun onHeightChanged(height: Int) {
            if (height < 0) { // height of notch
                topHeightKeyboard = -height
            } else if ((height + topHeightKeyboard) > 250 && (height + topHeightKeyboard) != heightKeyboard) {
                heightKeyboard = height + topHeightKeyboard
                handler.postDelayed({
                    if (!isShowKeyboard) {
                        isShowKeyboard = true
                        val layoutParams = layoutGroup.layoutParams
                        layoutParams.height = heightKeyboard
                        layoutGroup.layoutParams = layoutParams
                    }
                }, 100)
            }
        }
    }

    private fun initPreviewText() {
        addTextProperties?.let {
            Utils.setTypeface(requireContext(), tv_preview)
            tv_preview.setTextColor(Color.parseColor("#000000"))
            tv_preview.text = it.text
            tv_preview.typeface = it.getTypeface(requireContext())
            tv_preview.setTextColor(it.textColor)

            if (it.paddingHeight > 0) {
                tv_preview.setPadding(
                    tv_preview.paddingLeft,
                    it.paddingHeight,
                    tv_preview.paddingRight,
                    it.paddingHeight
                )
            }
            if (it.paddingWidth > 0) {
                tv_preview.setPadding(
                    it.paddingWidth,
                    tv_preview.paddingTop,
                    it.paddingWidth,
                    tv_preview.paddingBottom
                )
            }
            if (it.textShader != null) {
                tv_preview.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                tv_preview.paint.shader = it.textShader
            }

            tv_preview.setPadding(
                SystemUtil.dpToPx(requireContext(), it.paddingWidth),
                tv_preview.paddingTop,
                SystemUtil.dpToPx(requireContext(), it.paddingWidth),
                tv_preview.paddingBottom
            )
            tv_preview.textAlignment = it.textAlign
            tv_preview.textSize = it.textSize.toFloat()
            textSizePreView = it.textSize
            tv_preview.invalidate()
        }
    }

    private fun openFontTab() {
        edt_text.clearFocus()
        activeTab(btn_font)
        layoutColor.visibility = View.GONE
        font_layout.visibility = View.VISIBLE
        handler.postDelayed({
            rv_fonts?.smoothScrollToPosition(fontPickerAdapter.getSelectedPos())
        }, 300)
        closeKeyboard(btn_color)
        addTextProperties?.let {
            handler.postDelayed({
                rvShadow?.smoothScrollToPosition(it.textShadowIndex)
            }, 300)
        }
        closeKeyboard(btn_color)
    }

    private fun closeTextFragment() {
        activeTab(btn_close)
        closeKeyboard(btn_close)
        (requireActivity() as EditImageActivity).onBackPressed()
    }

    private fun backPress() {
        activeTab(btn_back)
        closeKeyboard(btn_back)
        (requireActivity() as EditImageActivity).onBackPressed()
    }

    private fun addNewText() {
        text_properties_layout.visibility = View.VISIBLE
        btn_add_new_text.visibility = View.INVISIBLE
        activeTab(btn_add_new_text)
        openQueryTab()
    }

    private fun openQueryTab() {
        openKeyboard()
        edt_text.requestFocus()
        activeTab(btn_qwerty)
        edt_text.visibility = View.VISIBLE
        layoutColor.visibility = View.GONE
        font_layout.visibility = View.GONE
    }

    private fun activeTab(view: View) {
        fl_qwerty.isSelected = btn_qwerty == view
        fl_color.isSelected = btn_color == view
        fl_font.isSelected = btn_font == view
        fl_submit.isSelected = btn_submit == view
        fl_close.isSelected = btn_close == view
    }

    private fun openKeyboard() {
        val inputMethodManager: InputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    private fun closeKeyboard(v: View) {
        val inputMethodManager: InputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
    }

    override fun onClick(view: View) {
        when (view) {
            btn_submit -> {
                addText()
            }
            btn_back -> {
                backPress()
            }
            btn_close -> {
                closeTextFragment()
            }
            btn_qwerty -> {
                openQueryTab()
            }
            btn_color -> {
                openColorTab()
            }
            btn_font -> {
                openFontTab()
            }
            btn_add_new_text -> {
                addNewText()
            }
            btn_size_up -> {
                tv_preview.textSize = (++textSizePreView).toFloat()
                tv_size_content.text = textSizePreView.toString()
            }
            btn_size_down -> {
                tv_preview.textSize = (--textSizePreView).toFloat()
                tv_size_content.text = textSizePreView.toString()
            }
        }
    }

}
