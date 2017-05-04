package com.lzp.classroomassistant.assistant;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.lzp.classroomassistant.BaseFragment;
import com.lzp.classroomassistant.R;
import com.lzp.classroomassistant.assistant.courseware.FileActivity;
import com.lzp.classroomassistant.assistant.notice.NoticeActivity;

import butterknife.OnClick;


public class AssistantFragment extends BaseFragment {


    @OnClick({R.id.noticeBtnId, R.id.fileBtnId})
    void onClick(View view){
        switch (view.getId()){
            case R.id.noticeBtnId:
                Intent intent = new Intent(getActivity(),NoticeActivity.class);
                startActivity(intent);
                break;
            case R.id.fileBtnId:
                Intent fileIntent = new Intent(getActivity(), FileActivity.class);
                startActivity(fileIntent);
                break;
        }
    }
    @Override
    protected int getLayoutView() {
        return R.layout.fragment_assistant;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

    }

}
