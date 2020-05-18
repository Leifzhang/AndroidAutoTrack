package com.wallstreetcn.autotrack.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wallstreetcn.sample.Entity
import com.wallstreetcn.sample.R
import com.wallstreetcn.sample.ToastHelper
import com.wallstreetcn.sample.adapter.Test
import kotlinx.android.synthetic.main.recycler_item_view.view.*

class TestAdapter : RecyclerView.Adapter<TestAdapter.ViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.recycler_item_view, viewGroup, false)
        )
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.bindViewHolder(i)
    }

    override fun getItemCount(): Int {
        return 10
    }

    class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        @Test
        private val entity = Entity()

        override fun onClick(v: View?) {
            Log.i("onBindViewHolder", layoutPosition.toString())
        }

        fun bindViewHolder(position: Int) {
            itemView.titleTv.text = "这是第" + position + "条目"
             itemView.setOnClickListener(this)
            /*itemView.setOnClickListener {
                Log.i("", "")
            }*/
        }
    }
}
