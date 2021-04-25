package com.wallstreetcn.sample;

import android.app.Activity;

import androidx.fragment.app.Fragment;

/**
 * @Author LiABao
 * @Since 2021/1/5
 */
public class JavaFragment extends Fragment {
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Activity activity = getActivity();
    }
}
