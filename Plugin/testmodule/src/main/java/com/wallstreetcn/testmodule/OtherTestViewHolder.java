package com.wallstreetcn.testmodule;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OtherTestViewHolder extends RecyclerView.ViewHolder {
    private int i = 100;

    public OtherTestViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(v -> {
            Log.i("", "" + i);
        });
    }
}
