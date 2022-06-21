package com.brandshaastra.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.brandshaastra.DTO.BusinessDataDto;
import com.brandshaastra.DTO.UserDTO;
import com.brandshaastra.R;
import com.brandshaastra.api.apiClient;
import com.brandshaastra.api.apiRest;
import com.brandshaastra.databinding.ActivityOtpBinding;
import com.brandshaastra.https.HttpsRequest;
import com.brandshaastra.interfaces.Consts;
import com.brandshaastra.interfaces.Helper;
import com.brandshaastra.network.NetworkManager;
import com.brandshaastra.preferences.SharedPrefrence;
import com.brandshaastra.utils.CustomEditText;
import com.brandshaastra.utils.CustomTextViewBold;
import com.brandshaastra.utils.FileUtility;
import com.brandshaastra.utils.ProjectUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.widget.Toast.LENGTH_LONG;

public class OtpActivity extends AppCompatActivity {

    ActivityOtpBinding binding;
    UserDTO userDTO;
    private Context mContext;
    private String TAG = OtpActivity.class.getSimpleName();
    private SharedPrefrence prefrence;
    //  private UserDTO userDTO;
    private SharedPreferences firebase;
    private boolean isHide = false;
    //private ActivityOtpBinding binding;
    ImageView img_back;
    ImageView btn_signIn;
    String mobile = "", mainotp = "", verificationCode = "", name = "", email = "", pasword = "", is_varified = "", signin_varified = "";
    String image_uri = "";
    ProgressDialog progressDialog;
    private RelativeLayout RRsncbar;
    CustomEditText otp_receieved;
    TextInputEditText edtotp1, edtotp2, edtotp3, edtotp4, edtotp5, etotp0;
    TextView txt_timer, txt_resend_timer;
    boolean flag = false;
    CustomTextViewBold txt_resend, otp_txt;
    public int counter;
    Double latitude;
    Double longitude;
    private Location mylocation;
    String partner_location = "";
    protected LocationManager locationManager;
    // File image_file;
    HashMap<String, File> imageMap;
    List<BusinessDataDto> businessdataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(OtpActivity.this, R.layout.activity_otp);
        getSupportActionBar().hide();
        mContext = OtpActivity.this;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        prefrence = SharedPrefrence.getInstance(mContext);
        firebase = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        Log.e("tokensss", firebase.getString(Consts.DEVICE_TOKEN, ""));

