package com.example.slide.ui.select_music.search

import android.app.Activity
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.slide.R
import com.example.slide.base.BaseFragment
import com.example.slide.base.InitViewTools
import com.example.slide.databinding.ActivitySearchBinding
import com.example.slide.ui.select_music.TrimMusicDialogFragment
import com.example.slide.ui.select_music.event.SongLoadedEvent
import com.example.slide.ui.select_music.event.SongLoadingEvent
import com.example.slide.ui.select_music.model.Track
import com.example.slide.ui.select_music.provider.impl.LocalMusicProvider
import com.example.slide.ui.video.video_preview.VideoCreateActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class SearchFragment : BaseFragment<ActivitySearchBinding>() {
    override fun bindingView(): ActivitySearchBinding {
        return ActivitySearchBinding.inflate(layoutInflater)
    }

    override fun initViewTools() = InitViewTools({ R.layout.activity_search }, { true })

    private val ioScope = CoroutineScope(Dispatchers.IO)

    private lateinit var searchAdapter: SearchAdapter

    private var trimMusicDialogFragment: TrimMusicDialogFragment? = null

    private var searchFromUser = ""

    override fun releaseData() {
        //
    }

    override fun initConfiguration() {
        super.initConfiguration()
        initSearch()
    }

    override fun initTask() {
        super.initTask()
        loadSong()
    }

    override fun initListener() {
        super.initListener()
        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
                charSequence?.let {
                    searchFromUser = it.toString()
                    searchAdapter.filter.filter(it)
                }
            }

            override fun afterTextChanged(charSequence: Editable?) {

            }
        })

        binding.btnBack.setOnClickListener {
            closeKeyboard(binding.btnBack)
            requireActivity().onBackPressed()
        }

        binding.btnClearText.setOnClickListener { binding.edtSearch.setText("") }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSongLoading(event: SongLoadingEvent) {
        //loading
        binding.progress.visibility = View.VISIBLE
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSongLoaded(event: SongLoadedEvent) {
        binding.progress.visibility = View.GONE
        searchAdapter.updateData()
        searchAdapter.filter.filter(searchFromUser)
    }

    private fun initSearch() {

        if (LocalMusicProvider.getInstance().state == LocalMusicProvider.LOADED)
            binding.progress.visibility = View.INVISIBLE
        else
            binding.progress.visibility = View.VISIBLE

        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.rvSearch.layoutManager = layoutManager
        searchAdapter = SearchAdapter(
            itemSelected = {
                if (isBind) trimMusic(it)
            },
            resultsFound = { if (isBind) showResults() },
            noResultsFound = { if (isBind) noResults() }
        )
        binding.rvSearch.adapter = searchAdapter
    }

    private fun trimMusic(track: Track) {
        if (track.duration <= 5000)
            trimMMusicDone(track)
        else
            showBottomSheet(track)
    }

    private fun noResults() {
        binding.rvSearch.visibility = View.INVISIBLE
        binding.tvNoResults.visibility = View.VISIBLE
    }

    private fun showResults() {
        binding.rvSearch.visibility = View.VISIBLE
        binding.tvNoResults.visibility = View.INVISIBLE
    }

    private fun trimMMusicDone(track: Track) {
        requireActivity().setResult(Activity.RESULT_OK, VideoCreateActivity.getIntent(track))
        requireActivity().finish()
    }

    private fun showBottomSheet(track: Track) {
        trimMusicDialogFragment = TrimMusicDialogFragment.getInstance(track)
        trimMusicDialogFragment!!.show(parentFragmentManager, TrimMusicDialogFragment.TAG)
    }

    private fun loadSong() = ioScope.launch {
        LocalMusicProvider.getInstance().init()
    }

    private fun closeKeyboard(v: View) {
        val inputMethodManager: InputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
    }

}