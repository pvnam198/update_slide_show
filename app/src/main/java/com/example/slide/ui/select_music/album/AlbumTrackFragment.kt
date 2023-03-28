package com.example.slide.ui.select_music.album

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.slide.R
import com.example.slide.base.BaseFragment
import com.example.slide.base.InitViewTools
import com.example.slide.databinding.FragmentAudioAlbumTrackBinding
import com.example.slide.ui.select_music.SelectMusicActivity
import com.example.slide.ui.select_music.SubTrackAdapter
import com.example.slide.ui.select_music.event.SongLoadedEvent
import com.example.slide.ui.select_music.event.SongLoadingEvent
import com.example.slide.ui.select_music.model.MusicAlbum
import com.example.slide.ui.select_music.provider.impl.LocalMusicProvider
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class AlbumTrackFragment : BaseFragment<FragmentAudioAlbumTrackBinding>() {

    override fun bindingView(): FragmentAudioAlbumTrackBinding {
        return FragmentAudioAlbumTrackBinding.inflate(layoutInflater)
    }

    override fun initViewTools() = InitViewTools({ R.layout.fragment_audio_album_track }, { true })

    companion object {

        private const val ALBUM_EXTRA = "album"

        fun getInstance(album: MusicAlbum): AlbumTrackFragment {
            val trackFragment = AlbumTrackFragment()
            val bundle = Bundle()
            bundle.putSerializable(ALBUM_EXTRA, album)
            trackFragment.arguments = bundle
            return trackFragment;
        }
    }

    var adapter: SubTrackAdapter? = null

    var album: MusicAlbum? = null

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(ALBUM_EXTRA, album)
    }

    override fun extractData(bundle: Bundle?) {
        album = bundle?.getSerializable(ALBUM_EXTRA) as MusicAlbum?
        super.extractData(bundle)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSongLoading(event: SongLoadingEvent) {
        intialState()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSongLoaded(event: SongLoadedEvent) {
        intialState()
        album ?: return
        adapter?.updateData(LocalMusicProvider.getInstance().getSongsByAlbum(album!!.id))
    }

    override fun initConfiguration() {
        super.initConfiguration()
        intialState()
        val layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        binding.trackRecycleView.layoutManager = layoutManager
        binding.trackRecycleView.setHasFixedSize(true)
        adapter = SubTrackAdapter(requireActivity() as SelectMusicActivity)
        binding.trackRecycleView.adapter = adapter
        album?.let {
            adapter?.updateData(LocalMusicProvider.getInstance().getSongsByAlbum(it.id))
            binding.tvTitle.text = it.name
            binding.tvArtist.text = it.artist
            binding.tvSongCount.text = getString(R.string.song_number_format, it.songNumber)

            if (it.arlUrl?.isNotEmpty() == true)
                Glide.with(this).load(it.arlUrl).placeholder(R.drawable.ic_audio_album).into(binding.ivAlbum)
        }
    }

    override fun initListener() {
        super.initListener()
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun intialState() {
        if (LocalMusicProvider.getInstance().state == LocalMusicProvider.LOADING) {
            binding.progress.visibility = View.VISIBLE
            binding.trackRecycleView.visibility = View.INVISIBLE
            binding.noSongLayout.visibility = View.GONE
        } else {
            if (LocalMusicProvider.getInstance().state == LocalMusicProvider.LOADED && LocalMusicProvider.getInstance().albums.size == 0) {
                binding.progress.visibility = View.GONE
                binding.trackRecycleView.visibility = View.INVISIBLE
                binding.noSongLayout.visibility = View.VISIBLE
            } else {
                binding.progress.visibility = View.GONE
                binding.trackRecycleView.visibility = View.VISIBLE
                binding.noSongLayout.visibility = View.GONE
            }

        }
    }

    override fun releaseData() {
    }
}