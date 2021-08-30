package com.wallstreetcn.sample.adapter;

import android.util.Log;
import android.view.View;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wallstreetcn.sample.DoubleTapCheck;
import com.wallstreetcn.sample.ToastHelper;
import com.wallstreetcn.sample.viewexpose.AutoExposeImp;
import com.wallstreetcn.sample.viewexpose.ItemViewExtensionKt;
import com.wallstreetcn.sample.viewexpose.OnExposeListener;

import java.util.Collections;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class OtherTestViewHolder extends RecyclerView.ViewHolder {
    private int i = 100;
    private DoubleTapCheck doubleTapCheck = new DoubleTapCheck();


    public OtherTestViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(v -> {
            Log.i("TAG", doubleTapCheck.toString());
        });
        ItemViewExtensionKt.addExposeListener(itemView, new AutoExposeImp(this));

        String result="(Landroidx/recyclerview/widget/RecyclerView$ViewHolder;)V";
    }
}
