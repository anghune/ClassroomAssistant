package com.lzp.classroomassistant.mainpages;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RadioButton;

import com.lzp.classroomassistant.BaseActivity;
import com.lzp.classroomassistant.R;

import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements MainContract.View{

    @InjectView(R.id.tab_curriculum)
    RadioButton mTabCurriculum;
    @InjectView(R.id.tab_letter)
    RadioButton mTabLetter;
    @InjectView(R.id.tab_assistant)
    RadioButton mTabAssistant;
    @InjectView(R.id.tab_personal)
    RadioButton mTabPersonal;
    @InjectView(R.id.tab_vp)
    ViewPager mTabViewPager;
    @InjectView(R.id.main_toolbar)
    Toolbar mToolbar;


    @OnClick({R.id.tab_curriculum,R.id.tab_letter,R.id.tab_assistant,R.id.tab_personal})
    void onClick(View view){
        switch (view.getId()){
            case R.id.tab_curriculum:
                mTabViewPager.setCurrentItem(0,true);
                break;
            case R.id.tab_letter:
                mTabViewPager.setCurrentItem(1,true);
                break;
            case R.id.tab_assistant:
                mTabViewPager.setCurrentItem(2,true);
                break;
            case R.id.tab_personal:
                mTabViewPager.setCurrentItem(3,true);
                break;


        }
    }

    private int current = 0;
    private MainContract.Presenter mPresenter;
    private ViewPagerAdapter mAdapter;

    @Override
    protected int getLayoutView() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mPresenter = new MainPresenter(this);
        RadioButton[] radioButtons = {
                mTabCurriculum,
                mTabLetter,
                mTabAssistant,
                mTabPersonal
        };

//        mPresenter.setRadioButton(0,radioButtons);
        mPresenter.initFragmentList(radioButtons);

        setSupportActionBar(mToolbar);

    }



    @Override
    public void setViewpager(ArrayList<Fragment> fragments, final RadioButton[] radioButtons) {
        mTabViewPager.setOffscreenPageLimit(1);
        mAdapter = new ViewPagerAdapter(fragments,getSupportFragmentManager(),mTabViewPager);
        mAdapter.setOnExtraPageChangeListener(new ViewPagerAdapter.OnExtraPageChangeListener() {
            @Override
            public void onExtraPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onExtraPageSelected(int position) {
                setRadioButton(position,radioButtons);
                current = position;
            }

            @Override
            public void onExtraPageScrollStateChanged(int state) {

            }
        });

        mTabViewPager.setAdapter(mAdapter);
        mTabViewPager.setCurrentItem(0);

    }

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    private void setRadioButton(int pos,  RadioButton[] radioButtons){
        for (int i = 0; i < radioButtons.length; i++){
            if (i == pos ){
                radioButtons[i].setChecked(true);
            }else {
                radioButtons[i].setChecked(false);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (current == 0){
            super.onBackPressed();
        }else {
            mTabViewPager.setCurrentItem(current-1,true);
        }

    }
}
