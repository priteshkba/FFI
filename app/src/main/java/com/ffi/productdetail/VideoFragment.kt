package com.ffi.productdetail

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ffi.R
import com.ffi.Utils.getMediaLink
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import kotlinx.android.synthetic.main.adapter_video.*


class VideoFragment(val media: String) : Fragment() {

    var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.adapter_video, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializePlayer()
    }

    private fun initializePlayer() {
        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(
                DefaultRenderersFactory(context),
                DefaultTrackSelector(),
                DefaultLoadControl()
            )
            playerView?.setPlayer(player)
            /*player.setPlayWhenReady(playWhenReady)
            player.seekTo(currentWindow, playbackPosition)*/
        }
        val mediaSource =
            buildMediaSource(Uri.parse(getMediaLink().replace("https", "http") + media))
        player?.prepare(mediaSource, true, false)
        player?.playWhenReady=true
    }

    private fun buildMediaSource(uri: Uri): MediaSource {

        val userAgent = "exoplayer-codelab"

        if (uri.getLastPathSegment()?.contains("mp3")!! || uri.getLastPathSegment()
                ?.contains("mp4")!!
        ) {
            return ExtractorMediaSource.Factory(DefaultHttpDataSourceFactory(userAgent))
                .createMediaSource(uri)
        } else /*if (uri.getLastPathSegment()!!.contains("m3u8"))*/ {
            return HlsMediaSource.Factory(DefaultHttpDataSourceFactory(userAgent))
                .createMediaSource(uri)
        }/* else {
           *//* val dashChunkSourceFactory = DefaultDashChunkSource.Factory(
                DefaultHttpDataSourceFactory("ua", 100))*//*
            val manifestDataSourceFactory = DefaultHttpDataSourceFactory(userAgent)
            return DashMediaSource.Factory(dashChunkSourceFactory, manifestDataSourceFactory).createMediaSource(uri)
        }*/
    }

    private fun releasePlayer() {
        if (player != null) {
            /*        playbackPosition = player.getCurrentPosition()
                    currentWindow = player.getCurrentWindowIndex()
                    playWhenReady = player.getPlayWhenReady()*/
            player?.release()
            player = null
        }
    }

    override fun onPause() {
        releasePlayer()
        super.onPause()
    }
}