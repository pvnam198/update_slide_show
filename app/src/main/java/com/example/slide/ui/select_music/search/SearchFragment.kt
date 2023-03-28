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
import com.example.slide.ui.select_music.TrimMusicDialogFragment
import com.example.slide.ui.select_music.event.SongLoadedEvent
import com.example.slide.ui.select_music.event.SongLoadingEvent
import com.example.slide.ui.select_music.model.Track
import com.example.slide.ui.select_music.provider.impl.LocalMusicProvider
import com.example.slide.ui.video.video_preview.VideoCreateActivity
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class SearchFragment : BaseFragment() {

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
        edt_search.addTextChangedListener(object : TextWatcher {
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

        btn_back.setOnClickListener {
            closeKeyboard(btn_back)
            requireActivity().onBackPressed()
        }

        btn_clear_text.setOnClickListener { edt_search.setText("") }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSongLoading(event: SongLoadingEvent) {
        //loading
        progress.visibility = View.VISIBLE
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSongLoaded(event: SongLoadedEvent) {
        progress.visibility = View.GONE
        searchAdapter.updateData()
        searchAdapter.filter.filter(searchFromUser)
    }

    private fun initSearch() {

        if (LocalMusicProvider.getInstance().state == LocalMusicProvider.LOADED)
            progress.visibility = View.INVISIBLE
        else
            progress.visibility = View.VISIBLE

        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        rv_search.layoutManager = layoutManager
        searchAdapter = SearchAdapter(
            itemSelected = {
                if (isBind) trimMusic(it)
            },
            resultsFound = { if (isBind) showResults() },
            noResultsFound = { if (isBind) noResults() }
        )
        rv_search.adapter = searchAdapter
    }

    private fun trimMusic(track: Track) {
        if (track.duration <= 5000)
            trimMMusicDone(track)
        else
            showBottomSheet(track)
    }

    private fun noResults() {
        rv_search.visibility = View.INVISIBLE
        tv_no_results.visibility = View.VISIBLE
    }

    private fun showResults() {
        rv_search.visibility = View.VISIBLE
        tv_no_results.visibility = View.INVISIBLE
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