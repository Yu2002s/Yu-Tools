package com.yu.tools.bean

import android.graphics.drawable.Drawable

data class App(
    var isChecked: Boolean,
    val appDir: String,
    val appName: String,
    val packageName: String,
    val appIcon: Drawable
)