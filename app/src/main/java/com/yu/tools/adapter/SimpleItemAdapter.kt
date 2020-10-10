package com.yu.tools.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yu.tools.R

class SimpleItemAdapter(private val data: ArrayList<HashMap<String, Any>>) :
    RecyclerView.Adapter<SimpleItemAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    private lateinit var clickListener: OnItemClickListener

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.clickListener = listener
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.img_icon)
        val title: TextView = view.findViewById(R.id.tv_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_simple, parent, false)
        val viewHolder = ViewHolder(itemView)
        itemView.setOnClickListener {
            if (::clickListener.isInitialized) {
                clickListener.onItemClick(viewHolder.layoutPosition)
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.icon.setImageResource(data[position]["icon"] as Int)
        holder.title.text = data[position]["title"].toString()
    }

    override fun getItemCount(): Int {
        return data.size
    }
}