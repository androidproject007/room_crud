package com.brandshaastra.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.brandshaastra.R;
import com.brandshaastra.api.apiClient;
import com.brandshaastra.api.apiRest;
import com.brandshaastra.databinding.ActivityServiceBinding;
import com.brandshaastra.interfaces.Consts;
import com.brandshaastra.ui.BottomNavigationViewHelper;
import com.brandshaastra.ui.adapter.ServiceAdapter;
import com.brandshaastra.ui.model.ServiceModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ServiceActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    ActivityServiceBinding binding;
    List<ServiceModel> serviceList;
    ServiceAdapter serviceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(ServiceActivity.this, R.layout.activity_service);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        binding.bottomnavigation.setOnNavigationItemSelectedListener(this);
        BottomNavigationViewHelper.disableShiftMode(binding.bottomnavigation);
        Menu menu = binding.bottomnavigation.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);

    }

    public void getBusinessdata() {

        Retrofit retrofit = apiClient.getClient(Consts.BASE_URL + Consts.USER_CONTROLLER);
        apiRest apiRest = retrofit.create(com.brandshaastra.api.apiRest.class);
        Call<ResponseBody> call = apiRest.getAllBusinessData("4555");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    ResponseBody responseBody = response.body();
                    String s = responseBody.toString();
                    Log.e("business_data", s);

                    serviceList = new ArrayList<>();

                    serviceAdapter = new ServiceAdapter(ServiceActivity.this, serviceList);
                    binding.serviceRv.setLayoutManager(new LinearLayoutManager(ServiceActivity.this));
                    binding.serviceRv.setAdapter(serviceAdapter);

                    try {
                        JSONObject jsonObject = new JSONObject(s);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("business_data", t.getMessage());
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.nav_home:
                Intent intent = new Intent(this, HomeScreenActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();
                break;
            case R.id.nav_business:
                Intent intent1 = new Intent(this, BusinessActivity.class);
                startActivity(intent1);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();
                break;

            case R.id.nav_video:
               // Intent intent3 = new Intent(this, VideoDashboardActivity.class);
                Intent intent3 = new Intent(this, HomeScreenActivity.class);
                startActivity(intent3);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();
                break;
            case R.id.nav_service:

                break;
            case R.id.nav_account:
                Intent intent2 = new Intent(this, AccountActivity.class);
                startActivity(intent2);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();
                break;
        }
        return true;
    }
}