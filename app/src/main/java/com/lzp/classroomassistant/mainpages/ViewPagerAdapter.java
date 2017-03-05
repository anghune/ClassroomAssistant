package com.lzp.classroomassistant.mainpages;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by WangChunhe on 2017/2/15.
 */

public class ViewPagerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener{

    private ArrayList<Fragment> mFragmentList;
    private ArrayList<String> mTitleLists;
    private FragmentManager mFragmentManager;
    private ViewPager mViewPager;
    private int mCurrentPageIndex = 0;

    private OnExtraPageChangeListener onExtraPageChangeListener;


    public ViewPagerAdapter(ArrayList<Fragment> fragmentList, FragmentManager fragmentManager, ViewPager viewPager) {
        this.mFragmentList = fragmentList;
        this.mFragmentManager = fragmentManager;
        this.mViewPager = viewPager;
//        this.mViewPager.setAdapter(this);
        this.mViewPager.addOnPageChangeListener(this);
    }

    public void setTitleLists(ArrayList<String> titleLists) {
        mTitleLists = titleLists;
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mFragmentList.get(position).getView());
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = mFragmentList.get(position);
        fragment.setMenuVisibility(true);
        if (!fragment.isAdded()) {
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            ft.add(fragment, fragment.getClass().getSimpleName());
            ft.commitAllowingStateLoss();
            mFragmentManager.executePendingTransactions();
        }

        if (fragment.getView().getParent() == null){
            container.addView(fragment.getView());
        }
        return fragment.getView();
    }

    public void setOnExtraPageChangeListener(OnExtraPageChangeListener onExtraPageChangeListener) {
        this.onExtraPageChangeListener = onExtraPageChangeListener;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (onExtraPageChangeListener != null){
            onExtraPageChangeListener.onExtraPageScrolled(position,positionOffset,positionOffsetPixels);

        }
    }

    @Override
    public void onPageSelected(int position) {

        mFragmentList.get(mCurrentPageIndex).onPause();
        if (mFragmentList.get(position).isAdded()){
            mFragmentList.get(position).onResume();
        }
        mCurrentPageIndex = position;

        if (onExtraPageChangeListener != null){
            onExtraPageChangeListener.onExtraPageSelected(position);
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (onExtraPageChangeListener != null){
            onExtraPageChangeListener.onExtraPageScrollStateChanged(state);
        }
    }




    public interface OnExtraPageChangeListener {
        void onExtraPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        void onExtraPageSelected(int position);

        void onExtraPageScrollStateChanged(int state);
    }
}
