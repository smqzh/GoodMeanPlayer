package com.gm.player.ui.fragment;

import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.gm.player.R;
import com.gm.player.db.DownLoadFiles;
import com.gm.player.util.FileUtil;
import com.gm.player.util.YcToast;

import org.litepal.crud.DataSupport;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AboutFragment extends BaseFragment {

    @BindView(R.id.line_clear)
    LinearLayout lineClear;
    Unbinder unbinder;
    private String TAG = this.getClass().getSimpleName();
    public static final String ARG_MAR = "ARG_MAR";

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_about;
    }


    public static AboutFragment newInstance(String key) {
        AboutFragment contentFragment = new AboutFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_MAR, key);
        contentFragment.setArguments(bundle);
        return contentFragment;
    }


    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        unbinder = ButterKnife.bind(this, mRootView);
        lineClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataSupport.deleteAll(DownLoadFiles.class);
                String path= Environment.getExternalStorageDirectory().getAbsolutePath()+"/GoodMeanPlayer/";
                File file=new File(path);
                if(file.exists()){
                    FileUtil.deleteAllFiles(file);
                        YcToast.get().toast("已清空缓存");
                }else{
                    YcToast.get().toast("文件不存在");
                }
            }
        });
    }

    @Override
    protected String setTagName() {
        return "关于";
    }

}
