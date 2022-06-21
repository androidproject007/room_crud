package com.brandshaastra.ui.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.brandshaastra.DTO.BusinessDataDto;
import com.brandshaastra.DTO.UserDTO;
import com.brandshaastra.R;
import com.brandshaastra.api.apiClient;
import com.brandshaastra.api.apiRest;
import com.brandshaastra.databinding.ActivityUpdateBusinessBinding;
import com.brandshaastra.interfaces.Consts;
import com.brandshaastra.network.NetworkManager;
import com.brandshaastra.preferences.SharedPrefrence;
import com.brandshaastra.utils.ProjectUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.widget.Toast.LENGTH_LONG;

public class UpdateBusinessActivity extends AppCompatActivity implements View.OnClickListener {

    boolean edit = false;
    ActivityUpdateBusinessBinding binding;
    List<String> checkboxValueList = new ArrayList<>();
    public static HashMap<String, File> paramsFile;
    String img_path = "";
    String social_media_str = "";
    String business_profile_id, IMAGE, MOBILE, NAME, EMAIL, ADDRESS, WEBSITE, DESCRIPTION, FACEBOOK, YOUTUBE, INSTAGRAM, WHATSAPP, social_media = "";
    SharedPrefrence prefrence;
    UserDTO userDTO;
    BusinessDataDto businessDataDto;
    boolean check_box_click_flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(UpdateBusinessActivity.this, R.layout.activity_update_business);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        prefrence = SharedPrefrence.getInstance(this);
        userDTO = prefrence.getParentUser(Consts.USER_DTO);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        businessDataDto = prefrence.getBusinessData(Consts.BUSINESSDATA_DTO);

        edit = getIntent().getBooleanExtra("edit", false);
        if (edit) {
            getSupportActionBar().setTitle("Update Business");
            binding.updateSignupBtn.setText("UPDATE");
            business_profile_id = getIntent().getStringExtra(Consts.BUSINESS_ID);
            IMAGE = getIntent().getStringExtra(Consts.IMAGE);
            MOBILE = getIntent().getStringExtra(Consts.MOBILE);
            NAME = getIntent().getStringExtra(Consts.NAME);
            ADDRESS = getIntent().getStringExtra(Consts.ADDRESS);
            WEBSITE = getIntent().getStringExtra(Consts.WEBSITE);
            DESCRIPTION = getIntent().getStringExtra(Consts.DESCRIPTION);
            FACEBOOK = getIntent().getStringExtra(Consts.FACEBOOK);
            YOUTUBE = getIntent().getStringExtra(Consts.YOUTUBE);
            INSTAGRAM = getIntent().getStringExtra(Consts.INSTAGRAM);
            WHATSAPP = getIntent().getStringExtra(Consts.WHATSAPP);
            EMAIL = getIntent().getStringExtra(Consts.EMAIL);
            social_media = getIntent().getStringExtra("social_media_str");
            binding.updateDesignation.setVisibility(View.GONE);
            binding.updateReferalCode.setVisibility(View.GONE);

            Glide.with(this).load(IMAGE).placeholder(R.drawable.image_gallery).into(binding.updateComponeyImg);
            binding.updateEmail.setText(EMAIL);
            binding.updateMobile.setText(MOBILE);
            binding.updateName.setText(NAME);
            binding.updateWebsite.setText(WEBSITE);
            binding.updateAddress.setText(ADDRESS);
            binding.updateDescription.setText(DESCRIPTION);

            if (FACEBOOK.equalsIgnoreCase("1")) {
                binding.updateFacebook.setChecked(true);
            }
            if (INSTAGRAM.equalsIgnoreCase("1")) {
                binding.updateInsta.setChecked(true);
            }
            if (WHATSAPP.equalsIgnoreCase("1")) {
                binding.updateWhatsapp.setChecked(true);
            }
            if (YOUTUBE.equalsIgnoreCase("1")) {
                binding.updateYoutube.setChecked(true);
            }
            binding.updateEmail.setText(EMAIL);
        } else {
            getSupportActionBar().setTitle("Add Business");
            binding.updateSignupBtn.setText("SUBMIT");
        }

