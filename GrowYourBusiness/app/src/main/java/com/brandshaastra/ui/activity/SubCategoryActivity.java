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
import com.brandshaastra.DTO.SubCategoryDTO;
import com.brandshaastra.DTO.UserDTO;
import com.brandshaastra.R;
import com.brandshaastra.preferences.SharedPrefrence;
import com.brandshaastra.ui.adapter.SubCatAdapter;
import com.brandshaastra.api.apiClient;
import com.brandshaastra.api.apiRest;
import com.brandshaastra.databinding.ActivitySubCategoryBinding;
import com.brandshaastra.interfaces.Consts;
import com.brandshaastra.network.NetworkManager;

import org.json.JSONArray;
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

public class SubCategoryActivity extends AppCompatActivity {
    private SharedPreferences firebase;
    ActivitySubCategoryBinding binding;
    SubCatAdapter subCatAdapter;
    private SharedPrefrence prefrence;
    String cat_id = "";
    String cat_name = "";
    String cat_tracker = "";
    boolean media_type;
    UserDTO userDTO;
    List<SubCategoryDTO> subcatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(SubCategoryActivity.this, R.layout.activity_sub_category);
        //setContentView(R.layout.activity_sub_category);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        cat_id = getIntent().getStringExtra("cat_id");
        cat_name = getIntent().getStringExtra(Consts.CATEGORY_NAME);
        cat_tracker = getIntent().getStringExtra(Consts.TRACKER);
        media_type = getIntent().getBooleanExtra(Consts.MEDIA_TYPE_VIDEO, false);
        Log.e("CATEGORY_NAME/cat_track", " subcategoryActivity " + cat_name + " tracker:-- " + cat_tracker);
        getSupportActionBar().setTitle(cat_name);
        firebase = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        prefrence = SharedPrefrence.getInstance(this);
        userDTO = prefrence.getParentUser(Consts.USER_DTO);

        if (media_type) {
            getProductVideo();
        } else {

            getProduct();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void getProduct() {
        if (NetworkManager.isConnectToInternet(this)) {

            Log.e("SUBCAT_UID", "" + userDTO.getUser_id());
            //   progressDialog.show();
            Retrofit retrofit = apiClient.getClient(Consts.BASE_URL + Consts.USER_CONTROLLER);
            apiRest api = retrofit.create(apiRest.class);
            Call<ResponseBody> callone = api.getAllSubCategory(userDTO.getUser_id(), cat_id);
            callone.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    //            progressDialog.dismiss();
                    try {
                        if (response.isSuccessful()) {
                            ResponseBody responseBody = response.body();

                            String s = responseBody.string();
                            Log.e("CATALOG_SUB____CATEGORY", "" + s);
                            JSONObject object = new JSONObject(s);
                            subcatList = new ArrayList<>();

                            String message = object.getString("message");
                            int sstatus = object.getInt("status");

                            if (sstatus == 1) {
                                JSONArray jsonElements = object.getJSONArray("data");

                                subcatList = new Gson().fromJson(object.getJSONArray("data").toString(),
                                        new TypeToken<ArrayList<SubCategoryDTO>>() {
                                        }.getType());

                                subCatAdapter = new SubCatAdapter(SubCategoryActivity.this, subcatList, cat_tracker, cat_name, media_type);
                                binding.subcategoryRv.setLayoutManager(new GridLayoutManager(SubCategoryActivity.this, 2));
                                binding.subcategoryRv.setAdapter(subCatAdapter);
                                //   binding.subcategoryRv.setExpanded(true);

                            } else if (sstatus == 3) {

                                Toast.makeText(SubCategoryActivity.this, message,
                                        LENGTH_LONG).show();

                                startActivity(new Intent(SubCategoryActivity.this, CheckSigninActivity.class));
                                finish();
                            } else {
                                Toast.makeText(SubCategoryActivity.this, message, LENGTH_LONG).show();
                            }

                        } else {
                            /*Toast.makeText(SubCategoryActivity.this, "Try again. Server is not responding.",
                                    LENGTH_LONG).show();
*/
                            //        progressDialog.dismiss();

                         /*   Fragment fragment = new ServerNotRespondingFragment();
                            FragmentTransaction fragmentTransaction = SubCategoryActivity.this.getSupportFragmentManager().beginTransaction();
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
/*            Toast.makeText(SubCategoryActivity.this, "Check Your Internet connection and Try again ",
                    LENGTH_LONG).show();*/
            //        progressDialog.dismiss();

          /*  Fragment fragment = new ServerNotRespondingFragment();
            FragmentTransaction fragmentTransaction = SubCategoryActivity.this.getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                    android.R.anim.fade_out);
            fragmentTransaction.replace(R.id.frame, fragment, TAG);
            fragmentTransaction.commitAllowingStateLoss();
*/
        }
    }


    public void getProductVideo() {
        if (NetworkManager.isConnectToInternet(this)) {

            Log.e("SUBCAT_UID", "" + userDTO.getUser_id());
            //   progressDialog.show();
            Retrofit retrofit = apiClient.getClient(Consts.BASE_URL + Consts.USER_CONTROLLER);
            apiRest api = retrofit.create(apiRest.class);
            Call<ResponseBody> callone = api.getAllSubCatVideo(userDTO.getUser_id(), cat_id);
            callone.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    //            progressDialog.dismiss();
                    try {
                        if (response.isSuccessful()) {
                            ResponseBody responseBody = response.body();
                            String s = responseBody.string();
                            Log.e("CATALOG_SUB____CATEGORYVIDEO", "" + s);
                            JSONObject object = new JSONObject(s);
                            subcatList = new ArrayList<>();

                            String message = object.getString("message");
                            int sstatus = object.getInt("status");

                            if (sstatus == 1) {
                                JSONArray jsonElements = object.getJSONArray("data");

                                subcatList = new Gson().fromJson(object.getJSONArray("data").toString(),
                                        new TypeToken<ArrayList<SubCategoryDTO>>() {
                                        }.getType());

                                subCatAdapter = new SubCatAdapter(SubCategoryActivity.this, subcatList, cat_tracker, cat_name, media_type);
                                binding.subcategoryRv.setLayoutManager(new GridLayoutManager(SubCategoryActivity.this, 2));
                                binding.subcategoryRv.setAdapter(subCatAdapter);
                                //   binding.subcategoryRv.setExpanded(true);

                            } else if (sstatus == 3) {

                                Toast.makeText(SubCategoryActivity.this, message,
                                        LENGTH_LONG).show();

                                startActivity(new Intent(SubCategoryActivity.this, CheckSigninActivity.class));
                                finish();
                            } else {
                                Toast.makeText(SubCategoryActivity.this, message, LENGTH_LONG).show();
                            }

                        } else {
                            /*Toast.makeText(SubCategoryActivity.this, "Try again. Server is not responding.",
                                    LENGTH_LONG).show();
*/
                            //        progressDialog.dismiss();

                         /*   Fragment fragment = new ServerNotRespondingFragment();
                            FragmentTransaction fragmentTransaction = SubCategoryActivity.this.getSupportFragmentManager().beginTransaction();
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
/*            Toast.makeText(SubCategoryActivity.this, "Check Your Internet connection and Try again ",
                    LENGTH_LONG).show();*/
            //        progressDialog.dismiss();

          /*  Fragment fragment = new ServerNotRespondingFragment();
            FragmentTransaction fragmentTransaction = SubCategoryActivity.this.getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                    android.R.anim.fade_out);
            fragmentTransaction.replace(R.id.frame, fragment, TAG);
            fragmentTransaction.commitAllowingStateLoss();
*/
        }
    }

}