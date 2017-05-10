package com.lzp.classroomassistant.assistant.courseware;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.Toast;

import com.lzp.classroomassistant.BaseActivity;
import com.lzp.classroomassistant.R;
import com.lzp.classroomassistant.data.Courseware;
import com.lzp.classroomassistant.data.User;
import com.lzp.classroomassistant.util.Constant;
import com.lzp.classroomassistant.util.PopupMenuUtil;
import com.lzp.classroomassistant.util.ToastUtil;
import com.lzp.classroomassistant.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class FileActivity extends BaseActivity implements FileListAdapter.Listener{

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.refreshId)
    SwipeRefreshLayout mRefreshLayout;
    @InjectView(R.id.recyclerId)
    RecyclerView mRecyclerView;
    @InjectView(R.id.linearLayout)
    LinearLayout mLinearLayout;

    private static final String TAG = "FileActivity";
    private FileListAdapter mFileListAdapter;
    private ArrayList<Courseware> mCoursewares = new ArrayList<>();
    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener;

    @Override
    protected int getLayoutView() {
        return R.layout.activity_file;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        initToolBar();
        initSwipeLayout();
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
                        showFileChooser();
                        break;
                }
                return false;
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(FileActivity.this,LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(layoutManager);
        mFileListAdapter = new FileListAdapter(mCoursewares,this);
        mRecyclerView.setAdapter(mFileListAdapter);
    }

    private void initSwipeLayout() {
        mRefreshLayout.setEnabled(true);
        mRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mLinearLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mLinearLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mRefreshLayout.setRefreshing(true);
                //自动刷新
                loadData();
            }
        });
        //下拉加载
        mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mFileListAdapter.notifyDataSetChanged();
                loadData();
            }
        };
        mRefreshLayout.setOnRefreshListener(mOnRefreshListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notice_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        String[] mimetypes = {"application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/msword",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "application/vnd.ms-excel",
                "application/vnd.openxmlformats-officedocument.presentationml.presentation", "application/vnd.ms-powerpoint",
                "application/zip", "application/pdf"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);

        try {
            startActivityForResult( Intent.createChooser(intent, "请选择文件"), Constant.ACTIVITY_REQUEST_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a File Manager.",  Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.ACTIVITY_REQUEST_CODE
                && resultCode == RESULT_OK){
            // Get the Uri of the selected file
            Uri uri = data.getData();
            String[] info = Utils.getPath(this, uri);
            uploadFile(info);
            for (int i = 0; i < 3; i++){
                Log.i(TAG," 文件路径 ： " + info[i]);
            }
        }
    }

    private void uploadFile(String[] strings){
        String path = strings[0];
        String name = strings[1];
        final String size = strings[2];

        final BmobFile bmobFile = new BmobFile(new File(path));
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if(e == null){
                    //bmobFile.getFileUrl()--返回的上传文件的完整地址
                    Courseware courseware = new Courseware(bmobFile.getFilename(),size,
                            bmobFile.getFileUrl(), BmobUser.getCurrentUser(User.class));
                    saveFileInfo(courseware);
                }else{
                    Log.i(TAG,"上传文件失败：" + e.getMessage());
                }
            }
            @Override
            public void onProgress(Integer value) {
                Log.d(TAG," 进度 " + value);
                if (value.intValue() == 100){
                    ToastUtil.showToast("上传文件成功!!" );
                }
            }
        });
    }

    private void saveFileInfo(Courseware courseware){
        courseware.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null){
                    ToastUtil.showToast("上传成功！");
                    autoRefresh();
                } else {
                    Log.i(TAG,e.getMessage());
                }
            }
        });
    }
    private void autoRefresh(){
        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
            }
        });
        mOnRefreshListener.onRefresh();
    }
    private void loadData(){
        BmobQuery<Courseware> query = new BmobQuery<>();
        query.include("author");
        query.addWhereEqualTo("author",new BmobPointer(BmobUser.getCurrentUser(User.class)));
        query.findObjects(new FindListener<Courseware>() {
            @Override
            public void done(List<Courseware> list, BmobException e) {
                if (e == null){
                    mCoursewares = (ArrayList<Courseware>) list;
                    mFileListAdapter.replaceData((ArrayList<Courseware>) list);
                    mRefreshLayout.setRefreshing(false);
                } else {
                    Log.d(TAG, e.getMessage());
                }
            }
        });
    }

    @Override
    public void onClickMore(View view, final int pos) {
        PopupMenuUtil.showPopupMenu(R.menu.file_menu, this, view, new PopupMenuUtil.ItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.delete:
                        delete(pos);
                        break;
                    case R.id.download:
                        downloadFile(pos);
                        break;
                }
                return true;
            }
        });
