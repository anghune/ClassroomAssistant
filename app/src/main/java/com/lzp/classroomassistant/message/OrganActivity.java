package com.lzp.classroomassistant.message;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.lzp.classroomassistant.BaseActivity;
import com.lzp.classroomassistant.R;
import com.lzp.classroomassistant.data.Organization;
import com.lzp.classroomassistant.data.User;
import com.lzp.classroomassistant.data.UserOrgan;
import com.lzp.classroomassistant.net.HttpManager;
import com.lzp.classroomassistant.net.ProgressSubscriber;
import com.lzp.classroomassistant.net.SubscriberOnNextListener;
import com.lzp.classroomassistant.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import rx.Observable;

public class OrganActivity extends BaseActivity implements MemberListAdapter.Listener {
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.organRecyclerId)
    RecyclerView mOrganRecycler;

    private ArrayList<User> mMemberList = new ArrayList<>();
    private MemberListAdapter mListAdapter;
    private BmobIMUserInfo info;
    private TitleItemDecoration mTitleItemDecoration;
    private static final String TAG = "OrganActivity";



    @Override
    protected int getLayoutView() {
        return R.layout.activity_organ;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

        initToolBar();
        loadUserOrgan();
    }

    private void initToolBar(){
        mToolbar.setTitle(R.string.contacts_title);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mOrganRecycler.setLayoutManager(layoutManager);
        mOrganRecycler.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

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
                    mTitleItemDecoration = new TitleItemDecoration(OrganActivity.this,mMemberList);
                    mListAdapter = new MemberListAdapter(mMemberList,OrganActivity.this);
                    mOrganRecycler.addItemDecoration(mTitleItemDecoration);
                    mOrganRecycler.setAdapter(mListAdapter);
                }else {

                }
            }
        });
    }

    private synchronized void loadMember(final Organization organization){
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereRelatedTo("member", new BmobPointer(organization));
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

    @Override
    public void itemOnClick(View view, int pos) {
            User user = mMemberList.get(pos);
        if (user.getUsername().equals(BmobUser.getCurrentUser(User.class).getUsername())){
            ToastUtil.showToast("不能和自己聊天");
            return;
        }
        info = new BmobIMUserInfo(user.getObjectId(),user.getUsername(),user.getAvatar());
        //启动一个会话，设置isTransient设置为false,则会在本地数据库的会话列表中先创建（如果没有）与该用户的会话信息，且将用户信息存储到本地的用户表中
        BmobIMConversation c = BmobIM.getInstance().startPrivateConversation(info,false,null);
        c.setDraft(user.getName());
        Log.d(TAG," getName == " + user.getName());
        Bundle bundle = new Bundle();
        bundle.putSerializable("c", c);
        startActivity(ChatActivity.class, bundle, false);
        finish();
    }
}
