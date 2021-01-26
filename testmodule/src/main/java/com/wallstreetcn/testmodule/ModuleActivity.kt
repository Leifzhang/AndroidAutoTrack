package com.wallstreetcn.testmodule

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class ModuleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_module)
        findViewById<View>(R.id.textView1)?.setOnClickListener {
            KronosContext.requireApplication().show("点击按钮")
        }

    }
}