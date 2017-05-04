package com.lzp.classroomassistant.assistant.courseware;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.lzp.classroomassistant.BaseActivity;
import com.lzp.classroomassistant.R;

import butterknife.InjectView;

public class AddFileActivity extends BaseActivity{

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected int getLayoutView() {
        return R.layout.activity_add_file;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

        initToolBar();
    }

    private void initToolBar(){
        mToolbar.setTitle(R.string.add_file_act_title);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menuSure:
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.suer_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
}
