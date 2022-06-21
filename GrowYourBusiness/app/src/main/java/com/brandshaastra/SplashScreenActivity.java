package com.brandshaastra;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.brandshaastra.DTO.UserDTO;
import com.brandshaastra.https.HttpsRequest;
import com.brandshaastra.interfaces.Consts;
import com.brandshaastra.interfaces.Helper;
import com.brandshaastra.preferences.SharedPrefrence;
import com.brandshaastra.ui.activity.BusinessDocumentActivity;
import com.brandshaastra.ui.activity.CheckSigninActivity;
import com.brandshaastra.ui.activity.HomeScreenActivity;

import org.json.JSONObject;

import java.util.HashMap;

public class SplashScreenActivity extends AppCompatActivity {

    private SharedPrefrence prefference;
    private static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 1003;
    private String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WAKE_LOCK};
    private boolean cameraAccepted, storageAccepted, accessNetState, fineLoc, corasLoc, readstorage, wakelock, background;
    private Handler handler = new Handler();
    private static int SPLASH_TIME_OUT = 3000;
    Context mContext;
    MediaPlayer mediaPlayer;
    private SharedPreferences firebase;
    private UserDTO userDTO;
    private SharedPrefrence prefrence;
    ProgressDialog progressDialog;
    private HashMap<String, String> parms = new HashMap<>();
    private String TAG = SplashScreenActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();
        prefrence = SharedPrefrence.getInstance(SplashScreenActivity.this);
        firebase = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        userDTO = prefrence.getParentUser(Consts.USER_DTO);
        Log.e("USER_ID", "" + userDTO.getUser_id());

        mContext = SplashScreenActivity.this;
        prefference = SharedPrefrence.getInstance(SplashScreenActivity.this);
        //check();
        //rglanguage(prefference.getValue(Consts.LANGUAGE));

        new Handler().postDelayed(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public void run() {

                /*if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || !Environment.isExternalStorageManager()) {

                    startActivity(new Intent(SplashScreenActivity.this, PermissionActivity.class));
                    finish();
                } else {*/
                if (prefference.getBooleanValue(Consts.IS_REGISTERED)) {
                    Log.e("USER_ID", "" + userDTO.getUser_id());

                    check_state();

                } else {
                    startActivity(new Intent(SplashScreenActivity.this, CheckSigninActivity.class));
                    finish();
                    overridePendingTransition(R.anim.anim_slide_in_left,
                            R.anim.anim_slide_out_left);
                }
//                }
            }
        }, 2000);
    }

    public void check_state() {

        Log.e("USER_ID_trscker", "1");

        parms.put(Consts.USER_ID, userDTO.getUser_id());
        Log.e("USER_ID_trscker", " 2 " + "userid-params" + parms.toString());

        new HttpsRequest(Consts.CHECK_APPROVE, parms, SplashScreenActivity.this).stringPost("TAG", Consts.USER_CONTROLLER, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
//                Log.e(TAG, "backResponse: " + response.toString());
                if (flag) {
                    Intent in = new Intent(SplashScreenActivity.this, HomeScreenActivity.class);
                    startActivity(in);
                    finish();
                    overridePendingTransition(R.anim.anim_slide_in_left,
                            R.anim.anim_slide_out_left);
                } else {
                    Intent in = new Intent(SplashScreenActivity.this, BusinessDocumentActivity.class);
                    startActivity(in);
                    finish();
                    overridePendingTransition(R.anim.anim_slide_in_left,
                            R.anim.anim_slide_out_left);
                }
            }
        });


    }
}