        RRsncbar = findViewById(R.id.RRsncbarr);
        progressDialog = new ProgressDialog(OtpActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait");
        otp_txt = findViewById(R.id.otp_txt);
        txt_resend_timer = findViewById(R.id.txt_resend_timer);

        binding.CBsignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OtpActivity.this, BusinessDocumentActivity.class));
            }
        });

        flag = getIntent().getBooleanExtra("signin_flag", false);
        if (flag) {
            email = getIntent().getStringExtra(Consts.EMAIL_ID);
            mainotp = getIntent().getStringExtra(Consts.OTP);
        } else {
            name = getIntent().getStringExtra(Consts.NAME);
            email = getIntent().getStringExtra(Consts.EMAIL_ID);
            image_uri = getIntent().getStringExtra(Consts.IMAGE);

            Log.e("email", "" + email);
            Log.e("IMAGE", "" + image_uri);
            String path = FileUtility.getPath(OtpActivity.this, Uri.parse(image_uri));

            Log.e("IMAGE", " path " + path);

            File image_file = new File(image_uri);

            imageMap = new HashMap<>();

            imageMap.put(Consts.IMAGE, image_file);

            //pasword=getIntent().getStringExtra(Consts.PASSWORD);
            mobile = getIntent().getStringExtra(Consts.MOBILE);
            mainotp = getIntent().getStringExtra(Consts.OTP);
            is_varified = getIntent().getStringExtra(Consts.IS_VARIFIED);
        }


        btn_signIn = findViewById(R.id.CBsignIn);
        otp_receieved = findViewById(R.id.otp_receieved);
        txt_timer = findViewById(R.id.txt_timer);
        txt_resend = findViewById(R.id.txt_resend);

        otp_txt.setText(email);
        otp_receieved.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (otp_receieved.getText().toString().length() == 6)     //size as per your requirement
                {
                    otp_receieved.requestFocus();
                    btn_signIn.performClick();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });
        btn_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (otp_receieved.getText().toString().trim().isEmpty()) {


                    showSickbar("Please enter valid otp");

                    //  ProjectUtils.showToast(mContext, "Please enter valid otp");
                    setfocus();
                    return;
                }
                String otp = otp_receieved.getText().toString().trim();
                if (!otp.trim().isEmpty()) {
                    if (mainotp.equalsIgnoreCase(otp)) {

                        if (flag) {

                            login();
                            // Toast.makeText(mContext, "login called", Toast.LENGTH_SHORT).show();
                        } else {
                            register();
                        }
                    } else {
                        Toast.makeText(OtpActivity.this, "Please enter valid otp", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    showSickbar("Please enter valid otp");
                    //  ProjectUtils.showToast(mContext, "Please enter otp");
                    setfocus();
                }

            }
        });

        txt_resend.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                if (NetworkManager.isConnectToInternet(OtpActivity.this)) {

                    sendotp();
                } else {
                    Toast.makeText(OtpActivity.this, getResources().getString(R.string.internet_concation), Toast.LENGTH_SHORT).show();
                }

                txt_resend_timer.setVisibility(View.VISIBLE);
                txt_resend.setClickable(false);
                txt_resend.setTextColor(getResources().getColor(R.color.otp_resend_color));
                new CountDownTimer(30000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        txt_resend_timer.setText(String.valueOf(counter));
                        counter++;
                    }

                    @Override
                    public void onFinish() {
                        counter = 0;
                        txt_resend_timer.setVisibility(View.GONE);
                        txt_resend.setTextColor(getResources().getColor(R.color.blue_color));
                        txt_resend.setClickable(true);
                    }
                }.start();
            }
        });
    }

    public void sendotp() {

        progressDialog.show();
        Retrofit retrofit = apiClient.getClient(Consts.BASE_URL + Consts.USER_CONTROLLER);
        apiRest api = retrofit.create(apiRest.class);
        Call<ResponseBody> callone = api.sendOtp2(email);
        callone.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();

                try {
                    if (response.isSuccessful()) {
                        ResponseBody responseBody = response.body();

                        String s = responseBody.string();
                        JSONObject object = new JSONObject(s);

                        String message = object.getString("message");
                        int sstatus = object.getInt("status");

                        if (sstatus == 1) {

                            try {
                                String otp = object.getString("otp");
                                mainotp = otp;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        } else {
                            Toast.makeText(OtpActivity.this, message,
                                    LENGTH_LONG).show();
                        }


                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(OtpActivity.this, "Try Again Later ",
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
                Toast.makeText(OtpActivity.this, "Try again. Server is not responding",
                        LENGTH_LONG).show();


            }
        });


    }

    public HashMap<String, String> getResendparm() {
        HashMap<String, String> parms = new HashMap<>();
        parms.put(Consts.MOBILE, mobile);
        Log.e(TAG, parms.toString());
        return parms;
    }


    public void register() {

        progressDialog.show();
        new HttpsRequest(Consts.REGISTER_API, getparm(), imageMap, mContext).imagePost(TAG, Consts.USER_CONTROLLER, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                //  ProjectUtils.showToast(mContext, msg);
                progressDialog.dismiss();
                if (flag) {
                    try {

                        Log.e(TAG + "register123_response", response.toString());


                        userDTO = new Gson().fromJson(response.getJSONObject("data").toString(), UserDTO.class);

                        Log.e("USERDTO", "" + userDTO.getUser_id());
                        prefrence.setParentUser(userDTO, Consts.USER_DTO);
                        prefrence.setBooleanValue(Consts.IS_REGISTERED, true);


                        Intent in = new Intent(mContext, BusinessDocumentActivity.class);
                        startActivity(in);
                        finish();
                        overridePendingTransition(R.anim.anim_slide_in_left,
                                R.anim.anim_slide_out_left);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    ProjectUtils.showToast(mContext, msg);

                    overridePendingTransition(R.anim.anim_slide_in_left,
                            R.anim.anim_slide_out_left);

                }


            }
        });
    }

    public void showSickbar(String msg) {
        Toast.makeText(OtpActivity.this, msg, Toast.LENGTH_SHORT).show();

    }

    public HashMap<String, String> getparm() {
        HashMap<String, String> parms = new HashMap<>();
        parms.put(Consts.NAME, name);
        parms.put(Consts.EMAIL, email);
        //  parms.put(Consts.PASSWORD, pasword);
        parms.put(Consts.REFERRAL_CODE, "");
        parms.put(Consts.MOBILE, mobile);
        parms.put(Consts.DEVICE_TYPE, "ANDROID");
        parms.put(Consts.DEVICE_TOKEN, firebase.getString(Consts.DEVICE_TOKEN, ""));
        parms.put(Consts.DEVICE_ID, "12345");
        Log.e(TAG + "register123", parms.toString());
        return parms;
    }


    public HashMap<String, String> getparmotp(String userid) {

        HashMap<String, String> parms = new HashMap<>();
        parms.put("user_id", userid);
        parms.put("mobile", mobile);


        return parms;
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();

        finish();
    }

    public void setfocus() {

        if (otp_receieved.getText().toString().trim().isEmpty()) {
            otp_receieved.requestFocus();
            return;
        }
    }

    public HashMap<String, String> getloginparm() {
        HashMap<String, String> parms = new HashMap<>();
        parms.put(Consts.EMAIL, email);
        parms.put("otp", mainotp);
        parms.put(Consts.DEVICE_TYPE, "ANDROID");
        parms.put(Consts.DEVICE_TOKEN, firebase.getString(Consts.DEVICE_TOKEN, ""));
        parms.put(Consts.DEVICE_ID, "12345");
        Log.e(TAG + " Login", parms.toString());
        return parms;
    }

    public void login() {
        progressDialog.show();
        new HttpsRequest(Consts.LOGIN_API, getloginparm(), mContext).stringPost(TAG, Consts.USER_CONTROLLER, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                progressDialog.dismiss();
//                Log.e(TAG + " Login_response_flag", response.toString());

                if (flag) {
                    try {
                        Log.e(TAG + " Login_response", response.toString());

                        // ProjectUtils.showToast(mContext, msg);

                        userDTO = new Gson().fromJson(response.getJSONObject("data").toString(), UserDTO.class);

                        Type getpetDTO = new TypeToken<List<BusinessDataDto>>() {
                        }.getType();
                        businessdataList = (ArrayList<BusinessDataDto>) new Gson().fromJson(response.getJSONArray("bussiness_data").toString(), getpetDTO);
                        Log.e("businessdataList", "" + businessdataList.toString() + " " + businessdataList.size());
                        prefrence.setParentUser(userDTO, Consts.USER_DTO);

                        String is_v = userDTO.getIs_verified();
                        prefrence.setBooleanValue(Consts.IS_REGISTERED, true);

                        if (is_v.equalsIgnoreCase("0")) {
                            Intent in = new Intent(mContext, BusinessDocumentActivity.class);
                            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(in);
                            finish();
                            overridePendingTransition(R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left);
                        } else {
                            if (businessdataList.size() != 0) {
                                prefrence.setBusinessData(businessdataList.get(0), Consts.BUSINESSDATA_DTO);
                            }
                            Intent in = new Intent(OtpActivity.this, HomeScreenActivity.class);
                            startActivity(in);
                            finish();
                            overridePendingTransition(R.anim.anim_slide_in_left,
                                    R.anim.anim_slide_out_left);

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(OtpActivity.this, msg, Toast.LENGTH_SHORT).show();

                    //ProjectUtils.showToast(mContext, msg);
                }


            }
        });
    }

}