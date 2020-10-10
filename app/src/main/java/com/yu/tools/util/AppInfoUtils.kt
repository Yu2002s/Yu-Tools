package com.yu.tools.util

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.*

class AppInfoUtils(private val packageManager: PackageManager) {

    fun getAppActivitySize(packageName: String): Int {
        val packageInfo: PackageInfo? =
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
        return packageInfo?.activities?.size ?: 0
    }

    fun getAppServiceSize(packageName: String): Int {
        val packageInfo: PackageInfo? =
            packageManager.getPackageInfo(packageName, PackageManager.GET_SERVICES)
        return packageInfo?.services?.size ?: 0
    }

    fun getAppReceiverSize(packageName: String): Int {
        val packageInfo: PackageInfo? =
            packageManager.getPackageInfo(packageName, PackageManager.GET_RECEIVERS)
        return packageInfo?.receivers?.size ?: 0
    }

    fun getAppProviderSize(packageName: String): Int {
        val packageInfo: PackageInfo? =
            packageManager.getPackageInfo(packageName, PackageManager.GET_PROVIDERS)
        return packageInfo?.providers?.size ?: 0
    }

    @SuppressLint("WrongConstant")
    fun getAppLauncherIntent(packageName: String): String {
        val builder = StringBuilder()
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.`package` = packageName
        val resolveInfoList =
            packageManager.queryIntentActivities(intent, PackageManager.GET_ACTIVITIES)
        if (resolveInfoList.size == 0) {
            return "未找到桌面入口"
        }
        for (resolveInfo in resolveInfoList) {
            builder.append(resolveInfo.activityInfo.name).append("\n")
        }
        return builder.toString()
    }

    @SuppressLint("WrongConstant")
    fun getAppMainIntent(packageName: String): String {
        val builder = StringBuilder()
        val intent = Intent(Intent.ACTION_MAIN)
        intent.`package` = packageName
        val resolveInfoList =
            packageManager.queryIntentActivities(intent, PackageManager.GET_ACTIVITIES)
        if (resolveInfoList.size == 0) {
            return "未找到隐式入口"
        }
        for (resolveInfo in resolveInfoList) {
            builder.append(resolveInfo.activityInfo.name).append("\n")
        }
        return builder.toString()
    }
}