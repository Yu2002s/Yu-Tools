package com.yu.tools.util

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.os.Looper
import android.widget.Toast
import java.io.*

object FileUtils {

    fun saveTextFileForData(context: Context, name: String, content: String) {
        try {
            val output = context.openFileOutput(name, Context.MODE_PRIVATE)
            val write = BufferedWriter(OutputStreamWriter(output))
            write.use {
                it.write(content)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun getTextFileForData(context: Context, name: String): String {
        val builder = StringBuilder()
        try {
            val input = context.openFileInput(name)
            val reader = BufferedReader(InputStreamReader(input))
            reader.use {
                reader.forEachLine {
                    builder.append(it)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return builder.toString()
    }

    fun saveTextFileForPath(path: String, content: String) {
        try {
            val os = FileOutputStream(path)
            val write = BufferedWriter(OutputStreamWriter(os))
            write.use {
                it.write(content)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun getTextFileForPath(path: String): String {
        val builder = StringBuilder()
        try {
            val input = FileInputStream(path)
            val reader = BufferedReader(InputStreamReader(input))
            reader.use {
                it.forEachLine {
                    builder.append(it)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return builder.toString()
    }

    class FileCopy(private val context: Activity) {

        private val dialog = ProgressDialog(context)

        init {
            dialog.apply {
                setTitle("正在复制...")
                setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
                setCancelable(false)
                show()
            }
        }

        fun copyFile(inPath: String, outPath: String) {
            val file = File(outPath).parentFile
            if (file != null) {
                if (!file.exists()) {
                    file.mkdirs()
                }
            }
            Thread {
                Looper.prepare()
                try {
                    val fis = FileInputStream(inPath)
                    val fos = FileOutputStream(outPath)
                    val bs = ByteArray(1024)
                    val fileLength = File(inPath).length()
                    var len: Int
                    var count = 0L
                    var progress: Int
                    var flags = true
                    fis.use {
                        while (flags) {
                            len = fis.read(bs)
                            flags = len != -1
                            count += len
                            progress = ((100 * count) / fileLength).toInt()
                            context.runOnUiThread {
                                dialog.progress = progress
                            }
                            if (flags) {
                                fos.use {
                                    it.write(bs, 0, len)
                                }
                            }
                        }
                    }
                    dialog.dismiss()
                    Toast.makeText(context, "文件已复制到$outPath", Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    dialog.dismiss()
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
                Looper.loop()
            }.start()
        }
    }
}