package com.lzp.classroomassistant.mainpages;

import android.support.v4.app.Fragment;
import android.widget.RadioButton;

import java.util.ArrayList;

/**
 * Created by WangChunhe on 2017/2/15.
 */

public class MainPresenter implements MainContract.Presenter{

    private MainContract.View mainView;

    public MainPresenter(MainContract.View mainView) {
        this.mainView = mainView;
    }

    @Override
    public void setRadioButton(int position, RadioButton[] radioButtons) {

    }

    @Override
    public void initFragmentList(RadioButton[] radioButtons) {
        ArrayList<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new BlankFragment());
        fragmentList.add(new BlankFragment());
        fragmentList.add(new BlankFragment());
        fragmentList.add(new BlankFragment());
        mainView.setViewpager(fragmentList,radioButtons);


    }
}
