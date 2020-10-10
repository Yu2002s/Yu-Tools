package com.yu.tools.fragment

import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.yu.tools.BaseFragment
import com.yu.tools.R
import com.yu.tools.adapter.AppAdapter
import com.yu.tools.bean.App
import com.yu.tools.util.AppInfoUtils
import com.yu.tools.util.ChineseComparator
import com.yu.tools.util.LinearItemDecoration
import kotlinx.android.synthetic.main.fragment_app.*
import java.util.*
import kotlin.collections.ArrayList

class AppFragment : BaseFragment(), AppAdapter.OnCheckedModeChangeListener,
    SearchView.OnQueryTextListener {

    private val data = ArrayList<App>()
    private val systemApp = ArrayList<App>()
    private var adapter: AppAdapter? = null
    private lateinit var appInfoUtils: AppInfoUtils

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_app, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerview.layoutManager = LinearLayoutManager(view.context)
        recyclerview.addItemDecoration(LinearItemDecoration())

        toolbar.title = "管理"
        val a = activity as AppCompatActivity
        a.setSupportActionBar(toolbar)
        setHasOptionsMenu(true)

        MyThread().start()

    }

    private inner class MyThread : Thread() {
        override fun run() {
            getApps()
            activity?.runOnUiThread {
                if (adapter == null) {
                    adapter = AppAdapter(data)
                    adapter?.setOnCheckedChangeListener(this@AppFragment)
                    recyclerview.adapter = adapter
                } else {
                    adapter?.notifyDataSetChanged()
                }
                progress.visibility = View.GONE
            }
        }
    }

    private fun getApps() {
        try {
            val pm = activity?.packageManager
            if (pm != null) {
                appInfoUtils = AppInfoUtils(pm)
                val packageInfoList = pm.getInstalledApplications(0)
                for (packageInfo in packageInfoList) {
                    val app = App(
                        false,
                        packageInfo.sourceDir,
                        packageInfo.loadLabel(pm).toString(),
                        packageInfo.packageName,
                        packageInfo.loadIcon(pm)
                    )
                    if (packageInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0) {
                        data.add(app)
                    } else {
                        systemApp.add(app)
                    }
                }
                val comparator = ChineseComparator()
                Collections.sort(data, comparator)
                Collections.sort(systemApp, comparator)
                if (getSpBoolean("show")) {
                    data.addAll(systemApp)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCheckedChanged(isChecked: Boolean) {
        disable.visibility = if (isChecked) View.VISIBLE else View.GONE
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val multiItem = menu.findItem(R.id.multi)
        val checkedAllItem = menu.findItem(R.id.checkedAll)
        val uncheckedAllItem = menu.findItem(R.id.uncheckedAll)
        if (adapter != null) {
            if (adapter!!.getCheckableMode()) {
                multiItem.title = "取消多选"
                checkedAllItem.isVisible = true
                uncheckedAllItem.isVisible = true
            } else {
                multiItem.title = "多选"
                checkedAllItem.isCheckable = false
                uncheckedAllItem.isVisible = false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_app, menu)
        val searchItem = menu.findItem(R.id.search)
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(this)

        val isShowSystemApp = getSpBoolean("show")
        menu.findItem(R.id.show).isChecked = isShowSystemApp
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.refresh -> {
                data.clear()
                systemApp.clear()
                progress.visibility = View.VISIBLE
                MyThread().start()
            }
            R.id.multi -> {
                adapter?.setCheckableMode()
                activity?.invalidateOptionsMenu()
            }
            R.id.checkedAll -> {
                adapter?.checkedAll()
            }
            R.id.uncheckedAll -> {
                adapter?.clearAllChecked()
                adapter?.notifyDataSetChanged()
            }
            R.id.show -> {
                item.isChecked = !item.isChecked
                if (item.isChecked) {
                    data.addAll(systemApp)
                } else {
                    data.removeAll(systemApp)
                }
                adapter?.notifyDataSetChanged()
                setSpBoolean("show", !getSpBoolean("show"))
            }
        }
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        adapter?.filter?.filter(newText)
        return true
    }
}

