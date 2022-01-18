package com.wallstreetcn.sample;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.wallstreetcn.sample.adapter.Test;
import com.wallstreetcn.sample.utils.PrivacyUtils;
import com.wallstreetcn.sample.utils.TestIOThreadExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Test
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Nullable
    private View.OnClickListener listener = v -> {
        Log.i("MainActivity", v.toString());
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, SecondActivity.class);
        startActivity(intent);
    };

    ExecutorService service;

    ExecutorService pool = Executors.newSingleThreadExecutor();
    @Test
    private Entity mdata = new Entity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TestIOThreadExecutor.Companion.getThreadPool("1234");
        //    service = Executors.newFixedThreadPool(2);
        //  service = Executors.newSingleThreadExecutor(Executors.defaultThreadFactory());
        findViewById(R.id.textView1).setOnClickListener(new View.OnClickListener() {

            private NewEntity mdata = new NewEntity();

            @Override
            public void onClick(View v) {
                String test = mdata.getTest();
                Log.i("MainActivity", test);
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, SecondActivity.class);
                startActivity(intent);
            }
        });
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            TelephonyManager manager = getSystemService(TelephonyManager.class);
            PrivacyUtils.getImei(manager);
            String did = manager.getDeviceId();
            String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            //     String newDid =PrivacyUtils.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        }
    }

    @Override
    public void onClick(View v) {
        Log.i("MainActivity", v.toString());
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, SecondActivity.class);
        startActivity(intent);
    }

}
