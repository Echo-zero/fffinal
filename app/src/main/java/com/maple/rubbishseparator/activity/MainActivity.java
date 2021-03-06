package com.maple.rubbishseparator.activity;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.android.volley.Request;
import com.maple.rubbishseparator.MyApplication;
import com.maple.rubbishseparator.R;
import com.maple.rubbishseparator.dialog.RecordDialog;
import com.maple.rubbishseparator.dialog.TextComfirmDialog;
import com.maple.rubbishseparator.fragment.MapPage;
import com.maple.rubbishseparator.fragment.Person;
import com.maple.rubbishseparator.fragment.SeparateGuide;
import com.maple.rubbishseparator.fragment.Shop;
import com.maple.rubbishseparator.helper.CompressHelper;
import com.maple.rubbishseparator.network.HttpHelper;
import com.maple.rubbishseparator.network.ServerCode;
import com.maple.rubbishseparator.network.VollySimpleRequest;
import com.maple.rubbishseparator.util.StoreState;
import com.maple.rubbishseparator.util.ViewControl;

import com.tencent.cloud.qcloudasrsdk.common.QCloudSourceType;
import com.tencent.cloud.qcloudasrsdk.models.QCloudFileRecognitionParams;
import com.tencent.cloud.qcloudasrsdk.recognizer.QCloudFileRecognizer;



import org.json.JSONException;
import org.json.JSONObject;


import java.io.File;
import java.io.FileInputStream;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


import dmax.dialog.SpotsDialog;

import static java.lang.String.valueOf;

public class MainActivity extends PermissionActivity implements TextComfirmDialog.OnButtomClickedListener, View.OnClickListener, RecordDialog.RecordBack {
    String user_id;//??????id
    String phoneNumber;//????????????


    //dialog??????
    public final int dialog_1 = 101;//???????????????????????????
    public final int dialog_2 = 102;//??????????????????
    public final int dialog_3 = 103;//????????????????????????

    private TextView tv_guide;
    private TextView tv_map;
    private TextView tv_shop;
    private TextView tv_person;

    private SpotsDialog loadingDialog;

    //fragment
    private SeparateGuide fragment_guide;//????????????????????????
    private MapPage fragment_map;
    private Shop fragment_shop;
    private Person fragment_person;


    //activity back

