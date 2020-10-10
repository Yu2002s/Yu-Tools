package com.yu.tools

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {

    fun showToast(str: String) {
        Toast.makeText(activity, str, Toast.LENGTH_SHORT).show()
    }

    fun setSpBoolean(name: String, i: Boolean) {
        val sp = activity!!.getSharedPreferences(name, AppCompatActivity.MODE_PRIVATE).edit()
        sp.putBoolean("key", i)
        sp.apply()
    }

    fun getSpBoolean(name: String): Boolean {
        val sp = activity!!.getSharedPreferences(name, AppCompatActivity.MODE_PRIVATE)
        return sp.getBoolean("key", false)
    }
}