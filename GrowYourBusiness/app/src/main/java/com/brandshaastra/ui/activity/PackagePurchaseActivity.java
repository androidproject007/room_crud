package com.brandshaastra.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.brandshaastra.network.NetworkManager;
import com.brandshaastra.utils.ProjectUtils;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.brandshaastra.DTO.BusinessDataDto;
import com.brandshaastra.DTO.UserDTO;
import com.brandshaastra.R;
import com.brandshaastra.api.apiClient;
import com.brandshaastra.api.apiRest;
import com.brandshaastra.databinding.ActivityPackagePurchaseBinding;
import com.brandshaastra.interfaces.Consts;
import com.brandshaastra.preferences.SharedPrefrence;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.widget.Toast.LENGTH_LONG;
import static com.brandshaastra.utils.ProjectUtils.TAG;

public class PackagePurchaseActivity extends AppCompatActivity implements View.OnClickListener, PaymentResultListener {

    ActivityPackagePurchaseBinding binding;
    List<String> businessdataList;
    UserDTO userDTO;
    private SharedPreferences firebase;
    private SharedPrefrence prefrence;
    String plan_id, plan_name, coupon_code, plan_amt, plan_image, razorpay = "";
    ProgressDialog progressDialog;
    boolean from_image = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(PackagePurchaseActivity.this, R.layout.activity_package_purchase);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        firebase = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        prefrence = SharedPrefrence.getInstance(PackagePurchaseActivity.this);

        userDTO = prefrence.getParentUser(Consts.USER_DTO);

        progressDialog = new ProgressDialog(this);
        progressDialog.setIcon(R.drawable.brand_shaastra_logo);
        progressDialog.setMessage("Please wait...");
        plan_id = getIntent().getStringExtra("plan_id");
        plan_name = getIntent().getStringExtra("plan_name");
        plan_image = getIntent().getStringExtra("plan_image");
        plan_amt = getIntent().getStringExtra("plan_amt");
        razorpay = getIntent().getStringExtra("razorpay");
        Log.e("razorpay", "" + razorpay);
        from_image = getIntent().getBooleanExtra("from_image", false);

        DecimalFormat newFormat = new DecimalFormat("####");
        int a = (int) (Integer.parseInt(plan_amt) * 0.18);

        int sum = (Integer.parseInt(plan_amt) + a);