//        PopupMenu popupMenu = new PopupMenu(this,view);
//        popupMenu.getMenuInflater()
//                .inflate(R.menu.file_menu,popupMenu.getMenu());
//        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                switch (item.getItemId()){
//                    case R.id.delete:
//                        delete(pos);
//                        break;
//                    case R.id.download:
//                        downloadFile(pos);
//                        break;
//                }
//                return true;
//            }
//        });
//        popupMenu.show();
    }

    private void delete(int pos){
        final Courseware courseware = mCoursewares.get(pos);
        final Courseware deler = new Courseware(courseware.getName(),courseware.getSize(),courseware.getPath(),courseware.getAuthor());
        deler.setObjectId(courseware.getObjectId());
        BmobFile file = new BmobFile();
        file.setUrl(courseware.getPath());//此url是上传文件成功之后通过bmobFile.getUrl()方法获取的。
        file.delete(new UpdateListener() {

            @Override
            public void done(BmobException e) {
                if(e==null){
                    deler.delete(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null){
                                ToastUtil.showToast("文件删除成功");
                                autoRefresh();
                            } else {
                                Log.e(TAG,e.getMessage());
                            }
                        }
                    });
                }else{
                    Log.d(TAG, "文件删除失败："+e.getErrorCode()+","+e.getMessage());
                }
            }
        });
    }

    private void downloadFile(int pos){
        Courseware courseware = mCoursewares.get(pos);
        BmobFile file = new BmobFile(courseware.getName(),"",courseware.getPath());
        //允许设置下载文件的存储路径，默认下载文件的目录为：context.getApplicationContext().getCacheDir()+"/bmob/"
        File saveFile = new File(Environment.getExternalStorageDirectory(), file.getFilename());
        file.download(saveFile, new DownloadFileListener() {

            @Override
            public void onStart() {
                ToastUtil.showToast("开始下载...");
            }

            @Override
            public void done(String savePath,BmobException e) {
                if(e==null){
                    ToastUtil.showToast("下载成功,保存路径:"+savePath);
                }else{
                    Log.e(TAG,"下载失败："+e.getErrorCode()+","+e.getMessage());
                }
            }

            @Override
            public void onProgress(Integer value, long newworkSpeed) {
                Log.i("bmob","下载进度："+value+","+newworkSpeed);
                if (value == 100){
                    ToastUtil.showToast("下载成功,保存路径");
                }
            }

        });
    }

    @Override
    public void onLongClick(View view, int pos) {
//        final Courseware courseware = mCoursewares.get(pos);
//        final Courseware deler = new Courseware(courseware.getName(),courseware.getSize(),courseware.getPath(),courseware.getAuthor());
//        deler.setObjectId(courseware.getObjectId());
//        deler.delete(new UpdateListener() {
//            @Override
//            public void done(BmobException e) {
//                if (e == null){
//                    ToastUtil.showToast("文件删除成功");
//                    autoRefresh();
//                } else {
//                    Log.e(TAG,e.getMessage());
//                }
//            }
//        });
    }
}
