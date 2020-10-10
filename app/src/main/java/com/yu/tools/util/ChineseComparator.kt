package com.yu.tools.util

import com.yu.tools.bean.App
import java.text.Collator
import java.util.*
import kotlin.Comparator

class ChineseComparator : Comparator<App> {

    private val collator: Collator = Collator.getInstance(Locale.CHINA)

    override fun compare(o1: App?, o2: App?): Int {
        val name1 = o1?.appName
        val name2 = o2?.appName
        if (collator.compare(name1, name2) > 0) {
            return 1
        } else if (collator.compare(name1, name2) < 0) {
            return -1
        }
        return 0
    }

}