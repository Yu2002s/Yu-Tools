package com.yu.tools.service

import android.app.Service
import android.content.Intent
import android.os.Environment
import android.os.IBinder
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.RandomAccessFile

class DownloadService : Service() {

    private var sdcardFile = Environment.getExternalStorageDirectory()
    private val threadCount = 4

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        val file = File(sdcardFile.path + "/Download")
        if (!file.exists()) {
            file.mkdirs()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val url = intent?.getStringExtra("url")
        if (!url.isNullOrEmpty()) {
            prepareDownload(url)
        }
        return START_NOT_STICKY
    }

    private fun prepareDownload(url: String) {
        Thread {
            try {
                val okHttpClient = OkHttpClient()
                val request = Request.Builder().url(url).build()
                // 进行异步请求
                val response = okHttpClient.newCall(request).execute()
                val body = response.body
                // 得到要下载的文件大小
                val contentLength = body?.contentLength()
                Log.d("jdy", "" + contentLength)
                val filePath = File(sdcardFile.path + "/jdy.exe")
                // 生成文件
                val randomAccessFile = RandomAccessFile(filePath, "rw")
                // 设置文件大小
                randomAccessFile.setLength(contentLength!!)
                randomAccessFile.close()

                val blockSize = contentLength / threadCount

                for (i in 0 until threadCount) {
                    val start = i * blockSize
                    var end = (i + 1) * blockSize - 1
                    if (i == threadCount - 1) {
                        end = contentLength - 1
                    }
                    startDownload(url, start, end)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun startDownload(url: String, start: Long, end: Long) {
        try {
            val okHttpClient = OkHttpClient()
            val request = Request.Builder().url(url).addHeader("range", "bytes=$start-$end").build()
            // 进行异步请求
            val response = okHttpClient.newCall(request).execute()
            // 得到文件大小
            val contentLength = response.body?.contentLength()
            // 得到输入流
            val inputStream = response.body?.byteStream()
            val filePath = File(sdcardFile.path + "/jdy.exe")
            val randomAccessFile = RandomAccessFile(filePath, "rw")
            randomAccessFile.seek(start)
            val bs = ByteArray(1024)
            var len: Int
            var count = 0L
            var progress: Int
            var flags = true
            inputStream?.use {
                while (flags) {
                    len = it.read(bs)
                    flags = len != -1
                    if (flags) {
                        count += len
                        progress = ((100 * count) / contentLength!!).toInt()
                        randomAccessFile.use {
                            randomAccessFile.write(bs, 0, len)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}