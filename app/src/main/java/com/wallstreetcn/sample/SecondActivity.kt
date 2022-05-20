package com.wallstreetcn.sample

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wallstreetcn.sample.adapter.TestAdapter
import com.wallstreetcn.sample.utils.PrivacyUtils
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SecondActivity : AppCompatActivity() {

    var poolExecutor: ExecutorService? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = TestAdapter()
        poolExecutor = Executors.newFixedThreadPool(2)
        val number=2L.toBigDecimal()
        val wifiMgr = applicationContext.getSystemService(Context.WIFI_SERVICE)
                as WifiManager
 /*       val info = wifiMgr.connectionInfo
        val ssid = info?.bssid*/

        val list: MutableList<PackageInfo> = mutableListOf()
        val pm: PackageManager = packageManager
        val installedPackages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        //   val newList = PrivacyUtils.getAppPackageInfo(pm, PackageManager.GET_META_DATA)
        for (pi in installedPackages) {
           // list.add(pi)
        }
    }
}
