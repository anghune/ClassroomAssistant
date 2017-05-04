package com.lzp.classroomassistant.assistant.courseware;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.lzp.classroomassistant.BaseActivity;
import com.lzp.classroomassistant.R;
import com.lzp.classroomassistant.util.Constant;

import butterknife.InjectView;

public class FileActivity extends BaseActivity {

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.refreshId)
    SwipeRefreshLayout mRefreshLayout;
    @InjectView(R.id.recyclerId)
    RecyclerView mRecyclerView;



    @Override
    protected int getLayoutView() {
        return R.layout.activity_file;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        initToolBar();
    }

    private void initToolBar () {
        mToolbar.setTitle(R.string.file_activity_title);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menuAdd:
                        Intent intent = new Intent(FileActivity.this,AddFileActivity.class);
                        startActivityForResult(intent, Constant.ACTIVITY_REQUEST_CODE);
                        break;
                }
                return false;
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(FileActivity.this,LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notice_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
}
