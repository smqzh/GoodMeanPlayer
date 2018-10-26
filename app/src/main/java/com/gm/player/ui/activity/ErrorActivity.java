package com.gm.player.ui.activity;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gm.player.R;
import com.gm.player.application.MySp;
import com.gm.player.base.BaseActivity;
import com.gm.player.util.Constant;
import com.gm.player.util.FileUtil;
import com.gm.player.util.ThreadPoolManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import cat.ereza.customactivityoncrash.config.CaocConfig;

/**
 * @author ling_cx
 * @version 1.0
 * @Description 错误页
 * @date 2017/8/10.
 * @Copyright: 2018 www.kind.com.cn Inc. All rights reserved.
 */
public class ErrorActivity extends BaseActivity {
	@BindView(R.id.customactivityoncrash_error_activity_image)
    ImageView mCustomactivityoncrashErrorActivityImage;
	@BindView(R.id.customactivityoncrash_error_activity_restart_button)
    Button mCustomactivityoncrashErrorActivityRestartButton;
	@BindView(R.id.customactivityoncrash_error_activity_more_info_button)
    Button mCustomactivityoncrashErrorActivityMoreInfoButton;
	@BindView(R.id.customactivityoncrash_error_activity_tell_us_button)
    Button mCustomactivityoncrashErrorActivityTellUsButton;


	@Override
	protected int attachLayoutRes() {
		return R.layout.activity_error;
	}

	@Override
	protected void initViews() {
		getWindow().getDecorView().setRotation(MySp.getRotation());
	}

	protected void initData() {
		//获取配置信息
		final CaocConfig config = CustomActivityOnCrash.getConfigFromIntent(getIntent());
		if (config.isShowRestartButton() && config.getRestartActivityClass()!=null) {
			mCustomactivityoncrashErrorActivityRestartButton.setText(R.string.customactivityoncrash_error_activity_restart_app);
			mCustomactivityoncrashErrorActivityRestartButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					CustomActivityOnCrash.restartApplication(ErrorActivity.this, config);
				}
			});
		} else {
			mCustomactivityoncrashErrorActivityRestartButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					CustomActivityOnCrash.closeApplication(ErrorActivity.this, config);
				}
			});
		}

		/** Show Error detail */
		if (config.isShowErrorDetails()) {
			mCustomactivityoncrashErrorActivityMoreInfoButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					AlertDialog dialog = new AlertDialog.Builder(ErrorActivity.this)
							.setTitle(R.string.customactivityoncrash_error_activity_error_details_title)
							.setMessage(CustomActivityOnCrash.getAllErrorDetailsFromIntent(ErrorActivity.this, getIntent()))
							.setPositiveButton(R.string.customactivityoncrash_error_activity_error_details_close, null)
							.setNeutralButton(R.string.customactivityoncrash_error_activity_error_details_copy,
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											copyErrorToClipboard();
											Toast.makeText(ErrorActivity.this, R.string.customactivityoncrash_error_activity_error_details_copied, Toast.LENGTH_SHORT).show();
										}
									})
							.show();
					TextView textView = (TextView) dialog.findViewById(android.R.id.message);
					textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.customactivityoncrash_error_activity_error_details_text_size));
				}
			});
		} else {
			mCustomactivityoncrashErrorActivityMoreInfoButton.setVisibility(View.GONE);
		}

		Integer defaultErrorActivityDrawableId = config.getErrorDrawable();
		ImageView errorImageView = ((ImageView) findViewById(R.id.customactivityoncrash_error_activity_image));

		if (defaultErrorActivityDrawableId != null) {
			errorImageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), defaultErrorActivityDrawableId, getTheme()));
		}
		String errorInformation = CustomActivityOnCrash.getAllErrorDetailsFromIntent(ErrorActivity.this, getIntent());
		ThreadPoolManager.getInstance().execute(new RunErrorLog(errorInformation));
	}

	/**
	 * 将错误信息复制到剪贴板
	 */
	private void copyErrorToClipboard() {
		String errorInformation = CustomActivityOnCrash.getAllErrorDetailsFromIntent(ErrorActivity.this, getIntent());

		ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
		ClipData clip = ClipData.newPlainText(getString(R.string.customactivityoncrash_error_activity_error_details_clipboard_label), errorInformation);
		clipboard.setPrimaryClip(clip);
	}


	private class RunErrorLog implements Runnable{

		private String errorInfomation;

		public RunErrorLog(String errorInfomation) {
			this.errorInfomation = errorInfomation;
		}

		@Override
		public void run() {

			BufferedWriter bw = null;
			try {
				// 构造给定文件名的FileWriter对象，并使用布尔值指示是否追加写入的数据。
				File file=new File(Constant.glog);
				FileUtil.createFlePath(ErrorActivity.this,Constant.glog);
				if(!file.exists()) {
					file.createNewFile();
				}
				FileWriter fileWriter = new FileWriter(Constant.glog, true);
				bw = new BufferedWriter(fileWriter);
				bw.write("\n  \n \n "+errorInfomation);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (bw != null) {
						bw.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
