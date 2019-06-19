package com.wallstreetcn.sample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.wallstreetcn.autotrack.adapter.Test;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    @Nullable
    private View.OnClickListener listener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Log.i("MainActivity", v.toString());
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, SecondActivity.class);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.textView1).setOnClickListener(new View.OnClickListener() {
            @Test
            private Entity mdata;

            @Override
            public void onClick(View v) {
                mdata = new Entity();
                Log.i("MainActivity", v.toString());
                @Test
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, SecondActivity.class);
                startActivity(intent);
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
