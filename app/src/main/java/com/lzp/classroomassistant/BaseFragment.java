package com.lzp.classroomassistant;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by WangChunhe on 2017/2/15.
 */

public abstract class BaseFragment extends Fragment {

    protected View mRootView;
    protected Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = getActivity();
    }

    protected abstract int getLayoutView();

    protected abstract void initViews(Bundle savedInstanceState);
}
