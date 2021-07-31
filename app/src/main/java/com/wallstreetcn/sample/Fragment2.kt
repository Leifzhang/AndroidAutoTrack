package com.wallstreetcn.sample

import android.os.Bundle
import androidx.fragment.app.Fragment
import java.util.*

/**
 * @Author LiABao
 * @Since 2021/1/5
 */
open class Fragment2 : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
    }
}
