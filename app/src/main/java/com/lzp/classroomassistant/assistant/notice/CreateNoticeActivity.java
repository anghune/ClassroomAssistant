package com.lzp.classroomassistant.assistant.notice;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lzp.classroomassistant.BaseActivity;
import com.lzp.classroomassistant.R;
import com.lzp.classroomassistant.data.Notice;
import com.lzp.classroomassistant.data.NoticeMember;
import com.lzp.classroomassistant.data.User;
import com.lzp.classroomassistant.util.Constant;
import com.lzp.classroomassistant.util.ToastUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.SaveListener;

public class CreateNoticeActivity extends BaseActivity {
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.contentEditId)
    EditText mContentEdit;
    @InjectView(R.id.receiveLayoutId)
    RelativeLayout mMemberLayout;
    @InjectView(R.id.avatarImage1)
    ImageView mAvatarImage1;
    @InjectView(R.id.avatarImage2)
    ImageView mAvatarImage2;
    @InjectView(R.id.avatarImage3)
    ImageView mAvatarImage3;
    @InjectView(R.id.countMemberTxtId)
    TextView mCountTxt;
    @InjectView(R.id.sendWaygroupId)
    RadioGroup mRadioGroup;

    private static final String TAG = "CreateNoticeActivity";
    private ArrayList<User> mReceiverList = new ArrayList<>();



    @OnClick({R.id.receiveLayoutId})
    void onClick (View view){
        switch (view.getId()){
            case R.id.receiveLayoutId:
                Intent intent = new Intent(CreateNoticeActivity.this, AddReceiverActivity.class);
                startActivityForResult(intent, Constant.ACTIVITY_REQUEST_CODE);
                break;
        }
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_create_notice;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        initToolBar();
    }

    private void initToolBar(){
        mToolbar.setTitle(R.string.create_notice_title);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menuSend:
                        sendNotice();

                        break;
                }
                return true;
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.send_menu,menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.ACTIVITY_REQUEST_CODE
                && resultCode == Constant.CODE_ADD_RECEIVER){
            mReceiverList = (ArrayList<User>)data.getSerializableExtra(Constant.VALUE);
            for (User user :mReceiverList){
                Log.d(TAG," 接收人姓名  " + user.getName());
            }
            int size = mReceiverList.size();
         setRecevierLayout(size);
        }
    }

    private void setRecevierLayout(int size){
        if (size == 0){
            mAvatarImage1.setVisibility(View.INVISIBLE);
            mAvatarImage2.setVisibility(View.INVISIBLE);
            mAvatarImage3.setVisibility(View.INVISIBLE);
        } else if (size == 1){
            mAvatarImage1.setVisibility(View.VISIBLE);
            mAvatarImage2.setVisibility(View.INVISIBLE);
            mAvatarImage3.setVisibility(View.INVISIBLE);
        } else if (size == 2){
            mAvatarImage1.setVisibility(View.VISIBLE);
            mAvatarImage2.setVisibility(View.VISIBLE);
            mAvatarImage3.setVisibility(View.INVISIBLE);
        }else if (size >= 3){
            mAvatarImage1.setVisibility(View.VISIBLE);
            mAvatarImage2.setVisibility(View.VISIBLE);
            mAvatarImage3.setVisibility(View.VISIBLE);
        }
        StringBuilder stringBuilder = new StringBuilder(String.valueOf(size)).append("个");
        mCountTxt.setText(stringBuilder.toString());
    }

    private void sendNotice (){
        if (mContentEdit.getText().toString().isEmpty()){
            ToastUtil.showToast("内容不能为空");
            return;
        }
        if (mReceiverList.isEmpty()){
            ToastUtil.showToast("接收者不能为空");
            return;
        }
        final ArrayList<BmobObject> list = new ArrayList<>();
        User user = BmobUser.getCurrentUser(User.class);
        String content = mContentEdit.getText().toString();
        Date date = new Date();
        int size = mReceiverList.size();
        String id = String.valueOf(date.getTime());
        final Notice notice = new Notice(id,content,user,size);
        notice.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null){
                    updateNoticeMember(notice,list);
                } else {
                    Log.d(TAG, e.getMessage());
                }
            }
        });
    }

    private void updateNoticeMember(Notice notice,ArrayList<BmobObject> list ){
       for (User user: mReceiverList){
           NoticeMember member = new NoticeMember(notice,user,false);
           list.add(member);
       }
        new BmobBatch().insertBatch(list).doBatch(new QueryListListener<BatchResult>() {
            @Override
            public void done(List<BatchResult> list, BmobException e) {
                if(e==null){
                    for(int i=0;i<list.size();i++){
                        BatchResult result = list.get(i);
                        BmobException ex =result.getError();
                        if(ex==null){
                            Log.d(TAG,"第 "+i+" 个数据批量添加成功："+result.getCreatedAt()+","+result.getObjectId()+","+result.getUpdatedAt());
                        }else{
                            Log.d(TAG,"第 "+i+" 个数据批量添加失败："+ex.getMessage()+","+ex.getErrorCode());
                        }
                    }
                    setResult(Constant.CODE_SEND_NOTICE);
                    finish();
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }

        });
    }
}
