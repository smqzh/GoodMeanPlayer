package com.gm.player.ui.fragment;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.gm.player.R;
import com.gm.player.db.DownLoadFiles;
import com.gm.player.ui.adapter.DownloadListAdapter;
import com.learning.downloader.FilesDownloadService;
import com.learning.downloader.callback.DownloadManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * Created by Konstantin on 22.12.2014.
 */
public class DownLoadFragment extends BaseFragment {
    @BindView(R.id.rlv_down)
    RecyclerView rlvDown;
    @BindView(R.id.tv_download_number)
    TextView tvDownloadNumber;
    private String TAG = this.getClass().getSimpleName();
    public static final String ARG_MAR = "ARG_MAR";
    Unbinder unbinder;
    private View containerView;
    protected String res;
    private Bitmap bitmap;
    private DownloadListAdapter adapter;
    private DownloadManager downloadManager;

    public static DownLoadFragment newInstance(String key) {
        DownLoadFragment downLoadFragment = new DownLoadFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_MAR, key);
        downLoadFragment.setArguments(bundle);
        return downLoadFragment;
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
        return R.layout.fragment_download;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        unbinder = ButterKnife.bind(this, mRootView);
        init();
    }

    @Override
    protected String setTagName() {
        return "下载";
    }

    private void init() {
        initView();
        initData();
    }

    private void initView() {
        adapter = new DownloadListAdapter(getActivity());
        rlvDown.setLayoutManager(new LinearLayoutManager(getActivity()));
        rlvDown.setAdapter(adapter);
        downloadManager = FilesDownloadService.getDownloadManager(getActivity().getApplicationContext());
    }

    private void initData() {
        List<DownLoadFiles> downLoadFiles = DownLoadFiles.queryFiles();
        adapter.setData(downLoadFiles);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}

