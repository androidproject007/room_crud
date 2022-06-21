package com.brandshaastra.ui.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.gson.Gson;
import com.brandshaastra.DTO.BusinessDataDto;
import com.brandshaastra.DTO.UserDTO;
import com.brandshaastra.R;
import com.brandshaastra.api.apiClient;
import com.brandshaastra.api.apiRest;
import com.brandshaastra.databinding.ActivityBusinessDocumentBinding;
import com.brandshaastra.interfaces.Consts;
import com.brandshaastra.network.NetworkManager;
import com.brandshaastra.preferences.SharedPrefrence;
import com.brandshaastra.utils.FileUtility;
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

public class BusinessDocumentActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityBusinessDocumentBinding binding;
    String checkbox_str = "";
    List<String> checkboxValueList = new ArrayList<>();
    public static HashMap<String, File> paramsFile;
    String img_path = "";
    String social_media_str = "";
    SharedPrefrence prefrence;
    UserDTO userDTO;
    BusinessDataDto businessDataDto;
    private SharedPreferences firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(BusinessDocumentActivity.this, R.layout.activity_business_document);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        prefrence = SharedPrefrence.getInstance(this);
        userDTO = prefrence.getParentUser(Consts.USER_DTO);
        firebase = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        getSupportActionBar().setTitle("Business Information");

        Log.e("business"," user_id "+userDTO.getUser_id());
        /*findViewById(R.id.signup_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BusinessDocumentActivity.this, HomeScreenActivity.class));
            }
        });*/

        setUiAction();

    }

    public void setUiAction() {

        binding.componeyImg.setOnClickListener(this);
        binding.signupBtn.setOnClickListener(this);

        binding.signupFacebook.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkboxValueList.add("0");
                } else {
                    if (checkboxValueList.contains("0")) {

                        checkboxValueList.remove("0");
                    }
                }
            }
        });
        binding.signupInsta.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkboxValueList.add("1");
                } else {
                    if (checkboxValueList.contains("1")) {

                        checkboxValueList.remove("1");
                    }
                }
            }
        });
        binding.signupWhatsapp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkboxValueList.add("2");
                } else {
                    if (checkboxValueList.contains("2")) {

                        checkboxValueList.remove("2");
                    }
                }
            }
        });
        binding.signupYoutube.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
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

                binding.componeyImg.setImageURI(imageUri);

                String path = FileUtility.getPath(this, imageUri);
                File file = new File(path);


                paramsFile = new HashMap<>();
                paramsFile.put(Consts.IMAGE, file);
            }
        }
    }

    public void clickForSubmit() {

        if (!ProjectUtils.isPhoneNumberValid(binding.signupCMobile.getText().toString().trim())) {
            Toast.makeText(this, getResources().getString(R.string.val_mobile), Toast.LENGTH_SHORT).show();

        } else if (!validation(binding.signupCName, getResources().getString(R.string.val_c_name))) {
            return;
        } else if (!ProjectUtils.isEmailValid(binding.signupCEmail.getText().toString().trim())) {
            Toast.makeText(this, getResources().getString(R.string.val_email), Toast.LENGTH_SHORT).show();

        } else if (!ProjectUtils.isEditTextFilled(binding.signupCAddress)) {
            Toast.makeText(this, getResources().getString(R.string.val_address), Toast.LENGTH_SHORT).show();

        } else if (img_path.trim().isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.val_profile_pic), Toast.LENGTH_SHORT).show();

        } else {
            if (NetworkManager.isConnectToInternet(this)) {

                StringBuffer sb = new StringBuffer();

                for (String s : checkboxValueList) {
                    sb.append(s + ",");
                    sb.append("");
                }
                social_media_str = sb.toString();
                Log.e("checkbox", "" + social_media_str);
                sendBusinessData();
            } else {
                Toast.makeText(this, getResources().getString(R.string.internet_concation), Toast.LENGTH_SHORT).show();

                // ProjectUtils.showToast(this, getResources().getString(R.string.internet_concation));
            }
        }


    }

    private void sendBusinessData() {

        MultipartBody.Part fileToSendfive = null;
        File image_file = new File(img_path);
        if (!img_path.trim().isEmpty()) {
            RequestBody requestBodyeight = RequestBody.create(MediaType.parse("multipart/form-data"), image_file);
            fileToSendfive = MultipartBody.Part.createFormData("image", image_file.getName(), requestBodyeight);

            Log.e("businessdata", "" + img_path);

        }

        RequestBody user_id123 = RequestBody.create(MediaType.parse("text/plain"), userDTO.getUser_id());

        Log.e("userrrrrr_idddd",""+user_id123);
        RequestBody mobile = RequestBody.create(MediaType.parse("text/plain"), binding.signupCMobile.getText().toString());
        Log.e("businessdata", "" + mobile.toString());

        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), binding.signupCName.getText().toString());
        Log.e("businessdata", "" + name.toString());

        RequestBody email = RequestBody.create(MediaType.parse("text/plain"), binding.signupCEmail.getText().toString());
        Log.e("businessdata", "" + email.toString());

        RequestBody address = RequestBody.create(MediaType.parse("text/plain"), binding.signupCAddress.getText().toString());
        Log.e("businessdata", "" + address.toString());

        RequestBody website = RequestBody.create(MediaType.parse("text/plain"), binding.signupCWebsite.getText().toString());
        Log.e("businessdata", "" + website.toString());

        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), binding.signupDescription.getText().toString());
        Log.e("businessdata", "" + description.toString());

        RequestBody designation = RequestBody.create(MediaType.parse("text/plain"), binding.signupCDesignation.getText().toString());
        Log.e("businessdata", "" + designation.toString());

        RequestBody referal_code = RequestBody.create(MediaType.parse("text/plain"), binding.signupCReferalCode.getText().toString());
        Log.e("businessdata", "" + referal_code.toString());

        RequestBody social_media_str2 = RequestBody.create(MediaType.parse("text/plain"), social_media_str);
        Log.e("businessdata", "" + social_media_str2.toString());
        //fileToSendfive
        ProjectUtils.showProgressDialog(this, false, "Please Wait").show();

        Log.e("PARAMS: --"," userid:-- "+userDTO.getUser_id()+"mobile"+binding.signupCMobile.getText().toString()
                        +"name " +
        binding.signupCName.getText().toString()+"email " +
        binding.signupCEmail.getText().toString()+" address/city " +
        binding.signupCAddress.getText().toString()+ " website " +
        binding.signupCWebsite.getText().toString()+" description " +
        binding.signupDescription.getText().toString()+" designation " +
        binding.signupCDesignation.getText().toString()+" referal code  "+
        binding.signupCReferalCode.getText().toString()+" social media "+
        social_media_str);
        Retrofit retrofit = apiClient.getClient(Consts.BASE_URL + Consts.USER_CONTROLLER);
        apiRest api = retrofit.create(apiRest.class);
        Call<ResponseBody> callone = api.upload_business_data(fileToSendfive,user_id123,mobile, name, email, address, website, description, designation, social_media_str2, referal_code);
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
                                //Toast.makeText(this, "response get2", Toast.LENGTH_SHORT).show();

                             //   businessDataDto = new Gson().fromJson(response.getJSONObject("data").toString(), UserDTO.class);

                                businessDataDto = new Gson().fromJson(object.getJSONObject("data").toString(), BusinessDataDto.class);
                                Log.e("BUSINESS_HOME"," asda "+businessDataDto.toString()+" name "+businessDataDto.getName() +" image "+businessDataDto.getImage());

                                prefrence.setBusinessData(businessDataDto,Consts.BUSINESSDATA_DTO);

                                BusinessDataDto b = prefrence.getBusinessData(Consts.BUSINESSDATA_DTO);

                                Log.e("BUSINESS_HOME"," asda "+b.toString()+" name "+b.getName() +" image "+b.getImage());

                                Intent intent = new Intent(BusinessDocumentActivity.this,HomeScreenActivity.class);
                                startActivity(intent);
                                finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }
                        else if (sstatus == 3) {

                            Toast.makeText(BusinessDocumentActivity.this,message ,
                                    LENGTH_LONG).show();

                            startActivity(new Intent(BusinessDocumentActivity.this, CheckSigninActivity.class));
                            finish();
                        }


                        else {
                            //Toast.makeText(this, "response get3", Toast.LENGTH_SHORT).show();

                            Toast.makeText(BusinessDocumentActivity.this, message,
                                    LENGTH_LONG).show();
                        }


                    } else {
                        ProjectUtils.cancelDialog();
                        Toast.makeText(BusinessDocumentActivity.this, "Try Again Later ",
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
                Toast.makeText(BusinessDocumentActivity.this, "Try again. Server is not responding",
                        LENGTH_LONG).show();
            }
        });


    }

    public void sendSignupData() {

        ProjectUtils.showProgressDialog(this, false, "Please Wait").show();
        Retrofit retrofit = apiClient.getClient(Consts.BASE_URL + Consts.USER_CONTROLLER);
        apiRest api = retrofit.create(apiRest.class);
        Call<ResponseBody> callone = api.sendOtp2(binding.signupCEmail.toString());
        callone.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtils.cancelDialog();

                try {
                    if (response.isSuccessful()) {

                        // Toast.makeText(this, "response get", Toast.LENGTH_SHORT).show();

                        ResponseBody responseBody = response.body();

                        String s = responseBody.string();
                        Log.e("register123otp_response", "" + s);

                        JSONObject object = new JSONObject(s);

                        String message = object.getString("message");
                        int sstatus = object.getInt("status");
                        String is_varified = object.getString("is_verified");
                        if (sstatus == 1) {

                            try {
                                //Toast.makeText(this, "response get2", Toast.LENGTH_SHORT).show();

                                String otp = object.getString("otp");

                                //startActivity(intent);
                                finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        } else {
                            //Toast.makeText(this, "response get3", Toast.LENGTH_SHORT).show();

                            Toast.makeText(BusinessDocumentActivity.this, message,
                                    LENGTH_LONG).show();
                        }


                    } else {
                        ProjectUtils.cancelDialog();
                        Toast.makeText(BusinessDocumentActivity.this, "Try Again Later ",
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
                Toast.makeText(BusinessDocumentActivity.this, "Try again. Server is not responding",
                        LENGTH_LONG).show();
            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.componey_img:
                ImagePicker.Companion.with(this)
                        .compress(1024)            //Final image size will be less than 1 MB(Optional)
                        .start(23);
                break;

            case R.id.signup_btn:

                if (NetworkManager.isConnectToInternet(this)){

                    clickForSubmit();
                }
                else {
                    Toast.makeText(this, getResources().getString(R.string.internet_concation), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


}