        setUiAction();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void setUiAction() {

        binding.updateComponeyImg.setOnClickListener(this);
        binding.updateSignupBtn.setOnClickListener(this);

        binding.updateFacebook.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                check_box_click_flag = true;
                if (isChecked) {
                    checkboxValueList.add("0");
                } else {
                    if (checkboxValueList.contains("0")) {

                        checkboxValueList.remove("0");
                    }
                }
            }
        });
        binding.updateInsta.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                check_box_click_flag = true;
                if (isChecked) {

                    checkboxValueList.add("1");
                } else {
                    if (checkboxValueList.contains("1")) {

                        checkboxValueList.remove("1");
                    }
                }
            }
        });
        binding.updateWhatsapp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                check_box_click_flag = true;
                if (isChecked) {
                    checkboxValueList.add("2");
                } else {
                    if (checkboxValueList.contains("2")) {

                        checkboxValueList.remove("2");
                    }
                }
            }
        });
        binding.updateYoutube.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                check_box_click_flag = true;
                if (isChecked) {
                    checkboxValueList.add("3");
                } else {
                    if (checkboxValueList.contains("3")) {

                        checkboxValueList.remove("3");
                    }
                }
            }
        });
    }

    public boolean validation(EditText editText, String msg) {
        if (!ProjectUtils.isEditTextFilled(editText)) {

            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == 23) {

                Uri imageUri = data.getData();
                img_path = imageUri.getPath();

                Glide.with(this).load(imageUri).placeholder(R.drawable.image_gallery).into(binding.updateComponeyImg);
                //binding.updateComponeyImg.setImageURI(imageUri);

                /*String path = FileUtility.getPath(this, imageUri);
                File file = new File(path);


                paramsFile = new HashMap<>();
                paramsFile.put(Consts.IMAGE, file);*/
            }
        }
    }

    public void clickForSubmit() {

        if (!ProjectUtils.isPhoneNumberValid(binding.updateMobile.getText().toString().trim())) {
            Toast.makeText(this, getResources().getString(R.string.val_mobile), Toast.LENGTH_SHORT).show();

        } else if (!validation(binding.updateName, getResources().getString(R.string.val_c_name))) {
            return;
        } else if (!ProjectUtils.isEmailValid(binding.updateEmail.getText().toString().trim())) {
            Toast.makeText(this, getResources().getString(R.string.val_email), Toast.LENGTH_SHORT).show();

        } else if (!ProjectUtils.isEditTextFilled(binding.updateAddress)) {
            Toast.makeText(this, getResources().getString(R.string.val_address), Toast.LENGTH_SHORT).show();

        } else if (!edit) {
            if (img_path.trim().isEmpty()) {
                Toast.makeText(this, getResources().getString(R.string.val_profile_pic), Toast.LENGTH_SHORT).show();
            }
        } else {
            if (NetworkManager.isConnectToInternet(this)) {


                if (check_box_click_flag) {

                    if (binding.updateFacebook.isChecked()) {
                        checkboxValueList.add("0");
                    } else {
                        checkboxValueList.remove("0");
                    }
                    if (binding.updateInsta.isChecked()) {
                        checkboxValueList.add("1");
                    } else {
                        checkboxValueList.remove("1");
                    }
                    if (binding.updateWhatsapp.isChecked()) {
                        checkboxValueList.add("2");
                    } else {
                        checkboxValueList.remove("2");
                    }
                    if (binding.updateYoutube.isChecked()) {
                        checkboxValueList.add("3");
                    } else {
                        checkboxValueList.remove("3");
                    }

                    StringBuffer sb = new StringBuffer();

                    for (String s : checkboxValueList) {
                        sb.append(s + ",");
                        sb.append("");
                    }
                    social_media_str = sb.toString();
                } else {
                    /*if (binding.updateFacebook.isChecked()){
                        checkboxValueList.add("0");
                    }else {
                        checkboxValueList.remove("0");
                    }if (binding.updateInsta.isChecked()){
                        checkboxValueList.add("1");
                    }else {
                        checkboxValueList.remove("1");
                    }if (binding.updateWhatsapp.isChecked()){
                        checkboxValueList.add("2");
                    }else {
                        checkboxValueList.remove("2");
                    }
                    if (binding.updateYoutube.isChecked()) {
                        checkboxValueList.add("3");
                    } else {
                        checkboxValueList.remove("3");
                    }

                    StringBuffer sb = new StringBuffer();

                    for (String s : checkboxValueList) {
                        sb.append(s + ",");
                        sb.append("");
                    }
                    */
                    social_media_str = social_media;
                }
                Log.e("checkbox", "" + social_media_str);

                Log.e("checkbox", " update ma gayu");

                updateData();

            } else {
                Toast.makeText(this, getResources().getString(R.string.internet_concation), Toast.LENGTH_SHORT).show();

                // ProjectUtils.showToast(this, getResources().getString(R.string.internet_concation));
            }
        }


    }

    private void updateData() {


        Log.e("update", "update called");

        MultipartBody.Part fileToSendfive = null;

        Log.e("img_path", " img_path " + img_path);

        File image_file = new File(img_path);
        if (!img_path.trim().isEmpty()) {

            RequestBody requestBodyeight = RequestBody.create(MediaType.parse("multipart/form-data"), image_file);
            fileToSendfive = MultipartBody.Part.createFormData("image", image_file.getName(), requestBodyeight);

            Log.e("businessdata", " in if conf" + img_path);

        }

        RequestBody business_id123 = RequestBody.create(MediaType.parse("text/plain"), business_profile_id);
        RequestBody user_id123 = RequestBody.create(MediaType.parse("text/plain"), userDTO.getUser_id());
        //RequestBody user_id123 = RequestBody.create(MediaType.parse("text/plain"), businessDataDto.getBussiness_profile_id());

        Log.e("userrrrrr_idddd", " business_profile_id " + user_id123);
        RequestBody mobile = RequestBody.create(MediaType.parse("text/plain"), binding.updateMobile.getText().toString());
        Log.e("businessdata", " mobile " + binding.updateMobile.getText().toString() + " " + mobile.toString());

        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), binding.updateName.getText().toString());
        Log.e("businessdata", " name " + binding.updateName.getText().toString() + " " + name.toString());

        RequestBody email = RequestBody.create(MediaType.parse("text/plain"), binding.updateEmail.getText().toString());
        Log.e("businessdata", " email " + binding.updateEmail.getText().toString() + " " + email.toString());

        RequestBody address = RequestBody.create(MediaType.parse("text/plain"), binding.updateAddress.getText().toString());
        Log.e("businessdata", " address " + binding.updateAddress.getText().toString() + " " + address.toString());

        RequestBody website = RequestBody.create(MediaType.parse("text/plain"), binding.updateWebsite.getText().toString());
        Log.e("businessdata", " website " + binding.updateAddress.getText().toString() + " " + website.toString());

        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), binding.updateDescription.getText().toString());
        Log.e("businessdata", " description " + binding.updateDescription.getText().toString() + " " + description.toString());

        RequestBody social_media_str2 = RequestBody.create(MediaType.parse("text/plain"), social_media_str);
        Log.e("businessdata", " designation " + social_media_str + " " + social_media_str2.toString());
        //fileToSendfive
        ProjectUtils.showProgressDialog(this, false, "Please Wait").show();


        Log.e("Update_PARAMS: --", " userid:-- " + userDTO.getUser_id() + "mobile" + binding.updateMobile.getText().toString()
                + "name " +
                binding.updateName.getText().toString() + "email " +
                binding.updateEmail.getText().toString() + " address/city " +
                binding.updateAddress.getText().toString() + " website " +
                binding.updateWebsite.getText().toString() + " description " +
                binding.updateDescription.getText().toString() + " designation " +
                binding.updateDesignation.getText().toString() + " referal code  " +
                binding.updateReferalCode.getText().toString() + " social media " +
                social_media_str);
        Log.e("update123", "1");
        Retrofit retrofit = apiClient.getClient(Consts.BASE_URL + Consts.USER_CONTROLLER);
        Log.e("update123", "2");
        apiRest api = retrofit.create(apiRest.class);
        Log.e("update123", "3");
        Call<ResponseBody> callone = api.update_data(fileToSendfive, business_id123, user_id123, mobile, name, email, address, website, description, social_media_str2);
        callone.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtils.pauseProgressDialog();
                Log.e("update123", "4");
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
                                //Toast.makeText(this, "response get2", Toast.LENGTH_SHORT).show();

                                if (businessDataDto.getBussiness_profile_id().equalsIgnoreCase(business_profile_id)) {

                                    businessDataDto = new Gson().fromJson(object.getJSONObject("data").toString(), new TypeToken<BusinessDataDto>() {
                                    }.getType());
                                    Log.e("BUS--------", "" + businessDataDto.getName());
                                    prefrence.setBusinessData(businessDataDto, Consts.BUSINESSDATA_DTO);
                                }

                                Intent intent = new Intent(UpdateBusinessActivity.this, HomeScreenActivity.class);
                                startActivity(intent);
                                finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        } else if (sstatus == 3) {

                            Toast.makeText(UpdateBusinessActivity.this, message,
                                    LENGTH_LONG).show();

                            startActivity(new Intent(UpdateBusinessActivity.this, CheckSigninActivity.class));
                            finish();
                        } else {
                            //Toast.makeText(this, "response get3", Toast.LENGTH_SHORT).show();

                            Toast.makeText(UpdateBusinessActivity.this, message,
                                    LENGTH_LONG).show();
                        }


                    } else {
                        ProjectUtils.pauseProgressDialog();
                        Log.e("ERROR", "" + response.errorBody().string());
                        Toast.makeText(UpdateBusinessActivity.this, "Try Again Later ",
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
                ProjectUtils.pauseProgressDialog();
                Toast.makeText(UpdateBusinessActivity.this, "Try again. Server is not responding",
                        LENGTH_LONG).show();
            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.update_componeyImg:
                ImagePicker.Companion.with(this)
                        .compress(1024)            //Final image size will be less than 1 MB(Optional)
                        .start(23);
                break;

            case R.id.update_signupBtn:
                if (NetworkManager.isConnectToInternet(this)) {

                    clickForSubmit();
                } else {
                    Toast.makeText(this, getResources().getString(R.string.internet_concation), Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }


}