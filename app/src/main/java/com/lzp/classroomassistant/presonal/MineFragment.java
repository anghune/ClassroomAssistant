package com.lzp.classroomassistant.presonal;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.lzp.classroomassistant.BaseFragment;
import com.lzp.classroomassistant.R;
import com.lzp.classroomassistant.data.Organization;
import com.lzp.classroomassistant.data.User;
import com.lzp.classroomassistant.data.UserOrgan;
import com.lzp.classroomassistant.login.LoginActivity;
import com.lzp.classroomassistant.net.HttpManager;
import com.lzp.classroomassistant.net.ProgressSubscriber;
import com.lzp.classroomassistant.net.SubscriberOnNextListener;
import com.lzp.classroomassistant.presonal.common.AddMemberActivity;
import com.lzp.classroomassistant.util.AlertDialogUtil;
import com.lzp.classroomassistant.util.Constant;
import com.lzp.classroomassistant.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.newim.BmobIM;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import rx.Observable;

import static com.lzp.classroomassistant.R.id.expandableListView;


public class MineFragment extends BaseFragment implements GroupExpandAdapter.Listener{


    @InjectView(expandableListView)
    ExpandableListView mExpandableListView;
    @InjectView(R.id.userNameTxt)
    TextView mNameTxt;
    @InjectView(R.id.userMumberTxt)
    TextView mMumberTxt;
    @InjectView(R.id.userCollegeTxt)
    TextView mCollegeTxt;

    private ArrayList<ArrayList<User>> mMemberList = new ArrayList<>();
    private ArrayList<Organization> mOrganList = new ArrayList<>();
    private GroupExpandAdapter mGroupExpandAdapter;

    @OnClick({R.id.exitBtn,R.id.creatOrgan})
    void onClick(View view){
        switch (view.getId()){
            case R.id.exitBtn:
                logOut();
                break;
            case R.id.creatOrgan:
                createOrgan();
                break;
        }

    }


    @Override
    protected int getLayoutView() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

        initUserInfo();
        loadUserOrgan();
    }

    private void initUserInfo(){
        User user = BmobUser.getCurrentUser(User.class);
        mNameTxt.setText(String.format(getString(R.string.info_name),user.getName()));
        mMumberTxt.setText(String.format(getString(R.string.info_mumber),user.getUsername()));
        mCollegeTxt.setText(String.format(getString(R.string.info_college),user.getCollege()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.ACTIVITY_REQUEST_CODE
                && resultCode == Constant.CODE_ADD_MEMBER) {
            mMemberList.clear();
            mOrganList.clear();
            loadUserOrgan();

        }
    }

    private void createOrgan(){
        Intent intent = new Intent(getActivity(), AddMemberActivity.class);
        startActivityForResult(intent, Constant.ACTIVITY_REQUEST_CODE);
    }
    private void logOut(){
        User.logOut();
        if (BmobUser.getCurrentUser(User.class) == null){
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
            BmobIM.getInstance().disConnect();
            getActivity().finish();
        }
    }

    private void loadUserOrgan(){
        BmobQuery<UserOrgan> query = new BmobQuery<>();
        query.include("userName");
        query.addWhereEqualTo("userName",BmobUser.getCurrentUser(User.class));
        query.findObjects(new FindListener<UserOrgan>() {
            @Override
            public void done(List<UserOrgan> list, BmobException e) {
                if (e == null){
                    loadOrgan(list.get(0));
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
//                    synchronized (mMemberList) {
                        for (int i = 0; i < size; i++) {
                            loadMember(list.get(i));
                        }
//                    }
                        mGroupExpandAdapter = new GroupExpandAdapter(mMemberList,mOrganList ,MineFragment.this);
                        mExpandableListView.setAdapter(mGroupExpandAdapter);
                }else {

                }
            }
        });
    }

    private synchronized void loadMember(final Organization organization){
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereRelatedTo("member", new BmobPointer(organization));
        Observable observable = query.findObjectsObservable(User.class);
        HttpManager.toSubscribe(observable, new ProgressSubscriber<List<User>>(new SubscriberOnNextListener<List<User>>() {
            @Override
            public void onNext(List<User> list) {
                mMemberList.add((ArrayList<User>) list);
                mOrganList.add(organization);
                mGroupExpandAdapter.notifyDataSetChanged();
            }
        },getActivity()));
    }

    @Override
    public void onClickMore(View view, final int groupPosition) {
        PopupMenu popupMenu = new PopupMenu(getActivity(),view);
        popupMenu.getMenuInflater()
                .inflate(R.menu.group_menu,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.delete:
                        AlertDialogUtil.showDialog(getActivity(), "是否移除该组织", new AlertDialogUtil.AlertDialogListener() {
                            @Override
                            public void sureOnClick() {
                                removeGroup(groupPosition);
                            }
                        });
                        break;
                }
                return true;
            }
        });
        popupMenu.show();
    }

    private void removeGroup(int groupId){
        Organization organization = mOrganList.get(groupId);
        organization.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null){
                    ToastUtil.showToast("删除成功！");
                    mMemberList.clear();
                    mOrganList.clear();
                    loadUserOrgan();
                } else {
                    ToastUtil.showToast("删除失败！" + e.getMessage());
                    Log.d("wang 删除失败", e.getMessage());
                }
            }
        });
    }


}
