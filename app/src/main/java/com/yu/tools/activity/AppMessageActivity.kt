package com.yu.tools.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import com.yu.tools.BaseActivity
import com.yu.tools.R
import com.yu.tools.toBoldString
import com.yu.tools.toDate
import com.yu.tools.util.AppBarScrollChangeListener
import com.yu.tools.util.AppInfoUtils
import kotlinx.android.synthetic.main.activity_app_message.*
import kotlinx.android.synthetic.main.activity_app_message.appbar
import kotlinx.android.synthetic.main.activity_app_message.toolbar
import kotlinx.android.synthetic.main.activity_app_message.toolbarLayout
import kotlinx.android.synthetic.main.fragment_find.*
import java.io.File
import java.text.DecimalFormat

class AppMessageActivity : BaseActivity() {

    private lateinit var appDir: String
    private lateinit var appName: String
    private lateinit var appInfoUtils: AppInfoUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_message)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        appDir = intent.getStringExtra("appDir").toString()
        MyThread().start()

        appbar.addOnOffsetChangedListener(object : AppBarScrollChangeListener() {
            override fun onChangeListener(state: Int) {
                if (state == State.COLLAPSED) {
                    toolbarLayout.title = appName
                } else {
                    toolbarLayout.title = " "
                }
            }
        })
    }

    inner class MyThread : Thread() {
        override fun run() {
            try {
                appInfoUtils = AppInfoUtils(packageManager)
                val packageInfo = packageManager.getPackageArchiveInfo(appDir, 0)
                if (packageInfo != null) {
                    val applicationInfo = packageInfo.applicationInfo
                    appName = applicationInfo.loadLabel(packageManager) as String
                    runOnUiThread {
                        app_icon.setImageDrawable(applicationInfo.loadIcon(packageManager))
                        app_name.text =
                            getSimpleMessage(packageInfo).toBoldString(0, appName.length)
                        apkTextView.text = getApkMessage(packageInfo).toBoldString(0, 4)
                        intentTextView.text = getIntentMessage(packageInfo).toBoldString(0, 4)
                        componentTextView.text =
                            getComponentsMessage(packageName).toBoldString(0, 4)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getSimpleMessage(packageInfo: PackageInfo): String {
        val decimalFormat = DecimalFormat("0.00")
        return appName +
                "\n" + packageInfo.versionName +
                " | " + decimalFormat.format(File(appDir).length() / 1024 / 1024) + "M" +
                "\n" + packageInfo.packageName
    }

    private fun getApkMessage(packageInfo: PackageInfo): String {
        return "基本信息\n\n" +
                "首装信息：" + packageInfo.firstInstallTime.toDate() +
                "\n更新时间：" + packageInfo.lastUpdateTime.toDate() + "\n"
    }

    private fun getIntentMessage(packageInfo: PackageInfo): String {
        return "入口信息\n\n桌面入口：\n" + appInfoUtils.getAppLauncherIntent(packageInfo.packageName) +
                "\n隐式入口：\n" + appInfoUtils.getAppMainIntent(packageInfo.packageName)
    }

    private fun getComponentsMessage(packageName: String): String {
        return "组件信息\n\nActivity " + appInfoUtils.getAppActivitySize(packageName) +
                "\nServices " + appInfoUtils.getAppServiceSize(packageName) +
                "\nReceiver " + appInfoUtils.getAppReceiverSize(packageName) +
                "\nProvider " + appInfoUtils.getAppProviderSize(packageName) + "\n"
    }
}
