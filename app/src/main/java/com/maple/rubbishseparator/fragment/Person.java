package com.maple.rubbishseparator.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.bumptech.glide.Glide;
import com.itheima.roundedimageview.RoundedImageView;
import com.maple.rubbishseparator.R;
import com.maple.rubbishseparator.activity.QRActivity;
import com.maple.rubbishseparator.network.HttpHelper;
import com.maple.rubbishseparator.network.ServerCode;
import com.maple.rubbishseparator.network.VollySimpleRequest;
import com.maple.rubbishseparator.util.QRCode;
import com.maple.rubbishseparator.util.ViewControl;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;


public class Person extends Fragment implements View.OnClickListener {
    private View rootview;
    private String user_id;
    private String phoneNumber;

    private String headstate;
    private String name;

    //view
    private SmartRefreshLayout refreshLayout;
    private RoundedImageView iv_head;
    private TextView tv_name;
    private TextView tv_phoneNumber;
    private ImageView iv_qrcode;
    private LinearLayout layout_exit;
    private LinearLayout layout_changehead;
    private LinearLayout layout_changename;
    private LinearLayout layout_order;


    private SpotsDialog dialog;

    public Person() {
        // Required empty public constructor
    }

    private Context context;

    public void setParams(Context context, String user_id, String phoneNumber) {
        this.context = context;
        this.user_id = user_id;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (rootview == null) {
            rootview = inflater.inflate(R.layout.fragment_person, container, false);
            iv_head = rootview.findViewById(R.id.person_head);
            tv_name = rootview.findViewById(R.id.person_name);
            tv_phoneNumber = rootview.findViewById(R.id.person_phone);
            iv_qrcode = rootview.findViewById(R.id.person_sqcode);
            refreshLayout = rootview.findViewById(R.id.person_refresh);
            layout_exit = rootview.findViewById(R.id.person_exit);
            layout_changehead = rootview.findViewById(R.id.person_changehead);
            layout_changename = rootview.findViewById(R.id.person_changename);
            layout_order = rootview.findViewById(R.id.person_order);


            layout_order.setOnClickListener(this);
            layout_changename.setOnClickListener(this);
            layout_changehead.setOnClickListener(this);
            layout_exit.setOnClickListener(this);
            iv_head.setOnClickListener(this);
            tv_name.setOnClickListener(this);
            iv_qrcode.setOnClickListener(this);
        }

        init();
        return rootview;
    }


    private void init() {
        decoratePane(refreshLayout);
        attachRefreshListener();
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setEnableRefresh(false);
        decorateLoading();
        getUserInfo();
        createQRCode();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        if (ViewControl.avoidRetouch()) {
            switch (v.getId()) {
                case R.id.person_changename:
                    Intent intent_changename = new Intent();
                    intent_changename.setAction("com.maple.changeNameReceiver");
                    context.sendBroadcast(intent_changename);
                    break;
                case R.id.person_changehead:
                    Intent intent_changehead = new Intent();
                    intent_changehead.setAction("com.maple.changeHeadReceiver");
                    context.sendBroadcast(intent_changehead);
                    break;
                case R.id.person_head:
                    break;
                case R.id.person_name:
                    tologin();
                    break;
                case R.id.person_phone:
                    break;
                case R.id.person_sqcode:
                    showQRCode();
                    break;

                case R.id.person_order:
                    Intent intent_order = new Intent();
                    intent_order.setAction("com.maple.orderReceiver");
                    context.sendBroadcast(intent_order);
                    break;

                case R.id.person_exit:
                    Intent intent_exit = new Intent();
                    intent_exit.setAction("com.maple.exitLoginReceiver");
                    context.sendBroadcast(intent_exit);
                    break;
                default:
                    break;
            }
        }
    }


    //????????????
    private void tologin() {
        if (user_id == null && phoneNumber == null) {
            Intent intent = new Intent();
            intent.setAction("com.maple.loginReceiver");
            context.sendBroadcast(intent);
        }
    }

