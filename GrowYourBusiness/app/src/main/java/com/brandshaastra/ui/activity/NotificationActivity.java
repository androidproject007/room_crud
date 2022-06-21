package com.brandshaastra.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.brandshaastra.DTO.UserDTO;
import com.brandshaastra.R;
import com.brandshaastra.https.HttpsRequest;
import com.brandshaastra.interfaces.Consts;
import com.brandshaastra.interfaces.Helper;
import com.brandshaastra.network.NetworkManager;
import com.brandshaastra.preferences.SharedPrefrence;
import com.brandshaastra.ui.adapter.NotificationAdapter;
import com.brandshaastra.ui.model.NotificationModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    RecyclerView notification_rv;
    HashMap<String,String> notificationMap = new HashMap<>();
    String Tag = "Notification";
    NotificationAdapter notificationAdapter;
    List<NotificationModel> notificationList;
    SharedPrefrence prefrence;
    UserDTO userDTO;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        getSupportActionBar().setTitle("Notification");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        prefrence = SharedPrefrence.getInstance(this);
        userDTO = prefrence.getParentUser(Consts.USER_DTO);
        notification_rv = findViewById(R.id.notification_rv);

        notificationMap.put(Consts.USER_ID,userDTO.getUser_id());

        if (NetworkManager.isConnectToInternet(this)) {

            getNotification();
        } else {
            Toast.makeText(this, getResources().getString(R.string.internet_concation), Toast.LENGTH_SHORT).show();
        }


    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void getNotification(){

        new HttpsRequest(Consts.NOTIFICATION_API,notificationMap,NotificationActivity.this).stringPost(Tag,Consts.USER_CONTROLLER, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                if (flag){


                    try {
                        Log.e(Tag,""+response.toString());

                        Type getDTO = new TypeToken<List<NotificationModel>>(){}.getType();

                        notificationList = new Gson().fromJson(response.getJSONArray("data").toString(),getDTO);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    notificationAdapter = new NotificationAdapter(NotificationActivity.this,notificationList);
                    notification_rv.setLayoutManager(new LinearLayoutManager(NotificationActivity.this));
                    notification_rv.setAdapter(notificationAdapter);
                }
            }
        });
    }
}