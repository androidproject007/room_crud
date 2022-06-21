package com.brandshaastra.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.brandshaastra.utils.ProjectUtils;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.kbeanie.multipicker.api.entity.ChosenImage;
import com.kbeanie.multipicker.api.entity.ChosenVideo;
import com.brandshaastra.DTO.BusinessDataDto;
import com.brandshaastra.DTO.UserDTO;

import com.brandshaastra.databinding.ActivityHomeScreenBinding;
import com.brandshaastra.databinding.HomescreenBottomsheetDialogBinding;
import com.brandshaastra.interfaces.OnBusinessDataItemSelected;
import com.brandshaastra.interfaces.OnVideoSelect;
import com.brandshaastra.preferences.SharedPrefrence;
import com.brandshaastra.ui.adapter.FirstHomeScreenAdapter;
import com.brandshaastra.ui.adapter.HomeBottomDialogAdapter;
import com.brandshaastra.ui.adapter.HomeFirstAdapter;
import com.brandshaastra.ui.adapter.HomeScreenAdapter;
import com.brandshaastra.ui.adapter.SliderAdapter;
import com.brandshaastra.ui.model.FirstAdapterImageModel;
import com.brandshaastra.ui.model.HomeDialogModel;
import com.brandshaastra.ui.model.HomeFirstModel;
import com.brandshaastra.ui.model.SiderModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.brandshaastra.DTO.CategoryDTO;
import com.brandshaastra.R;

import com.brandshaastra.api.apiClient;
import com.brandshaastra.api.apiRest;
import com.brandshaastra.https.HttpsRequest;
import com.brandshaastra.interfaces.Consts;
import com.brandshaastra.interfaces.Helper;
import com.brandshaastra.network.NetworkManager;
import com.brandshaastra.utils.CameraUtils;
import com.brandshaastra.utils.CustomTextViewBold;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.widget.Toast.LENGTH_LONG;

public class HomeScreenActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener, OnBusinessDataItemSelected, CameraUtils.OnCameraResult, OnVideoSelect {

    ActivityHomeScreenBinding binding;
    private HashMap<String, String> parmsCategory = new HashMap<>();
    ArrayList<SiderModel> silderarraylist = new ArrayList<>();
    ArrayList<FirstAdapterImageModel> firstAdapterListlist;
    String TAG = "HomeScreenActivity";
    UserDTO userDTO;
    private SharedPreferences firebase;
    HomeScreenAdapter homeScreenAdapter;
    List<HomeDialogModel> homeDialogList;
    List<HomeFirstModel> homeFirstList;
    HomeFirstAdapter homeFirstAdapter;
    private SharedPrefrence prefrence;
    List<String> imageList;
    private HashMap<String, String> parmsversion = new HashMap<>();
    private Dialog dailograting;

    List<BusinessDataDto> businessdataList;
    boolean doubleBackToExitPressedOnce = false;
    BusinessDataDto businessDataDto;
    private CameraUtils cameraUtils;
    boolean media_type = false;
    BottomSheetDialog bottomSheetDialog;
    HomescreenBottomsheetDialogBinding dialogBinding;
    CustomTextViewBold tvupdateapp;
    public ImageView img_sub_cat;
    String youtube = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(HomeScreenActivity.this, R.layout.activity_home_screen);
        //BottomNavigationViewHelper.disableShiftMode(binding.bottomnavigation);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        cameraUtils = new CameraUtils(this, this);


        binding.bottomnavigation.setOnNavigationItemSelectedListener(this);
        firebase = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        prefrence = SharedPrefrence.getInstance(HomeScreenActivity.this);

        userDTO = prefrence.getParentUser(Consts.USER_DTO);
        businessDataDto = prefrence.getBusinessData(Consts.BUSINESSDATA_DTO);

        dailograting = new Dialog(HomeScreenActivity.this, R.style.Theme_Dialog);
        dailograting.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dailograting.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dailograting.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        dailograting.setContentView(R.layout.dailog_update);
        dailograting.setCancelable(false);
        tvupdateapp = dailograting.findViewById(R.id.tvupdateapp);
        img_sub_cat = dailograting.findViewById(R.id.img_sub_cat);

