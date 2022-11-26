package com.storm.mediaplayer

import android.media.MediaPlayer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
/*
 * 为MediaPlayer增加生命周期感知能力
 */
class MyMediaPlayer:MediaPlayer(),LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause(){
        pause()//1.暂停时停止播放
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume(){
        start()
    }
}
