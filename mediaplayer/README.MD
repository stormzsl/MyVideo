#### 前言:[MediaPlayer官方学习](https://developer.android.google.cn/guide/topics/media/mediaplayer?hl=zh_cn)

##### 1.唤醒锁定权限:<uses-permission android:name="android.permission.WAKE_LOCK" />

如果播放器应用需要防止屏幕变暗或处理器进入休眠状态， 或者要使用 MediaPlayer.setScreenOnWhilePlaying() 
或 MediaPlayer.setWakeMode() 方法，则您必须申请此权限。

##### 2.MediaPlayer状态扭转图
![MediaPlayer状态图](https://developer.android.google.cn/static/images/mediaplayer_state_diagram.gif?hl=zh-cn)

##### 3.在Service中使用MediaPlayer
如果您希望即使当应用未在屏幕上显示时，应用仍会在后台播放媒体内容（也就是说，您希望应用在用户与其他应用互动时继续播放媒体内容），
则您必须启动一个 Service 并由此控制 MediaPlayer 实例 。
您需要将 MediaPlayer 嵌入到 MediaBrowserServiceCompat Service 中，并使其在其他 Activity 中与 MediaBrowserCompat 进行互动。

##### 4.异步运行
首先，与 Activity 类似，Service 中的所有工作均默认在单个线程中完成 
- 实际上，如果您从同一应用中运行 Activity 和 Service， 则它们会默认使用相同的线程（“主线程”）。
- 因此，Service 需要快速处理传入的 Intent，并且在响应它们时避免执行冗长的计算。
- 如果需要完成大量的工作或可能会阻塞调用，则您必须异步执行这些任务：
- 从您自己实现的其他线程异步执行，或使用框架的诸多工具进行异步处理。

例如，从您的主线程中使用 MediaPlayer 时，您应该调用 prepareAsync() 而非 prepare()，
并实现 MediaPlayer.OnPreparedListener，以便在准备工作完成后获得通知并开始播放。例如：

```kotlin
    private const val ACTION_PLAY: String = "com.example.action.PLAY"

    class MyService: Service(), MediaPlayer.OnPreparedListener {

        private var mMediaPlayer: MediaPlayer? = null

        override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
            
            val action: String = intent.action
            when(action) {
                ACTION_PLAY -> {
                    mMediaPlayer = todo  // initialize it here
                    mMediaPlayer?.apply {
                        setOnPreparedListener(this@MyService)
                        prepareAsync() // prepare async to not block main thread
                    }

                }
            }
            
        }

        /** Called when MediaPlayer is ready */
        override fun onPrepared(mediaPlayer: MediaPlayer) {
            mediaPlayer.start()
        }
    }
    
```

##### 5.处理异步错误
在同步操作中，系统通常会通过异常或错误代码来指示错误，但当您使用异步资源时，应确保以适当的方式向应用发出错误通知。
对于 MediaPlayer，您可以实现 MediaPlayer.OnErrorListener 并在 MediaPlayer 实例中对其进行设置来发出错误通知：

```kotlin
    class MyService : Service(), MediaPlayer.OnErrorListener {

        private var mediaPlayer: MediaPlayer? = null

        fun initMediaPlayer() {
            // ...initialize the MediaPlayer here...
            mediaPlayer?.setOnErrorListener(this)
        }

        override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
            // ... react appropriately ...
            // The MediaPlayer has moved to the Error state, must be reset!
        }
    }
    
```

> 请务必注意，出现错误时，MediaPlayer 会进入“Error”状态（如需完整的状态图，请参阅 类的参考文档），
> 而您必须先进行重置，然后才能再次使用它。

##### 6.使用唤醒锁定
当设计在后台播放媒体内容的应用时，设备可能会在您的 Service 运行时进入休眠状态。
由于 Android 系统尝试在设备处于休眠状态时节省电量，因此系统会尝试关闭手机上任何不必要的功能，包括 CPU 和 WLAN 硬件。
不过，如果您的 Service 正在播放或流式传输音乐，则您需要防止系统干扰播放。

为了确保您的 Service 在这些情况下能继续运行，您必须使用“唤醒锁定”。
唤醒锁定可以告诉系统：您的应用正在使用一些即使在手机处于闲置状态时也应该可用的功能。
> 注意：您应始终谨慎使用唤醒锁定，并只使其保留必要的时长，因为它们会显著缩短设备的电池续航时间。
> 为确保 CPU 在 MediaPlayer 播放时继续运行，请在初始化 时调用 setWakeMode() 方法。
> 完成该操作后，MediaPlayer 会在播放时保持指定的锁定状态，并在暂停或停止播放时释放锁定：
```kotlin
    mediaPlayer = MediaPlayer().apply {
        // ... other initialization here ...
        setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
    }
```

不过，此示例中获取的唤醒锁定只能保证 CPU 保持唤醒状态。
如果您使用 WLAN 并通过网络流式传输媒体内容，则您可能也希望保持 WifiLock，该锁定必须手动获取和释放。
因此，当您开始使用远程网址准备 MediaPlayer 时，您应创建并获取 WLAN 锁定。例如：
```kotlin
    val wifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager
    val wifiLock: WifiManager.WifiLock =
        wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock")

    wifiLock.acquire()
```
当您暂停或停止媒体内容，或者当您不再需要网络时，应释放该锁定：
```kotlin
    wifiLock.release()
```

##### 7.资源释放
如前文所述，MediaPlayer 对象会消耗大量的系统资源，因此您应仅使其保留必要的时长，并在操作完成后调用 release()。
请务必明确调用此清理方法，而非依赖于系统垃圾回收，因为垃圾回收器要经过一段时间才会回收 MediaPlayer，
原因在于它仅对内存需求敏感，而对缺少其他媒体相关资源并不敏感。
因此，当您使用 Service 时，应始终替换 onDestroy() 方法以确保释放 MediaPlayer：
```kotlin
    class MyService : Service() {

        private var mediaPlayer: MediaPlayer? = null
        // ...

        override fun onDestroy() {
            super.onDestroy()
            mediaPlayer?.release()
        }
    }
```
> 除了在关闭时释放您的 MediaPlayer 之外，您还应始终寻找其他释放机会。
> 例如，如果您预计在很长一段时间都无法播放媒体内容（例如，在失去音频焦点后），则您应果断释放现有的 MediaPlayer 并在之后重新创建。
> 另一方面，如果您预计只是短时间停止播放，则您可能应该保留 MediaPlayer，以避免再次创建和准备它所产生的开销。

##### 8.从 ContentResolver 检索媒体

媒体播放器应用中可能有用的另一项功能是能够检索用户保存在设备上音乐。
您可以通过查询 ContentResolver 找到外部媒体来完成该操作：
```kotlin

    val resolver: ContentResolver = contentResolver
    val uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    val cursor: Cursor? = resolver.query(uri, null, null, null, null)
    when {
        cursor == null -> {
            // query failed, handle error.
        }
        !cursor.moveToFirst() -> {
            // no media on the device
        }
        else -> {
            val titleColumn: Int = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE)
            val idColumn: Int = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID)
            do {
                val thisId = cursor.getLong(idColumn)
                val thisTitle = cursor.getString(titleColumn)
                // ...process entry...
            } while (cursor.moveToNext())
        }
    }
    cursor?.close()
```

要将其与 MediaPlayer 结合使用，您可以执行以下操作：
```kotlin
    val id: Long =  TODO  // retrieve it from somewhere
    val contentUri: Uri =
        ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id )

    mediaPlayer = MediaPlayer().apply {
        setAudioStreamType(AudioManager.STREAM_MUSIC)
        setDataSource(applicationContext, contentUri)
    }

    // ...prepare and start...
    
```