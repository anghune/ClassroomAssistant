package com.lzp.classroomassistant.mainpages;

import android.support.v4.app.Fragment;
import android.widget.RadioButton;

import com.lzp.classroomassistant.BasePresenter;
import com.lzp.classroomassistant.BaseView;

import java.util.ArrayList;

/**
 * Created by WangChunhe on 2017/2/15.
 */

public interface MainContract {


    interface View extends BaseView<Presenter> {

        void setViewpager(ArrayList<Fragment> fragments, RadioButton[]radioButtons);

    }


    interface Presenter extends BasePresenter{

        void setRadioButton(int position, RadioButton[] radioButtons);

        void initFragmentList(RadioButton[] radioButtons);

    }

}
