package com.yu.tools.util

import java.lang.StringBuilder

/**
 *  顶层函数
 */

fun String.letterCount(str: String): Int {
    var count = 0
    for (s in str) {
        if (s.isLetter()) {
            count ++
        }
    }
    return count
}

