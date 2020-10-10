package com.yu.tools.util

import com.google.android.material.appbar.AppBarLayout
import kotlin.math.abs

abstract class AppBarScrollChangeListener : AppBarLayout.OnOffsetChangedListener {

    private var state = State.EXPANDED

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        if (abs(verticalOffset) >= appBarLayout?.totalScrollRange!!) {
            if (state != State.COLLAPSED) {
                state = State.COLLAPSED
                onChangeListener(state)
            }
        } else if (verticalOffset == 0) {
            if (state != State.EXPANDED) {
                state = State.EXPANDED
                onChangeListener(state)
            }
        } else {
            if (state != State.IDLE) {
                state = State.IDLE
                onChangeListener(state)
            }
        }
    }

   abstract fun onChangeListener(state: Int)

    class State {
        companion object {
            const val COLLAPSED = 1
            const val EXPANDED = 2
            const val IDLE = 3
        }
    }
}