        getVersion();
        parmsCategory.put(Consts.USER_ID, userDTO.getUser_id());
        parmsCategory.put(Consts.DEVICE_TOKEN, firebase.getString(Consts.DEVICE_TOKEN, ""));
        getSupportActionBar().hide();

        if (getIntent().hasExtra(Consts.MEDIA_TYPE_VIDEO)) {

            media_type = getIntent().getBooleanExtra(Consts.MEDIA_TYPE_VIDEO, false);
        }


        Menu menu = binding.bottomnavigation.getMenu();
        if (media_type) {
            MenuItem menuItem = menu.getItem(1);
            menuItem.setChecked(true);

        } else {

            MenuItem menuItem = menu.getItem(0);
            menuItem.setChecked(true);

        }
        bottomSheetDialog = new BottomSheetDialog(HomeScreenActivity.this);
        dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.homescreen_bottomsheet_dialog, null, false);

        bottomSheetDialog.setContentView(dialogBinding.getRoot());
        bottomSheetDialog.setCancelable(true);


        if (media_type) {

            binding.pagerhome.setVisibility(View.GONE);
            binding.demo.setVisibility(View.GONE);
            binding.horizontalcardviewpager.setVisibility(View.GONE);
            binding.dots.setVisibility(View.GONE);
            binding.homeBusinessName.setVisibility(View.GONE);
            binding.homeFirstRecyclerview.setVisibility(View.GONE);

            if (NetworkManager.isConnectToInternet(this)) {

                getCategoryVideo();
            } else {
                Toast.makeText(this, getResources().getString(R.string.internet_concation), Toast.LENGTH_SHORT).show();
            }
        } else {
            // binding.pagerhome.setVisibility(View.VISIBLE);
            binding.dots.setVisibility(View.VISIBLE);
            binding.homeBusinessName.setVisibility(View.VISIBLE);
            binding.homeFirstRecyclerview.setVisibility(View.VISIBLE);
            if (NetworkManager.isConnectToInternet(this)) {

                getslider();
            } else {
                Toast.makeText(this, getResources().getString(R.string.internet_concation), Toast.LENGTH_SHORT).show();
            }
        }
        getBusinessdata();
        binding.homeBusinessRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomDialog();
            }
        });

        binding.homeNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeScreenActivity.this, NotificationActivity.class));
            }
        });
        binding.demo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtube));
                startActivity(browserIntent);
            }
        });

    }

    public void getVersion() {

        parmsversion.put(Consts.USER_ID, userDTO.getUser_id());
        new HttpsRequest(Consts.GET_VERSION, parmsversion, HomeScreenActivity.this).stringPost(TAG, Consts.USER_CONTROLLER, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                if (flag) {
                    Log.e("version_res", "" + response.toString());
                    try {

                        Log.e("version_res", "" + response.toString());
                        int version = -1;

                        String update_image = "";
                        JSONObject jsonObjectmain = new JSONObject(String.valueOf(response));
                        JSONArray jsonArray = jsonObjectmain.getJSONArray("data");

                        String pname = getPackageName();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);

                            String sversion = obj.getString("version_code");
                            String serverpname = obj.getString("package_name");
                            if (serverpname.equalsIgnoreCase(pname)) {
                                try {
                                    PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                                    version = pInfo.versionCode;
                                    Log.e("version_track"," version :-- "+ pInfo.versionCode);

                                    update_image = obj.getString("update_image");
                                } catch (PackageManager.NameNotFoundException e) {
                                    e.printStackTrace();
                                }

                                if (sversion.equalsIgnoreCase(String.valueOf(version))) {
                                    Log.e("version_track","11");
                                } else {
                                    Log.e("version_track","1");
                                    dailograting.show();
                                    String thumbnailMq = "http://img.youtube.com/vi/" + "iUJ4Omb8lro" + "/mqdefault.jpg";

                                    Glide.with(HomeScreenActivity.this).load(update_image).into(img_sub_cat);

                                    /*img_sub_cat.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/iUJ4Omb8lro"));
                                            try {
                                                startActivity(webIntent);
                                            } catch (ActivityNotFoundException ex) {
                                            }


                                        }
                                    });*/

                                    tvupdateapp.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            final String appPackageName = getApplication().getPackageName();
                                            try {
                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                            } catch (ActivityNotFoundException anfe) {
                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                                            }
                                        }
                                    });

                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {

                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        cameraUtils.onActivityResult(requestCode, resultCode, data);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        cameraUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
                        int sstatus = jsonObject.getInt("status");
                        if (sstatus == 1) {

                            Type getpetDTO = new TypeToken<List<BusinessDataDto>>() {
                            }.getType();
                            businessdataList = (ArrayList<BusinessDataDto>) new Gson().fromJson(jsonObject.getJSONArray("data").toString(), getpetDTO);
                        } else if (sstatus == 3) {

                            Toast.makeText(HomeScreenActivity.this, message,
                                    LENGTH_LONG).show();

                            startActivity(new Intent(HomeScreenActivity.this, CheckSigninActivity.class));
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
    public void onBackPressed() {

        if (media_type) {
            startActivity(new Intent(getApplicationContext(), HomeScreenActivity.class));
            finish();
            //onBackPressed();
        } else {

            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click back again to exit", Toast.LENGTH_SHORT).show();

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }

    }

    private void getTextImageRecyclerData() {

        FirstHomeScreenAdapter screenAdapter = new FirstHomeScreenAdapter(this, firstAdapterListlist, this);
        binding.homeFirstRecyclerview.setLayoutManager(new GridLayoutManager(this, 2));
        binding.homeFirstRecyclerview.setAdapter(screenAdapter);

        binding.homeBusinessName.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.e("BUSINESS_HOME", " asda " + businessDataDto.toString() + " name " + businessDataDto.getName() + " image " + businessDataDto.getImage());
        binding.businessDialog.setText(businessDataDto.getName());
        if (!businessDataDto.getImage().equalsIgnoreCase("") || businessDataDto.getImage().isEmpty()) {
            Glide.with(getApplicationContext()).load(businessDataDto.getImage()).placeholder(R.drawable.image_gallery).into(binding.businessPic);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.nav_home:
                Intent intent31 = new Intent(this, HomeScreenActivity.class);
                // intent31.putExtra(Consts.MEDIA_TYPE_VIDEO,false);
                startActivity(intent31);
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

    public void getslider() {

        Log.e("tracker123", "1");

        Retrofit retrofit = apiClient.getClient(Consts.BASE_URL + Consts.USER_CONTROLLER);
        apiRest apiRest = retrofit.create(com.brandshaastra.api.apiRest.class);
        Call<ResponseBody> call = apiRest.getSlider(userDTO.getUser_id(), firebase.getString(Consts.DEVICE_TOKEN, ""));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        ResponseBody responseBody = response.body();
                        String str = responseBody.string();

                        Log.e("GET_SLIDER",""+str);
                        JSONObject jsonObject = new JSONObject(str);

                        String msg = jsonObject.getString("message");
                        int status = jsonObject.getInt("status");
                        Log.e("GET_SLIDER"," status "+status);


                        if (status == 1) {
                            youtube = jsonObject.getString("youtube");

                            silderarraylist = new ArrayList<>();
                            firstAdapterListlist = new ArrayList<>();

                            Type getpetDTO = new TypeToken<List<SiderModel>>() {
                            }.getType();
                            silderarraylist = (ArrayList<SiderModel>) new Gson().fromJson(jsonObject.getJSONArray("data").toString(), getpetDTO);

                            Type getDTO = new TypeToken<List<FirstAdapterImageModel>>() {
                            }.getType();


                            firstAdapterListlist = new Gson().fromJson(jsonObject.getJSONArray("image_data").toString(), getDTO);
                            Log.e("tracker123", "3" + silderarraylist.toString());
                            binding.horizontalcardviewpager.setAdapter(new SliderAdapter(HomeScreenActivity.this, silderarraylist));
                            binding.horizontalcardviewpager.startAutoScroll(true);
                            //  binding.pagerhome.setAdapter(new SliderAdapter(HomeScreenActivity.this, silderarraylist));
                            binding.dots.attachViewPager(binding.pagerhome);

                            Timer timer = new Timer();
                            timer.scheduleAtFixedRate(new SliderTimer(), 1000, 6000);
                            //getPopulerService();
                            getTextImageRecyclerData();

                            getCategory();
                            // Timer timer = new Timer();
                            // timer.scheduleAtFixedRate(new SliderTimer(), 4000, 6000);
                        } else if (status == 3) {
                            Toast.makeText(HomeScreenActivity.this, msg, Toast.LENGTH_SHORT).show();
                            prefrence.clearAllPreferences();
                            Intent intent = new Intent(HomeScreenActivity.this, CheckSigninActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            Toast.makeText(HomeScreenActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
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

    public void getCategory() {
        if (NetworkManager.isConnectToInternet(this)) {

            //   progressDialog.show();
            Retrofit retrofit = apiClient.getClient(Consts.BASE_URL + Consts.USER_CONTROLLER);
            apiRest api = retrofit.create(apiRest.class);
            Call<ResponseBody> callone = api.getAllCategory(userDTO.getUser_id(), firebase.getString(Consts.DEVICE_TOKEN, ""));
            callone.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    //            progressDialog.dismiss();
                    try {
                        if (response.isSuccessful()) {
                            ResponseBody responseBody = response.body();

                            String s = responseBody.string();
                            Log.e("CATALOG_CATEGORY", "" + s);
                            JSONObject object = new JSONObject(s);
                            List<CategoryDTO> homeOuterList = new ArrayList<>();


                            String message = object.getString("message");
                            int sstatus = object.getInt("status");

                            if (sstatus == 1) {
                                JSONArray jsonElements = object.getJSONArray("data");

                                homeOuterList = new Gson().fromJson(object.getJSONArray("data").toString(),
                                        new TypeToken<ArrayList<CategoryDTO>>() {
                                        }.getType());

                                homeScreenAdapter = new HomeScreenAdapter(HomeScreenActivity.this, homeOuterList, media_type);
                                binding.homeRecyclerview.setLayoutManager(new GridLayoutManager(HomeScreenActivity.this, 2));
                                binding.homeRecyclerview.setAdapter(homeScreenAdapter);

                            } else if (sstatus == 3) {

                                Toast.makeText(HomeScreenActivity.this, message,
                                        LENGTH_LONG).show();

                                startActivity(new Intent(HomeScreenActivity.this, CheckSigninActivity.class));
                                finish();
                            } else {
                                Toast.makeText(HomeScreenActivity.this, message, LENGTH_LONG).show();
                            }

                        } else {
                            /*Toast.makeText(HomeScreenActivity.this, "Try again. Server is not responding.",
                                    LENGTH_LONG).show();
*/
                            //        progressDialog.dismiss();

                         /*   Fragment fragment = new ServerNotRespondingFragment();
                            FragmentTransaction fragmentTransaction = HomeScreenActivity.this.getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                                    android.R.anim.fade_out);
                            fragmentTransaction.replace(R.id.frame, fragment, TAG);
                            fragmentTransaction.commitAllowingStateLoss();
*/
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    //       progressDialog.dismiss();
                    Log.d(TAG, t.getMessage());

                }
            });
        } else {
/*            Toast.makeText(HomeScreenActivity.this, "Check Your Internet connection and Try again ",
                    LENGTH_LONG).show();*/
            //        progressDialog.dismiss();

          /*  Fragment fragment = new ServerNotRespondingFragment();
            FragmentTransaction fragmentTransaction = HomeScreenActivity.this.getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                    android.R.anim.fade_out);
            fragmentTransaction.replace(R.id.frame, fragment, TAG);
            fragmentTransaction.commitAllowingStateLoss();
*/
        }
    }

    public void getCategoryVideo() {
        if (NetworkManager.isConnectToInternet(this)) {

            //   progressDialog.show();
            Retrofit retrofit = apiClient.getClient(Consts.BASE_URL + Consts.USER_CONTROLLER);
            apiRest api = retrofit.create(apiRest.class);
            Call<ResponseBody> callone = api.getAllCategoryVideo(userDTO.getUser_id(), firebase.getString(Consts.DEVICE_TOKEN, ""));
            callone.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    //            progressDialog.dismiss();
                    try {
                        if (response.isSuccessful()) {
                            ResponseBody responseBody = response.body();

                            String s = responseBody.string();
                            Log.e("CATALOG_CATEGORY", "" + s);
                            JSONObject object = new JSONObject(s);
                            List<CategoryDTO> homeOuterList = new ArrayList<>();


                            String message = object.getString("message");
                            int sstatus = object.getInt("status");

                            if (sstatus == 1) {
                                JSONArray jsonElements = object.getJSONArray("data");

                                homeOuterList = new Gson().fromJson(object.getJSONArray("data").toString(),
                                        new TypeToken<ArrayList<CategoryDTO>>() {
                                        }.getType());

                                homeScreenAdapter = new HomeScreenAdapter(HomeScreenActivity.this, homeOuterList, media_type);
                                binding.homeRecyclerview.setLayoutManager(new GridLayoutManager(HomeScreenActivity.this, 2));
                                binding.homeRecyclerview.setAdapter(homeScreenAdapter);

                            } else if (sstatus == 3) {

                                Toast.makeText(HomeScreenActivity.this, message,
                                        LENGTH_LONG).show();

                                startActivity(new Intent(HomeScreenActivity.this, CheckSigninActivity.class));
                                finish();
                            } else {
                                Toast.makeText(HomeScreenActivity.this, message, LENGTH_LONG).show();
                            }

                        } else {
                            /*Toast.makeText(HomeScreenActivity.this, "Try again. Server is not responding.",
                                    LENGTH_LONG).show();
*/
                            //        progressDialog.dismiss();

                         /*   Fragment fragment = new ServerNotRespondingFragment();
                            FragmentTransaction fragmentTransaction = HomeScreenActivity.this.getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                                    android.R.anim.fade_out);
                            fragmentTransaction.replace(R.id.frame, fragment, TAG);
                            fragmentTransaction.commitAllowingStateLoss();
*/
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    //       progressDialog.dismiss();
                    Log.d(TAG, t.getMessage());

                }
            });
        } else {
/*            Toast.makeText(HomeScreenActivity.this, "Check Your Internet connection and Try again ",
                    LENGTH_LONG).show();*/
            //        progressDialog.dismiss();

          /*  Fragment fragment = new ServerNotRespondingFragment();
            FragmentTransaction fragmentTransaction = HomeScreenActivity.this.getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                    android.R.anim.fade_out);
            fragmentTransaction.replace(R.id.frame, fragment, TAG);
            fragmentTransaction.commitAllowingStateLoss();
*/
        }
    }

    @Override
    public void onItemSelected(int positon, String id) {

        if (NetworkManager.isConnectToInternet(this)) {

            Log.e("b_data_tracker", "1");
            //   progressDialog.show();
            Retrofit retrofit = apiClient.getClient(Consts.BASE_URL + Consts.USER_CONTROLLER);
            apiRest api = retrofit.create(apiRest.class);
            Call<ResponseBody> callone = api.getBusinessDataById(userDTO.getUser_id(), id);
            callone.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    //            progressDialog.dismiss();
                    try {
                        if (response.isSuccessful()) {
                            ResponseBody responseBody = response.body();
                            Log.e("b_data_tracker", "2");

                            String s = responseBody.string();
                            Log.e("SELECTED_BUSINESSDATA", "" + s);
                            JSONObject object = new JSONObject(s);
                            Log.e("b_data_tracker", "3");

                            String message = object.getString("message");
                            int sstatus = object.getInt("status");

                            if (sstatus == 1) {
                                // JSONArray jsonElements = object.getJSONArray("data");
                                Log.e("b_data_tracker", "4");

                                businessDataDto = new Gson().fromJson(object.getJSONObject("data").toString(), BusinessDataDto.class);
                                Log.e("b_data_tracker", "5");

                                prefrence.setBusinessData(businessDataDto, Consts.BUSINESSDATA_DTO);

                                binding.businessDialog.setText(businessDataDto.getName());
                                Glide.with(HomeScreenActivity.this).load(businessDataDto.getImage()).placeholder(R.drawable.image_gallery).into(binding.businessPic);

                                bottomSheetDialog.dismiss();
                            } else if (sstatus == 3) {

                                Toast.makeText(HomeScreenActivity.this, message,
                                        LENGTH_LONG).show();

                                startActivity(new Intent(HomeScreenActivity.this, CheckSigninActivity.class));
                                finish();
                            } else {
                                Toast.makeText(HomeScreenActivity.this, message, LENGTH_LONG).show();
                            }

                        } else {
                            /*Toast.makeText(HomeScreenActivity.this, "Try again. Server is not responding.",
                                    LENGTH_LONG).show();
*/
                            //        progressDialog.dismiss();

                         /*   Fragment fragment = new ServerNotRespondingFragment();
                            FragmentTransaction fragmentTransaction = HomeScreenActivity.this.getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                                    android.R.anim.fade_out);
                            fragmentTransaction.replace(R.id.frame, fragment, TAG);
                            fragmentTransaction.commitAllowingStateLoss();
*/
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    //       progressDialog.dismiss();
                    Log.d(TAG, t.getMessage());

                }
            });
        } else {
/*            Toast.makeText(HomeScreenActivity.this, "Check Your Internet connection and Try again ",
                    LENGTH_LONG).show();*/
            //        progressDialog.dismiss();

          /*  Fragment fragment = new ServerNotRespondingFragment();
            FragmentTransaction fragmentTransaction = HomeScreenActivity.this.getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                    android.R.anim.fade_out);
            fragmentTransaction.replace(R.id.frame, fragment, TAG);
            fragmentTransaction.commitAllowingStateLoss();
*/
        }


    }


    @Override
    public void selectVideo() {

        cameraUtils.alertVideoSelcetion();

        //  startActivity(new Intent(HomeScreenActivity.this, ImageCanvasActivity.class).putExtra("video", true));
    }

    @Override
    public void onSuccess(List<ChosenImage> images) {

    }

    @Override
    public void onVideoSuccess(List<ChosenVideo> videos) {
        if (videos != null && videos.size() > 0) {
            Intent i = new Intent(HomeScreenActivity.this, ImageCanvasActivity.class);
            i.putExtra("video", true);
            i.putExtra("DATA", videos.get(0).getOriginalPath());
            //binding.ivProfilePic.setImageURI(Uri.fromFile(selectedImageFile));
            startActivity(i);

        }
    }

    @Override
    public void onError(String error) {

    }

    private class SliderTimer extends TimerTask {

        @Override
        public void run() {
            try {
                HomeScreenActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (binding.horizontalcardviewpager.getCurrentItem() < silderarraylist.size() - 1) {
                            binding.horizontalcardviewpager.setCurrentItem(binding.horizontalcardviewpager.getCurrentItem() + 1);
                        } else {
                            binding.horizontalcardviewpager.setCurrentItem(0);
                        }
                    }
                });
            } catch (Exception e) {

            }

        }
    }

    public void showBottomDialog() {


        dialogBinding.homeDialogClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        //homeDialogList = new ArrayList<>();
        //homeDialogList.add(new HomeDialogModel("raj", "https://images.unsplash.com/photo-1492144534655-ae79c964c9d7?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1447&q=80"));

        HomeBottomDialogAdapter dialogAdapter = new HomeBottomDialogAdapter(HomeScreenActivity.this, businessdataList, this);
        dialogBinding.homeBottomsheetRv.setLayoutManager(new LinearLayoutManager(this));
        dialogBinding.homeBottomsheetRv.setAdapter(dialogAdapter);
        bottomSheetDialog.show();
    }
}