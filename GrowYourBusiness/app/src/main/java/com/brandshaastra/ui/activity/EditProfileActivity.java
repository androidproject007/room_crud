package com.brandshaastra.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.brandshaastra.DTO.UserDTO;
import com.brandshaastra.R;
import com.brandshaastra.api.apiClient;
import com.brandshaastra.api.apiRest;
import com.brandshaastra.databinding.ActivityEditProfileBinding;
import com.brandshaastra.interfaces.Consts;
import com.brandshaastra.network.NetworkManager;
import com.brandshaastra.preferences.SharedPrefrence;
import com.brandshaastra.utils.ProjectUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.widget.Toast.LENGTH_LONG;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityEditProfileBinding binding;
    SharedPrefrence prefrence;
    UserDTO userDTO;

    String img_path = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(EditProfileActivity.this, R.layout.activity_edit_profile);
        getSupportActionBar().setTitle("Update Profile");
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        prefrence = SharedPrefrence.getInstance(this);
        userDTO = prefrence.getParentUser(Consts.USER_DTO);

        binding.editName.setText(userDTO.getName());
        Glide.with(getApplicationContext()).load(userDTO.getProfile_image()).placeholder(R.drawable.brand_shaastra_logo).into(binding.editProfileImg);

        binding.editProfileImg.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.edit_profile_img:

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

            case R.id.save:
                if (NetworkManager.isConnectToInternet(this)){
                    editData();
                }else {
                    Toast.makeText(this, getResources().getString(R.string.internet_concation), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public boolean validation(EditText editText, String msg) {
        if (!ProjectUtils.isEditTextFilled(editText)) {

            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

            return false;
        } else {
            return true;
        }
    }

    public void clickForSubmit() {

        if (!validation(binding.editName, getResources().getString(R.string.val_c_name))) {
            return;
        } else if (img_path.trim().isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.val_profile_pic), Toast.LENGTH_SHORT).show();

        } else {
            if (NetworkManager.isConnectToInternet(this)) {

                editData();
            } else {
                Toast.makeText(this, getResources().getString(R.string.internet_concation), Toast.LENGTH_SHORT).show();

                // ProjectUtils.showToast(this, getResources().getString(R.string.internet_concation));
            }
        }


    }


    private void editData() {

        MultipartBody.Part fileToSendfive = null;
        File image_file = new File(img_path);
        if (!img_path.trim().isEmpty()) {
            RequestBody requestBodyeight = RequestBody.create(MediaType.parse("multipart/form-data"), image_file);
            fileToSendfive = MultipartBody.Part.createFormData("image", image_file.getName(), requestBodyeight);

            Log.e("businessdata", "" + img_path);

        }

        RequestBody user_id123 = RequestBody.create(MediaType.parse("text/plain"), userDTO.getUser_id());

        Log.e("userrrrrr_idddd", "" + user_id123);
        
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), binding.editName.getText().toString());
        Log.e("businessdata", "" + name.toString());

        //fileToSendfive
        ProjectUtils.showProgressDialog(this, false, "Please Wait").show();

        Retrofit retrofit = apiClient.getClient(Consts.BASE_URL + Consts.USER_CONTROLLER);
        apiRest api = retrofit.create(apiRest.class);
        Call<ResponseBody> callone = api.editProfile(fileToSendfive, user_id123, name);
        callone.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtils.pauseProgressDialog();

                Log.e("businessdata", "" + response.message());
                try {
                    if (response.isSuccessful()) {

                        // Toast.makeText(this, "response get", Toast.LENGTH_SHORT).show();

                        ResponseBody responseBody = response.body();

                        String s = responseBody.string();
                        Log.e("register123otp_response", "" + s);

                        JSONObject object = new JSONObject(s);

                        String message = object.getString("message");
                        int sstatus = object.getInt("status");
                        if (sstatus == 1) {

                            try {
                                Toast.makeText(EditProfileActivity.this, message, Toast.LENGTH_SHORT).show();

                                onBackPressed();
                                finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        } else if (sstatus == 3){

                            Toast.makeText(EditProfileActivity.this, message,
                                    LENGTH_LONG).show();

                            startActivity(new Intent(EditProfileActivity.this,CheckSigninActivity.class));
                            finish();
                        }else {
                            //Toast.makeText(this, "response get3", Toast.LENGTH_SHORT).show();

                            Toast.makeText(EditProfileActivity.this, message,
                                    LENGTH_LONG).show();
                        }


                    } else {
                        ProjectUtils.cancelDialog();
                        Toast.makeText(EditProfileActivity.this, "Try Again Later ",
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
                Toast.makeText(EditProfileActivity.this, "Try again. Server is not responding",
                        LENGTH_LONG).show();
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == 23) {

                Uri imageUri = data.getData();
                img_path = imageUri.getPath();

                binding.editProfileImg.setImageURI(imageUri);
                Log.e("IMAGE_PATH", "" + img_path);
            }
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 24) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                ImagePicker.Companion.with(this)
                        .compress(1024)            //Final image size will be less than 1 MB(Optional)
                        .start(23);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, 24);
                }
            }
        }
    }
}