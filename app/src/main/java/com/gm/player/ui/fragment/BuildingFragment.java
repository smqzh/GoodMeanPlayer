package com.gm.player.ui.fragment;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.gm.player.R;
import com.gm.player.application.GmApplication;
import com.gm.player.application.MySp;
import com.gm.player.event.HeadNumberEvent;
import com.gm.player.ui.activity.IndexActivity;
import com.gm.player.util.Tools;
import com.gm.player.util.YcToast;

import org.apache.poi.ss.formula.functions.Index;
import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Konstantin on 22.12.2014.
 */
public class BuildingFragment extends BaseFragment {

    @BindView(R.id.tv_build_ip)
    TextView tvBuildIp;
    @BindView(R.id.et_build_port)
    EditText etBuildPort;
    @BindView(R.id.et_build_remote_ip)
    EditText etBuildRemoteIp;
    @BindView(R.id.et_build_remote_port)
    EditText etBuildRemotePort;
    //    @BindView(R.id.spr_protect)
//    Spinner sprProtect;
    @BindView(R.id.et_protect_time)
    EditText etProtectTime;
    //    @BindView(R.id.btn_build_select)
//    Button btnBuildSelect;
    @BindView(R.id.et_build_number)
    EditText etBuildNumber;
    Unbinder unbinder1;
    @BindView(R.id.button_save)
    Button buttonSave;
    /*   @BindView(R.id.button_listen)
       Button buttonListen;*/
    @BindView(R.id.button_switch)
    Button button_switch;
    @BindView(R.id.button_state)
    Button button_state;
    @BindView(R.id.multiple_actions)
    FloatingActionsMenu multipleActions;
    @BindView(R.id.button_rotation)
    Button buttonRotation;
    private String TAG = this.getClass().getSimpleName();
    public static final String ARG_MAR = "ARG_MAR";
    Unbinder unbinder;
    private View containerView;
    protected String res;
    private Bitmap bitmap;
    private String select;
    private String[] mItems;

