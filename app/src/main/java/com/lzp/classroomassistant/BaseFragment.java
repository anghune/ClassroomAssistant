package com.lzp.classroomassistant;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * Created by WangChunhe on 2017/2/15.
 */

public abstract class BaseFragment extends Fragment {

    protected View mRootView;
    protected Context mContext;
    protected AppData appData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = getActivity();
        appData = AppData.getInstance();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = LayoutInflater.from(getActivity()).inflate(getLayoutView(), null);
        ButterKnife.inject(this, mRootView);
        initViews(savedInstanceState);
        return mRootView;
    }

    protected abstract int getLayoutView();

    protected abstract void initViews(Bundle savedInstanceState);
}
