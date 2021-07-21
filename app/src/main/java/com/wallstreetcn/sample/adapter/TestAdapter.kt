package com.wallstreetcn.sample.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wallstreetcn.sample.Entity
import com.wallstreetcn.sample.R
import com.wallstreetcn.testmodule.ModuleActivity

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
            v?.context?.apply {
                startActivity(Intent(this, ModuleActivity::class.java))
            }
        }

        fun bindViewHolder(position: Int) {
            itemView.findViewById<TextView>(R.id.textView1).text = "这是第" + position + "条目"
            itemView.setOnClickListener(this)
        }
    }
}
