package com.wallstreetcn.sample.adapter;

import android.util.Log;
import android.view.View;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wallstreetcn.sample.DoubleTapCheck;
import com.wallstreetcn.sample.ToastHelper;

import java.util.Collections;

public class OtherTestViewHolder extends RecyclerView.ViewHolder {
    private int i = 100;
    private DoubleTapCheck doubleTapCheck=new DoubleTapCheck();
    public OtherTestViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(v -> {
            if(doubleTapCheck.isNotDoubleTap()) {
                Log.i("", "");
            }
        });
    }
}
