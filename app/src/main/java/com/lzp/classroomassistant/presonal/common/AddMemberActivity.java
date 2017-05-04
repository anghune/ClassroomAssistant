package com.lzp.classroomassistant.presonal.common;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.lzp.classroomassistant.BaseActivity;
import com.lzp.classroomassistant.R;
import com.lzp.classroomassistant.data.Organization;
import com.lzp.classroomassistant.data.User;
import com.lzp.classroomassistant.data.UserOrgan;
import com.lzp.classroomassistant.net.HttpManager;
import com.lzp.classroomassistant.net.ProgressSubscriber;
import com.lzp.classroomassistant.net.SubscriberOnNextListener;
import com.lzp.classroomassistant.util.AlertDialogUtil;
import com.lzp.classroomassistant.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;
import rx.Observable;

public class AddMemberActivity extends BaseActivity implements MemberListAdapter.Listener{
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.allUserRecycler)
    RecyclerView mRecyclerView;

    private MemberListAdapter mMemberListAdapter;
    private int mResultCode = 0;
    private UserOrgan mCurrUserOrgan;
    @Override
    protected int getLayoutView() {
        return R.layout.activity_add_member;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

        initToolbar();
        loadUserData();

    }
    private void initToolbar(){
        mToolbar.setTitle(R.string.add_member);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menuSure:
                        getCreateOrgan();
                        break;
                }
                return false;
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

    }

    private void getCreateOrgan(){
        final LayoutInflater inflater = LayoutInflater.from(this);
        final View v = inflater.inflate(R.layout.layout_input_dialog,null );
        final EditText editText = (EditText) v.findViewById(R.id.et_input_dialog);
        AlertDialogUtil.showDialog(this,v,"请输入组织名称" ,new AlertDialogUtil.AlertDialogListener() {
            @Override
            public void sureOnClick() {
                final String content = editText.getText().toString();
                final Organization organization = new Organization();
                organization.setName(content);
                BmobRelation relation = new BmobRelation();
                final int size = mMemberListAdapter.getCheckList().size();
                for (int i = 0; i < size; i++){
                    relation.add(mMemberListAdapter.getCheckList().get(i).getUserName());
                }
                relation.add(BmobUser.getCurrentUser(User.class));
                organization.setMember(relation);
                organization.setAuthor(BmobUser.getCurrentUser(User.class));
                final Observable observable = organization.saveObservable();
                HttpManager.toSubscribe(observable,new ProgressSubscriber<String>(new SubscriberOnNextListener<String>() {
                    @Override
                    public void onNext(String s) {
                        addOrgan(organization);
                    }
                }));
            }
        });
    }

    private void addOrgan(Organization organization){
        BmobRelation relation = new BmobRelation();
        relation.add(organization);
        ArrayList<BmobObject> list = new ArrayList<>();
        for (UserOrgan userOrgan: mMemberListAdapter.getCheckList()){
            userOrgan.setOrganiza(relation);
            list.add(userOrgan);
        }
        mCurrUserOrgan.setOrganiza(relation);
        list.add(mCurrUserOrgan);

        new BmobBatch().updateBatch(list).doBatch(new QueryListListener<BatchResult>() {
            @Override
            public void done(List<BatchResult> list, BmobException e) {
                if (e == null){
                    int size = list.size();
                    for(int i=0;i<size;i++){
                        BatchResult result = list.get(i);
                        BmobException ex =result.getError();
                        if(ex==null){
                            Log.d("wang","第"+i+"个数据批量添加成功");
                        }else{
                            Log.d("wang","第"+i+"个数据批量添加失败："+ex.getMessage()+","+ex.getErrorCode());
                        }
                    }
                    ToastUtil.showToast("创建成功！");
                    mMemberListAdapter.clearCheckList();
                    mResultCode = 104;
                    setResult(mResultCode);
                    finish();
                } else {
                    Log.i("wang","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(mResultCode);
    }

    private void loadUserData(){
        BmobQuery<UserOrgan> query = new BmobQuery<>();
        query.include("userName");
        query.addWhereNotEqualTo("userName",BmobUser.getCurrentUser(User.class));
        Observable observable = query.findObjectsObservable(UserOrgan.class);
        HttpManager.toSubscribe(observable,new ProgressSubscriber<List<UserOrgan>>(new SubscriberOnNextListener<List<UserOrgan>>() {
            @Override
            public void onNext(List<UserOrgan> list) {

                mMemberListAdapter = new MemberListAdapter(list,AddMemberActivity.this);
                mRecyclerView.setAdapter(mMemberListAdapter);

            }
        },this));
        BmobQuery<UserOrgan> query2 = new BmobQuery<>();
        query2.include("userName");
        query2.addWhereEqualTo("userName",BmobUser.getCurrentUser(User.class));
        query2.findObjects(new FindListener<UserOrgan>() {
            @Override
            public void done(List<UserOrgan> list, BmobException e) {
                if (e == null){
                    mCurrUserOrgan = list.get(0);
                } else {
                    Log.d("wang"," 请求失败");
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.suer_menu,menu);
        return true;
    }

    @Override
    public void onClick(View view, int pos) {



    }

    @Override
    public void onLongClick(View view, final int pos) {
//        UserOrgan userOrgan = new UserOrgan();
//        userOrgan.setUserName(appData.getUserList().get(pos));
//        userOrgan.save(new SaveListener<String>() {
//            @Override
//            public void done(String s, BmobException e) {
//                if (e == null){
//                    ToastUtil.showToast(" 关联成功 " + appData.getUserList().get(pos).getName());
//                }
//            }
//        });
    }
}
