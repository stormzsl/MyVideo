package com.storm.videoview

import android.media.PlaybackParams
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.MediaController
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val videoPath = "android.resource://$packageName/${R.raw.video1}"
        videoView.setMediaController(MediaController(this@MainActivity))//设置控制器显示，比如视频下方的暂停，播放等控制条，也可以不设置
        videoView.setVideoPath(videoPath)
//        videoView.setVideoPath("https://stream7.iqilu.com/10339/upload_transcode/202002/18/20200218093206z8V1JuPlpe.mp4")
        //或者使用videoView.setVideoURI(Uri.parse(videoPath))
//        videoView.start()


        videoView.setOnPreparedListener {
            loadingBar.visibility = View.INVISIBLE//加载进度条
            progressBar.max = it.duration //表示当前播放音视频的资源的时长
            it.playbackParams = PlaybackParams().apply {//设置播放属性
                speed = 2f//设置播放速度2倍速
                pitch = 0.5f//设置声音
            }
            it.isLooping = true //播放到末尾时，重复播放
            it.start()
        }

        lifecycleScope.launch {
            while (true){
                if(videoView.isPlaying){
                    progressBar.progress = videoView.currentPosition
                }
                delay(500)
            }
        }

        //播放完成时的回调
        videoView.setOnCompletionListener {

        }

        //播放错误时的回调，比如播放地址不存在
        videoView.setOnErrorListener { mp, what, extra ->
            false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}