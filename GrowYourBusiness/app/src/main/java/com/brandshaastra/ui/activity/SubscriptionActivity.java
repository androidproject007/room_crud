package com.brandshaastra.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.brandshaastra.DTO.UserDTO;
import com.brandshaastra.R;
import com.brandshaastra.api.apiClient;
import com.brandshaastra.api.apiRest;
import com.brandshaastra.databinding.ActivitySubscriptionBinding;
import com.brandshaastra.interfaces.Consts;
import com.brandshaastra.interfaces.OnPlanSelected;
import com.brandshaastra.network.NetworkManager;
import com.brandshaastra.preferences.SharedPrefrence;
import com.brandshaastra.ui.adapter.SubscriptionSliderAdapter;
import com.brandshaastra.ui.model.SliderModel;
import com.brandshaastra.utils.ProjectUtils;

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

public class SubscriptionActivity extends AppCompatActivity{

    ActivitySubscriptionBinding binding;
    SubscriptionSliderAdapter sliderAdapter;
    List<SliderModel> sliderlist;
    UserDTO userDTO;
    private SharedPreferences firebase;
    private SharedPrefrence prefrence;
    String plan_id = "";
    boolean from_image = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(SubscriptionActivity.this, R.layout.activity_subscription);
        getSupportActionBar().setTitle("Subscription");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        from_image = getIntent().getBooleanExtra("from_image",false);
        firebase = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        prefrence = SharedPrefrence.getInstance(SubscriptionActivity.this);
        userDTO = prefrence.getParentUser(Consts.USER_DTO);

        Log.e("SLIDER_RESAWD", "1");

        if (NetworkManager.isConnectToInternet(this)) {

            getSubscriptionData();
        } else {
            Toast.makeText(this, getResources().getString(R.string.internet_concation), Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void getSubscriptionData() {

        ProjectUtils.showProgressDialog(this, false, "Loading...");
        Log.e("SLIDER_RESAWD", "2");
        Retrofit retrofit = apiClient.getClient(Consts.BASE_URL + Consts.USER_CONTROLLER);
        apiRest api = retrofit.create(apiRest.class);
        Call<ResponseBody> call = api.getAllPlan(userDTO.getUser_id());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtils.pauseProgressDialog();
                Log.e("SLIDER_RESAWD", "3");

                try {
                    if (response.isSuccessful()) {

                        Log.e("SLIDER_RESAWD", "4");

                        ResponseBody responseBody = response.body();

                        String res = responseBody.string();
                        Log.e("SLIDER_RESAWD", "" + res);

                        JSONObject jsonObject = new JSONObject(res);

                        String message = jsonObject.getString("message");
                        int sstatus = jsonObject.getInt("status");
                        String razorpay = jsonObject.getString("razorpay");
                        if (sstatus == 1) {
                            sliderlist = new ArrayList<>();

                            Type getpetDTO = new TypeToken<List<SliderModel>>() {
                            }.getType();
                            sliderlist = (ArrayList<SliderModel>) new Gson().fromJson(jsonObject.getJSONArray("data").toString(), getpetDTO);

                            setAdapter(razorpay);
                        } else if (sstatus == 3) {

                            Toast.makeText(SubscriptionActivity.this, message,
                                    LENGTH_LONG).show();

                            startActivity(new Intent(SubscriptionActivity.this, CheckSigninActivity.class));
                            finish();
                        }

                    } else {
                        Log.e("SLIDER_RESAWD", "else");

                    }

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Log.e("SLIDER_RESAWD", "fail");

            }
        });
    }

    private void setAdapter(String razorpay) {

        sliderAdapter = new SubscriptionSliderAdapter(SubscriptionActivity.this, sliderlist,from_image,razorpay);
        binding.subscriptionPager.setAdapter(sliderAdapter);

        binding.subscriptionPager.setClipToPadding(false);
        binding.subscriptionPager.setClipChildren(false);
        binding.subscriptionPager.setOffscreenPageLimit(3);
        binding.subscriptionPager.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(40));
        transformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);
            }
        });

        binding.subscriptionPager.setPageTransformer(transformer);

    }

}