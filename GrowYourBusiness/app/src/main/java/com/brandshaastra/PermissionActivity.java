package com.brandshaastra;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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

public class PermissionActivity extends AppCompatActivity {

    private SharedPrefrence prefrence;
    UserDTO userDTO;
    private HashMap<String, String> parms = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        getSupportActionBar().hide();
        prefrence = SharedPrefrence.getInstance(PermissionActivity.this);
        userDTO = prefrence.getParentUser(Consts.USER_DTO);
        Log.e("USER_ID", "" + userDTO.getUser_id());

        findViewById(R.id.btnlocationapprove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= 23) {
                    requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE",
                            "android.permission.WRITE_EXTERNAL_STORAGE"}, 101);
                } else {
                    startActivity(new Intent(PermissionActivity.this, CheckSigninActivity.class));
                    finish();
                    overridePendingTransition(R.anim.anim_slide_in_left,
                            R.anim.anim_slide_out_left);
                }
            }
        });
    }

    /*public void check_state() {

        Log.e("USER_ID_trscker", "1");

        parms.put(Consts.USER_ID, userDTO.getUser_id());
        Log.e("USER_ID_trscker", " 2 " + "userid-params" + parms.toString());

        new HttpsRequest(Consts.CHECK_APPROVE, parms, PermissionActivity.this).stringPost("TAG", Consts.USER_CONTROLLER, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
//                Log.e(TAG, "backResponse: " + response.toString());
                if (flag) {
                    Intent in = new Intent(PermissionActivity.this, HomeScreenActivity.class);
                    startActivity(in);
                    finish();
                    overridePendingTransition(R.anim.anim_slide_in_left,
                            R.anim.anim_slide_out_left);
                } else {
                    Intent in = new Intent(PermissionActivity.this, BusinessDocumentActivity.class);
                    startActivity(in);
                    finish();
                    overridePendingTransition(R.anim.anim_slide_in_left,
                            R.anim.anim_slide_out_left);
                }
            }
        });


    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(PermissionActivity.this, "File permissions are required to continue...!", Toast.LENGTH_SHORT).show();
            }

        }
    }
}