package com.brandshaastra.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.brandshaastra.DTO.UserDTO;
import com.brandshaastra.R;
import com.brandshaastra.databinding.ActivityAccountBinding;
import com.brandshaastra.https.HttpsRequest;
import com.brandshaastra.interfaces.Consts;
import com.brandshaastra.interfaces.Helper;
import com.brandshaastra.preferences.SharedPrefrence;

import org.json.JSONObject;

import java.util.HashMap;

public class AccountActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    ActivityAccountBinding binding;
    private HashMap<String, String> params = new HashMap<>();
    private SharedPrefrence prefrence;
    UserDTO userDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(AccountActivity.this, R.layout.activity_account);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
//        BottomNavigationViewHelper.disableShiftMode(binding.bottomnavigation);
        binding.bottomnavigation.setOnNavigationItemSelectedListener(this);
        Menu menu = binding.bottomnavigation.getMenu();
        MenuItem menuItem = menu.getItem(4);
        menuItem.setChecked(true);

        prefrence = SharedPrefrence.getInstance(AccountActivity.this);
        userDTO = prefrence.getParentUser(Consts.USER_DTO);

        binding.accountName.setText(userDTO.getName());

        Log.e("USER_IMAGE", "" + userDTO.getProfile_image());
        Glide.with(AccountActivity.this).load(userDTO.getProfile_image()).placeholder(R.drawable.image_gallery).into(binding.accountImg);

        //userDTO = prefrence.getParentUser(Consts.USER_DTO);
        params.put(Consts.ARTIST_ID, userDTO.getUser_id());

        binding.subscription.setOnClickListener(this);
        binding.privacy.setOnClickListener(this);
        binding.tearmsCondition.setOnClickListener(this);
        binding.contactUs.setOnClickListener(this);
        binding.updateProfile.setOnClickListener(this);
        binding.rateApp.setOnClickListener(this);
        binding.shareApp.setOnClickListener(this);
        binding.accountLogout.setOnClickListener(this);
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
                //Intent intent3 = new Intent(this, VideoDashboardActivity.class);
                Intent intent3 = new Intent(this, HomeScreenActivity.class);
                intent3.putExtra(Consts.MEDIA_TYPE_VIDEO, true);
                startActivity(intent3);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();
                break;
            case R.id.nav_service:
                Intent intent2 = new Intent(this, ServiceActivity.class);
                startActivity(intent2);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();
                break;
            case R.id.nav_account:

                break;
        }
        return true;
    }

    public void confirmLogout() {
        try {
            new AlertDialog.Builder(this)
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(getResources().getString(R.string.app_name))
                    .setMessage(getResources().getString(R.string.logout_msg))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            prefrence.clearAllPreferences();
                            Intent intent = new Intent(AccountActivity.this, CheckSigninActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.subscription:
                startActivity(new Intent(AccountActivity.this, SubscriptionActivity.class));
                break;
            case R.id.contact_us:
                startActivity(new Intent(AccountActivity.this, ContactUsActivity.class));
                break;
            case R.id.rate_app:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.brandshaastra"));
                startActivity(browserIntent);
                break;
            case R.id.share_app:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/html");
                String shareBody = "https://play.google.com/store/apps/details?id=com.brandshaastra";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "The Brand Shaastra");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
//                shareApplication();
                break;
            case R.id.privacy:
                //shareApplication();
                startActivity(new Intent(AccountActivity.this, PrivacyActivity.class).putExtra(Consts.WEBVIEW_TYPE, "privacy"));
                break;
            case R.id.tearms_condition:
                startActivity(new Intent(AccountActivity.this, PrivacyActivity.class).putExtra(Consts.WEBVIEW_TYPE, "tearms"));

                //shareApplication();
                break;
            case R.id.update_profile:
                startActivity(new Intent(AccountActivity.this, EditProfileActivity.class));
                break;
            case R.id.account_logout:
                confirmLogout();
                break;

        }
    }
}