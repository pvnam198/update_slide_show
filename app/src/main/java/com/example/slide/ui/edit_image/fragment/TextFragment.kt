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
import com.example.slide.databinding.FragmentTextBinding
import com.example.slide.lib.keyboardheightprovider.KeyboardHeightProvider
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class TextFragment : BaseFragment<FragmentTextBinding>(), View.OnClickListener {

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

    override fun bindingView(): FragmentTextBinding {
        return FragmentTextBinding.inflate(layoutInflater)
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
            binding.rvShadow.adapter = textShadowAdapter
            textShadowAdapter.setShadowItemClickListener { textShadow, textShadowIndex ->
                Log.d("hehe", "initTextShadow: $textShadowIndex")
                binding.tvPreview.setShadowLayer(
                    textShadow.radius.toFloat(),
                    textShadow.dx.toFloat(),
                    textShadow.dy.toFloat(),
                    textShadow.colorShadow
                )
                binding.tvPreview.invalidate()
                it.textShadow = textShadow
                it.setTextShadowIndex(textShadowIndex)
            }
        }
    }

    private fun initTextBackground() {
        colorTextBackgroundAdapter = ColorTextAdapter { color, position ->
            addTextProperties?.let {
                it.isShowBackground = true
                if (!binding.switchBackground.isChecked) {
                    binding.switchBackground.isChecked = true
                }
                updateBackgroundPreview(color)
                it.backgroundColor = color
                it.backgroundColorIndex = position
            }
        }
        binding.rvTextBackground.adapter = colorTextBackgroundAdapter
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
                binding.tvPreview.background = shade
            } else {
                binding.tvPreview.background = null
                updatePaddingHeight(2)
                updatePaddingWidth(8)
            }
            updateFullScreenBackgroundPreview(binding.switchFullScreenBackground.isChecked)
        }
    }

    private fun initTextColors() {

        colorTextAdapter = ColorTextAdapter { color, position ->
            binding.tvPreview.setTextColor(color)
            binding.tvPreview.paint.shader = null
            addTextProperties?.textShader = null
            addTextProperties?.textColor = color
            addTextProperties?.textColorIndex = position
            setTextColor()
            textureAdapter.updateSelectedColor(-1)
        }

        textureAdapter = TextureAdapter { color, position ->
            shader = BitmapShader(color, Shader.TileMode.MIRROR, Shader.TileMode.MIRROR)
            binding.tvPreview.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            binding.tvPreview.paint.shader = shader
            addTextProperties?.textShader = shader
            addTextProperties?.textShaderIndex = position
            setTextColor()
            colorTextAdapter.disableSelected()
        }

        binding.rvColors.adapter = colorTextAdapter

        binding.rvTexture.adapter = textureAdapter

    }

    private fun initTextFonts() {
        binding.rvFonts.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        fontPickerAdapter = FontPickerAdapter(FontProvider.fonts(), requireContext()) { font ->
            addTextProperties?.fontName = font
            Utils.setTypeface(requireContext(), binding.tvPreview, font)
        }
        binding.rvFonts.adapter = fontPickerAdapter
    }

    override fun initListener() {

        binding.btnClose.setOnClickListener(this)
        binding.btnBack.setOnClickListener(this)
        binding.btnQwerty.setOnClickListener(this)
        binding.btnColor.setOnClickListener(this)
        binding.btnFont.setOnClickListener(this)
        binding.btnSubmit.setOnClickListener(this)
        binding.btnAddNewText.setOnClickListener(this)
        binding.btnSizeDown.setOnClickListener(this)
        binding.btnSizeUp.setOnClickListener(this)

        keyboardHeightTest = KeyboardHeightProvider(requireActivity())
        keyboardHeightTest!!.addKeyboardListener(keyboardListener)

        binding.edtText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val text = charSequence.toString()
                binding.tvPreview.text = text
                addTextProperties?.text = text
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        binding.edtText.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) openQueryTab()
        }

        textTransparentListener()

        backgroundTextTransparentListener()

        paddingWidthListener()

        paddingHeightListener()

        binding.switchBackground.setOnCheckedChangeListener { _, isChecked ->
            addTextProperties?.let {
                it.isShowBackground = isChecked
                if (!isChecked) {
                    updateBackgroundPreview()
                } else {
                    updateBackgroundPreview(it.backgroundColor)
                }
            }
        }

        binding.switchFullScreenBackground.setOnCheckedChangeListener { _, isChecked ->
            updateFullScreenBackgroundPreview(isChecked)
        }

    }

    private fun paddingWidthListener() {
        binding.sbPaddingWidth.setOnSeekBarChangeListener(object :
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
        binding.sbPaddingHeight.setOnSeekBarChangeListener(object :
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
                binding.tvPreview.setPadding(
                    SystemUtil.dpToPx(requireContext(), progress),
                    binding.tvPreview.paddingTop,
                    SystemUtil.dpToPx(requireContext(), progress),
                    binding.tvPreview.paddingBottom
                )
                it.paddingWidth = progress
            }
        }
    }

    private fun updatePaddingHeight(progress: Int) {
        addTextProperties?.let {
            if (it.isShowBackground) {
                binding.tvPreview.setPadding(
                    binding.tvPreview.paddingLeft,
                    SystemUtil.dpToPx(requireContext(), progress),
                    binding.tvPreview.paddingRight,
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
                    binding.tvPreview.layoutParams = LinearLayoutCompat.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                } else {
                    binding.tvPreview.layoutParams = LinearLayoutCompat.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                }
            }
        }
    }

    private fun backgroundTextTransparentListener() {
        binding.sbBackgroundTextTransparent.setOnSeekBarChangeListener(object :
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
                binding.tvPreview.background = shade
            }

        }
    }

    private fun textTransparentListener() {
        binding.sbTextTransparent.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
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
                binding.tvPreview.textSize = textSizePreView.toFloat()
                binding.tvSizeContent.text = textSizePreView.toString()
                it.fontName?.let { fontName ->
                    fontPickerAdapter.setCurrentTextFont(fontName)
                }
                binding.edtText.setText(it.text)
                binding.edtText.text?.let { text ->
                    binding.edtText.setSelection(text.length)
                }
                binding.tvPreview.typeface = it.getTypeface(requireContext())

                if (it.isColor) {
                    binding.tvPreview.setTextColor(it.textColor)
                } else {
                    binding.tvPreview.paint.shader = it.textShader
                }
                setTextColor()
                binding.sbTextTransparent.progress = 255 - it.textAlpha
                binding.sbBackgroundTextTransparent.progress = 255 - it.backgroundAlpha
                if (it.isShowBackground) {
                    binding.switchFullScreenBackground.isSelected = it.isFullScreen
                    binding.switchFullScreenBackground.isChecked = it.isFullScreen
                    binding.switchBackground.isSelected = it.isShowBackground
                    binding.switchBackground.isSelected = it.isShowBackground
                    updateBackgroundPreview(it.backgroundColor)
                    if (it.paddingWidth > 0) {
                        updatePaddingWidth(it.paddingWidth)
                    }
                    if (it.paddingHeight > 0) {
                        updatePaddingHeight(it.paddingHeight)
                    }
                }
                updateFullScreenBackgroundPreview(it.isFullScreen)
                binding.sbPaddingWidth.progress = it.paddingWidth
                binding.sbPaddingHeight.progress = it.paddingHeight
            }
        } else {
            addTextProperties?.textSize = textSizePreView
        }

        job = ioScope.launch {
            val bitmapTextures = ColorProvider.getTextureBitmaps(requireContext())
            uiScope.launch {
                textureAdapter.updateData(bitmapTextures)
                binding.layoutLoading.visibility = View.GONE
            }
        }

    }

    private fun setTextColor() {
        addTextProperties?.let {
            val red = Color.red(it.textColor)
            val green = Color.green(it.textColor)
            val blue = Color.blue(it.textColor)
            binding.tvPreview.setTextColor(Color.argb(it.textAlpha, red, green, blue))
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
        binding.edtText.clearFocus()
        activeTab(binding.btnColor)
        binding.layoutColor.visibility = View.VISIBLE
        binding.fontLayout.visibility = View.GONE
        closeKeyboard(binding.btnColor)

        addTextProperties?.let {
            handler.postDelayed({
                binding.switchBackground.isSelected = it.isShowBackground
                binding.switchBackground.isChecked = it.isShowBackground
                binding.switchFullScreenBackground.isSelected = it.isFullScreen
                binding.switchFullScreenBackground.isChecked = it.isFullScreen
            }, 100)
            if (it.isColor) {
                colorTextAdapter.updateSelectedColor(it.textColorIndex)
                textureAdapter.updateSelectedColor(-1)
                handler.postDelayed({
                    binding.rvColors.smoothScrollToPosition(it.textColorIndex)
                }, 100)
            } else {
                colorTextAdapter.disableSelected()
                textureAdapter.updateSelectedColor(it.textShaderIndex)
                handler.postDelayed({
                    binding.rvTexture.smoothScrollToPosition(it.textShaderIndex)
                }, 100)
            }
            if (it.isShowBackground) {
                colorTextBackgroundAdapter.updateSelectedColor(it.backgroundColorIndex)
                handler.postDelayed({
                    binding.rvTextBackground.smoothScrollToPosition(it.backgroundColorIndex)
                }, 100)
            }
        }
    }

    private fun addText() {
        activeTab(binding.btnSubmit)
        closeKeyboard(binding.btnSubmit)
        binding.textPropertiesLayout.visibility = View.INVISIBLE
        binding.btnAddNewText.visibility = View.VISIBLE
        binding.rvFonts.visibility = View.INVISIBLE
        binding.layoutColor.visibility = View.INVISIBLE

        val previewText = binding.tvPreview.text.toString()
        if (TextUtils.isEmpty(previewText)) return

        addTextProperties?.let {
            it.textWidth = binding.tvPreview.width
            it.textHeight = binding.tvPreview.height
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
        binding.edtText.setText("")
        binding.edtText.visibility = View.INVISIBLE
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
                        val layoutParams = binding.layoutGroup.layoutParams
                        layoutParams.height = heightKeyboard
                        binding.layoutGroup.layoutParams = layoutParams
                    }
                }, 100)
            }
        }
    }

    private fun initPreviewText() {
        addTextProperties?.let {
            Utils.setTypeface(requireContext(), binding.tvPreview)
            binding.tvPreview.setTextColor(Color.parseColor("#000000"))
            binding.tvPreview.text = it.text
            binding.tvPreview.typeface = it.getTypeface(requireContext())
            binding.tvPreview.setTextColor(it.textColor)

            if (it.paddingHeight > 0) {
                binding.tvPreview.setPadding(
                    binding.tvPreview.paddingLeft,
                    it.paddingHeight,
                    binding.tvPreview.paddingRight,
                    it.paddingHeight
                )
            }
            if (it.paddingWidth > 0) {
                binding.tvPreview.setPadding(
                    it.paddingWidth,
                    binding.tvPreview.paddingTop,
                    it.paddingWidth,
                    binding.tvPreview.paddingBottom
                )
            }
            if (it.textShader != null) {
                binding.tvPreview.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                binding.tvPreview.paint.shader = it.textShader
            }

            binding.tvPreview.setPadding(
                SystemUtil.dpToPx(requireContext(), it.paddingWidth),
                binding.tvPreview.paddingTop,
                SystemUtil.dpToPx(requireContext(), it.paddingWidth),
                binding.tvPreview.paddingBottom
            )
            binding.tvPreview.textAlignment = it.textAlign
            binding.tvPreview.textSize = it.textSize.toFloat()
            textSizePreView = it.textSize
            binding.tvPreview.invalidate()
        }
    }

    private fun openFontTab() {
        binding.edtText.clearFocus()
        activeTab(binding.btnFont)
        binding.layoutColor.visibility = View.GONE
        binding.fontLayout.visibility = View.VISIBLE
        handler.postDelayed({
            try {
                binding.rvFonts.smoothScrollToPosition(fontPickerAdapter.getSelectedPos())
            } catch (ignore: Exception) { }
        }, 300)
        closeKeyboard(binding.btnColor)
        addTextProperties?.let {
            handler.postDelayed({
                try {
                    binding.rvShadow.smoothScrollToPosition(it.textShadowIndex)
                } catch (ignore: Exception) { }
            }, 300)
        }
        closeKeyboard(binding.btnColor)
    }

    private fun closeTextFragment() {
        activeTab(binding.btnClose)
        closeKeyboard(binding.btnClose)
        (requireActivity() as EditImageActivity).onBackPressed()
    }

    private fun backPress() {
        activeTab(binding.btnBack)
        closeKeyboard(binding.btnBack)
        (requireActivity() as EditImageActivity).onBackPressed()
    }

    private fun addNewText() {
        binding.textPropertiesLayout.visibility = View.VISIBLE
        binding.btnAddNewText.visibility = View.INVISIBLE
        activeTab(binding.btnAddNewText)
        openQueryTab()
    }

    private fun openQueryTab() {
        openKeyboard()
        binding.edtText.requestFocus()
        activeTab(binding.btnQwerty)
        binding.edtText.visibility = View.VISIBLE
        binding.layoutColor.visibility = View.GONE
        binding.fontLayout.visibility = View.GONE
    }

    private fun activeTab(view: View) {
        binding.flQwerty.isSelected = binding.btnQwerty == view
        binding.flColor.isSelected = binding.btnColor == view
        binding.flFont.isSelected = binding.btnFont == view
        binding.flSubmit.isSelected = binding.btnSubmit == view
        binding.flClose.isSelected = binding.btnClose == view
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
            binding.btnSubmit -> {
                addText()
            }
            binding.btnBack -> {
                backPress()
            }
            binding.btnClose -> {
                closeTextFragment()
            }
            binding.btnQwerty -> {
                openQueryTab()
            }
            binding.btnColor -> {
                openColorTab()
            }
            binding.btnFont -> {
                openFontTab()
            }
            binding.btnAddNewText -> {
                addNewText()
            }
            binding.btnSizeUp -> {
                binding.tvPreview.textSize = (++textSizePreView).toFloat()
                binding.tvSizeContent.text = textSizePreView.toString()
            }
            binding.btnSizeDown -> {
                binding.tvPreview.textSize = (--textSizePreView).toFloat()
                binding.tvSizeContent.text = textSizePreView.toString()
            }
        }
    }

}
