package com.lzp.classroomassistant.assistant.notice;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.lzp.classroomassistant.BaseActivity;
import com.lzp.classroomassistant.R;
import com.lzp.classroomassistant.data.Organization;
import com.lzp.classroomassistant.data.User;
import com.lzp.classroomassistant.data.UserOrgan;
import com.lzp.classroomassistant.message.TitleItemDecoration;
import com.lzp.classroomassistant.net.HttpManager;
import com.lzp.classroomassistant.net.ProgressSubscriber;
import com.lzp.classroomassistant.net.SubscriberOnNextListener;
import com.lzp.classroomassistant.util.Constant;
import com.lzp.classroomassistant.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import rx.Observable;

public class AddReceiverActivity extends BaseActivity {

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.receiverRecycler)
    RecyclerView mRecyclerView;

    private int mResult = 0;
    private ArrayList<User> mMemberList = new ArrayList<>();
    private ReceiverListAdapter mListAdapter;
    private TitleItemDecoration mTitleItemDecoration;
    private static final String TAG = "AddReceiverActivity";

    @Override
    protected int getLayoutView() {
        return R.layout.activity_add_receiver;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

        initToolBar();
        loadUserOrgan();
    }

    private void initToolBar(){
        mToolbar.setTitle(R.string.select_receiver);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menuSure:
                        Intent intent = new Intent(AddReceiverActivity.this,CreateNoticeActivity.class);
                        intent.putExtra(Constant.VALUE, mListAdapter.getCheckList());
                        mResult = Constant.CODE_ADD_RECEIVER;
                        setResult(mResult, intent);
                        finish();
                        break;
                }
                return true;
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.suer_menu,menu);
        return true;
    }
    private void loadUserOrgan(){
        BmobQuery<UserOrgan> query = new BmobQuery<>();
        query.include("userName");
        query.addWhereEqualTo("userName", BmobUser.getCurrentUser(User.class));
        query.findObjects(new FindListener<UserOrgan>() {
            @Override
            public void done(List<UserOrgan> list, BmobException e) {
                if (e == null){
                    loadOrgan(list.get(0));
                    appData.setUserOrganList(list);
                } else {
                    ToastUtil.showToast("查询组织出错! " + e.getMessage());
                    Log.d("查询组织出错! ",e.getMessage());
                }
            }
        });
    }

    private void loadOrgan(UserOrgan userOrgan) {
        BmobQuery<Organization> query = new BmobQuery<>();
        query.include("author");
        query.addWhereRelatedTo("organiza",new BmobPointer(userOrgan));
        query.findObjectsObservable(Organization.class);
        query.findObjects(new FindListener<Organization>() {
            @Override
            public void done(List<Organization> list, BmobException e) {
                if (e == null){
                    int size = list.size();

                    for (int i = 0; i < size; i++) {
                        loadMember(list.get(i));
                    }
                    mTitleItemDecoration = new TitleItemDecoration(AddReceiverActivity.this,mMemberList);
                    mListAdapter = new ReceiverListAdapter(mMemberList);
                    mRecyclerView.addItemDecoration(mTitleItemDecoration);
                    mRecyclerView.setAdapter(mListAdapter);
                }else {

                }
            }
        });
    }

    private synchronized void loadMember(final Organization organization){
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereRelatedTo("member", new BmobPointer(organization));
        query.addWhereNotEqualTo("username",BmobUser.getCurrentUser(User.class).getUsername());
        final Observable observable = query.findObjectsObservable(User.class);
        HttpManager.toSubscribe(observable, new ProgressSubscriber<List<User>>(new SubscriberOnNextListener<List<User>>() {
            @Override
            public void onNext(List<User> list) {
                Log.d(TAG, " 组织 " + organization.getName());
                for (User user: list){
                    user.setOrgan(organization.getName());
                    mMemberList.add(user);

                    Log.d(TAG," 成员 " + user.getName());
                }
                mListAdapter.notifyDataSetChanged();
            }
        },this));
    }
}
