package com.brandshaastra.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.brandshaastra.DTO.UserDTO;
import com.brandshaastra.R;
import com.brandshaastra.api.apiClient;
import com.brandshaastra.api.apiRest;
import com.brandshaastra.databinding.ActivityProductBinding;
import com.brandshaastra.interfaces.Consts;
import com.brandshaastra.network.NetworkManager;
import com.brandshaastra.preferences.SharedPrefrence;
import com.brandshaastra.ui.adapter.ProductAdapter;
import com.brandshaastra.ui.model.ImageModel;
import com.brandshaastra.ui.model.VideoModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.widget.Toast.LENGTH_LONG;

public class ProductActivity extends AppCompatActivity {

    private SharedPreferences firebase;
    String subcat_id = "";
    String subcat_name = "";
    ActivityProductBinding binding;
    ProductAdapter productAdapter;
    List<ImageModel> imageList;
    List<VideoModel> videoList;
    private SharedPrefrence prefrence;
    String cat_tracker = "";
    String category_name = "";
    boolean media_type = false;
    UserDTO userDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(ProductActivity.this, R.layout.activity_product);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        firebase = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        prefrence = SharedPrefrence.getInstance(this);
        userDTO = prefrence.getParentUser(Consts.USER_DTO);
        if (getIntent().hasExtra(Consts.SUBCAT_ID)) {

            subcat_id = getIntent().getStringExtra(Consts.SUBCAT_ID);
            subcat_name = getIntent().getStringExtra(Consts.SUBCAT_NAME);
            category_name = getIntent().getStringExtra(Consts.CATEGORY_NAME);
            cat_tracker = getIntent().getStringExtra(Consts.TRACKER);
            media_type = getIntent().getBooleanExtra(Consts.MEDIA_TYPE_VIDEO, false);
            Log.e("CATEGORY_NAME_tracker", " productactivity " + category_name+" tracker:-- "+cat_tracker);

            getSupportActionBar().setTitle(subcat_name);

        }
        //setContentView(R.layout.activity_product);

