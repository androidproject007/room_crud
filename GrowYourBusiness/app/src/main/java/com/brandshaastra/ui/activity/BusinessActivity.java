package com.brandshaastra.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.brandshaastra.DTO.BusinessDataDto;
import com.brandshaastra.DTO.UserDTO;
import com.brandshaastra.R;
import com.brandshaastra.api.apiClient;
import com.brandshaastra.api.apiRest;
import com.brandshaastra.databinding.ActivityBusinessBinding;
import com.brandshaastra.interfaces.Consts;
import com.brandshaastra.network.NetworkManager;
import com.brandshaastra.preferences.SharedPrefrence;
import com.brandshaastra.ui.adapter.BusinessAdapter;
import com.brandshaastra.ui.model.BusinessModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.widget.Toast.LENGTH_LONG;

public class BusinessActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    ActivityBusinessBinding binding;
    BusinessAdapter businessAdapter;
    List<BusinessModel> businessList;
    UserDTO userDTO;
    private SharedPreferences firebase;
    private SharedPrefrence prefrence;
    List<BusinessDataDto> businessdataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(BusinessActivity.this, R.layout.activity_business);
        getSupportActionBar().setTitle("My Business");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        firebase = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        prefrence = SharedPrefrence.getInstance(BusinessActivity.this);
        userDTO = prefrence.getParentUser(Consts.USER_DTO);

        //  BottomNavigationViewHelper.disableShiftMode(binding.bottomnavigation);
        binding.bottomnavigation.setOnNavigationItemSelectedListener(this);

        Menu menu = binding.bottomnavigation.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);

        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BusinessActivity.this, BusinessDocumentActivity.class));
            }
        });

        if (NetworkManager.isConnectToInternet(this)) {

            getBusinessdata();
        } else {
            Toast.makeText(this, getResources().getString(R.string.internet_concation), Toast.LENGTH_SHORT).show();
        }
    }

    private void getBusinessdata() {

        Retrofit retrofit = apiClient.getClient(Consts.BASE_URL + Consts.USER_CONTROLLER);
        apiRest apiRest = retrofit.create(com.brandshaastra.api.apiRest.class);
        Call<ResponseBody> callback = apiRest.getAllBusinessData(userDTO.getUser_id());
        callback.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        ResponseBody responseBody = response.body();
                        String res = responseBody.string();
                        Log.e("home_business_res", "" + res);

                        JSONObject jsonObject = new JSONObject(res);
                        businessdataList = new ArrayList<>();

                        String message = jsonObject.getString("message");
                        int status = jsonObject.getInt("status");

                        if (status == 1) {
                            Type getpetDTO = new TypeToken<List<BusinessDataDto>>() {
                            }.getType();
                            businessdataList = (ArrayList<BusinessDataDto>) new Gson().fromJson(jsonObject.getJSONArray("data").toString(), getpetDTO);

                            if (businessdataList.size() <= 2) {
                                binding.floatingActionButton.setVisibility(View.VISIBLE);
                            }
                            businessAdapter = new BusinessAdapter(BusinessActivity.this, businessdataList);
                            binding.businessRv.setLayoutManager(new LinearLayoutManager(BusinessActivity.this));
                            binding.businessRv.setAdapter(businessAdapter);

                        } else if (status == 3) {

                            Toast.makeText(BusinessActivity.this,message ,
                                    LENGTH_LONG).show();

                            startActivity(new Intent(BusinessActivity.this, CheckSigninActivity.class));
                            finish();
                        }
                    } else {

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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.nav_home:
                Intent intent1 = new Intent(this, HomeScreenActivity.class);
                startActivity(intent1);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();

                break;
            case R.id.nav_business:
                break;
            case R.id.nav_video:
                //Intent intent3 = new Intent(this, VideoDashboardActivity.class);
                Intent intent3 = new Intent(this, HomeScreenActivity.class);
                intent3.putExtra(Consts.MEDIA_TYPE_VIDEO, true);
                startActivity(intent3);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();
                break;
            case R.id.nav_service:
                Intent intent = new Intent(this, ServiceActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();
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