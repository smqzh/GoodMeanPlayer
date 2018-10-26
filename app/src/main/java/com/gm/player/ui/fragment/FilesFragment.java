package com.gm.player.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gm.player.R;
import com.gm.player.bean.FileInfo;
import com.gm.player.ui.activity.FileDisplayActivity;
import com.gm.player.ui.activity.ImageActivity;
import com.gm.player.ui.activity.VideoActivity;
import com.gm.player.ui.adapter.RecyGridAdapter;
import com.gm.player.util.Constant;
import com.gm.player.util.FileUtil;
import com.gm.player.util.YcToast;
import com.gm.player.widget.GridSpacingItemDecoration;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.xm.weidongjian.progressbuttonlib.ProgressButton;

import static com.gm.player.util.Tools.isBuild;
import static com.gm.player.util.Tools.isBuildN;


/**
 * Created by Konstantin on 22.12.2014.
 */
public class FilesFragment extends BaseFragment  {
    @BindView(R.id.spr_type)
    Spinner sprType;
    @BindView(R.id.btn_file_scan)
    ProgressButton btnFileScan;
//    @BindView(R.id.et_file_scan)
//    EditText etFileScan;
//    @BindView(R.id.btn_file_search)
//    ProgressButton btnFileSearch;
    @BindView(R.id.rlv_file)
    RecyclerView mRlvFile;

    @BindView(R.id.avl_loading)
    AVLoadingIndicatorView mAvl;

    private RecyGridAdapter mAdapter;
    private List<FileInfo> mList = new ArrayList<>();
    private String select="";
    private  String Urlpicture= "";
    private String Urloffice="";
    private String Urlvideo="";
    private String TAG = this.getClass().getSimpleName();
    public static final String ARG_MAR = "ARG_MAR";
    Unbinder unbinder;
    private View containerView;
    protected String res;
    private Bitmap bitmap;
    private Context mContext;
    public static FilesFragment newInstance(String key) {
        FilesFragment filesFragment = new FilesFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_MAR, key);
        filesFragment.setArguments(bundle);
        return filesFragment;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.containerView = view.findViewById(R.id.container);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            res = getArguments().getString(ARG_MAR);
        }
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_file;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        unbinder = ButterKnife.bind(this, mRootView);
        init();
    }

    @Override
    protected String setTagName() {
        return "文件";
    }

    private void init() {
        initData();
        // KLog.d("----SSSSSS---"+mParam1);
        int spanCount = 5;//跟布局里面的spanCount属性是一致的
        int spacing = 5;//每一个矩形的间距
        boolean includeEdge = true;//如果设置成false那边缘地带就没有间距
        //设置每个item间距
        mRlvFile.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
        // getList();
        mAdapter = new RecyGridAdapter(getActivity(), mList);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                Log.d(TAG, "onItemChildClick: -----"+position);
                FileInfo fi=mList.get(position);
                if(select.equals("图片")) {
                    Intent intent = new Intent(getActivity(), ImageActivity.class);
                    intent.putExtra("path", fi.getUrl());
                    startActivity(intent);
                }else if(select.equals("文档")){
                    Intent intent = new Intent(getActivity(), FileDisplayActivity.class);
                    intent.putExtra("path", fi.getUrl());
                    startActivity(intent);
                }else if(select.equals("视频")){
                    Intent intent = new Intent(getActivity(), VideoActivity.class);
                    intent.putExtra("path", fi.getUrl());
                    startActivity(intent);
                }
                getActivity().overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            }
        });
        mRlvFile.setAdapter(mAdapter);
    }


    private void initData() {

        String[] mItems = getResources().getStringArray(R.array.fileType);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, mItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //绑定 Adapter到控件
        sprType.setAdapter(adapter);
        sprType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                String[] languages = getResources().getStringArray(R.array.fileType);
                select= languages[pos];
             //   Toast.makeText(getActivity(), "你点击的是:" + languages[pos], Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
    }


    @OnClick({R.id.btn_file_scan})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_file_scan:
                btnFileScan.startRotate();
               // btnFileScan.removeDrawable();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnFileScan.animFinish();
                        isBuildN();
                        new LoadFileAsyncTask().execute(select);
                    }
                },1500);
                break;
        }
    }

    private class LoadFileAsyncTask extends AsyncTask<String,Void,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mAvl.setVisibility(View.VISIBLE);

        }

        @Override
        protected Void doInBackground(String... strings) {
            mList.clear();
            String str=strings[0];
            if(str.equals("图片")){
                refreshFileList(Constant.picturepath,0);
            }
            else if(str.equals("视频")){
                refreshFileList(Constant.videopath,5);
            }
            else if(str.equals("文档")){
                refreshFileList(Constant.officepath,3);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mAdapter.notifyDataSetChanged();
            mAvl.setVisibility(View.GONE);
            int size =mList.size();
            if(size==0){
                YcToast.get().toast("当前暂无该类型文件");
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    btnFileScan.removeDrawable();
                }
            },500);
        }
    }


    public String refreshFileList(String strPath,int type) {
        Log.d(TAG, "refreshFileList: ---"+strPath);
        String destination="";
        File dir = new File(strPath);//文件夹dir
        File[] files = dir.listFiles();//文件夹下的所有文件或文件夹
        System.out.println("----------------------1");
        if (files == null)
            return null;
           System.out.println("----------------------2");
        for (int i = 0; i < files.length; i++) {

            if (files[i].isDirectory())
            {
                refreshFileList(files[i].getAbsolutePath(),type);//递归文件夹！！！
            }
            else {
                String  filepth = files[i].getAbsolutePath();
                if(type==0) {
                    if (FileUtil.isPicTure(filepth))//判断是不是图片
                    {
                        System.out.println("refreshFileList: -------" + filepth);
                        mList.add(new FileInfo(filepth, files[i].getName(), type));
                    }
                }else if(type==5){
                    if (FileUtil.isVideo(filepth))//判断是不是视频
                    {
                        System.out.println("refreshFileList: -------" + filepth);
                        mList.add(new FileInfo(filepth, files[i].getName(), type));
                    }
                }else{

                    if (FileUtil.isOffice(filepth))//判断是不是文档
                    {
                        Log.d(TAG, "refreshFileList: -------" + filepth);
                        String filename= files[i].getName();
                        if(filename.contains("doc")||filename.contains("docx")){
                            mList.add(new FileInfo(filepth, files[i].getName(), 1));
                        }
                        else if(filename.contains("ppt")||filename.contains("pptx")){
                            mList.add(new FileInfo(filepth, files[i].getName(), 2));
                        }
                        else if(filename.contains("pdf")||filename.contains("PDF")){
                            mList.add(new FileInfo(filepth, files[i].getName(), 3));
                        }
                        else if(filename.contains("xls")||filename.contains("xlsx")){
                            mList.add(new FileInfo(filepth, files[i].getName(), 4));
                        }
                    }

                }
            }
        }
        return destination;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}

