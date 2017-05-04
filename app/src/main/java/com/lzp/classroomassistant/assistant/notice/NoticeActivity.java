package com.lzp.classroomassistant.assistant.notice;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.lzp.classroomassistant.BaseActivity;
import com.lzp.classroomassistant.R;
import com.lzp.classroomassistant.data.Notice;
import com.lzp.classroomassistant.data.NoticeMember;
import com.lzp.classroomassistant.data.User;
import com.lzp.classroomassistant.util.Constant;
import com.lzp.classroomassistant.util.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class NoticeActivity extends BaseActivity implements NoticeListAdapter.Listener{
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.recyclerId)
    RecyclerView mRecyclerView;
    @InjectView(R.id.refreshId)
    SwipeRefreshLayout sw_refresh;
    @InjectView(R.id.containerLayout)
    LinearLayout mContainerLayout;

    private NoticeListAdapter mNoticeListAdapter;
    private ArrayList<NoticeMember> mNotices = new ArrayList<>();
    private static final String TAG = "NoticeActivity";
    private SwipeRefreshLayout.OnRefreshListener mRefreshListener;

    @Override
    protected int getLayoutView() {
        return R.layout.activity_notice;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

        initToolBar();
        initSwipeLayout();
    }

    private void initToolBar(){
        mToolbar.setTitle(R.string.assistant_notice);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menuAdd:
                        Intent intent = new Intent(NoticeActivity.this, CreateNoticeActivity.class);
                        startActivityForResult(intent, Constant.ACTIVITY_REQUEST_CODE);
                        break;
                }
                return true;
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mNoticeListAdapter = new NoticeListAdapter(mNotices,NoticeActivity.this);
        mRecyclerView.setAdapter(mNoticeListAdapter);

    }
    private void initSwipeLayout() {
        sw_refresh.setEnabled(true);
        sw_refresh.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mContainerLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mContainerLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                sw_refresh.setRefreshing(true);
                //自动刷新
                loadData();
            }
        });
        //下拉加载
        mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mNotices.clear();
                mNoticeListAdapter.notifyDataSetChanged();
                loadData();
            }
        };
        sw_refresh.setOnRefreshListener(mRefreshListener);
    }

    private void loadData() {
        User user = BmobUser.getCurrentUser(User.class);
        BmobQuery<NoticeMember> query = new BmobQuery<>();
        query.include("notice,member,notice.author");
        query.addWhereEqualTo("member",new BmobPointer(user));
        query.findObjects(new FindListener<NoticeMember>() {
            @Override
            public void done(List<NoticeMember> list, BmobException e) {
                if (e == null){
                    mNotices.addAll(list);
                    sw_refresh.setRefreshing(false);
                    for (NoticeMember noticeMember : list){
                        Log.d(TAG," 时间 "+ pareDateString(noticeMember.getNotice().getNoticeId())+"  内容 "+ noticeMember.getNotice().getContent()+ "  发布者 " + noticeMember.getNotice().getAuthor().getName()
                        +" isConFirm " + noticeMember.isConfirm() );
                    }
                } else {
                    Log.d(TAG, e.getMessage());
                }
            }
        });
        BmobQuery<Notice> query2 = new BmobQuery<>();
        query2.include("author");
        query2.addWhereEqualTo("author",new BmobPointer(user));
        query2.findObjects(new FindListener<Notice>() {
            @Override
            public void done(List<Notice> list, BmobException e) {
                if (e == null){
                    for (Notice notice: list){
                        queryCount(notice);
                    }
                    mNoticeListAdapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG,e.getMessage());
                }
            }
        });
    }

    private void queryCount(final Notice notice){
        BmobQuery<NoticeMember> query = new BmobQuery<>();
        query.include("notice");
        query.addWhereEqualTo("notice",new BmobPointer(notice));
        query.addWhereEqualTo("isConfirm",true);
        query.count(NoticeMember.class, new CountListener() {
            @Override
            public void done(Integer integer, BmobException e) {
                if (e == null){
                    boolean isConfirm = false;
                if (integer == notice.getCount()){
                    isConfirm = true;
                }
                NoticeMember member = new NoticeMember(notice,null,isConfirm);
                member.setCount(notice.getCount() - integer);
                mNotices.add(member);
                    Log.d(TAG," 自己发布的 " + member.getNotice().getContent() + " 时间 "
                            + pareDateString(member.getNotice().getNoticeId()) + " 人数  "
                            + member.getNotice().getCount());
                mNoticeListAdapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG,e.getMessage());
                }
            }
        });
    }



    private String pareDateString(String s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        long lt = new Long(s);
        java.util.Date date = new java.util.Date(lt);
        res = simpleDateFormat.format(date).replace("-","/");
        return res;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notice_menu,menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.ACTIVITY_REQUEST_CODE
                && resultCode == Constant.CODE_SEND_NOTICE){
            sw_refresh.post(new Runnable() {
                @Override
                public void run() {
                    sw_refresh.setRefreshing(true);
                }
            });
            mRefreshListener.onRefresh();
            Log.i(TAG," 需要刷新消息了！");
        }
    }

    @Override
    public void itemOnClick(View view, int pos) {
        NoticeMember noticeMember = mNotices.get(pos);
        NoticeMember member = new NoticeMember(noticeMember.getNotice(),noticeMember.getMember(),true);
        if (noticeMember.getMember() != null && !noticeMember.isConfirm()){
            Log.i(TAG,  "objectId  "+ noticeMember.getObjectId());
            member.update(noticeMember.getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null){
                        mNotices.clear();
                        ToastUtil.showToast("更新成功");
                        loadData();
                    } else {
                        Log.i(TAG,e.getMessage());
                        Log.i(TAG, e.toString());
                    }
                }
            });
        }
    }
}
