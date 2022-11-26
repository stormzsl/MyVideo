package com.storm.exoplayer

import android.content.Context
import android.media.session.PlaybackState
import android.net.Uri
import android.util.Log
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util

object PlayerManager : Player.EventListener{

     var TAG = PlayerManager::class.java.simpleName

    private var exoPlayer: SimpleExoPlayer? = null

    fun initPlayer(context: Context) {
        exoPlayer = ExoPlayerFactory.newSimpleInstance(
            context,
            DefaultTrackSelector(AdaptiveTrackSelection.Factory())
        )

        exoPlayer?.addListener(this)
    }

    fun playWithUrl(context: Context, url: String?) {
        require(!url.isNullOrEmpty()) { "Empty url" }
        Log.d(TAG,"playWithUrl: $url")
        exoPlayer?.run {
            prepare(buildMediaSource(context, Uri.parse(url)))
            playWhenReady = true

        }
    }

    fun getPlayer(): SimpleExoPlayer? {
        return exoPlayer
    }

    fun releasePlayer() {
        exoPlayer?.release()
    }

    private fun buildMediaSource(context: Context, uri: Uri): MediaSource {
        val dataSourceFactory = buildDataSourceFactory(context)
        return when (val type = Util.inferContentType(uri)) {
            C.TYPE_DASH -> DashMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
            C.TYPE_SS -> SsMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
            C.TYPE_HLS -> HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
            C.TYPE_OTHER -> ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri)
            else -> throw IllegalStateException("Unsupported type: $type")
        }
    }

    private fun buildDataSourceFactory(context: Context): DefaultDataSourceFactory {
        val appName = context.getString(R.string.app_name)
        return DefaultDataSourceFactory(
            context, DefaultHttpDataSourceFactory(
                Util.getUserAgent(context, appName)
            )
        )
    }

    /*
     * Called when the player starts or stops loading the source.
     * Params:isLoading – Whether the source is currently being loaded.
     */
    override fun onLoadingChanged(isLoading: Boolean) {
        super.onLoadingChanged(isLoading)
        Log.d(TAG,"onLoadingChanged: $isLoading")
    }

    /*
     * action_play什么意思暂时不知道
     */
    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        super.onPlayerStateChanged(playWhenReady, playbackState)
        when(playbackState){
            PlaybackState.STATE_PLAYING-> Log.d(TAG,"playbackstate.state_playing")
            PlaybackState.STATE_ERROR -> Log.d(TAG,"playbackstate.state_error")
            PlaybackState.STATE_PAUSED -> Log.d(TAG,"playbackstate.state_paused")
            PlaybackState.STATE_STOPPED->Log.d(TAG,"playbackstate.state_stopped")
            PlaybackState.STATE_BUFFERING -> Log.d(TAG,"playbackstate.state_buffering")
            PlaybackState.STATE_CONNECTING->Log.d(TAG,"playbackstate.state_connecting")
            PlaybackState.STATE_FAST_FORWARDING->Log.d(TAG,"playbackstate.state_fast_forwarding")
            PlaybackState.STATE_REWINDING->Log.d(TAG,"playbackstate.state_rewinding")
            PlaybackState.STATE_SKIPPING_TO_NEXT->Log.d(TAG,"playbackstate.state_skipping_to_next")
            PlaybackState.STATE_SKIPPING_TO_PREVIOUS->Log.d(TAG,"playbackstate.state_skipping_to_previous")
            PlaybackState.STATE_SKIPPING_TO_QUEUE_ITEM->Log.d(TAG,"playbackstate.state_skipping_to_queue_item")
            else -> {}

        }
    }

    override fun onRepeatModeChanged(repeatMode: Int) {
        super.onRepeatModeChanged(repeatMode)
        Log.d(TAG,"onRepeatModeChanged")
    }

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
        super.onShuffleModeEnabledChanged(shuffleModeEnabled)
        Log.d(TAG,"onShuffleModeEnabledChanged")
    }

    override fun onPlayerError(error: ExoPlaybackException?) {
        super.onPlayerError(error)
        Log.d(TAG,"onPlayerError：$error")
    }

    override fun onPositionDiscontinuity(reason: Int) {
        super.onPositionDiscontinuity(reason)
        Log.d(TAG,"onPositionDiscontinuity")
    }

    override fun onSeekProcessed() {
        super.onSeekProcessed()
        Log.d(TAG,"onSeekProcessed")
    }
}