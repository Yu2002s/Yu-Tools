package com.yu.tools.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yu.tools.R
import com.yu.tools.activity.AppMessageActivity
import com.yu.tools.bean.App
import java.util.*
import kotlin.collections.ArrayList

class AppAdapter(private var data: ArrayList<App>) : RecyclerView.Adapter<AppAdapter.ViewHolder>(),
    Filterable {

    private var unFilterData = ArrayList<App>()

    init {
        this.unFilterData = data
    }

    private val checkedList = ArrayList<Int>()
    private var isCheckableMode: Boolean = false

    private var checkedListener: OnCheckedModeChangeListener? = null

    interface OnCheckedModeChangeListener {
        fun onCheckedChanged(isChecked: Boolean)
    }

    fun setOnCheckedChangeListener(listener: OnCheckedModeChangeListener) {
        this.checkedListener = listener
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val appName: TextView = view.findViewById(R.id.appName)
        val appPackageName: TextView = view.findViewById(R.id.appPackageName)
        val appIcon: ImageView = view.findViewById(R.id.appIcon)
        val checkBox: CheckBox = view.findViewById(R.id.checkbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_list_app, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = data[position]
        holder.appName.text = app.appName
        holder.appPackageName.text = app.packageName
        Glide.with(holder.itemView.context).load(app.appIcon).into(holder.appIcon)
       // holder.appIcon.setImageDrawable(app.appIcon)
        holder.checkBox.isChecked = app.isChecked

        when (isCheckableMode) {
            true -> holder.checkBox.visibility = View.VISIBLE
            false -> holder.checkBox.visibility = View.GONE
        }

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkedList.add(position)
            } else {
                checkedList.remove(position)
            }
        }
        holder.itemView.setOnClickListener {
            if (isCheckableMode) {
                val isChecked = holder.checkBox.isChecked
                app.isChecked = !isChecked
                holder.checkBox.isChecked = !isChecked
            } else {
                val intent = Intent(holder.itemView.context, AppMessageActivity::class.java)
                    .putExtra("appDir", data[position].appDir)
                holder.itemView.context.startActivity(intent)
            }
        }
        holder.itemView.setOnLongClickListener {
            val popupMenu = PopupMenu(it.context, it)
            popupMenu.inflate(R.menu.menu_app)
            popupMenu.show()
            true
        }
    }

    override fun getItemCount(): Int = data.size

    // 得到选中模式
    fun getCheckableMode(): Boolean = isCheckableMode

    // 设置是否进入多选模式
    fun setCheckableMode() {
        isCheckableMode = !isCheckableMode
        if (!isCheckableMode) {
            checkedList.clear()
            clearAllChecked()
        }
        notifyDataSetChanged()
        checkedListener?.onCheckedChanged(isCheckableMode)
    }

    // 清除所有选中
    fun clearAllChecked() {
        for (app in data) {
            app.isChecked = false
        }
    }

    // 选中所有
    fun checkedAll() {
        for (app in data) {
            app.isChecked = true
        }
        notifyDataSetChanged()
    }

    private fun removeItem(position: Int) {
        data.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
    }

    fun removeCheckedItem() {
        checkedList.sort()
        for ((count, i) in checkedList.withIndex()) {
            removeItem(i - count)
        }
        checkedList.clear()
    }

    override fun getFilter(): Filter {
        return SearchFilter()
    }

    inner class SearchFilter : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults? {
            val content = constraint.toString().toUpperCase(Locale.ROOT).trim()
            data = if (content.isEmpty()) {
                unFilterData
            } else {
                var filteredData = ArrayList<App>()
                for (app in unFilterData) {
                    if (app.appName.toUpperCase(Locale.ROOT).contains(content)) {
                        filteredData.add(app)
                    }
                }
                filteredData
            }
            return null
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            notifyDataSetChanged()
        }

    }
}