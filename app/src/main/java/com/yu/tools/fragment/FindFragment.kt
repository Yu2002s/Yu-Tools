package com.yu.tools.fragment

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.yu.tools.BaseFragment
import com.yu.tools.R
import com.yu.tools.adapter.ImagePagerAdapter
import com.yu.tools.adapter.SimpleItemAdapter
import com.yu.tools.util.AppBarScrollChangeListener
import com.yu.tools.util.GridItemDecoration
import com.yu.tools.util.StatusBarUtil
import kotlinx.android.synthetic.main.fragment_find.*
import org.jsoup.Jsoup

class FindFragment : BaseFragment(), SimpleItemAdapter.OnItemClickListener {

    private val api = "http://www.netbian.com"

    private lateinit var data: ArrayList<HashMap<String, Any>>
    private lateinit var adapter: SimpleItemAdapter
    // 折叠状态
    private var collapseState = AppBarScrollChangeListener.State.EXPANDED

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_find, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        getImage()
        getData()
        initRecyclerView()

        toolbarLayout.setCollapsedTitleTextColor(Color.BLACK)
        toolbarLayout.setExpandedTitleColor(Color.WHITE)

        appbar.addOnOffsetChangedListener(object : AppBarScrollChangeListener() {
            override fun onChangeListener(state: Int) {
                collapseState = state
                if (state == State.COLLAPSED) {
                    StatusBarUtil.setStatusBar(activity, true)
                } else {
                    StatusBarUtil.setStatusBar(activity, false)
                }
            }
        })

        /**
        val okHttpClient = OkHttpClient()
        val body = FormBody.Builder()
            .add("keyword", "QQ")
            .add("type", "1")
            .build()
        val request = Request.Builder()
            .url("https://app.zol.com.cn/touch/index.php?c=search&type=1")
            .post(body)
            .build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val data = response.body?.string()
                activity?.runOnUiThread {
                    if (data != null) {
                        // showToast(data)
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {

            }
        })
        **/
    }

    /**
     *  得到轮番图片
     */
    private fun getImage() {
        Thread {
            try {
                val urls: ArrayList<String> = ArrayList()
                val document = Jsoup.connect(api).get()
                val elements = document.select("div.slide").select("li").select("a")
                for (element in elements) {
                    val imgUrl = element.select("img").attr("src")
                    urls.add(imgUrl)
                }
                activity?.runOnUiThread {
                    viewPager.adapter = ImagePagerAdapter(urls)
                    viewPager.currentItem = 500
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    /**
     *  handler 延时更新轮番图图片
     */
    private val mHandler: Handler = Handler(Looper.myLooper()!!) {
        if (viewPager.adapter?.count != 0) {
            viewPager.currentItem = viewPager.currentItem + 1
        }
        // 延时6秒持续更新
        it.target.sendEmptyMessageDelayed(0, 6000)
        false
    }

    private fun getData() {
        data = ArrayList()
        for (i in 0 until 9) {
            val map = HashMap<String, Any>()
            map["icon"] = R.mipmap.ic_sheep
            map["title"] = "TestText$i"
            data.add(map)
        }
    }

    private fun initRecyclerView() {
        recyclerview.layoutManager = GridLayoutManager(activity, 2)
        recyclerview.addItemDecoration(GridItemDecoration())

        adapter = SimpleItemAdapter(data)
        adapter.setOnItemClickListener(this)
        recyclerview.adapter = adapter
    }

    fun getCollapseState(): Int {
        return collapseState
    }

    override fun onItemClick(position: Int) {
        showToast(data[position]["title"].toString())
    }

    override fun onStart() {
        super.onStart()
        mHandler.sendEmptyMessageDelayed(0, 6000)
    }

    override fun onStop() {
        super.onStop()
        mHandler.removeCallbacksAndMessages(null)
    }
}