package com.storm.exoplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val urls : Array<String> = arrayOf(
            "https://vfx.mtime.cn/Video/2019/03/21/mp4/190321153853126488.mp4",
            "https://vfx.mtime.cn/Video/2019/03/19/mp4/190319222227698228.mp4",
            "https://vfx.mtime.cn/Video/2019/03/19/mp4/190319212559089721.mp4",
            "https://vfx.mtime.cn/Video/2019/03/18/mp4/190318231014076505.mp4",
            "https://vfx.mtime.cn/Video/2019/03/18/mp4/190318214226685784.mp4",
            "https://vfx.mtime.cn/Video/2019/03/19/mp4/190319104618910544.mp4",
            "https://vfx.mtime.cn/Video/2019/03/19/mp4/190319125415785691.mp4",
            "https://vfx.mtime.cn/Video/2019/03/17/mp4/190317150237409904.mp4",
            "https://vfx.mtime.cn/Video/2019/03/14/mp4/190314223540373995.mp4",
            "https://vfx.mtime.cn/Video/2019/03/14/mp4/190314102306987969.mp4",
            "https://vfx.mtime.cn/Video/2019/03/13/mp4/190313094901111138.mp4",
            "https://vfx.mtime.cn/Video/2019/03/12/mp4/190312143927981075.mp4",
            "https://vfx.mtime.cn/Video/2019/03/12/mp4/190312083533415853.mp4",
            "https://vfx.mtime.cn/Video/2019/03/09/mp4/190309153658147087.mp4",
            "https://vfx.mtime.cn/Video/2019/01/15/mp4/190115161611510728_480.mp4")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        video_list.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = ListAdapter(urls)
        }
    }
}