    private final int CAMERA_BACK = 301;
    private final int CHOOSE_IMAGE = 302;
    private final int RESIZE_BACK = 303;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getBaiduToken();
        init();
    }


    private void init() {
        //view
        //fragment?????????
        //????????????
        LinearLayout layout_guide = findViewById(R.id.main_bottom_guide);
        //????????????
        LinearLayout layout_map = findViewById(R.id.main_bottom_map);
        //????????????
        LinearLayout layout_shop = findViewById(R.id.main_bottom_shop);
        //????????????
        LinearLayout layout_person = findViewById(R.id.main_bottom_person);

        tv_guide = findViewById(R.id.main_bottom_guide_text);
        tv_map = findViewById(R.id.main_bottom_map_text);
        tv_shop = findViewById(R.id.main_bottom_shop_text);
        tv_person = findViewById(R.id.main_bottom_person_text);


        layout_guide.setOnClickListener(this);
        layout_map.setOnClickListener(this);
        layout_shop.setOnClickListener(this);
        layout_person.setOnClickListener(this);

        fragment_guide = new SeparateGuide();
        fragment_guide.setParams(MainActivity.this, user_id, phoneNumber);
        tv_guide.setTextColor(getResources().getColor(R.color.stringcolor_blue));
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.add(R.id.main_fragment, fragment_guide);
        fragmentTransaction.commit();


        decorateLoading();//????????????
        decorateBottom();//?????????????????????
        registerBroadcast();//?????????????????????
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        if (ViewControl.avoidRetouch() && checkPermission()) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = manager.beginTransaction();
            if (fragment_guide != null) {
                fragmentTransaction.hide(fragment_guide);
            }
            if (fragment_map != null) {
                fragmentTransaction.hide(fragment_map);
            }
            if (fragment_shop != null) {
                fragmentTransaction.hide(fragment_shop);
            }
            if (fragment_person != null) {
                fragmentTransaction.hide(fragment_person);
            }
            switch (v.getId()) {
                case R.id.main_bottom_guide:
                    if (fragment_guide != null) {
                        fragmentTransaction.show(fragment_guide);
                    } else {
                        fragment_guide = new SeparateGuide();
                        fragment_guide.setParams(MainActivity.this, user_id, phoneNumber);
                        fragmentTransaction.add(R.id.main_fragment, fragment_guide);
                    }
                    tv_guide.setTextColor(getResources().getColor(R.color.stringcolor_blue));
                    tv_map.setTextColor(getResources().getColor(R.color.stringcolor_grey));
                    tv_shop.setTextColor(getResources().getColor(R.color.stringcolor_grey));
                    tv_person.setTextColor(getResources().getColor(R.color.stringcolor_grey));
                    break;
                case R.id.main_bottom_map:
                    if (fragment_map != null) {
                        fragmentTransaction.show(fragment_map);
                    } else {
                        fragment_map = new MapPage();

                        fragmentTransaction.add(R.id.main_fragment, fragment_map);
                    }
                    tv_map.setTextColor(getResources().getColor(R.color.stringcolor_blue));
                    tv_guide.setTextColor(getResources().getColor(R.color.stringcolor_grey));
                    tv_shop.setTextColor(getResources().getColor(R.color.stringcolor_grey));
                    tv_person.setTextColor(getResources().getColor(R.color.stringcolor_grey));
                    break;
                case R.id.main_bottom_shop:
                    if (fragment_shop != null) {
                        fragmentTransaction.show(fragment_shop);
                    } else {
                        fragment_shop = new Shop();
                        fragmentTransaction.add(R.id.main_fragment, fragment_shop);
                    }
                    tv_shop.setTextColor(getResources().getColor(R.color.stringcolor_blue));
                    tv_guide.setTextColor(getResources().getColor(R.color.stringcolor_grey));
                    tv_map.setTextColor(getResources().getColor(R.color.stringcolor_grey));
                    tv_person.setTextColor(getResources().getColor(R.color.stringcolor_grey));
                    break;
                case R.id.main_bottom_person:
                    if (fragment_person != null) {
                        fragmentTransaction.show(fragment_person);
                    } else {
                        fragment_person = new Person();
                        fragment_person.setParams(MainActivity.this, user_id, phoneNumber);
                        fragmentTransaction.add(R.id.main_fragment, fragment_person);
                    }
                    tv_person.setTextColor(getResources().getColor(R.color.stringcolor_blue));
                    tv_guide.setTextColor(getResources().getColor(R.color.stringcolor_grey));
                    tv_map.setTextColor(getResources().getColor(R.color.stringcolor_grey));
                    tv_shop.setTextColor(getResources().getColor(R.color.stringcolor_grey));
                    break;
                default:
                    break;
            }
            fragmentTransaction.commit();
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case CAMERA_BACK:
                if (resultCode== Activity.RESULT_OK){
                    Log.i("uri",imageUri.toString());
                }
                break;

            case CHOOSE_IMAGE:
                if (data != null) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        Log.i("uri", uri.toString());

                    }
                }
                break;

            default:
                break;
        }
    }


    //?????????
    private void registerBroadcast() {
//        registerChangeHeadReceiver();//????????????
//        registerLoginReceiver();//????????????
//        registerLoginSuccessListener();//????????????
//        registerExitLoginListener();//????????????
        registerRecordListener();//????????????
        registerDetectPhotoListener();//??????????????????
//        registerUploadOrderListener();//?????????????????????
//        registerChangeNameReceiver();//???????????????
//        registertoPersonOrderListener();//????????????????????????
//        registerChangeNameListener();//???????????????
        initPhotoError();
        fixNetWork();
    }

    //???????????????
    private class ChangeNameListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (user_id != null && phoneNumber != null) {
                Intent intent_tochangename = new Intent(MainActivity.this, ChangeName.class);
                intent_tochangename.putExtra("user_id", user_id);
                intent_tochangename.putExtra("phoneNumber", phoneNumber);
                startActivity(intent_tochangename);
            }
        }
    }

    private void registerChangeNameListener() {
        ChangeNameListener chanagename = new ChangeNameListener();
        IntentFilter intentFilter = new IntentFilter("com.maple.changeNameReceiver");
        registerReceiver(chanagename, intentFilter);

    }


    //????????????????????????
    private class ToPersonOrderReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (user_id != null && phoneNumber != null) {
                Intent intent_topersonorder = new Intent(MainActivity.this, PersonOrder.class);
                intent_topersonorder.putExtra("user_id", user_id);
                intent_topersonorder.putExtra("phoneNumber", phoneNumber);
                startActivity(intent_topersonorder);
            }
        }
    }

    private void registertoPersonOrderListener() {
        ToPersonOrderReceiver toPersonReceiver = new ToPersonOrderReceiver();
        IntentFilter intentFilter = new IntentFilter("com.maple.orderReceiver");
        registerReceiver(toPersonReceiver, intentFilter);

    }


    //??????????????????
    private class UploadOrderReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (user_id != null && phoneNumber != null) {
                Intent intent_upload = new Intent(MainActivity.this, UploadOrder.class);
                intent_upload.putExtra("user_id", user_id);
                intent_upload.putExtra("phoneNumber", phoneNumber);
                intent_upload.putExtra("latitude", intent.getStringExtra("latitude"));
                intent_upload.putExtra("longitude", intent.getStringExtra("longitude"));
                startActivity(intent_upload);

            }
        }
    }


    private void registerUploadOrderListener() {
        UploadOrderReceiver uploadReceiver = new UploadOrderReceiver();
        IntentFilter intentFilter = new IntentFilter("com.maple.uploadOrderListener");
        registerReceiver(uploadReceiver, intentFilter);

    }



    private Uri imageUri;

    private class DetectPhoto extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_DENIED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_DENIED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_DENIED) {
                File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
                if (outputImage.exists()){
                    outputImage.delete();
                }
                try {
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                    imageUri = FileProvider.getUriForFile(MyApplication.context,"com.maple.rubbishseparator.fileprovider", outputImage);
                }else {
                    imageUri = Uri.fromFile(outputImage);
                }
                //??????
                String type = intent.getStringExtra("type");//0:???????????? 1:????????????
                if (type.equals("0")) {
                    //??????
                    Intent intent1 = new Intent("android.media.action.IMAGE_CAPTURE");
                    intent1.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent1,CAMERA_BACK);
                } else {
                    Intent intentFromPhotos = getGalleryIntent();
                    startActivityForResult(intentFromPhotos, CHOOSE_IMAGE);
                }
            } else {
                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
                requestPermission(permissions, 0x0001);
            }
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    public Intent getGalleryIntent() {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT < 19) {
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
        } else {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        }
        return intent;
    }


    //????????????
    public void compresspic() {
        detectPic(CompressHelper.compressFile(String.valueOf(imageUri), 30));
    }

    private void detectPic(String file_state) {
        loadingDialog.dismiss();
        Intent intent = new Intent(this, ShowBaiduResult.class);
        intent.putExtra("imageurl", file_state);
        startActivity(intent);
    }



    private void initPhotoError() {
        // android 7.0???????????????????????????
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }

    private void fixNetWork() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()   // or .detectAll() for all detectable problems
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());
    }

    /**
     * ????????????????????????SDCard???????????????
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }


    private void registerDetectPhotoListener() {
        DetectPhoto detectPhoto = new DetectPhoto();
        IntentFilter intentFilter = new IntentFilter("com.maple.detectPhoto");
        registerReceiver(detectPhoto, intentFilter);

    }


    /*





    ????????????????????????
     */


    //??????????????????,??????tencent_api_id
    @Override
    public void recordBack(String url) {
        File file = new File(url);
        if (file.exists()) {
            //????????????id
            Map<String, String> params = new HashMap<>();
            params.put("requestCode", ServerCode.GET_TENCENT_ID);
            VollySimpleRequest.getInstance(this).sendStringRequest(Request.Method.POST, HttpHelper.MAIN_MOBILE, response -> {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    requestDetect(jsonObject.getString("app_id"), jsonObject.getString("secret_key"), jsonObject.getString("secret_pass"), url);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> {
                Toast.makeText(MainActivity.this, getString(R.string.fail), Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
            }, params);
        }
    }

    //??????????????????

    @SuppressLint("ShowToast")
    public void requestDetect(String app_id, String secret_key, String secret_pass, String
            filename) {
        QCloudFileRecognizer recognizer = new QCloudFileRecognizer(app_id, secret_key, secret_pass);
        recognizer.setCallback((qCloudFileRecognizer, l, s, i, e) -> {
            switch (i) {
                case 0:
                    //????????????
                    break;
                case 1:
                    //???????????????
                    break;
                case 2:
                    //????????????
                    loadingDialog.dismiss();
                    Intent intent = new Intent(MainActivity.this, ShowResult.class);
                    String search_target = s.substring(19, s.length() - 2);
                    intent.putExtra("search_target", search_target);
                    startActivity(intent);
                    break;
                case 3:
                    //????????????
                    loadingDialog.dismiss();
                    Toast.makeText(MainActivity.this, getString(R.string.fail), Toast.LENGTH_SHORT);
                    break;
                default:
                    break;
            }
        });

        try {
            FileInputStream fileInputStream = new FileInputStream(new File(filename));
            int length = fileInputStream.available();
            byte[] audioData = new byte[length];
            fileInputStream.read(audioData);
            QCloudFileRecognitionParams params = (QCloudFileRecognitionParams) QCloudFileRecognitionParams.defaultRequestParams();
            params.setData(audioData);
            params.setSourceType(QCloudSourceType.QCloudSourceTypeData);
            params.setFilterDirty(0);// 0 ??????????????? ??????????????? 1???????????????
            params.setFilterModal(0);// 0 ??????????????? ??????????????????  1???????????????????????? 2:????????????
            params.setConvertNumMode(1);//1??????????????? ?????????????????????????????????????????????0??????????????????????????????

            params.setHotwordId(""); // ?????? id??????????????????????????????????????????????????????????????????????????????????????????????????? id ?????????????????????????????????????????????????????????????????? id ????????????????????????????????????????????? id???
            recognizer.recognize(params);
        } catch (Exception e) {
            Log.i("record", "error" + e.toString());
        }
    }


    //????????????
    private class DetectRecord extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_DENIED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_DENIED) {
                File file_voice = new File(StoreState.VOICE_STATE);
                if (!file_voice.exists()) {
                    Log.i("record", "makefile");
                    boolean s = file_voice.mkdir();
                }
                RecordDialog dialog = new RecordDialog(MainActivity.this);
                dialog.setRecordListener(MainActivity.this::recordBack);
                dialog.show();
                // ??????????????????????????????????????????????????????
                // ??????????????????????????????????????????????????????
                Display display = getWindowManager().getDefaultDisplay();
                WindowManager.LayoutParams lp = Objects.requireNonNull(dialog.getWindow()).getAttributes();
                lp.alpha = 0.9f;
                lp.width = (int) (display.getWidth() * 1.0); //????????????
                dialog.getWindow().setAttributes(lp);
            } else {
                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};
                requestPermission(permissions, 0x0001);
            }
        }
    }

    private void registerRecordListener() {
        DetectRecord detectRecord = new DetectRecord();
        IntentFilter intentFilter = new IntentFilter("com.maple.RecordDialog");
        registerReceiver(detectRecord, intentFilter);

    }

    //????????????
    private class ExitLoginReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (user_id != null && phoneNumber != null) {
                showTipsDialog(dialog_3, getResources().getString(R.string.notice), getResources().getString(R.string.comfirm_logout));
            }
        }
    }


    private void registerExitLoginListener() {
        ExitLoginReceiver exitLoginReceiver = new ExitLoginReceiver();
        IntentFilter intentFilter = new IntentFilter("com.maple.exitLoginReceiver");
        registerReceiver(exitLoginReceiver, intentFilter);

    }


    //????????????
    private class LoginListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            user_id = intent.getStringExtra("user_id");
            phoneNumber = intent.getStringExtra("phoneNumber");
            if (fragment_guide != null) {
                fragment_guide.setParams(MainActivity.this, user_id, phoneNumber);
                fragment_guide.getUserInfo();
            }
            if (fragment_person != null) {
                fragment_person.setParams(MainActivity.this, user_id, phoneNumber);
                fragment_person.getUserInfo();
            }
        }
    }

    private void registerLoginSuccessListener() {
        LoginListener loginListener = new LoginListener();
        IntentFilter intentFilter = new IntentFilter("com.maple.loginSuccessReceiver");
        registerReceiver(loginListener, intentFilter);

    }


    //????????????
    private class LoginReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //????????????
            if (checkPermission()) {
                Intent intent_tologin = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent_tologin);
            }
        }
    }

    private void registerLoginReceiver() {
        LoginReceiver receiver = new LoginReceiver();
        IntentFilter intentFilter = new IntentFilter("com.maple.loginReceiver");
        registerReceiver(receiver, intentFilter);
    }


    //?????????

    private class ChangeNameBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }

    private void registerChangeNameReceiver() {
        ChangeNameBroadcast baudrateReceiver = new ChangeNameBroadcast();
        IntentFilter intentFilter = new IntentFilter("com.maple.changeNameReceiver");
        registerReceiver(baudrateReceiver, intentFilter);
    }


    //??????????????????
    private class ChangeHeadBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //????????????
            if (checkPermission()) {
                if (user_id != null) {
                    if (!user_id.equals("")) {
                        Intent intent_changehead = new Intent(MainActivity.this, ChangeHead.class);
                        intent_changehead.putExtra("user_id", user_id);
                        intent_changehead.putExtra("phoneNumber", phoneNumber);
                        startActivity(intent_changehead);
                    } else {
                        showTipsDialog(dialog_2, getResources().getString(R.string.notice), getResources().getString(R.string.login_account));
                    }
                } else {
                    showTipsDialog(dialog_2, getResources().getString(R.string.notice), getResources().getString(R.string.login_account));
                }
            }
        }
    }

    private void registerChangeHeadReceiver() {
        ChangeHeadBroadcast baudrateReceiver = new ChangeHeadBroadcast();
        IntentFilter intentFilter = new IntentFilter("com.maple.changeHeadReceiver");
        registerReceiver(baudrateReceiver, intentFilter);
    }


    //?????????????????????
    private void decorateBottom() {


    }


    //????????????
    private boolean checkPermission() {
        int result = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result_network = ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        int result_networkstate = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE);
        int result_networkwifi = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE);
        int result_package = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int result_ = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        int result_location = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int result_location2 = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int result_getlocaton = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS);
        if (result_package == PackageManager.PERMISSION_DENIED || result == PackageManager.PERMISSION_DENIED || result_ == PackageManager.PERMISSION_DENIED || result_location == PackageManager.PERMISSION_DENIED || result_location2 == PackageManager.PERMISSION_DENIED || result_network == PackageManager.PERMISSION_DENIED || result_networkstate == PackageManager.PERMISSION_DENIED || result_networkwifi == PackageManager.PERMISSION_DENIED || result_getlocaton == PackageManager.PERMISSION_DENIED) {
            String[] permissions = {Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_WIFI_STATE};
            requestPermission(permissions, 0x0001);
            return false;
        } else {
            //???????????????file
            File file = new File(StoreState.IMAGE_STATE);
            File file_voice = new File(StoreState.VOICE_STATE);
            if (!file.exists()) {
                boolean s = file.mkdir();
            }
            if (!file_voice.exists()) {
                boolean s = file_voice.mkdir();
            }
            return true;
        }
    }

    /**
     * ?????????????????????
     */
    private void showTipsDialog(int code, String title, String content) {
        TextComfirmDialog dialog = new TextComfirmDialog(MainActivity.this, title, content, code);
        dialog.setButtomClickedListener(this);
        dialog.show();
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = Objects.requireNonNull(dialog.getWindow()).getAttributes();
        lp.alpha = 0.8f;//???????????????
        lp.width = (int) (display.getWidth() * 0.8); //????????????
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    public void onButtomClick(int num, int dialog_id) {
        if (num == 1) {
            switch (dialog_id) {
                case dialog_1:
                    startAppSettings();
                    break;
                case dialog_2:
                    toPersonPage();
                    break;
                case dialog_3:
                    //????????????
                    SharedPreferences sharedPreferences = getSharedPreferences("account", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();
                    Intent intent_tosplash = new Intent(MainActivity.this, SplashActivity.class);
                    startActivity(intent_tosplash);
                    finish();
                    break;
                default:
                    break;
            }
        }
    }

    //??????????????????
    private void toPersonPage() {
        if (checkPermission()) {
            Intent intent_tologin = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent_tologin);
        }
    }

    //???????????????
    private void decorateLoading() {
        loadingDialog = new SpotsDialog(this);
        loadingDialog.setCanceledOnTouchOutside(false);
    }

    public String sHA1(Context context) {
        try {
            @SuppressLint("PackageManagerGetSignatures") PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuilder hexString = new StringBuilder();
            for (byte b : publicKey) {
                String appendString = Integer.toHexString(0xFF & b)
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            String result = hexString.toString();
            return result.substring(0, result.length() - 1);
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

}