        if (getIntent().hasExtra("plan_image")) {

            Glide.with(this).load(plan_image).placeholder(R.drawable.brand_shaastra_logo).into(binding.businessRvImg);
            binding.packageName.setText(plan_name);
            binding.packageAmtTxt.setText(Html.fromHtml("&#x20B9;" + plan_amt));
            binding.discountAmtTxt.setText(Html.fromHtml("&#x20B9;" + 00));
            binding.taxAmtTxt.setText(Html.fromHtml("&#x20B9;" + String.valueOf(a)));
//            binding.totalAmtTxt.setText(Html.fromHtml("&#x20B9;" + plan_amt));
            binding.totalAmtTxt.setText(Html.fromHtml("&#x20B9;" + sum));
        }
        findViewById(R.id.applay_btn).setOnClickListener(this);
        findViewById(R.id.purchase_btn).setOnClickListener(this);


    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void applayCode() {
        progressDialog.show();
        Retrofit retrofit = apiClient.getClient(Consts.BASE_URL + Consts.USER_CONTROLLER);
        apiRest apiRest = retrofit.create(com.brandshaastra.api.apiRest.class);
        Call<ResponseBody> callback = apiRest.applayCoupanCode(userDTO.getUser_id(), coupon_code, plan_id);
        callback.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
                try {
                    if (response.isSuccessful()) {
                        ResponseBody responseBody = response.body();
                        String res = responseBody.string();
                        Log.e("purchase res", "" + res);

                        JSONObject jsonObject = new JSONObject(res);

                        String message = jsonObject.getString("message");
                        int sstatus = jsonObject.getInt("status");
                        if (sstatus == 1) {

                            binding.packageAmtTxt.setText(jsonObject.getString("original_plan_price"));
                            binding.discountAmtTxt.setText(jsonObject.getString("discouted_price"));
                            binding.totalAmtTxt.setText(jsonObject.getString("plan_price"));

                            Toast.makeText(PackagePurchaseActivity.this, message, Toast.LENGTH_SHORT).show();
                        } else if (sstatus == 3) {

                            Toast.makeText(PackagePurchaseActivity.this, message,
                                    LENGTH_LONG).show();

                            startActivity(new Intent(PackagePurchaseActivity.this, CheckSigninActivity.class));
                            finish();
                        } else {
                            Toast.makeText(PackagePurchaseActivity.this, message,
                                    LENGTH_LONG).show();
                        }
                    } else {
                        Log.e("purchase_error", "" + response.errorBody().string());
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.applay_btn:

                if (ProjectUtils.isEditTextFilled(binding.applayEtx)) {
                    coupon_code = binding.applayEtx.getText().toString();
                    applayCode();
                } else {
                    Toast.makeText(this, "Please Enter Valid Coupon Code", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.purchase_btn:
                String final_val = binding.totalAmtTxt.getText().toString();
                String b = final_val.substring(1);
                planSelected(b, plan_id);
                break;
        }
    }

    public void planSelected(String price, String id) {

        if (Double.parseDouble(price) > 0) {
            if (NetworkManager.isConnectToInternet(this)) {
                plan_id = id;

                double total = Double.parseDouble(price);

                DecimalFormat newFormat = new DecimalFormat("####");
                int mainprice = Integer.valueOf(newFormat.format(total));

                Checkout checkout = new Checkout();
                // with testing_key  checkout.setKeyID("rzp_test_EhkNPQuioSOOps");
                //  checkout.setKeyID("rzp_live_s53g9Sc8tnBBtY");
                checkout.setKeyID(razorpay);
                // checkout.setImage(R.drawable.logo);
                // final Activity activity = context;
                try {
                    JSONObject options = new JSONObject();

                    options.put("name", "The Brand Shaastra");
/*
                            options.put("description", "Reference No. #123456");
*/
                    options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
                    //     options.put("order_id", "order_DBJOWzybf0sJbb");//from response of step 3.
                    options.put("theme.color", "#3399cc");
                    options.put("currency", "INR");
                    options.put("amount", String.valueOf(mainprice * 100));//pass amount in currency subunits
                    options.put("prefill.email", userDTO.getEmail());
                    options.put("prefill.contact", userDTO.getMobile());
                    JSONObject retryObj = new JSONObject();
                    retryObj.put("enabled", true);
                    retryObj.put("max_count", 4);
                    options.put("retry", retryObj);
                    checkout.open(PackagePurchaseActivity.this, options);
                } catch (Exception e) {
                    Log.e(TAG, "Error in starting Razorpay Checkout", e);
                }

            } else {
                ProjectUtils.showLong(this, getResources().getString(R.string.internet_concation));
            }
        } else {
            ProjectUtils.showLong(this, getResources().getString(R.string.val_money));
        }

    }

    @Override
    public void onPaymentSuccess(String s) {
        addMoney();
        Log.e("RAZORPAY", " SUCCESS " + s);

    }

    @Override
    public void onPaymentError(int i, String s) {
        Log.e("RAZORPAY", " FAILURE " + s);

    }

    private void addMoney() {

        Retrofit retrofit = apiClient.getClient(Consts.BASE_URL + Consts.USER_CONTROLLER);
        apiRest apiRest = retrofit.create(com.brandshaastra.api.apiRest.class);
        Call<ResponseBody> call = apiRest.addPlan(userDTO.getUser_id(), plan_id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ResponseBody responseBody = response.body();
                String str = null;
                try {

                    if (from_image) {
                        startActivity(new Intent(PackagePurchaseActivity.this, ImageCanvasActivity.class));
                        finish();
                    } else {

                        startActivity(new Intent(PackagePurchaseActivity.this, HomeScreenActivity.class));
                        finish();
                    }
                    str = responseBody.string();
                    Log.e("plan_res", "" + str);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("plan_res", " failure " + t.getMessage());

            }
        });
    }

}