        if (media_type) {
            if (NetworkManager.isConnectToInternet(this)) {
                getVideoContent();
            } else {
                Toast.makeText(this, getResources().getString(R.string.internet_concation), Toast.LENGTH_SHORT).show();
            }

        } else {
            if (NetworkManager.isConnectToInternet(this)) {

                getProduct();
            } else {
                Toast.makeText(this, getResources().getString(R.string.internet_concation), Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void getVideoContent() {

       /* imageList = new ArrayList<>();
        imageList.add(new ImageModel("101", "23", "24", "SAS", "https://webknight.co.in/grow_your_bussiness/assets/video/video1.mp4"));
        imageList.add(new ImageModel("101", "23", "24", "SAS", "https://webknight.co.in/grow_your_bussiness/assets/video/video2.mp4"));
       */

        if (NetworkManager.isConnectToInternet(this)) {

            //   progressDialog.show();
            Retrofit retrofit = apiClient.getClient(Consts.BASE_URL + Consts.USER_CONTROLLER);
            apiRest api = retrofit.create(apiRest.class);
            Log.e("GET_VIDEO",""+userDTO.getUser_id()+" "+subcat_id);
            Call<ResponseBody> callone = api.getAllSubCategoryVideo(userDTO.getUser_id(), subcat_id);
            callone.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    //            progressDialog.dismiss();
                    try {
                        if (response.isSuccessful()) {
                            ResponseBody responseBody = response.body();

                            String s = responseBody.string();
                            Log.e("imagesVIDEOWALA", "" + s);
                            JSONObject object = new JSONObject(s);
                            videoList = new ArrayList<>();

                            String message = object.getString("message");
                            int sstatus = object.getInt("status");

                            if (sstatus == 1) {

                                imageList = new Gson().fromJson(object.getJSONArray("data").toString(),
                                        new TypeToken<ArrayList<ImageModel>>() {
                                        }.getType());


                                productAdapter = new ProductAdapter(ProductActivity.this, imageList,cat_tracker, category_name, media_type);
                                binding.productRv.setLayoutManager(new GridLayoutManager(ProductActivity.this, 2));
                                binding.productRv.setAdapter(productAdapter);

                            } else if (sstatus == 3) {
                                Toast.makeText(ProductActivity.this, message, LENGTH_LONG).show();
                                startActivity(new Intent(ProductActivity.this, CheckSigninActivity.class));
                                finish();
                            } else {
                                Toast.makeText(ProductActivity.this, message, LENGTH_LONG).show();
                            }

                        } else {

        /*Toast.makeText(ProductActivity.this, "Try again. Server is not responding.",
                                    LENGTH_LONG).show();
*//*
                            //        progressDialog.dismiss();

                         *//*   Fragment fragment = new ServerNotRespondingFragment();
                            FragmentTransaction fragmentTransaction = ProductActivity.this.getSupportFragmentManager().beginTransaction();
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

                }
            });
        } else {
/*            Toast.makeText(ProductActivity.this, "Check Your Internet connection and Try again ",
                    LENGTH_LONG).show();*//*
            //        progressDialog.dismiss();

          *//*  Fragment fragment = new ServerNotRespondingFragment();
            FragmentTransaction fragmentTransaction = ProductActivity.this.getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                    android.R.anim.fade_out);
            fragmentTransaction.replace(R.id.frame, fragment, TAG);
            fragmentTransaction.commitAllowingStateLoss();
*/
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void getProduct() {
        if (NetworkManager.isConnectToInternet(this)) {

            //   progressDialog.show();
            Retrofit retrofit = apiClient.getClient(Consts.BASE_URL + Consts.USER_CONTROLLER);
            apiRest api = retrofit.create(apiRest.class);
            Call<ResponseBody> callone = api.getAllSubCategoryImage(userDTO.getUser_id(), subcat_id);
            callone.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    //            progressDialog.dismiss();
                    try {
                        if (response.isSuccessful()) {
                            ResponseBody responseBody = response.body();

                            String s = responseBody.string();
                            Log.e("imagesshivlo", "" + s);
                            JSONObject object = new JSONObject(s);
                            imageList = new ArrayList<>();

                            String message = object.getString("message");
                            int sstatus = object.getInt("status");

                            if (sstatus == 1) {

                                imageList = new Gson().fromJson(object.getJSONArray("data").toString(),
                                        new TypeToken<ArrayList<ImageModel>>() {
                                        }.getType());


                                productAdapter = new ProductAdapter(ProductActivity.this, imageList,cat_tracker, category_name, media_type);
                                binding.productRv.setLayoutManager(new GridLayoutManager(ProductActivity.this, 2));
                                binding.productRv.setAdapter(productAdapter);

                            } else if (sstatus == 3) {

                                Toast.makeText(ProductActivity.this, message,
                                        LENGTH_LONG).show();

                                startActivity(new Intent(ProductActivity.this, CheckSigninActivity.class));
                                finish();
                            } else {
                                Toast.makeText(ProductActivity.this, message, LENGTH_LONG).show();
                            }

                        } else {
                            /*Toast.makeText(ProductActivity.this, "Try again. Server is not responding.",
                                    LENGTH_LONG).show();
*/
                            //        progressDialog.dismiss();

                         /*   Fragment fragment = new ServerNotRespondingFragment();
                            FragmentTransaction fragmentTransaction = ProductActivity.this.getSupportFragmentManager().beginTransaction();
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

                }
            });
        } else {
/*            Toast.makeText(ProductActivity.this, "Check Your Internet connection and Try again ",
                    LENGTH_LONG).show();*/
            //        progressDialog.dismiss();

          /*  Fragment fragment = new ServerNotRespondingFragment();
            FragmentTransaction fragmentTransaction = ProductActivity.this.getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                    android.R.anim.fade_out);
            fragmentTransaction.replace(R.id.frame, fragment, TAG);
            fragmentTransaction.commitAllowingStateLoss();
*/
        }
    }

}