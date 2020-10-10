package com.yu.tools.util

import java.lang.StringBuilder

fun test(num1: Int, num2: Int, a: (Int, Int) -> Int) : Int {
    return a(num1, num2)
}

fun StringBuilder.add(block: StringBuilder.() -> Unit) : StringBuilder {
    block()
    return this
}

fun main() {
    val a = test(100, 200) {
            num1, num2->
        num1 + num2
    }
    println(a)

    StringBuilder().add {
        append("aaa")
    }
}