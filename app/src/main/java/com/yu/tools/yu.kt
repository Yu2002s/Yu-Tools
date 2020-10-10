package com.yu.tools

import android.annotation.SuppressLint
import android.content.ContentValues
import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.StyleSpan
import okhttp3.internal.parseCookie
import java.text.SimpleDateFormat

/**
 *  对部分文本设置 bold style
 */
fun String.toBoldString(start: Int, end: Int) : SpannableString {
    val spannableString = SpannableString(this)
    val styleSpan = StyleSpan(Typeface.BOLD)
    spannableString.setSpan(styleSpan, start, end, SpannableString.SPAN_INCLUSIVE_INCLUSIVE)
    return spannableString
}

/**
 *  Long 转日期
 */
@SuppressLint("SimpleDateFormat")
fun Long.toDate() : String = SimpleDateFormat("yyyy年MM月dd日").format(this)

/**
 *  实现String对Int相乘功能
 */
operator fun String.times(num: Int) = repeat(num)

/**
 *  快速保存数据到数据库
 */
fun cvof(vararg pairs: Pair<String, Any?>) = ContentValues().apply {
    for (pair in pairs) {
        val key = pair.first
        when (val value = pair.second) {
            is Int -> put(key, value)
            is String -> put(key, value)
            is Double -> put(key, value)
        }
    }
}

/*
*  infix 函数
 */
infix fun<A, B> A.with(that: B) : Pair<A, B> = Pair(this, that)

fun main() {
    mapOf("A" with 2)
}