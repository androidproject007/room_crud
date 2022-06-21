package com.brandshaastra.ui.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.brandshaastra.R;
import com.brandshaastra.api.apiClient;
import com.brandshaastra.api.apiRest;
import com.brandshaastra.databinding.ActivitySignUpBinding;
import com.brandshaastra.interfaces.Consts;
import com.brandshaastra.network.NetworkManager;
import com.brandshaastra.preferences.SharedPrefrence;
import com.brandshaastra.utils.ProjectUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.widget.Toast.LENGTH_LONG;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    ActivitySignUpBinding binding;
    String TAG = "SignUpActivity";
    String email = "";
    private SharedPrefrence prefrence;
    private SharedPreferences firebase;
    String img_path = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(SignUpActivity.this, R.layout.activity_sign_up);
        prefrence = SharedPrefrence.getInstance(SignUpActivity.this);
        firebase = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        getSupportActionBar().hide();

        if (getIntent().hasExtra(Consts.EMAIL_ID)) {

            email = getIntent().getStringExtra(Consts.EMAIL_ID);
            binding.CETemailadd.setText(email);
        }
        /*findViewById(R.id.CBsignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this,OtpActivity.class));
            }
        });*/
        setUiAction();

    }

    public void setUiAction() {
        binding.CBsignUp.setOnClickListener(this);
        binding.CTVsignin.setOnClickListener(this);
        binding.tvTerms.setOnClickListener(this);
        binding.tvPrivacy.setOnClickListener(this);
        binding.profileImg.setOnClickListener(this);
        /*if (getIntent() != null) {
            email = getIntent().getStringExtra(Consts.EMAIL_ID);
            binding.CETemailadd.setText(email);

        }*/
        //  ivEnterShow = findViewById(R.id.ivEnterShow);
        //  ivReEnterShow = findViewById(R.id.ivReEnterShow);
        // ivReEnterShow.setOnClickListener(this);
        // ivEnterShow.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_img:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, 24);
                    } else {

                        ImagePicker.Companion.with(this)
                                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                                .start(23);
                    }
                }
                break;
            case R.id.CBsignUp:
                if (NetworkManager.isConnectToInternet(this)) {

                    clickForSubmit();
                } else {
                    Toast.makeText(this, getResources().getString(R.string.internet_concation), Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.CTVsignin:
                //startActivity(new Intent(SignUpActivity.this, SigninActivity.class));
                finish();
                break;
            case R.id.tvTerms:
                //  startActivity(new Intent(SignUpActivity.this, Terms.class));
                break;
            case R.id.tvPrivacy:
                //  startActivity(new Intent(SignUpActivity.this, Privacy.class));
                break;
        }
    }

    public void clickForSubmit() {

        if (!ProjectUtils.isPhoneNumberValid(binding.edtmono.getText().toString().trim())) {
            Toast.makeText(SignUpActivity.this, getResources().getString(R.string.val_mobile), Toast.LENGTH_SHORT).show();

        } else if (!validation(binding.CETfirstname, getResources().getString(R.string.val_name))) {
            return;
        } else if (!ProjectUtils.isEmailValid(binding.CETemailadd.getText().toString().trim())) {
            Toast.makeText(SignUpActivity.this, getResources().getString(R.string.val_email), Toast.LENGTH_SHORT).show();

        } else if (img_path.trim().isEmpty()) {
            Toast.makeText(SignUpActivity.this, getResources().getString(R.string.val_profile_pic), Toast.LENGTH_SHORT).show();

        } else if (!validateTerms()) {
            return;
        } else {
            if (NetworkManager.isConnectToInternet(SignUpActivity.this)) {

                Log.e("tracker", "1");
                sendotp();
            } else {
                Toast.makeText(SignUpActivity.this, getResources().getString(R.string.internet_concation), Toast.LENGTH_SHORT).show();

                // ProjectUtils.showToast(SignUpActivity.this, getResources().getString(R.string.internet_concation));
            }
        }


    }


    public void sendotp() {

        ProjectUtils.showProgressDialog(SignUpActivity.this, false, "Please Wait").show();
        Retrofit retrofit = apiClient.getClient(Consts.BASE_URL + Consts.USER_CONTROLLER);
        apiRest api = retrofit.create(apiRest.class);
        Call<ResponseBody> callone = api.sendOtp2(ProjectUtils.getEditTextValue(binding.CETemailadd));
        callone.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtils.pauseProgressDialog();

                try {
                    if (response.isSuccessful()) {

                        // Toast.makeText(mContext, "response get", Toast.LENGTH_SHORT).show();

                        ResponseBody responseBody = response.body();

                        String s = responseBody.string();
                        Log.e("register123otp_response", "" + s);

                        JSONObject object = new JSONObject(s);

                        String message = object.getString("message");
                        int sstatus = object.getInt("status");
                        String is_varified = object.getString("is_verified");
                        if (sstatus == 1) {

                            try {
                                //Toast.makeText(mContext, "response get2", Toast.LENGTH_SHORT).show();

                                String otp = object.getString("otp");

                                Intent intent = new Intent(SignUpActivity.this, OtpActivity.class);
                                intent.putExtra(Consts.NAME, ProjectUtils.getEditTextValue(binding.CETfirstname));
                                intent.putExtra(Consts.EMAIL_ID, ProjectUtils.getEditTextValue(binding.CETemailadd));
                                intent.putExtra(Consts.IMAGE, img_path);
                                intent.putExtra(Consts.REFERRAL_CODE, "");
                                intent.putExtra(Consts.IS_VARIFIED, is_varified);
                                intent.putExtra(Consts.MOBILE, ProjectUtils.getEditTextValue(binding.edtmono));
                                intent.putExtra(Consts.OTP, otp);
                                startActivity(intent);
                                finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }
                        else if (sstatus == 3) {

                            Toast.makeText(SignUpActivity.this, message,
                                    LENGTH_LONG).show();

                            startActivity(new Intent(SignUpActivity.this, CheckSigninActivity.class));
                            finish();
                        }
                        else {
                            //Toast.makeText(mContext, "response get3", Toast.LENGTH_SHORT).show();

                            Toast.makeText(SignUpActivity.this, message,
                                    LENGTH_LONG).show();
                        }


                    } else {
                        ProjectUtils.cancelDialog();
                        Toast.makeText(SignUpActivity.this, "Try Again Later ",
                                LENGTH_LONG).show();

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtils.cancelDialog();
                Toast.makeText(SignUpActivity.this, "Try again. Server is not responding",
                        LENGTH_LONG).show();
            }
        });

    }


    private boolean validateTerms() {
        if (binding.termsCB.isChecked()) {
            return true;
        } else {
            Toast.makeText(SignUpActivity.this, getResources().getString(R.string.trms_acc), Toast.LENGTH_SHORT).show();

            return false;
        }
    }

    private boolean checkpass() {
        if (binding.CETenterpassword.getText().toString().trim().equals("")) {
            Toast.makeText(SignUpActivity.this, getResources().getString(R.string.val_pass), Toast.LENGTH_SHORT).show();

            return false;
        }
        return true;
    }

    public HashMap<String, String> getparm() {
        HashMap<String, String> parms = new HashMap<>();
        parms.put(Consts.NAME, ProjectUtils.getEditTextValue(binding.CETfirstname));
        parms.put(Consts.EMAIL_ID, ProjectUtils.getEditTextValue(binding.CETemailadd));
        // parms.put(Consts.PASSWORD, ProjectUtils.getEditTextValue(CETenterpassword));
        parms.put(Consts.REFERRAL_CODE, "");
        parms.put(Consts.MOBILE, ProjectUtils.getEditTextValue(binding.edtmono));
        parms.put(Consts.DEVICE_TYPE, "ANDROID");
        parms.put(Consts.DEVICE_TOKEN, firebase.getString(Consts.DEVICE_TOKEN, ""));
        parms.put(Consts.DEVICE_ID, "12345");
        Log.e(TAG, parms.toString());
        return parms;
    }


    public HashMap<String, String> getparmtwo() {
        HashMap<String, String> parms = new HashMap<>();
        parms.put(Consts.MOBILE, ProjectUtils.getEditTextValue(binding.edtmono));
        Log.e(TAG, parms.toString());
        return parms;
    }


    public boolean validation(EditText editText, String msg) {
        if (!ProjectUtils.isEditTextFilled(editText)) {

            Toast.makeText(SignUpActivity.this, msg, Toast.LENGTH_SHORT).show();

            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();

        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == 23) {

                Uri imageUri = data.getData();
                img_path = imageUri.getPath();
                Log.e("IMAGE_PATH",""+img_path);
                binding.profileImg.setImageURI(imageUri);

            }
        }
    }
}