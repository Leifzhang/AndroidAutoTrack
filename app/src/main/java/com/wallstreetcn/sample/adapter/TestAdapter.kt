package com.wallstreetcn.sample.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wallstreetcn.sample.R

import kotlinx.android.synthetic.main.recycler_item_view.view.*

class TestAdapter : RecyclerView.Adapter<TestAdapter.ViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.recycler_item_view, viewGroup, false))
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.bindViewHolder(i)
    }

    override fun getItemCount(): Int {
        return 10
    }

    class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindViewHolder(position: Int) {
            itemView.titleTv.text = "这是第" + position + "条目"
            itemView.setOnClickListener {
                //   ToastHelper.toast(this@ViewHolder, it)
                Log.i("onBindViewHolder", adapterPosition.toString())
            }
        }
    }
}