    //??????????????????
    public void getUserInfo() {
        if (user_id != null && phoneNumber != null) {
            dialog.show();
            Map<String, String> params = new HashMap<>();
            params.put("requestCode", ServerCode.GETID_EFFIT);
            params.put("Id", user_id);
            params.put("phoneNumber", phoneNumber);
            VollySimpleRequest.getInstance(context).sendStringRequest(Request.Method.POST, HttpHelper.MAIN_MOBILE, response -> {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String result = jsonObject.getString("result");
                    if (result.equals("1")) {
                        //id??????,??????
                        headstate = jsonObject.getString("headstate");
                        name = jsonObject.getString("username");

                        refreshPage();
                    }

                    //????????????
                    refreshLayout.finishRefresh();
                    refreshLayout.setEnableRefresh(true);
                    dialog.dismiss();
                } catch (JSONException e) {
                    //????????????
                    refreshLayout.finishRefresh();
                    refreshLayout.setEnableRefresh(true);
                    dialog.dismiss();
                    e.printStackTrace();
                }
            }, error -> {
                refreshLayout.finishRefresh();
                refreshLayout.setEnableRefresh(true);
                dialog.dismiss();
            }, params);
        } else {
            refreshLayout.finishRefresh();
            refreshLayout.setEnableRefresh(true);
        }
    }


    //????????????
    @SuppressLint("SetTextI18n")
    private void refreshPage() {
        if (!headstate.equals("0")) {
            Glide.with(context).load(headstate).into(iv_head);
        }
        if (name != null) {
            tv_name.setText(name);
        }
        if (phoneNumber != null) {
            tv_phoneNumber.setText(getResources().getString(R.string.phonenumber) + phoneNumber);
        }

    }

    //???????????????
    private void showQRCode() {
        if (user_id != null && phoneNumber != null) {
            Intent intent = new Intent(context, QRActivity.class);
            intent.putExtra("user_id", user_id);
            startActivity(intent);
        }
    }

    //??????????????????
    private void decoratePane(SmartRefreshLayout pane) {
        pane.setHeaderHeight(40);
        pane.setFooterHeight(30);
        pane.setEnableLoadMore(true);//????????????????????????
        pane.setEnableOverScrollDrag(true);
        ClassicsHeader.REFRESH_HEADER_PULLING = getString(R.string.pulltorefresh);
        ClassicsHeader.REFRESH_HEADER_REFRESHING = getString(R.string.refreshing);
        ClassicsHeader.REFRESH_HEADER_LOADING = getString(R.string.isloading);
        ClassicsHeader.REFRESH_HEADER_RELEASE = getString(R.string.releasetorefresh);
        ClassicsHeader.REFRESH_HEADER_FINISH = getString(R.string.refreshfinish);
        ClassicsHeader.REFRESH_HEADER_FAILED = getString(R.string.refreshfail);

    }

    //?????????????????????????????????

    private void attachRefreshListener() {

        //??????????????????????????????????????????
        refreshLayout.setOnRefreshListener(refreshLayout -> {
            getUserInfo();
            refreshLayout.setEnableRefresh(false);
            refreshLayout.setEnableLoadMore(false);

        });

        //??????????????????????????????????????????
        refreshLayout.setOnLoadMoreListener(refreshLayout -> {
            //????????????
            refreshLayout.setEnableRefresh(false);
            refreshLayout.setEnableLoadMore(false);

        });

    }

    //???????????????
    private void decorateLoading() {
        if (dialog == null) {
            dialog = new SpotsDialog(context);
            dialog.setCanceledOnTouchOutside(false);
        }
    }

    //?????????????????????
    private void createQRCode() {
        Bitmap bitmap;
        if (user_id == null) {
            //?????????????????????
            bitmap = QRCode.createQRcodeImage("1", 200, 200);
        } else {
            bitmap = QRCode.createQRcodeImage(user_id, 200, 200);
        }
        iv_qrcode.setImageBitmap(bitmap);
    }


}