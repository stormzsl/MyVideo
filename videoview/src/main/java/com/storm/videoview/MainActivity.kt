package com.storm.videoview

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.MediaController
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val videoPath = "android.resource://$packageName/${R.raw.video1}"
        videoView.setMediaController(MediaController(this@MainActivity))
        videoView.setVideoPath(videoPath)
//        videoView.setVideoPath("https://stream7.iqilu.com/10339/upload_transcode/202002/18/20200218093206z8V1JuPlpe.mp4")
        //或者使用videoView.setVideoURI(Uri.parse(videoPath))
        videoView.start()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}