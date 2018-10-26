package com.gm.player.ui.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gm.player.R;
import com.gm.player.bean.FileInfo;

import java.io.File;
import java.util.List;

public class RecyGridAdapter extends BaseQuickAdapter<FileInfo,BaseViewHolder> {

    private Context mContext;
    public RecyGridAdapter(Context mContext, @Nullable List<FileInfo> data) {
        super(R.layout.item_file_info ,data);
        this.mContext=mContext;
    }

    @Override
    protected void convert(BaseViewHolder helper, FileInfo item) {
        helper.setText(R.id.tv_video_info,item.getuName());
        int type=item.getType();
        if(type==0||type==5) {
            Glide.with(mContext).load(new File(item.getUrl()))
                    .override(180, 160)
                    .centerCrop()
                    .placeholder(R.drawable.icon_pic)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into((ImageView) helper.getView(R.id.iv_info));
        }else if(type==1){
            Glide.with(mContext).load(R.drawable.icon_word)
                    .override(180, 160)
                    .centerCrop()
                    .placeholder(R.drawable.icon_word)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into((ImageView) helper.getView(R.id.iv_info));
        }
        else if(type==2){
            Glide.with(mContext).load(R.drawable.icon_ppt)
                    .override(180, 160)
                    .centerCrop()
                    .placeholder(R.drawable.icon_ppt)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into((ImageView) helper.getView(R.id.iv_info));
        }
        else if(type==3){
            Glide.with(mContext).load(R.drawable.icon_pdf)
                    .override(180, 160)
                    .centerCrop()
                    .placeholder(R.drawable.icon_pdf)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into((ImageView) helper.getView(R.id.iv_info));
        }
        else if(type==4){
            Glide.with(mContext).load(R.drawable.icon_xls)
                    .override(180, 160)
                    .centerCrop()
                    .placeholder(R.drawable.icon_xls)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into((ImageView) helper.getView(R.id.iv_info));
        }
    }
}