    public static BuildingFragment newInstance(String key) {
        BuildingFragment contentFragment = new BuildingFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_MAR, key);
        contentFragment.setArguments(bundle);
        return contentFragment;
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
        return R.layout.fragment_build;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        unbinder = ButterKnife.bind(this, mRootView);
        init();
    }

    @Override
    protected String setTagName() {
        return "配置";
    }

    private void init() {
        initView();
        initData();
        initSetListener();
    }

    private void initView() {
        button_state.setOnFocusChangeListener(new ButtonFocusChangleListener());
        button_switch.setOnFocusChangeListener(new ButtonFocusChangleListener());
        buttonSave.setOnFocusChangeListener(new ButtonFocusChangleListener());
        buttonRotation.setOnFocusChangeListener(new ButtonFocusChangleListener());
        IndexActivity index=(IndexActivity)getActivity();
        index.RotationView(MySp.getRotation());
    }

    private class ButtonFocusChangleListener implements View.OnFocusChangeListener {

        @Override
        public void onFocusChange(View view, boolean b) {

            switch (view.getId()) {

                case R.id.button_state:
                    if (b) {
                        button_state.setBackgroundResource(R.drawable.button_focus);
                    } else {
                        button_state.setBackgroundResource(R.drawable.button_normal);
                    }
                    break;
                case R.id.button_switch:
                    if (b) {
                        button_switch.setBackgroundResource(R.drawable.button_focus);
                    } else {
                        button_switch.setBackgroundResource(R.drawable.button_normal);
                    }
                    break;
                case R.id.button_save:
                    if (b) {
                        buttonSave.setBackgroundResource(R.drawable.button_focus);
                    } else {
                        buttonSave.setBackgroundResource(R.drawable.button_normal);
                    }
                    break;
                case R.id.button_rotation:
                    if (b) {
                        buttonRotation.setBackgroundResource(R.drawable.button_focus);
                    } else {
                        buttonRotation.setBackgroundResource(R.drawable.button_normal);
                    }

                    break;

            }

        }
    }

    private void initData() {

        mItems = getResources().getStringArray(R.array.fileType);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, mItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //绑定 Adapter到控件
       /* sprProtect.setAdapter(adapter);
        sprProtect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                String[] languages = getResources().getStringArray(R.array.fileType);
                select = languages[pos];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });*/
        if (MySp.getVideoNormal() == 0) {
            button_state.setText("拉伸");
        } else {
            button_state.setText("正常");
        }
        buttonRotation.setText(MySp.getRotation()+"°");
        initBaseData();
    }

    private void initBaseData() {
        String ip = Tools.getLocalIP();
        if (ip != null) {
            tvBuildIp.setText(ip);
        }
        etBuildPort.setText(MySp.getLocalPort() + "");
        etBuildRemotePort.setText(MySp.getRemotePort() + "");
        etBuildRemoteIp.setText(MySp.getServerIP());
        etProtectTime.setText(MySp.getScreenTime() + "");
        etBuildNumber.setText(MySp.getDeviceNumber());
//        int size = mItems.length;
//        for (int i = 0; i < size; i++) {
//            if (MySp.getScreenType().equals(mItems[i])) {
//                sprProtect.setSelection(i);
//                break;
//            }
//        }
    }


    private void initSetListener() {

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  multipleActions.toggle();
                clickSave();
            }
        });
        button_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MySp.getVideoNormal() == 0) {
                    button_state.setText("正常");
                    MySp.setVideoNormal(1);
                } else {
                    MySp.setVideoNormal(0);
                    button_state.setText("拉伸");
                }
            }
        });

        buttonRotation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int rotation= MySp.getRotation();
                rotation=(rotation+90)%360;
                MySp.setRotation(rotation);
                buttonRotation.setText(rotation+"°");
                IndexActivity index=(IndexActivity)getActivity();
                index.RotationView(rotation);
            }
        });
       /* buttonListen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                multipleActions.toggle();

                String remotePort = etBuildRemotePort.getText().toString();
                if (remotePort.equals("")) {
                    YcToast.get().toast("服务器端口不能为空");
                    return;
                }
                MySp.setRemotePort(Integer.parseInt(remotePort));

                String remoteIP = etBuildRemoteIp.getText().toString();
                if (remoteIP.equals("")) {
                    YcToast.get().toast("服务器IP不能为空");
                    return;
                }
                MySp.setServerIP(remoteIP);
                MySp.setScreenType(select);
                String number=etBuildNumber.getText().toString();
                MySp.setDeviceNumber(number);

                IndexActivity indexActivity=(IndexActivity)getActivity();
                indexActivity.reConnect();
            }
        });
*/
        button_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  multipleActions.toggle();
                /**
                 * int ORIENTATION_PORTRAIT = 1;  竖屏
                 * int ORIENTATION_LANDSCAPE = 2; 横屏
                 */
                //获取屏幕的方向  ,数值1表示竖屏，数值2表示横屏
                int screenNum = getResources().getConfiguration().orientation;
                //（如果竖屏）设置屏幕为横屏
                if (screenNum == 1) {
                    GmApplication.getInstance().setScreenFlag(true);
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    //设置为置屏幕为.竖屏
                    MySp.setScreenState(0);
                } else {
                    GmApplication.getInstance().setScreenFlag(false);
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    MySp.setScreenState(1);
                }
            }
        });


    }

    private void clickSave() {
        String localPort = etBuildPort.getText().toString();
        if (localPort.equals("")) {
            YcToast.get().toast("本地端口不能为空");
            return;
        }
        MySp.setLocalPort(Integer.parseInt(localPort));

        String remotePort = etBuildRemotePort.getText().toString();
        if (remotePort.equals("")) {
            YcToast.get().toast("服务器端口不能为空");
            return;
        }
        MySp.setRemotePort(Integer.parseInt(remotePort));

        String remoteIP = etBuildRemoteIp.getText().toString();
        if (remoteIP.equals("")) {
            YcToast.get().toast("服务器IP不能为空");
            return;
        }
        MySp.setServerIP(remoteIP);
        MySp.setScreenType(select);
        String number = etBuildNumber.getText().toString();
        MySp.setDeviceNumber(number);
        String screenTime = etProtectTime.getText().toString();
        if (screenTime.equals("")) {
            YcToast.get().toast("屏保时间请设置");
            return;
        }
        MySp.setScreenTime(Integer.parseInt(screenTime));
        EventBus.getDefault().post(new HeadNumberEvent(number));
        IndexActivity indexActivity = (IndexActivity) getActivity();
        indexActivity.StopDefaultScreen();
        indexActivity.initDefaultPlayer();//屏保开始
        indexActivity.reConnect();
        YcToast.get().toast("保存成功");
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder1 = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}

