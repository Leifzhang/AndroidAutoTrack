package com.wallstreetcn.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.wallstreetcn.autotrack.adapter.TestAdapter
import com.wallstreetcn.sample.utils.TestIOThreadExecutor
import kotlinx.android.synthetic.main.activity_second.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SecondActivity : AppCompatActivity() {
    var poolExecutor: ExecutorService? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = TestAdapter()
        poolExecutor = Executors.newFixedThreadPool(2)
    }
}
