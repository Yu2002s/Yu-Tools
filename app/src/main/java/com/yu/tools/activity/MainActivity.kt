package com.yu.tools.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yu.tools.BaseActivity
import com.yu.tools.R
import com.yu.tools.fragment.AppFragment
import com.yu.tools.fragment.FindFragment
import com.yu.tools.util.AppBarScrollChangeListener
import com.yu.tools.util.StatusBarUtil
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), BottomNavigationView.OnNavigationItemSelectedListener,
    ViewPager.OnPageChangeListener {

    private val fragments = ArrayList<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPermission()

        fragments.add(AppFragment())
        fragments.add(FindFragment())

        bottomNavigationView.setOnNavigationItemSelectedListener(this)
        viewPager.addOnPageChangeListener(this)
        viewPager.adapter = MyPagerAdapter(supportFragmentManager, 0)
    }

    inner class MyPagerAdapter(
        fm: FragmentManager,
        root: Int,
    ) : FragmentPagerAdapter(fm, root) {
        override fun getCount(): Int {
            return fragments.size
        }

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item1 -> viewPager.currentItem = 0
            R.id.item2 -> viewPager.currentItem = 1
        }
        return true
    }

    override fun onPageSelected(position: Int) {
        bottomNavigationView.menu.getItem(position).isChecked = true
        when (position) {
            0 -> StatusBarUtil.setStatusBar(this, true)
            1 -> {
                val state = (fragments[1] as FindFragment).getCollapseState()
                val isLight = state == AppBarScrollChangeListener.State.COLLAPSED
                StatusBarUtil.setStatusBar(this, isLight)
            }
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageScrollStateChanged(state: Int) {}
}