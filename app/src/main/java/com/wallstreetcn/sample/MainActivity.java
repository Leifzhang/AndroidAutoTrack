package com.wallstreetcn.sample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.wallstreetcn.sample.adapter.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//@Keep
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
        assert true;
        //    service = Executors.newFixedThreadPool(2);
        //  service = Executors.newSingleThreadExecutor(Executors.defaultThreadFactory());
        findViewById(R.id.textView1).setOnClickListener(new View.OnClickListener() {
            @Test
            private Entity mdata;

            @Override
            public void onClick(View v) {
                Log.i("MainActivity", v.toString());
                Intent intent = new Intent();
            }
        });
    }

    @Override
    public void onClick(View v) {
        Log.i("MainActivity", v.toString());
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, SecondActivity.class);
        startActivity(intent);
    }

}
