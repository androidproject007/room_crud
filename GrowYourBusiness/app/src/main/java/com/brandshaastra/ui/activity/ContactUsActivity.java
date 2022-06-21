package com.brandshaastra.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.brandshaastra.DTO.UserDTO;
import com.brandshaastra.R;
import com.brandshaastra.https.HttpsRequest;
import com.brandshaastra.interfaces.Consts;
import com.brandshaastra.interfaces.Helper;
import com.brandshaastra.network.NetworkManager;
import com.brandshaastra.preferences.SharedPrefrence;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ContactUsActivity extends AppCompatActivity {
    ImageView chat, email, contact_img;
    HashMap<String, String> contactus;
    SharedPrefrence sharedPrefrence;
    UserDTO userDTO;
    String status = "";
    String message = "";
    String mobile_no = "";
    String email_str = "";
    String w_image = "";
    String m_image = "";
    String image = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sharedPrefrence = SharedPrefrence.getInstance(this);
        userDTO = sharedPrefrence.getParentUser(Consts.USER_DTO);
        chat = findViewById(R.id.chat);
        email = findViewById(R.id.email);
        contact_img = findViewById(R.id.contact_img);
        getSupportActionBar().setTitle("Contact Us");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        if (NetworkManager.isConnectToInternet(this)) {

            getContactUsData();
        } else {
            Toast.makeText(this, getResources().getString(R.string.internet_concation), Toast.LENGTH_SHORT).show();
        }

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://wa.me/91" + mobile_no;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email_str});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Inquiry");
                intent.putExtra(Intent.EXTRA_TEXT, "Hello...");
                startActivity(intent);

            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void getContactUsData() {

        contactus = new HashMap<>();
        contactus.put(Consts.USER_ID, userDTO.getUser_id());

        new HttpsRequest(Consts.GET_CONTACT_US, contactus, this).stringPost("Contactus", Consts.USER_CONTROLLER, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                if (flag) {


                    try {

                        Log.e("image_response",""+response.toString());
                        status = response.getString("status");
                        message = response.getString("message");
                        mobile_no = response.getString("mobile_no");
                        Log.e("mobile_no", "" + mobile_no);
                        email_str = response.getString("email");
                        w_image = response.getString("w_image");
                        m_image = response.getString("m_image");
                        image = response.getString("image");
                        Glide.with(ContactUsActivity.this).load(image).placeholder(R.drawable.brand_shaastra_logo).into(contact_img);
                        Glide.with(ContactUsActivity.this).load(w_image).placeholder(R.drawable.brand_shaastra_logo).into(chat);
                        Glide.with(ContactUsActivity.this).load(m_image).placeholder(R.drawable.brand_shaastra_logo).into(email);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.e("contact_res", "" + response.toString());
                }
            }
        });
    }
}