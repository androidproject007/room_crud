package com.brandshaastra.ui.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.brandshaastra.R;
import com.brandshaastra.api.apiClient;
import com.brandshaastra.api.apiRest;
import com.brandshaastra.databinding.ActivityCheckSigninBinding;
import com.brandshaastra.interfaces.Consts;
import com.brandshaastra.network.NetworkManager;
import com.brandshaastra.utils.ProjectUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.widget.Toast.LENGTH_LONG;

public class CheckSigninActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private String TAG = CheckSigninActivity.class.getSimpleName();
    ActivityCheckSigninBinding binding;
    ProgressDialog progressDialog;
    private SharedPreferences firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(CheckSigninActivity.this, R.layout.activity_check_signin);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        firebase = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        mContext = CheckSigninActivity.this;
        progressDialog = new ProgressDialog(CheckSigninActivity.this);
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);

        /*findViewById(R.id.CBsignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CheckSigninActivity.this, SigninActivity.class));
            }
        });*/
        setUiAction();
    }

    public void setUiAction() {
        binding.CBsignIn.setOnClickListener(this);
    }

    public void clickForSubmit() {

        if (!ProjectUtils.isEmailValid(binding.CETemailadd.getText().toString().trim())) {
            ProjectUtils.showToast(mContext, getResources().getString(R.string.val_email));

        } else {
            if (NetworkManager.isConnectToInternet(mContext)) {
                login();
            } else {
                ProjectUtils.showToast(mContext, getResources().getString(R.string.internet_concation));
            }
        }


    }

    public void login() {
        Log.e("EMAIL", "" + ProjectUtils.getEditTextValue(binding.CETemailadd));

        progressDialog.show();
        Retrofit retrofit = apiClient.getClient(Consts.BASE_URL + Consts.USER_CONTROLLER);
        apiRest api = retrofit.create(apiRest.class);
        Call<ResponseBody> callone = api.checksignin(ProjectUtils.getEditTextValue(binding.CETemailadd), firebase.getString(Consts.DEVICE_TOKEN, ""));
        callone.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
                //       Log.e("RES", response.message());
                try {
                    if (response.isSuccessful()) {
                        ResponseBody responseBody = response.body();

                        String s = responseBody.string();
                        Log.e("Checksignin_response", "" + s);
                        JSONObject object = new JSONObject(s);

                        String message = object.getString("message");
                        int sstatus = object.getInt("status");

                        if (sstatus == 1) {

                          /*  Intent in = new Intent(mContext, SigninActivity.class);
                            in.putExtra(Consts.EMAIL_ID, ProjectUtils.getEditTextValue(binding.CETemailadd));
                            startActivity(in);
*/
                            sendotp();
                        } else if (sstatus == 2) {
                            Toast.makeText(CheckSigninActivity.this, message,
                                    LENGTH_LONG).show();
                        } else {
                            Intent in = new Intent(mContext, SignUpActivity.class);
                            in.putExtra(Consts.EMAIL_ID, ProjectUtils.getEditTextValue(binding.CETemailadd));
                            startActivity(in);

                            overridePendingTransition(R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left);
                        }


                    } else {
                        Toast.makeText(CheckSigninActivity.this, "Try again. Server is not responding",
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

                Toast.makeText(CheckSigninActivity.this, "Try again. Server is not responding",
                        LENGTH_LONG).show();


            }
        });
    }

    public void clickDone() {
        new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(getString(R.string.app_name))
                .setMessage(getResources().getString(R.string.close_msg))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //       Glob.BUBBLE_VALUE = "0";
                        dialog.dismiss();
                        Intent i = new Intent();
                        i.setAction(Intent.ACTION_MAIN);
                        i.addCategory(Intent.CATEGORY_HOME);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public void sendotp() {
        progressDialog.show();
        Retrofit retrofit = apiClient.getClient(Consts.BASE_URL + Consts.USER_CONTROLLER);
        apiRest api = retrofit.create(apiRest.class);
        Call<ResponseBody> callone = api.sendOtp2(ProjectUtils.getEditTextValue(binding.CETemailadd));
        callone.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();

                try {
                    if (response.isSuccessful()) {
                        ResponseBody responseBody = response.body();

                        String s = responseBody.string();
                        Log.e(TAG + " Login_response", s);

                        JSONObject object = new JSONObject(s);

                        String message = object.getString("message");
                        int sstatus = object.getInt("status");


                        if (sstatus == 1) {

                            try {

                                String otp = object.getString("otp");

                                Intent intent = new Intent(CheckSigninActivity.this, OtpActivity.class);
                                intent.putExtra("signin_flag", true);
                                intent.putExtra(Consts.EMAIL_ID, ProjectUtils.getEditTextValue(binding.CETemailadd));
                                intent.putExtra(Consts.OTP, otp);
                                startActivity(intent);
                                finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }
                        else {
                            Toast.makeText(CheckSigninActivity.this, message,
                                    LENGTH_LONG).show();
                        }


                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(CheckSigninActivity.this, "Try Again Later ",
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
                progressDialog.dismiss();
                Toast.makeText(CheckSigninActivity.this, "Try again. Server is not responding",
                        LENGTH_LONG).show();


            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.CBsignIn:
                clickForSubmit();
                break;
        }
    }
}