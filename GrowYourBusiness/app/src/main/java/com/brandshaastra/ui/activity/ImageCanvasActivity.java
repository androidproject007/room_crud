package com.brandshaastra.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

//import com.brandshaastra.ffmpeg.ExecuteBinaryResponseHandler;
//import com.brandshaastra.ffmpeg.FFmpeg;
import com.brandshaastra.databinding.ActivityImageCanvasBinding;
import com.brandshaastra.utils.FileUtility;
import com.bumptech.glide.Glide;
import com.easystudio.rotateimageview.OnDragTouchListener;
import com.easystudio.rotateimageview.RotateZoomImageView;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.brandshaastra.DTO.BusinessDataDto;
import com.brandshaastra.DTO.UserDTO;
import com.brandshaastra.R;
import com.brandshaastra.https.HttpsRequest;
import com.brandshaastra.interfaces.Consts;
import com.brandshaastra.interfaces.CustomGridLayoutManager;
import com.brandshaastra.interfaces.Helper;
import com.brandshaastra.interfaces.OnFontStyleChange;
import com.brandshaastra.interfaces.ThemeItemClick;
import com.brandshaastra.network.NetworkManager;
import com.brandshaastra.photoeditor.OnPhotoEditorListener;
import com.brandshaastra.photoeditor.PhotoEditor;
import com.brandshaastra.photoeditor.PhotoEditorView;
import com.brandshaastra.photoeditor.SaveSettings;
import com.brandshaastra.photoeditor.TextStyleBuilder;
import com.brandshaastra.photoeditor.ViewType;
import com.brandshaastra.preferences.SharedPrefrence;
import com.brandshaastra.ui.StickerBSFragment;
import com.brandshaastra.ui.adapter.FontStyleAdapter;
import com.brandshaastra.ui.adapter.ImageCanvasAdapter;
import com.brandshaastra.ui.adapter.ThemeAdapter;
import com.brandshaastra.utils.CameraUtils;
import com.brandshaastra.utils.CustomTextViewBold;
import com.brandshaastra.utils.DimensionData;
import com.brandshaastra.utils.FontCache;
import com.brandshaastra.utils.TextEditorDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


import static android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION;
import static com.brandshaastra.utils.Utils.getScaledDimension;


public class ImageCanvasActivity extends AppCompatActivity implements ThemeItemClick,
        View.OnClickListener, OnFontStyleChange,
        CompoundButton.OnCheckedChangeListener, OnPhotoEditorListener, StickerBSFragment.StickerListener {

    //    List<Drawable> themeList;
    List<String> themeList;
    ActivityImageCanvasBinding binding;
    boolean share_flag = false;

    ThemeAdapter themeAdapter;
    int selectedTheme = 0;
    List<String> imageList;
    ImageCanvasAdapter adapter;
    float xDown = 0, yDown = 0;
    BottomSheetDialog bottomSheetDialog;
    SeekBar bottom_seekbar;
    CustomTextViewBold bottomsheet_title, bottomsheet_text_size;
    LinearLayout bottomsheet_close;
    RelativeLayout bottomsheet_relative, bottomsheet_relative2, bottomsheet_relative3;
    Switch setting_logo_switch, setting_socialmedia_switch, setting_website_switch,
            setting_email_switch, setting_phone_switch, setting_address_switch;
    RecyclerView font_rv;
    private String imagePath = "";
    private String[] newCommand;
    int selected_theme_no = 0;
    FontStyleAdapter fontStyleAdapter;
    ArrayList<String> stringImageList = new ArrayList<>();
    HashMap<String, File> imageMap;

    boolean text_flag = false;
    boolean image_flag = false;
    boolean video_flag = false;
    private SharedPreferences firebase;
    private UserDTO userDTO;
    private SharedPrefrence prefrence;
    String position = "";
    HashMap<String, String> params = new HashMap<>();
    HashMap<String, String> downloadVideoparams = new HashMap<>();
    ImageView setting_theme_color, setting_font_color;
    int frame_border_color = 0;
    boolean frame_flag = false;
    boolean image_background_flag = false;
    int image_background_color = -1;
    PhotoEditor mPhotoEditor;
    PhotoEditorView mPhotoEditorView;
    private CameraUtils cameraUtils;
    String category_name = "";
    String cat_tracker = "";
    BusinessDataDto businessDataDto;
    public RotateZoomImageView rotateZoomImageView,rotateZoomImageView_video;
    private int originalDisplayWidth;
    private int originalDisplayHeight;
    private int newCanvasWidth, newCanvasHeight;
    private int DRAW_CANVASW = 0;
    private int DRAW_CANVASH = 0;
    private StickerBSFragment mStickerBSFragment;
    //    FFmpeg fFmpeg;
    private MediaPlayer mediaPlayer;
    private String videoPath = "";
    private ArrayList<String> exeCmd;
    String fileN = "";
    String video_img_path = "";
    String video_url2 = "";
    boolean image_loaded_flag = false;
    boolean VIDEO_FFMPEG_flag = false;
    private ProgressDialog progressDialog;
    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.start();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImageCanvasBinding.inflate(getLayoutInflater());
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"}, 101);
        }
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                //todo when permission is granted
            } else {
                //request for the permission
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }*/

        setContentView(R.layout.activity_image_canvas);
        setContentView(binding.getRoot());
//        fFmpeg = FFmpeg.getInstance(this);
        getSupportActionBar().hide();
        //////getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        prefrence = SharedPrefrence.getInstance(ImageCanvasActivity.this);
        firebase = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        userDTO = prefrence.getParentUser(Consts.USER_DTO);
        businessDataDto = prefrence.getBusinessData(Consts.BUSINESSDATA_DTO);
        // cameraUtils = new CameraUtils(this, this);

        text_flag = getIntent().getBooleanExtra("text", false);
        image_flag = getIntent().getBooleanExtra("image", false);
        video_flag = getIntent().getBooleanExtra("video", false);
        rotateZoomImageView = findViewById(R.id.text_image_img);
        rotateZoomImageView_video = findViewById(R.id.imageView);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(250, 250);
        //lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        rotateZoomImageView.setLayoutParams(lp);
        rotateZoomImageView_video.setLayoutParams(lp);


        setUidata();

        if (text_flag) {

            binding.imageToolsSubLinear.setWeightSum(5);
            //binding.imageToolsSubLinear.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,6f));
            binding.imageRecyclerview.setVisibility(View.GONE);
            rotateZoomImageView.setVisibility(View.GONE);
            binding.rotateImgClose.setVisibility(View.GONE);
            binding.resizeImg.setVisibility(View.GONE);
            binding.pickimage.setVisibility(View.GONE);
            binding.addText.setVisibility(View.GONE);
            binding.videoShare.setVisibility(View.GONE);

            binding.movableEditText.setVisibility(View.VISIBLE);
        }
        if (image_flag) {

            binding.imageToolsSubLinear.setWeightSum(6);
            //binding.imageToolsSubLinear.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,7f));
            binding.imageRecyclerview.setVisibility(View.GONE);
            //  binding.resizeImg.setVisibility(View.VISIBLE);
            // binding.rotateImgClose.setVisibility(View.VISIBLE);
            binding.editBg.setVisibility(View.GONE);
            binding.videoShare.setVisibility(View.GONE);
            binding.movableEditText.setVisibility(View.GONE);

        }
        if (video_flag) {

            binding.imageToolsSubLinear.setWeightSum(6);
            //binding.imageToolsSubLinear.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,7f));
            binding.imageShare.setVisibility(View.GONE);
            binding.videoShare.setVisibility(View.VISIBLE);

            binding.imgDone.setVisibility(View.VISIBLE);
            binding.imageRecyclerview.setVisibility(View.GONE);
            //  binding.resizeImg.setVisibility(View.VISIBLE);
            // binding.rotateImgClose.setVisibility(View.VISIBLE);
            binding.videoImage.setVisibility(View.VISIBLE);
            //binding.imgSticker.setVisibility(View.VISIBLE);
            binding.videoSurface.setVisibility(View.VISIBLE);
            // binding.imgClose.setVisibility(View.VISIBLE);
            // binding.imgDone.setVisibility(View.VISIBLE);
            binding.editBg.setVisibility(View.GONE);
            binding.movableEditText.setVisibility(View.GONE);

            video_url2 = getIntent().getStringExtra("video_url2");
            videoPath = getIntent().getStringExtra("DATA");
            Log.e("VIDEOPATH", "" + videoPath);

            /*if (NetworkManager.isConnectToInternet(this)) {

                DownloadTask downloadTask = new DownloadTask(ImageCanvasActivity.this);
                downloadTask.execute(videoPath);
            } else {
                Toast.makeText(this, getResources().getString(R.string.internet_concation), Toast.LENGTH_SHORT).show();
            }*/


        }
        if (getIntent().hasExtra(Consts.POSITION)) {

            binding.editBg.setVisibility(View.GONE);
            binding.frame.setVisibility(View.GONE);

            category_name = getIntent().getStringExtra(Consts.CATEGORY_NAME);
            cat_tracker = getIntent().getStringExtra(Consts.TRACKER);
            Log.e("CATEGORY_NAME_track", " 0 " + category_name + " tracker:-- " + cat_tracker);

            position = getIntent().getStringExtra(Consts.POSITION);
            stringImageList = (ArrayList<String>) getIntent().getSerializableExtra(Consts.IMAGE_LIST);

            getImageList();
        }
    }

    private void setUidata() {
        if (!businessDataDto.getImage().equalsIgnoreCase("") || businessDataDto.getImage().isEmpty()) {

            Glide.with(this).load(businessDataDto.getImage()).placeholder(R.drawable.brand_shaastra_logo).into(binding.first.businessIcon);
            Glide.with(this).load(businessDataDto.getImage()).placeholder(R.drawable.brand_shaastra_logo).into(binding.second.businessIcon);
            Glide.with(this).load(businessDataDto.getImage()).placeholder(R.drawable.brand_shaastra_logo).into(binding.third.businessIcon);
            Glide.with(this).load(businessDataDto.getImage()).placeholder(R.drawable.brand_shaastra_logo).into(binding.forth.businessIcon);
            Glide.with(this).load(businessDataDto.getImage()).placeholder(R.drawable.brand_shaastra_logo).into(binding.fifth.businessIcon);
            Glide.with(this).load(businessDataDto.getImage()).placeholder(R.drawable.brand_shaastra_logo).into(binding.sixth.businessIcon);
            Glide.with(this).load(businessDataDto.getImage()).placeholder(R.drawable.brand_shaastra_logo).into(binding.seventh.businessIcon);
            Glide.with(this).load(businessDataDto.getImage()).placeholder(R.drawable.brand_shaastra_logo).into(binding.nineth.businessIcon);
            Glide.with(this).load(businessDataDto.getImage()).placeholder(R.drawable.brand_shaastra_logo).into(binding.tenth.businessIcon);
            Glide.with(this).load(businessDataDto.getImage()).placeholder(R.drawable.brand_shaastra_logo).into(binding.eleven.businessIcon);
            Glide.with(this).load(businessDataDto.getImage()).placeholder(R.drawable.brand_shaastra_logo).into(binding.twelve.businessIcon);
            Glide.with(this).load(businessDataDto.getImage()).placeholder(R.drawable.brand_shaastra_logo).into(binding.thirteen.businessIcon);
            Glide.with(this).load(businessDataDto.getImage()).placeholder(R.drawable.brand_shaastra_logo).into(binding.fourteen.businessIcon);
            Glide.with(this).load(businessDataDto.getImage()).placeholder(R.drawable.brand_shaastra_logo).into(binding.fifteen.businessIcon);
            Glide.with(this).load(businessDataDto.getImage()).placeholder(R.drawable.brand_shaastra_logo).into(binding.sixteenth.businessIcon);
            Glide.with(this).load(businessDataDto.getImage()).placeholder(R.drawable.brand_shaastra_logo).into(binding.seventeenth.businessIcon);
            Glide.with(this).load(businessDataDto.getImage()).placeholder(R.drawable.brand_shaastra_logo).into(binding.eighteen.businessIcon);
            Glide.with(this).load(businessDataDto.getImage()).placeholder(R.drawable.brand_shaastra_logo).into(binding.nineteen.businessIcon);
            Glide.with(this).load(businessDataDto.getImage()).placeholder(R.drawable.brand_shaastra_logo).into(binding.twenty.businessIcon);
            Glide.with(this).load(businessDataDto.getImage()).placeholder(R.drawable.brand_shaastra_logo).into(binding.twentyone.businessIcon);
            Glide.with(this).load(businessDataDto.getImage()).placeholder(R.drawable.brand_shaastra_logo).into(binding.twentytwo.businessIcon);
            Glide.with(this).load(businessDataDto.getImage()).placeholder(R.drawable.brand_shaastra_logo).into(binding.twentythree.businessIcon);
            Glide.with(this).load(businessDataDto.getImage()).placeholder(R.drawable.brand_shaastra_logo).into(binding.twentyfour.businessIcon);
            Glide.with(this).load(businessDataDto.getImage()).placeholder(R.drawable.brand_shaastra_logo).into(binding.twentyfive.businessIcon);
            Glide.with(this).load(businessDataDto.getImage()).placeholder(R.drawable.brand_shaastra_logo).into(binding.twentysix.businessIcon);
            Glide.with(this).load(businessDataDto.getImage()).placeholder(R.drawable.brand_shaastra_logo).into(binding.twentyseven.businessIcon);
            Glide.with(this).load(businessDataDto.getImage()).placeholder(R.drawable.brand_shaastra_logo).into(binding.twentyeight.businessIcon);
        }

        binding.first.businessCallDetails.setText(businessDataDto.getMobile_no());
        binding.first.businessMailDetails.setText(businessDataDto.getEmail());
        binding.first.businessWebsiteDetails.setText(businessDataDto.getWebsite());
        binding.first.businessLocationDetails.setText(businessDataDto.getAddress());

        binding.second.businessCallDetails.setText(businessDataDto.getMobile_no());
        binding.second.businessMailDetails.setText(businessDataDto.getEmail());
        binding.second.businessWebsiteDetails.setText(businessDataDto.getWebsite());
        binding.second.businessLocationDetails.setText(businessDataDto.getAddress());

        binding.third.businessCallDetails.setText(businessDataDto.getMobile_no());
        binding.third.businessMailDetails.setText(businessDataDto.getEmail());
        binding.third.businessWebsiteDetails.setText(businessDataDto.getWebsite());
        binding.third.businessLocationDetails.setText(businessDataDto.getAddress());

        binding.forth.businessCallDetails.setText(businessDataDto.getMobile_no());
        binding.forth.businessMailDetails.setText(businessDataDto.getEmail());
        binding.forth.businessLocationDetails.setText(businessDataDto.getAddress());

        binding.fifth.businessCallDetails.setText(businessDataDto.getMobile_no());
        binding.fifth.businessMailDetails.setText(businessDataDto.getEmail());
        binding.fifth.businessLocationDetails.setText(businessDataDto.getAddress());

        binding.sixth.businessCallDetails.setText(businessDataDto.getMobile_no());
        binding.sixth.businessMailDetails.setText(businessDataDto.getEmail());
        binding.sixth.businessWebsiteDetails.setText(businessDataDto.getWebsite());
        binding.sixth.businessLocationDetails.setText(businessDataDto.getAddress());

        binding.seventh.businessCallDetails.setText(businessDataDto.getMobile_no());
        binding.seventh.businessMailDetails.setText(businessDataDto.getEmail());
        binding.seventh.businessLocationDetails.setText(businessDataDto.getAddress());

        binding.eight.businessCallDetails.setText(businessDataDto.getMobile_no());
        binding.eight.businessMailDetails.setText(businessDataDto.getEmail());
        binding.eight.businessLocationDetails.setText(businessDataDto.getAddress());

        binding.nineth.businessCallDetails.setText(businessDataDto.getMobile_no());
        binding.nineth.businessWebsiteDetails.setText(businessDataDto.getWebsite());

        binding.tenth.businessCallDetails.setText(businessDataDto.getMobile_no());
        binding.tenth.businessMailDetails.setText(businessDataDto.getEmail());
        binding.tenth.businessWebsiteDetails.setText(businessDataDto.getWebsite());
        binding.tenth.businessLocationDetails.setText(businessDataDto.getAddress());

        binding.eleven.businessCallDetails.setText(businessDataDto.getMobile_no());
        binding.eleven.businessMailDetails.setText(businessDataDto.getEmail());
        binding.eleven.businessWebsiteDetails.setText(businessDataDto.getWebsite());

        binding.twelve.businessCallDetails.setText(businessDataDto.getMobile_no());
        binding.twelve.businessMailDetails.setText(businessDataDto.getEmail());
        binding.twelve.businessWebsiteDetails.setText(businessDataDto.getWebsite());
        binding.twelve.businessLocationDetails.setText(businessDataDto.getAddress());

        binding.thirteen.businessCallDetails.setText(businessDataDto.getMobile_no());
        binding.thirteen.businessMailDetails.setText(businessDataDto.getEmail());
        binding.thirteen.businessWebsiteDetails.setText(businessDataDto.getWebsite());
        binding.thirteen.businessLocationDetails.setText(businessDataDto.getAddress());

        binding.fourteen.businessCallDetails.setText(businessDataDto.getMobile_no());
        binding.fourteen.businessMailDetails.setText(businessDataDto.getEmail());
        binding.fourteen.businessWebsiteDetails.setText(businessDataDto.getWebsite());
        binding.fourteen.businessLocationDetails.setText(businessDataDto.getAddress());

        binding.fifteen.businessCallDetails.setText(businessDataDto.getMobile_no());
        binding.fifteen.businessMailDetails.setText(businessDataDto.getEmail());
        binding.fifteen.businessWebsiteDetails.setText(businessDataDto.getWebsite());

        binding.sixteenth.businessCallDetails.setText(businessDataDto.getMobile_no());
        binding.sixteenth.businessWebsiteDetails.setText(businessDataDto.getWebsite());

        binding.seventeenth.businessCallDetails.setText(businessDataDto.getMobile_no());
        binding.seventeenth.businessWebsiteDetails.setText(businessDataDto.getWebsite());

        binding.eighteen.businessCallDetails.setText(businessDataDto.getMobile_no());
        binding.eighteen.businessWebsiteDetails.setText(businessDataDto.getWebsite());
        binding.eighteen.businessMailDetails.setText(businessDataDto.getEmail());

        binding.nineteen.businessCallDetails.setText(businessDataDto.getMobile_no());
        binding.nineteen.businessMailDetails.setText(businessDataDto.getEmail());
        binding.nineteen.businessLocationDetails.setText(businessDataDto.getAddress());

        binding.twenty.businessCallDetails.setText(businessDataDto.getMobile_no());
        binding.twenty.businessMailDetails.setText(businessDataDto.getEmail());
        binding.twenty.businessLocationDetails.setText(businessDataDto.getAddress());

        binding.twentyone.businessCallDetails.setText(businessDataDto.getMobile_no());
        binding.twentyone.businessMailDetails.setText(businessDataDto.getEmail());

        binding.twentytwo.businessCallDetails.setText(businessDataDto.getMobile_no());
        binding.twentytwo.businessMailDetails.setText(businessDataDto.getEmail());
        binding.twentytwo.businessLocationDetails.setText(businessDataDto.getAddress());

        binding.twentythree.businessCallDetails.setText(businessDataDto.getMobile_no());
        binding.twentythree.businessMailDetails.setText(businessDataDto.getEmail());
        binding.twentythree.businessName.setText(businessDataDto.getName());

        binding.twentyfour.businessCallDetails.setText(businessDataDto.getMobile_no());
        binding.twentyfour.businessMailDetails.setText(businessDataDto.getEmail());
        binding.twentyfour.businessName.setText(businessDataDto.getName());

        binding.twentyfive.businessCallDetails.setText(businessDataDto.getMobile_no());
        binding.twentyfive.businessMailDetails.setText(businessDataDto.getEmail());
        binding.twentyfive.businessName.setText(businessDataDto.getName());

        binding.twentysix.businessCallDetails.setText(businessDataDto.getMobile_no());
        binding.twentysix.businessMailDetails.setText(businessDataDto.getEmail());
        binding.twentysix.businessName.setText(businessDataDto.getName());

        binding.twentyseven.businessCallDetails.setText(businessDataDto.getMobile_no());
        binding.twentyseven.businessMailDetails.setText(businessDataDto.getEmail());
        binding.twentyseven.businessName.setText(businessDataDto.getName());

        binding.twentyeight.businessCallDetails.setText(businessDataDto.getMobile_no());
        binding.twentyeight.businessMailDetails.setText(businessDataDto.getEmail());
        binding.twentyeight.businessName.setText(businessDataDto.getName());

        changeDrawablecolor(binding.third.footerCallImage.getBackground(), getResources().getColor(R.color.frame3_bg));
        changeDrawablecolor(binding.third.footerWebsiteImage.getBackground(), getResources().getColor(R.color.frame3_bg));
        changeDrawablecolor(binding.third.footerLocationImage.getBackground(), getResources().getColor(R.color.frame3_bg));
        changeDrawablecolor(binding.third.footerMailImage.getBackground(), getResources().getColor(R.color.frame3_bg));

        changeDrawablecolor(binding.tenth.locationMainRelative.getBackground(), getResources().getColor(R.color.frame10));
        changeDrawablecolor(binding.tenth.mailMainRelative.getBackground(), getResources().getColor(R.color.frame10));
        changeDrawablecolor(binding.tenth.callMainRelative.getBackground(), getResources().getColor(R.color.frame10));
        changeDrawablecolor(binding.tenth.websiteMainRelative.getBackground(), getResources().getColor(R.color.frame10));

        /*changeDrawablecolor(binding.fourteen.locationMainRelative.getBackground(), getResources().getColor(R.color.fram));
        changeDrawablecolor(binding.fourteen.mailMainRelative.getBackground(), getResources().getColor(R.color.frame10));
        changeDrawablecolor(binding.fourteen.callMainRelative.getBackground(), getResources().getColor(R.color.frame10));
        changeDrawablecolor(binding.fourteen.websiteMainRelative.getBackground(), getResources().getColor(R.color.frame10));*/

        changeDrawablecolor(binding.forth.footerCallImage.getBackground(), getResources().getColor(R.color.frame4_bg));
        changeDrawablecolor(binding.forth.footerLocationImage.getBackground(), getResources().getColor(R.color.frame4_bg));
        changeDrawablecolor(binding.forth.footerMailImage.getBackground(), getResources().getColor(R.color.frame4_bg));

        binding.frameRelative.setBackground(ContextCompat.getDrawable(this, R.drawable.top_curved_card_bg));

        binding.frameRelative.setPadding(0, 0, 0, 0);

        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottomsheet_dialog_layout);

        setting_theme_color = bottomSheetDialog.findViewById(R.id.setting_theme_color);
        setting_font_color = bottomSheetDialog.findViewById(R.id.setting_font_color);
        bottom_seekbar = bottomSheetDialog.findViewById(R.id.bottomsheet_seekbar);
        bottomsheet_title = bottomSheetDialog.findViewById(R.id.bottomsheet_title);
        bottomsheet_text_size = bottomSheetDialog.findViewById(R.id.bottomsheet_text_size);
        bottomsheet_close = bottomSheetDialog.findViewById(R.id.bottomsheet_close);
        bottomsheet_relative = bottomSheetDialog.findViewById(R.id.bottomsheet_relative);
        bottomsheet_relative2 = bottomSheetDialog.findViewById(R.id.bottomsheet_relative2);
        bottomsheet_relative3 = bottomSheetDialog.findViewById(R.id.bottomsheet_relative3);
        setting_logo_switch = bottomSheetDialog.findViewById(R.id.setting_logo_switch);
        setting_socialmedia_switch = bottomSheetDialog.findViewById(R.id.setting_socialmedia_switch);
        setting_website_switch = bottomSheetDialog.findViewById(R.id.setting_website_switch);
        setting_email_switch = bottomSheetDialog.findViewById(R.id.setting_email_switch);
        setting_phone_switch = bottomSheetDialog.findViewById(R.id.setting_phone_switch);
        setting_address_switch = bottomSheetDialog.findViewById(R.id.setting_address_switch);
        font_rv = bottomSheetDialog.findViewById(R.id.font_rv);
        binding.imgDone.setOnClickListener(this);

        getThemeData();
        getOnClickListeners();

        if (video_flag) {


            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setInverseBackgroundForced(true);
            mStickerBSFragment = new StickerBSFragment();
            mStickerBSFragment.setStickerListener(this);

            mPhotoEditor = new PhotoEditor.Builder(this, binding.videoImage)
                    .setPinchTextScalable(true) // set flag to make text scalable when pinch
                    .setDeleteView(binding.imgDelete)
                    //.setDefaultTextTypeface(mTextRobotoTf)
                    //.setDefaultEmojiTypeface(mEmojiTypeFace)
                    .build(); // build photo editor sdk

            mPhotoEditor.setOnPhotoEditorListener(this);
            binding.imgDelete.setOnClickListener(this);
            binding.imgClose.setOnClickListener(this);
            binding.imgDone.setOnClickListener(this);
            binding.imgSticker.setOnClickListener(this);

            //   lp.addRule(RelativeLayout.CENTER_IN_PARENT);

            binding.videoSurface.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
                    Surface surface = new Surface(surfaceTexture);

                    try {
                        mediaPlayer = new MediaPlayer();

                        Log.d("VideoPath>>", videoPath);
                        mediaPlayer.setDataSource(videoPath);
                        mediaPlayer.setSurface(surface);
                        mediaPlayer.prepare();
                        mediaPlayer.setOnCompletionListener(onCompletionListener);
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mediaPlayer.start();
                    } catch (IllegalArgumentException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (SecurityException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IllegalStateException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

                }

                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                    return false;
                }

                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

                }
            });

            exeCmd = new ArrayList<>();

//            if (FFmpeg.getInstance(this).isSupported()) {
//                // ffmpeg is supported
//                Log.e("FFMPEG", "  ffmpeg is supported ");
//            } else {
//                // ffmpeg is not supported
//                Log.e("FFMPEG", "  ffmpeg is not supported ");
//            }
            /*try {
                fFmpeg.loadBinary(new FFmpegLoadBinaryResponseHandler() {
                    @Override
                    public void onFailure() {
                        Log.d("binaryLoad", "onFailure");

                    }

                    @Override
                    public void onSuccess() {
                        Log.d("binaryLoad", "onSuccess");
                    }

                    @Override
                    public void onStart() {
                        Log.d("binaryLoad", "onStart");

                    }

                    @Override
                    public void onFinish() {
                        Log.d("binaryLoad", "onFinish");

                    }
                });
            } catch (FFmpegNotSupportedException e) {
                e.printStackTrace();
            }*/


        }
    }

    private void setCanvasAspectRatio() {
        originalDisplayHeight = getDisplayHeight();
        originalDisplayWidth = getDisplayWidth();

        DimensionData displayDiamenion =
                getScaledDimension(new DimensionData((int) DRAW_CANVASW, (int) DRAW_CANVASH),
                        new DimensionData(originalDisplayWidth, originalDisplayHeight));

        newCanvasWidth = displayDiamenion.width;
        newCanvasHeight = displayDiamenion.height;
    }

    private int getDisplayWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    private int getDisplayHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    private void getImageList() {

        adapter = new ImageCanvasAdapter(ImageCanvasActivity.this, stringImageList);
        Log.e("CATEGORY_NAME_tracker", " 1 " + category_name + " tracker:-- " + cat_tracker);

        // if (category_name.equalsIgnoreCase("Greetings")) {
        if (cat_tracker.equalsIgnoreCase("2")) {

            CustomGridLayoutManager customGridLayoutManager = new CustomGridLayoutManager(this);
            customGridLayoutManager.setScrollEnabled(false);
            binding.imageRecyclerview.setLayoutManager(customGridLayoutManager);
        } else {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            binding.imageRecyclerview.setLayoutManager(linearLayoutManager);
        }
        binding.imageRecyclerview.scrollToPosition(Integer.parseInt(position));
        if (binding.imageRecyclerview.getOnFlingListener() == null) {

            SnapHelper snapHelper = new LinearSnapHelper();
            snapHelper.attachToRecyclerView(binding.imageRecyclerview);
        }
        binding.imageRecyclerview.setAdapter(adapter);

    }

    private void getOnClickListeners() {

        binding.addText.setOnClickListener(this);
        binding.imageviewBackBtn.setOnClickListener(this);
        //  binding.businessIconClose.setOnClickListener(this);
        binding.text.setOnClickListener(this);
        binding.textlay.setOnClickListener(this);
        binding.textcolor.setOnClickListener(this);
        binding.textsize.setOnClickListener(this);
        binding.fontstyle.setOnClickListener(this);
        binding.pickimage.setOnClickListener(this);
        binding.settings.setOnClickListener(this);
        binding.imageView.setOnClickListener(this);
        binding.movableEditText.setOnClickListener(this);
        binding.imageShare.setOnClickListener(this);
        binding.videoShare.setOnClickListener(this);
        binding.editBg.setOnClickListener(this);
        binding.frame.setOnClickListener(this);

        setting_logo_switch.setOnCheckedChangeListener(this);
        setting_socialmedia_switch.setOnCheckedChangeListener(this);
        setting_website_switch.setOnCheckedChangeListener(this);
        setting_email_switch.setOnCheckedChangeListener(this);
        setting_phone_switch.setOnCheckedChangeListener(this);
        setting_address_switch.setOnCheckedChangeListener(this);


        if (binding.first.businessLogoRelative.getVisibility() == View.VISIBLE) {
            setting_logo_switch.setChecked(true);
        } else if (binding.first.businessLogoRelative.getVisibility() == View.GONE) {
            setting_logo_switch.setChecked(false);
        }

        if (binding.first.businessSocialmediaImageRelative.getVisibility() == View.VISIBLE) {
            setting_socialmedia_switch.setChecked(true);
        } else if (binding.first.businessSocialmediaImageRelative.getVisibility() == View.GONE) {
            setting_socialmedia_switch.setChecked(false);
        }

        if (binding.first.callMainRelative.getVisibility() == View.VISIBLE) {
            setting_phone_switch.setChecked(true);
        } else if (binding.first.callMainRelative.getVisibility() == View.GONE) {
            setting_phone_switch.setChecked(false);
        }
        if (binding.first.locationMainRelative.getVisibility() == View.VISIBLE) {
            setting_address_switch.setChecked(true);
        } else if (binding.first.locationMainRelative.getVisibility() == View.GONE) {
            setting_address_switch.setChecked(false);
        }
        if (binding.first.websiteMainRelative.getVisibility() == View.VISIBLE) {
            setting_website_switch.setChecked(true);
        } else if (binding.first.websiteMainRelative.getVisibility() == View.GONE) {
            setting_website_switch.setChecked(false);
        }
        if (binding.first.mailMainRelative.getVisibility() == View.VISIBLE) {
            setting_email_switch.setChecked(true);
        } else if (binding.first.mailMainRelative.getVisibility() == View.GONE) {
            setting_email_switch.setChecked(false);
        }
       /* binding.businessIcon.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                // binding.businessIconClose.setVisibility(View.VISIBLE);
                return false;
            }
        });
        */
        binding.movableEditText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (v.getId() == R.id.movable_edit_text) {

                    binding.movableEditText.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {

                            switch (event.getActionMasked()) {

                                case MotionEvent.ACTION_DOWN:
                                    xDown = event.getX();
                                    yDown = event.getY();
                                    return true;
                                //break;
                                case MotionEvent.ACTION_MOVE:
                                    float movedX, movedY;
                                    movedX = event.getX();
                                    movedY = event.getY();

                                    float distenceX = movedX - xDown;
                                    float distenceY = movedY - yDown;

                                    binding.movableEditText.setX(binding.movableEditText.getX() + distenceX);
                                    binding.movableEditText.setY(binding.movableEditText.getY() + distenceY);

                            /*    xDown = movedX;
                            yDown = movedY;*/
                                    return true;
                                //break;
                                case MotionEvent.ACTION_UP:
                                    return false;
                                //break;
                            }
                            binding.rel.invalidate();
                            return true;
                        }
                    });
                }

                return false;
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void getThemeData() {

        themeList = new ArrayList<>();
        for (int i = 1; i < 29; i++) {

            themeList.add("https://webknight.co.in/grow_your_bussiness/assets/webp/frame_img_" + i + ".webp");

        }
        themeAdapter = new ThemeAdapter(ImageCanvasActivity.this, themeList, this);
        binding.themes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.themes.setAdapter(themeAdapter);
    }

    @Override
    public void getThemeItem(int value) {
        selectedTheme = value;
        selected_theme_no = value;
        Log.e("selected_theme_no", "" + selected_theme_no);
        switch (selectedTheme) {

            case 0:
                binding.firstFrameInclude.setVisibility(View.VISIBLE);
                binding.thirdFrameInclude.setVisibility(View.GONE);
                binding.secondFrameInclude.setVisibility(View.GONE);
                binding.forthFrameInclude.setVisibility(View.GONE);
                binding.fifthFrameInclude.setVisibility(View.GONE);
                binding.sixthFrameInclude.setVisibility(View.GONE);
                binding.seventhFrameInclude.setVisibility(View.GONE);
                binding.eightFrameInclude.setVisibility(View.GONE);
                binding.ninethFrameInclude.setVisibility(View.GONE);
                binding.tenthFrameInclude.setVisibility(View.GONE);
                binding.eleventhFrameInclude.setVisibility(View.GONE);
                binding.twelvethFrameInclude.setVisibility(View.GONE);
                binding.thirteenFrameInclude.setVisibility(View.GONE);
                binding.fourteenFrameInclude.setVisibility(View.GONE);
                binding.fifteenFrameInclude.setVisibility(View.GONE);
                binding.sixteenthFrameInclude.setVisibility(View.GONE);
                binding.seventeenthFrameInclude.setVisibility(View.GONE);
                binding.eighteenFrameInclude.setVisibility(View.GONE);
                binding.nineteenFrameInclude.setVisibility(View.GONE);
                binding.twentyFrameInclude.setVisibility(View.GONE);
                binding.twentyoneFrameInclude.setVisibility(View.GONE);
                binding.twentytwoFrameInclude.setVisibility(View.GONE);
                binding.twentythreeFrameInclude.setVisibility(View.GONE);
                binding.twentyfourFrameInclude.setVisibility(View.GONE);
                binding.twentyfiveFrameInclude.setVisibility(View.GONE);
                binding.twentysixFrameInclude.setVisibility(View.GONE);
                binding.twentysevenFrameInclude.setVisibility(View.GONE);
                binding.twentyeightFrameInclude.setVisibility(View.GONE);
                break;
            case 1:

                binding.firstFrameInclude.setVisibility(View.GONE);
                binding.thirdFrameInclude.setVisibility(View.GONE);
                binding.secondFrameInclude.setVisibility(View.VISIBLE);
                binding.forthFrameInclude.setVisibility(View.GONE);
                binding.fifthFrameInclude.setVisibility(View.GONE);
                binding.sixthFrameInclude.setVisibility(View.GONE);
                binding.seventhFrameInclude.setVisibility(View.GONE);
                binding.eightFrameInclude.setVisibility(View.GONE);
                binding.ninethFrameInclude.setVisibility(View.GONE);
                binding.tenthFrameInclude.setVisibility(View.GONE);
                binding.eleventhFrameInclude.setVisibility(View.GONE);
                binding.twelvethFrameInclude.setVisibility(View.GONE);
                binding.thirteenFrameInclude.setVisibility(View.GONE);
                binding.fourteenFrameInclude.setVisibility(View.GONE);
                binding.fifteenFrameInclude.setVisibility(View.GONE);
                binding.sixteenthFrameInclude.setVisibility(View.GONE);
                binding.seventeenthFrameInclude.setVisibility(View.GONE);
                binding.eighteenFrameInclude.setVisibility(View.GONE);
                binding.nineteenFrameInclude.setVisibility(View.GONE);
                binding.twentyFrameInclude.setVisibility(View.GONE);
                binding.twentyoneFrameInclude.setVisibility(View.GONE);
                binding.twentytwoFrameInclude.setVisibility(View.GONE);
                binding.twentythreeFrameInclude.setVisibility(View.GONE);
                binding.twentyfourFrameInclude.setVisibility(View.GONE);
                binding.twentyfiveFrameInclude.setVisibility(View.GONE);
                binding.twentysixFrameInclude.setVisibility(View.GONE);
                binding.twentysevenFrameInclude.setVisibility(View.GONE);
                binding.twentyeightFrameInclude.setVisibility(View.GONE);
                break;
            case 2:
                binding.firstFrameInclude.setVisibility(View.GONE);
                binding.thirdFrameInclude.setVisibility(View.VISIBLE);
                binding.secondFrameInclude.setVisibility(View.GONE);
                binding.forthFrameInclude.setVisibility(View.GONE);
                binding.fifthFrameInclude.setVisibility(View.GONE);
                binding.sixthFrameInclude.setVisibility(View.GONE);
                binding.seventhFrameInclude.setVisibility(View.GONE);
                binding.eightFrameInclude.setVisibility(View.GONE);
                binding.ninethFrameInclude.setVisibility(View.GONE);
                binding.tenthFrameInclude.setVisibility(View.GONE);
                binding.eleventhFrameInclude.setVisibility(View.GONE);
                binding.twelvethFrameInclude.setVisibility(View.GONE);
                binding.thirteenFrameInclude.setVisibility(View.GONE);
                binding.fourteenFrameInclude.setVisibility(View.GONE);
                binding.fifteenFrameInclude.setVisibility(View.GONE);
                binding.sixteenthFrameInclude.setVisibility(View.GONE);
                binding.seventeenthFrameInclude.setVisibility(View.GONE);
                binding.eighteenFrameInclude.setVisibility(View.GONE);
                binding.nineteenFrameInclude.setVisibility(View.GONE);
                binding.twentyFrameInclude.setVisibility(View.GONE);
                binding.twentyoneFrameInclude.setVisibility(View.GONE);
                binding.twentytwoFrameInclude.setVisibility(View.GONE);
                binding.twentythreeFrameInclude.setVisibility(View.GONE);
                binding.twentyfourFrameInclude.setVisibility(View.GONE);
                binding.twentyfiveFrameInclude.setVisibility(View.GONE);
                binding.twentysixFrameInclude.setVisibility(View.GONE);
                binding.twentysevenFrameInclude.setVisibility(View.GONE);
                binding.twentyeightFrameInclude.setVisibility(View.GONE);
                break;

            case 3:

                binding.firstFrameInclude.setVisibility(View.GONE);
                binding.thirdFrameInclude.setVisibility(View.GONE);
                binding.secondFrameInclude.setVisibility(View.GONE);
                binding.forthFrameInclude.setVisibility(View.VISIBLE);
                binding.fifthFrameInclude.setVisibility(View.GONE);
                binding.sixthFrameInclude.setVisibility(View.GONE);
                binding.seventhFrameInclude.setVisibility(View.GONE);
                binding.eightFrameInclude.setVisibility(View.GONE);
                binding.ninethFrameInclude.setVisibility(View.GONE);
                binding.tenthFrameInclude.setVisibility(View.GONE);
                binding.eleventhFrameInclude.setVisibility(View.GONE);
                binding.twelvethFrameInclude.setVisibility(View.GONE);
                binding.thirteenFrameInclude.setVisibility(View.GONE);
                binding.fourteenFrameInclude.setVisibility(View.GONE);
                binding.fifteenFrameInclude.setVisibility(View.GONE);
                binding.sixteenthFrameInclude.setVisibility(View.GONE);
                binding.seventeenthFrameInclude.setVisibility(View.GONE);
                binding.eighteenFrameInclude.setVisibility(View.GONE);
                binding.nineteenFrameInclude.setVisibility(View.GONE);
                binding.twentyFrameInclude.setVisibility(View.GONE);
                binding.twentyoneFrameInclude.setVisibility(View.GONE);
                binding.twentytwoFrameInclude.setVisibility(View.GONE);
                binding.twentythreeFrameInclude.setVisibility(View.GONE);
                binding.twentyfourFrameInclude.setVisibility(View.GONE);
                binding.twentyfiveFrameInclude.setVisibility(View.GONE);
                binding.twentysixFrameInclude.setVisibility(View.GONE);
                binding.twentysevenFrameInclude.setVisibility(View.GONE);
                binding.twentyeightFrameInclude.setVisibility(View.GONE);
                break;

            case 4:
                binding.firstFrameInclude.setVisibility(View.GONE);
                binding.thirdFrameInclude.setVisibility(View.GONE);
                binding.secondFrameInclude.setVisibility(View.GONE);
                binding.forthFrameInclude.setVisibility(View.GONE);
                binding.fifthFrameInclude.setVisibility(View.VISIBLE);
                binding.sixthFrameInclude.setVisibility(View.GONE);
                binding.seventhFrameInclude.setVisibility(View.GONE);
                binding.eightFrameInclude.setVisibility(View.GONE);
                binding.ninethFrameInclude.setVisibility(View.GONE);
                binding.tenthFrameInclude.setVisibility(View.GONE);
                binding.eleventhFrameInclude.setVisibility(View.GONE);
                binding.twelvethFrameInclude.setVisibility(View.GONE);
                binding.thirteenFrameInclude.setVisibility(View.GONE);
                binding.fourteenFrameInclude.setVisibility(View.GONE);
                binding.fifteenFrameInclude.setVisibility(View.GONE);
                binding.sixteenthFrameInclude.setVisibility(View.GONE);
                binding.seventeenthFrameInclude.setVisibility(View.GONE);
                binding.eighteenFrameInclude.setVisibility(View.GONE);
                binding.nineteenFrameInclude.setVisibility(View.GONE);
                binding.twentyFrameInclude.setVisibility(View.GONE);
                binding.twentyoneFrameInclude.setVisibility(View.GONE);
                binding.twentytwoFrameInclude.setVisibility(View.GONE);
                binding.twentythreeFrameInclude.setVisibility(View.GONE);
                binding.twentyfourFrameInclude.setVisibility(View.GONE);
                binding.twentyfiveFrameInclude.setVisibility(View.GONE);
                binding.twentysixFrameInclude.setVisibility(View.GONE);
                binding.twentysevenFrameInclude.setVisibility(View.GONE);
                binding.twentyeightFrameInclude.setVisibility(View.GONE);
                break;

            case 5:
                binding.firstFrameInclude.setVisibility(View.GONE);
                binding.thirdFrameInclude.setVisibility(View.GONE);
                binding.secondFrameInclude.setVisibility(View.GONE);
                binding.forthFrameInclude.setVisibility(View.GONE);
                binding.fifthFrameInclude.setVisibility(View.GONE);
                binding.sixthFrameInclude.setVisibility(View.VISIBLE);
                binding.seventhFrameInclude.setVisibility(View.GONE);
                binding.eightFrameInclude.setVisibility(View.GONE);
                binding.ninethFrameInclude.setVisibility(View.GONE);
                binding.tenthFrameInclude.setVisibility(View.GONE);
                binding.eleventhFrameInclude.setVisibility(View.GONE);
                binding.twelvethFrameInclude.setVisibility(View.GONE);
                binding.thirteenFrameInclude.setVisibility(View.GONE);
                binding.fourteenFrameInclude.setVisibility(View.GONE);
                binding.fifteenFrameInclude.setVisibility(View.GONE);
                binding.sixteenthFrameInclude.setVisibility(View.GONE);
                binding.seventeenthFrameInclude.setVisibility(View.GONE);
                binding.eighteenFrameInclude.setVisibility(View.GONE);
                binding.nineteenFrameInclude.setVisibility(View.GONE);
                binding.twentyFrameInclude.setVisibility(View.GONE);
                binding.twentyoneFrameInclude.setVisibility(View.GONE);
                binding.twentytwoFrameInclude.setVisibility(View.GONE);
                binding.twentythreeFrameInclude.setVisibility(View.GONE);
                binding.twentyfourFrameInclude.setVisibility(View.GONE);
                binding.twentyfiveFrameInclude.setVisibility(View.GONE);
                binding.twentysixFrameInclude.setVisibility(View.GONE);
                binding.twentysevenFrameInclude.setVisibility(View.GONE);
                binding.twentyeightFrameInclude.setVisibility(View.GONE);
                break;

            case 6:
                binding.firstFrameInclude.setVisibility(View.GONE);
                binding.thirdFrameInclude.setVisibility(View.GONE);
                binding.secondFrameInclude.setVisibility(View.GONE);
                binding.forthFrameInclude.setVisibility(View.GONE);
                binding.fifthFrameInclude.setVisibility(View.GONE);
                binding.sixthFrameInclude.setVisibility(View.GONE);
                binding.seventhFrameInclude.setVisibility(View.VISIBLE);
                binding.eightFrameInclude.setVisibility(View.GONE);
                binding.ninethFrameInclude.setVisibility(View.GONE);
                binding.tenthFrameInclude.setVisibility(View.GONE);
                binding.eleventhFrameInclude.setVisibility(View.GONE);
                binding.twelvethFrameInclude.setVisibility(View.GONE);
                binding.thirteenFrameInclude.setVisibility(View.GONE);
                binding.fourteenFrameInclude.setVisibility(View.GONE);
                binding.fifteenFrameInclude.setVisibility(View.GONE);
                binding.sixteenthFrameInclude.setVisibility(View.GONE);
                binding.seventeenthFrameInclude.setVisibility(View.GONE);
                binding.eighteenFrameInclude.setVisibility(View.GONE);
                binding.nineteenFrameInclude.setVisibility(View.GONE);
                binding.twentyFrameInclude.setVisibility(View.GONE);
                binding.twentyoneFrameInclude.setVisibility(View.GONE);
                binding.twentytwoFrameInclude.setVisibility(View.GONE);
                binding.twentythreeFrameInclude.setVisibility(View.GONE);
                binding.twentyfourFrameInclude.setVisibility(View.GONE);
                binding.twentyfiveFrameInclude.setVisibility(View.GONE);
                binding.twentysixFrameInclude.setVisibility(View.GONE);
                binding.twentysevenFrameInclude.setVisibility(View.GONE);
                binding.twentyeightFrameInclude.setVisibility(View.GONE);
                break;

            case 7:
                binding.firstFrameInclude.setVisibility(View.GONE);
                binding.thirdFrameInclude.setVisibility(View.GONE);
                binding.secondFrameInclude.setVisibility(View.GONE);
                binding.forthFrameInclude.setVisibility(View.GONE);
                binding.fifthFrameInclude.setVisibility(View.GONE);
                binding.sixthFrameInclude.setVisibility(View.GONE);
                binding.seventhFrameInclude.setVisibility(View.GONE);
                binding.eightFrameInclude.setVisibility(View.VISIBLE);
                binding.ninethFrameInclude.setVisibility(View.GONE);
                binding.tenthFrameInclude.setVisibility(View.GONE);
                binding.eleventhFrameInclude.setVisibility(View.GONE);
                binding.twelvethFrameInclude.setVisibility(View.GONE);
                binding.thirteenFrameInclude.setVisibility(View.GONE);
                binding.fourteenFrameInclude.setVisibility(View.GONE);
                binding.fifteenFrameInclude.setVisibility(View.GONE);
                binding.sixteenthFrameInclude.setVisibility(View.GONE);
                binding.seventeenthFrameInclude.setVisibility(View.GONE);
                binding.eighteenFrameInclude.setVisibility(View.GONE);
                binding.nineteenFrameInclude.setVisibility(View.GONE);
                binding.twentyFrameInclude.setVisibility(View.GONE);
                binding.twentyoneFrameInclude.setVisibility(View.GONE);
                binding.twentytwoFrameInclude.setVisibility(View.GONE);
                binding.twentythreeFrameInclude.setVisibility(View.GONE);
                binding.twentyfourFrameInclude.setVisibility(View.GONE);
                binding.twentyfiveFrameInclude.setVisibility(View.GONE);
                binding.twentysixFrameInclude.setVisibility(View.GONE);
                binding.twentysevenFrameInclude.setVisibility(View.GONE);
                binding.twentyeightFrameInclude.setVisibility(View.GONE);
                break;

            case 8:
                binding.firstFrameInclude.setVisibility(View.GONE);
                binding.thirdFrameInclude.setVisibility(View.GONE);
                binding.secondFrameInclude.setVisibility(View.GONE);
                binding.forthFrameInclude.setVisibility(View.GONE);
                binding.fifthFrameInclude.setVisibility(View.GONE);
                binding.sixthFrameInclude.setVisibility(View.GONE);
                binding.seventhFrameInclude.setVisibility(View.GONE);
                binding.eightFrameInclude.setVisibility(View.GONE);
                binding.ninethFrameInclude.setVisibility(View.VISIBLE);
                binding.tenthFrameInclude.setVisibility(View.GONE);
                binding.eleventhFrameInclude.setVisibility(View.GONE);
                binding.twelvethFrameInclude.setVisibility(View.GONE);
                binding.thirteenFrameInclude.setVisibility(View.GONE);
                binding.fourteenFrameInclude.setVisibility(View.GONE);
                binding.fifteenFrameInclude.setVisibility(View.GONE);
                binding.sixteenthFrameInclude.setVisibility(View.GONE);
                binding.seventeenthFrameInclude.setVisibility(View.GONE);
                binding.eighteenFrameInclude.setVisibility(View.GONE);
                binding.nineteenFrameInclude.setVisibility(View.GONE);
                binding.twentyFrameInclude.setVisibility(View.GONE);
                binding.twentyoneFrameInclude.setVisibility(View.GONE);
                binding.twentytwoFrameInclude.setVisibility(View.GONE);
                binding.twentythreeFrameInclude.setVisibility(View.GONE);
                binding.twentyfourFrameInclude.setVisibility(View.GONE);
                binding.twentyfiveFrameInclude.setVisibility(View.GONE);
                binding.twentysixFrameInclude.setVisibility(View.GONE);
                binding.twentysevenFrameInclude.setVisibility(View.GONE);
                binding.twentyeightFrameInclude.setVisibility(View.GONE);
                break;

            case 9:
                binding.firstFrameInclude.setVisibility(View.GONE);
                binding.thirdFrameInclude.setVisibility(View.GONE);
                binding.secondFrameInclude.setVisibility(View.GONE);
                binding.forthFrameInclude.setVisibility(View.GONE);
                binding.fifthFrameInclude.setVisibility(View.GONE);
                binding.sixthFrameInclude.setVisibility(View.GONE);
                binding.seventhFrameInclude.setVisibility(View.GONE);
                binding.eightFrameInclude.setVisibility(View.GONE);
                binding.ninethFrameInclude.setVisibility(View.GONE);
                binding.tenthFrameInclude.setVisibility(View.VISIBLE);
                binding.eleventhFrameInclude.setVisibility(View.GONE);
                binding.twelvethFrameInclude.setVisibility(View.GONE);
                binding.thirteenFrameInclude.setVisibility(View.GONE);
                binding.fourteenFrameInclude.setVisibility(View.GONE);
                binding.fifteenFrameInclude.setVisibility(View.GONE);
                binding.sixteenthFrameInclude.setVisibility(View.GONE);
                binding.seventeenthFrameInclude.setVisibility(View.GONE);
                binding.eighteenFrameInclude.setVisibility(View.GONE);
                binding.nineteenFrameInclude.setVisibility(View.GONE);
                binding.twentyFrameInclude.setVisibility(View.GONE);
                binding.twentyoneFrameInclude.setVisibility(View.GONE);
                binding.twentytwoFrameInclude.setVisibility(View.GONE);
                binding.twentythreeFrameInclude.setVisibility(View.GONE);
                binding.twentyfourFrameInclude.setVisibility(View.GONE);
                binding.twentyfiveFrameInclude.setVisibility(View.GONE);
                binding.twentysixFrameInclude.setVisibility(View.GONE);
                binding.twentysevenFrameInclude.setVisibility(View.GONE);
                binding.twentyeightFrameInclude.setVisibility(View.GONE);
                break;

            case 10:
                binding.firstFrameInclude.setVisibility(View.GONE);
                binding.thirdFrameInclude.setVisibility(View.GONE);
                binding.secondFrameInclude.setVisibility(View.GONE);
                binding.forthFrameInclude.setVisibility(View.GONE);
                binding.fifthFrameInclude.setVisibility(View.GONE);
                binding.sixthFrameInclude.setVisibility(View.GONE);
                binding.seventhFrameInclude.setVisibility(View.GONE);
                binding.eightFrameInclude.setVisibility(View.GONE);
                binding.ninethFrameInclude.setVisibility(View.GONE);
                binding.tenthFrameInclude.setVisibility(View.GONE);
                binding.eleventhFrameInclude.setVisibility(View.VISIBLE);
                binding.twelvethFrameInclude.setVisibility(View.GONE);
                binding.thirteenFrameInclude.setVisibility(View.GONE);
                binding.fourteenFrameInclude.setVisibility(View.GONE);
                binding.fifteenFrameInclude.setVisibility(View.GONE);
                binding.sixteenthFrameInclude.setVisibility(View.GONE);
                binding.seventeenthFrameInclude.setVisibility(View.GONE);
                binding.eighteenFrameInclude.setVisibility(View.GONE);
                binding.nineteenFrameInclude.setVisibility(View.GONE);
                binding.twentyFrameInclude.setVisibility(View.GONE);
                binding.twentyoneFrameInclude.setVisibility(View.GONE);
                binding.twentytwoFrameInclude.setVisibility(View.GONE);
                binding.twentythreeFrameInclude.setVisibility(View.GONE);
                binding.twentyfourFrameInclude.setVisibility(View.GONE);
                binding.twentyfiveFrameInclude.setVisibility(View.GONE);
                binding.twentysixFrameInclude.setVisibility(View.GONE);
                binding.twentysevenFrameInclude.setVisibility(View.GONE);
                binding.twentyeightFrameInclude.setVisibility(View.GONE);
                break;

            case 11:
                binding.firstFrameInclude.setVisibility(View.GONE);
                binding.thirdFrameInclude.setVisibility(View.GONE);
                binding.secondFrameInclude.setVisibility(View.GONE);
                binding.forthFrameInclude.setVisibility(View.GONE);
                binding.fifthFrameInclude.setVisibility(View.GONE);
                binding.sixthFrameInclude.setVisibility(View.GONE);
                binding.seventhFrameInclude.setVisibility(View.GONE);
                binding.eightFrameInclude.setVisibility(View.GONE);
                binding.ninethFrameInclude.setVisibility(View.GONE);
                binding.tenthFrameInclude.setVisibility(View.GONE);
                binding.eleventhFrameInclude.setVisibility(View.GONE);
                binding.twelvethFrameInclude.setVisibility(View.VISIBLE);
                binding.thirteenFrameInclude.setVisibility(View.GONE);
                binding.fourteenFrameInclude.setVisibility(View.GONE);
                binding.fifteenFrameInclude.setVisibility(View.GONE);
                binding.sixteenthFrameInclude.setVisibility(View.GONE);
                binding.seventeenthFrameInclude.setVisibility(View.GONE);
                binding.eighteenFrameInclude.setVisibility(View.GONE);
                binding.nineteenFrameInclude.setVisibility(View.GONE);
                binding.twentyFrameInclude.setVisibility(View.GONE);
                binding.twentyoneFrameInclude.setVisibility(View.GONE);
                binding.twentytwoFrameInclude.setVisibility(View.GONE);
                binding.twentythreeFrameInclude.setVisibility(View.GONE);
                binding.twentyfourFrameInclude.setVisibility(View.GONE);
                binding.twentyfiveFrameInclude.setVisibility(View.GONE);
                binding.twentysixFrameInclude.setVisibility(View.GONE);
                binding.twentysevenFrameInclude.setVisibility(View.GONE);
                binding.twentyeightFrameInclude.setVisibility(View.GONE);
                break;

            case 12:
                binding.firstFrameInclude.setVisibility(View.GONE);
                binding.thirdFrameInclude.setVisibility(View.GONE);
                binding.secondFrameInclude.setVisibility(View.GONE);
                binding.forthFrameInclude.setVisibility(View.GONE);
                binding.fifthFrameInclude.setVisibility(View.GONE);
                binding.sixthFrameInclude.setVisibility(View.GONE);
                binding.seventhFrameInclude.setVisibility(View.GONE);
                binding.eightFrameInclude.setVisibility(View.GONE);
                binding.ninethFrameInclude.setVisibility(View.GONE);
                binding.tenthFrameInclude.setVisibility(View.GONE);
                binding.eleventhFrameInclude.setVisibility(View.GONE);
                binding.twelvethFrameInclude.setVisibility(View.GONE);
                binding.thirteenFrameInclude.setVisibility(View.VISIBLE);
                binding.fourteenFrameInclude.setVisibility(View.GONE);
                binding.fifteenFrameInclude.setVisibility(View.GONE);
                binding.sixteenthFrameInclude.setVisibility(View.GONE);
                binding.seventeenthFrameInclude.setVisibility(View.GONE);
                binding.eighteenFrameInclude.setVisibility(View.GONE);
                binding.nineteenFrameInclude.setVisibility(View.GONE);
                binding.twentyFrameInclude.setVisibility(View.GONE);
                binding.twentyoneFrameInclude.setVisibility(View.GONE);
                binding.twentytwoFrameInclude.setVisibility(View.GONE);
                binding.twentythreeFrameInclude.setVisibility(View.GONE);
                binding.twentyfourFrameInclude.setVisibility(View.GONE);
                binding.twentyfiveFrameInclude.setVisibility(View.GONE);
                binding.twentysixFrameInclude.setVisibility(View.GONE);
                binding.twentysevenFrameInclude.setVisibility(View.GONE);
                binding.twentyeightFrameInclude.setVisibility(View.GONE);
                break;
            case 13:
                binding.firstFrameInclude.setVisibility(View.GONE);
                binding.thirdFrameInclude.setVisibility(View.GONE);
                binding.secondFrameInclude.setVisibility(View.GONE);
                binding.forthFrameInclude.setVisibility(View.GONE);
                binding.fifthFrameInclude.setVisibility(View.GONE);
                binding.sixthFrameInclude.setVisibility(View.GONE);
                binding.seventhFrameInclude.setVisibility(View.GONE);
                binding.eightFrameInclude.setVisibility(View.GONE);
                binding.ninethFrameInclude.setVisibility(View.GONE);
                binding.tenthFrameInclude.setVisibility(View.GONE);
                binding.eleventhFrameInclude.setVisibility(View.GONE);
                binding.twelvethFrameInclude.setVisibility(View.GONE);
                binding.thirteenFrameInclude.setVisibility(View.GONE);
                binding.fourteenFrameInclude.setVisibility(View.VISIBLE);
                binding.fifteenFrameInclude.setVisibility(View.GONE);
                binding.sixteenthFrameInclude.setVisibility(View.GONE);
                binding.seventeenthFrameInclude.setVisibility(View.GONE);
                binding.eighteenFrameInclude.setVisibility(View.GONE);
                binding.nineteenFrameInclude.setVisibility(View.GONE);
                binding.twentyFrameInclude.setVisibility(View.GONE);
                binding.twentyoneFrameInclude.setVisibility(View.GONE);
                binding.twentytwoFrameInclude.setVisibility(View.GONE);
                binding.twentythreeFrameInclude.setVisibility(View.GONE);
                binding.twentyfourFrameInclude.setVisibility(View.GONE);
                binding.twentyfiveFrameInclude.setVisibility(View.GONE);
                binding.twentysixFrameInclude.setVisibility(View.GONE);
                binding.twentysevenFrameInclude.setVisibility(View.GONE);
                binding.twentyeightFrameInclude.setVisibility(View.GONE);
                break;

            case 14:
                binding.firstFrameInclude.setVisibility(View.GONE);
                binding.thirdFrameInclude.setVisibility(View.GONE);
                binding.secondFrameInclude.setVisibility(View.GONE);
                binding.forthFrameInclude.setVisibility(View.GONE);
                binding.fifthFrameInclude.setVisibility(View.GONE);
                binding.sixthFrameInclude.setVisibility(View.GONE);
                binding.seventhFrameInclude.setVisibility(View.GONE);
                binding.eightFrameInclude.setVisibility(View.GONE);
                binding.ninethFrameInclude.setVisibility(View.GONE);
                binding.tenthFrameInclude.setVisibility(View.GONE);
                binding.eleventhFrameInclude.setVisibility(View.GONE);
                binding.twelvethFrameInclude.setVisibility(View.GONE);
                binding.thirteenFrameInclude.setVisibility(View.GONE);
                binding.fourteenFrameInclude.setVisibility(View.GONE);
                binding.fifteenFrameInclude.setVisibility(View.VISIBLE);
                binding.sixteenthFrameInclude.setVisibility(View.GONE);
                binding.seventeenthFrameInclude.setVisibility(View.GONE);
                binding.eighteenFrameInclude.setVisibility(View.GONE);
                binding.nineteenFrameInclude.setVisibility(View.GONE);
                binding.twentyFrameInclude.setVisibility(View.GONE);
                binding.twentyoneFrameInclude.setVisibility(View.GONE);
                binding.twentytwoFrameInclude.setVisibility(View.GONE);
                binding.twentythreeFrameInclude.setVisibility(View.GONE);
                binding.twentyfourFrameInclude.setVisibility(View.GONE);
                binding.twentyfiveFrameInclude.setVisibility(View.GONE);
                binding.twentysixFrameInclude.setVisibility(View.GONE);
                binding.twentysevenFrameInclude.setVisibility(View.GONE);
                binding.twentyeightFrameInclude.setVisibility(View.GONE);
                break;
            case 15:
                binding.firstFrameInclude.setVisibility(View.GONE);
                binding.thirdFrameInclude.setVisibility(View.GONE);
                binding.secondFrameInclude.setVisibility(View.GONE);
                binding.forthFrameInclude.setVisibility(View.GONE);
                binding.fifthFrameInclude.setVisibility(View.GONE);
                binding.sixthFrameInclude.setVisibility(View.GONE);
                binding.seventhFrameInclude.setVisibility(View.GONE);
                binding.eightFrameInclude.setVisibility(View.GONE);
                binding.ninethFrameInclude.setVisibility(View.GONE);
                binding.tenthFrameInclude.setVisibility(View.GONE);
                binding.eleventhFrameInclude.setVisibility(View.GONE);
                binding.twelvethFrameInclude.setVisibility(View.GONE);
                binding.thirteenFrameInclude.setVisibility(View.GONE);
                binding.fourteenFrameInclude.setVisibility(View.GONE);
                binding.fifteenFrameInclude.setVisibility(View.GONE);
                binding.sixteenthFrameInclude.setVisibility(View.VISIBLE);
                binding.seventeenthFrameInclude.setVisibility(View.GONE);
                binding.eighteenFrameInclude.setVisibility(View.GONE);
                binding.nineteenFrameInclude.setVisibility(View.GONE);
                binding.twentyFrameInclude.setVisibility(View.GONE);
                binding.twentyoneFrameInclude.setVisibility(View.GONE);
                binding.twentytwoFrameInclude.setVisibility(View.GONE);
                binding.twentythreeFrameInclude.setVisibility(View.GONE);
                binding.twentyfourFrameInclude.setVisibility(View.GONE);
                binding.twentyfiveFrameInclude.setVisibility(View.GONE);
                binding.twentysixFrameInclude.setVisibility(View.GONE);
                binding.twentysevenFrameInclude.setVisibility(View.GONE);
                binding.twentyeightFrameInclude.setVisibility(View.GONE);
                break;
            case 16:
                binding.firstFrameInclude.setVisibility(View.GONE);
                binding.thirdFrameInclude.setVisibility(View.GONE);
                binding.secondFrameInclude.setVisibility(View.GONE);
                binding.forthFrameInclude.setVisibility(View.GONE);
                binding.fifthFrameInclude.setVisibility(View.GONE);
                binding.sixthFrameInclude.setVisibility(View.GONE);
                binding.seventhFrameInclude.setVisibility(View.GONE);
                binding.eightFrameInclude.setVisibility(View.GONE);
                binding.ninethFrameInclude.setVisibility(View.GONE);
                binding.tenthFrameInclude.setVisibility(View.GONE);
                binding.eleventhFrameInclude.setVisibility(View.GONE);
                binding.twelvethFrameInclude.setVisibility(View.GONE);
                binding.thirteenFrameInclude.setVisibility(View.GONE);
                binding.fourteenFrameInclude.setVisibility(View.GONE);
                binding.fifteenFrameInclude.setVisibility(View.GONE);
                binding.sixteenthFrameInclude.setVisibility(View.GONE);
                binding.seventeenthFrameInclude.setVisibility(View.VISIBLE);
                binding.eighteenFrameInclude.setVisibility(View.GONE);
                binding.nineteenFrameInclude.setVisibility(View.GONE);
                binding.twentyFrameInclude.setVisibility(View.GONE);
                binding.twentyoneFrameInclude.setVisibility(View.GONE);
                binding.twentytwoFrameInclude.setVisibility(View.GONE);
                binding.twentythreeFrameInclude.setVisibility(View.GONE);
                binding.twentyfourFrameInclude.setVisibility(View.GONE);
                binding.twentyfiveFrameInclude.setVisibility(View.GONE);
                binding.twentysixFrameInclude.setVisibility(View.GONE);
                binding.twentysevenFrameInclude.setVisibility(View.GONE);
                binding.twentyeightFrameInclude.setVisibility(View.GONE);
                break;
            case 17:
                binding.firstFrameInclude.setVisibility(View.GONE);
                binding.thirdFrameInclude.setVisibility(View.GONE);
                binding.secondFrameInclude.setVisibility(View.GONE);
                binding.forthFrameInclude.setVisibility(View.GONE);
                binding.fifthFrameInclude.setVisibility(View.GONE);
                binding.sixthFrameInclude.setVisibility(View.GONE);
                binding.seventhFrameInclude.setVisibility(View.GONE);
                binding.eightFrameInclude.setVisibility(View.GONE);
                binding.ninethFrameInclude.setVisibility(View.GONE);
                binding.tenthFrameInclude.setVisibility(View.GONE);
                binding.eleventhFrameInclude.setVisibility(View.GONE);
                binding.twelvethFrameInclude.setVisibility(View.GONE);
                binding.thirteenFrameInclude.setVisibility(View.GONE);
                binding.fourteenFrameInclude.setVisibility(View.GONE);
                binding.fifteenFrameInclude.setVisibility(View.GONE);
                binding.sixteenthFrameInclude.setVisibility(View.GONE);
                binding.seventeenthFrameInclude.setVisibility(View.GONE);
                binding.eighteenFrameInclude.setVisibility(View.VISIBLE);
                binding.nineteenFrameInclude.setVisibility(View.GONE);
                binding.twentyFrameInclude.setVisibility(View.GONE);
                binding.twentyoneFrameInclude.setVisibility(View.GONE);
                binding.twentytwoFrameInclude.setVisibility(View.GONE);
                binding.twentythreeFrameInclude.setVisibility(View.GONE);
                binding.twentyfourFrameInclude.setVisibility(View.GONE);
                binding.twentyfiveFrameInclude.setVisibility(View.GONE);
                binding.twentysixFrameInclude.setVisibility(View.GONE);
                binding.twentysevenFrameInclude.setVisibility(View.GONE);
                binding.twentyeightFrameInclude.setVisibility(View.GONE);
                break;
            case 18:
                binding.firstFrameInclude.setVisibility(View.GONE);
                binding.thirdFrameInclude.setVisibility(View.GONE);
                binding.secondFrameInclude.setVisibility(View.GONE);
                binding.forthFrameInclude.setVisibility(View.GONE);
                binding.fifthFrameInclude.setVisibility(View.GONE);
                binding.sixthFrameInclude.setVisibility(View.GONE);
                binding.seventhFrameInclude.setVisibility(View.GONE);
                binding.eightFrameInclude.setVisibility(View.GONE);
                binding.ninethFrameInclude.setVisibility(View.GONE);
                binding.tenthFrameInclude.setVisibility(View.GONE);
                binding.eleventhFrameInclude.setVisibility(View.GONE);
                binding.twelvethFrameInclude.setVisibility(View.GONE);
                binding.thirteenFrameInclude.setVisibility(View.GONE);
                binding.fourteenFrameInclude.setVisibility(View.GONE);
                binding.fifteenFrameInclude.setVisibility(View.GONE);
                binding.sixteenthFrameInclude.setVisibility(View.GONE);
                binding.seventeenthFrameInclude.setVisibility(View.GONE);
                binding.eighteenFrameInclude.setVisibility(View.GONE);
                binding.nineteenFrameInclude.setVisibility(View.VISIBLE);
                binding.twentyFrameInclude.setVisibility(View.GONE);
                binding.twentyoneFrameInclude.setVisibility(View.GONE);
                binding.twentytwoFrameInclude.setVisibility(View.GONE);
                binding.twentythreeFrameInclude.setVisibility(View.GONE);
                binding.twentyfourFrameInclude.setVisibility(View.GONE);
                binding.twentyfiveFrameInclude.setVisibility(View.GONE);
                binding.twentysixFrameInclude.setVisibility(View.GONE);
                binding.twentysevenFrameInclude.setVisibility(View.GONE);
                binding.twentyeightFrameInclude.setVisibility(View.GONE);
                break;
            case 19:
                binding.firstFrameInclude.setVisibility(View.GONE);
                binding.thirdFrameInclude.setVisibility(View.GONE);
                binding.secondFrameInclude.setVisibility(View.GONE);
                binding.forthFrameInclude.setVisibility(View.GONE);
                binding.fifthFrameInclude.setVisibility(View.GONE);
                binding.sixthFrameInclude.setVisibility(View.GONE);
                binding.seventhFrameInclude.setVisibility(View.GONE);
                binding.eightFrameInclude.setVisibility(View.GONE);
                binding.ninethFrameInclude.setVisibility(View.GONE);
                binding.tenthFrameInclude.setVisibility(View.GONE);
                binding.eleventhFrameInclude.setVisibility(View.GONE);
                binding.twelvethFrameInclude.setVisibility(View.GONE);
                binding.thirteenFrameInclude.setVisibility(View.GONE);
                binding.fourteenFrameInclude.setVisibility(View.GONE);
                binding.fifteenFrameInclude.setVisibility(View.GONE);
                binding.sixteenthFrameInclude.setVisibility(View.GONE);
                binding.seventeenthFrameInclude.setVisibility(View.GONE);
                binding.eighteenFrameInclude.setVisibility(View.GONE);
                binding.nineteenFrameInclude.setVisibility(View.GONE);
                binding.twentyFrameInclude.setVisibility(View.VISIBLE);
                binding.twentyoneFrameInclude.setVisibility(View.GONE);
                binding.twentytwoFrameInclude.setVisibility(View.GONE);
                binding.twentythreeFrameInclude.setVisibility(View.GONE);
                binding.twentyfourFrameInclude.setVisibility(View.GONE);
                binding.twentyfiveFrameInclude.setVisibility(View.GONE);
                binding.twentysixFrameInclude.setVisibility(View.GONE);
                binding.twentysevenFrameInclude.setVisibility(View.GONE);
                binding.twentyeightFrameInclude.setVisibility(View.GONE);
                break;
            case 20:
                binding.firstFrameInclude.setVisibility(View.GONE);
                binding.thirdFrameInclude.setVisibility(View.GONE);
                binding.secondFrameInclude.setVisibility(View.GONE);
                binding.forthFrameInclude.setVisibility(View.GONE);
                binding.fifthFrameInclude.setVisibility(View.GONE);
                binding.sixthFrameInclude.setVisibility(View.GONE);
                binding.seventhFrameInclude.setVisibility(View.GONE);
                binding.eightFrameInclude.setVisibility(View.GONE);
                binding.ninethFrameInclude.setVisibility(View.GONE);
                binding.tenthFrameInclude.setVisibility(View.GONE);
                binding.eleventhFrameInclude.setVisibility(View.GONE);
                binding.twelvethFrameInclude.setVisibility(View.GONE);
                binding.thirteenFrameInclude.setVisibility(View.GONE);
                binding.fourteenFrameInclude.setVisibility(View.GONE);
                binding.fifteenFrameInclude.setVisibility(View.GONE);
                binding.sixteenthFrameInclude.setVisibility(View.GONE);
                binding.seventeenthFrameInclude.setVisibility(View.GONE);
                binding.eighteenFrameInclude.setVisibility(View.GONE);
                binding.nineteenFrameInclude.setVisibility(View.GONE);
                binding.twentyFrameInclude.setVisibility(View.GONE);
                binding.twentyoneFrameInclude.setVisibility(View.VISIBLE);
                binding.twentytwoFrameInclude.setVisibility(View.GONE);
                binding.twentythreeFrameInclude.setVisibility(View.GONE);
                binding.twentyfourFrameInclude.setVisibility(View.GONE);
                binding.twentyfiveFrameInclude.setVisibility(View.GONE);
                binding.twentysixFrameInclude.setVisibility(View.GONE);
                binding.twentysevenFrameInclude.setVisibility(View.GONE);
                binding.twentyeightFrameInclude.setVisibility(View.GONE);
                break;
            case 21:
                binding.firstFrameInclude.setVisibility(View.GONE);
                binding.thirdFrameInclude.setVisibility(View.GONE);
                binding.secondFrameInclude.setVisibility(View.GONE);
                binding.forthFrameInclude.setVisibility(View.GONE);
                binding.fifthFrameInclude.setVisibility(View.GONE);
                binding.sixthFrameInclude.setVisibility(View.GONE);
                binding.seventhFrameInclude.setVisibility(View.GONE);
                binding.eightFrameInclude.setVisibility(View.GONE);
                binding.ninethFrameInclude.setVisibility(View.GONE);
                binding.tenthFrameInclude.setVisibility(View.GONE);
                binding.eleventhFrameInclude.setVisibility(View.GONE);
                binding.twelvethFrameInclude.setVisibility(View.GONE);
                binding.thirteenFrameInclude.setVisibility(View.GONE);
                binding.fourteenFrameInclude.setVisibility(View.GONE);
                binding.fifteenFrameInclude.setVisibility(View.GONE);
                binding.sixteenthFrameInclude.setVisibility(View.GONE);
                binding.seventeenthFrameInclude.setVisibility(View.GONE);
                binding.eighteenFrameInclude.setVisibility(View.GONE);
                binding.nineteenFrameInclude.setVisibility(View.GONE);
                binding.twentyFrameInclude.setVisibility(View.GONE);
                binding.twentyoneFrameInclude.setVisibility(View.GONE);
                binding.twentytwoFrameInclude.setVisibility(View.VISIBLE);
                binding.twentythreeFrameInclude.setVisibility(View.GONE);
                binding.twentyfourFrameInclude.setVisibility(View.GONE);
                binding.twentyfiveFrameInclude.setVisibility(View.GONE);
                binding.twentysixFrameInclude.setVisibility(View.GONE);
                binding.twentysevenFrameInclude.setVisibility(View.GONE);
                binding.twentyeightFrameInclude.setVisibility(View.GONE);
                break;
            case 22:
                binding.firstFrameInclude.setVisibility(View.GONE);
                binding.thirdFrameInclude.setVisibility(View.GONE);
                binding.secondFrameInclude.setVisibility(View.GONE);
                binding.forthFrameInclude.setVisibility(View.GONE);
                binding.fifthFrameInclude.setVisibility(View.GONE);
                binding.sixthFrameInclude.setVisibility(View.GONE);
                binding.seventhFrameInclude.setVisibility(View.GONE);
                binding.eightFrameInclude.setVisibility(View.GONE);
                binding.ninethFrameInclude.setVisibility(View.GONE);
                binding.tenthFrameInclude.setVisibility(View.GONE);
                binding.eleventhFrameInclude.setVisibility(View.GONE);
                binding.twelvethFrameInclude.setVisibility(View.GONE);
                binding.thirteenFrameInclude.setVisibility(View.GONE);
                binding.fourteenFrameInclude.setVisibility(View.GONE);
                binding.fifteenFrameInclude.setVisibility(View.GONE);
                binding.sixteenthFrameInclude.setVisibility(View.GONE);
                binding.seventeenthFrameInclude.setVisibility(View.GONE);
                binding.eighteenFrameInclude.setVisibility(View.GONE);
                binding.nineteenFrameInclude.setVisibility(View.GONE);
                binding.twentyFrameInclude.setVisibility(View.GONE);
                binding.twentyoneFrameInclude.setVisibility(View.GONE);
                binding.twentytwoFrameInclude.setVisibility(View.GONE);
                binding.twentythreeFrameInclude.setVisibility(View.VISIBLE);
                binding.twentyfourFrameInclude.setVisibility(View.GONE);
                binding.twentyfiveFrameInclude.setVisibility(View.GONE);
                binding.twentysixFrameInclude.setVisibility(View.GONE);
                binding.twentysevenFrameInclude.setVisibility(View.GONE);
                binding.twentyeightFrameInclude.setVisibility(View.GONE);
                break;
            case 23:
                binding.firstFrameInclude.setVisibility(View.GONE);
                binding.thirdFrameInclude.setVisibility(View.GONE);
                binding.secondFrameInclude.setVisibility(View.GONE);
                binding.forthFrameInclude.setVisibility(View.GONE);
                binding.fifthFrameInclude.setVisibility(View.GONE);
                binding.sixthFrameInclude.setVisibility(View.GONE);
                binding.seventhFrameInclude.setVisibility(View.GONE);
                binding.eightFrameInclude.setVisibility(View.GONE);
                binding.ninethFrameInclude.setVisibility(View.GONE);
                binding.tenthFrameInclude.setVisibility(View.GONE);
                binding.eleventhFrameInclude.setVisibility(View.GONE);
                binding.twelvethFrameInclude.setVisibility(View.GONE);
                binding.thirteenFrameInclude.setVisibility(View.GONE);
                binding.fourteenFrameInclude.setVisibility(View.GONE);
                binding.fifteenFrameInclude.setVisibility(View.GONE);
                binding.sixteenthFrameInclude.setVisibility(View.GONE);
                binding.seventeenthFrameInclude.setVisibility(View.GONE);
                binding.eighteenFrameInclude.setVisibility(View.GONE);
                binding.nineteenFrameInclude.setVisibility(View.GONE);
                binding.twentyFrameInclude.setVisibility(View.GONE);
                binding.twentyoneFrameInclude.setVisibility(View.GONE);
                binding.twentytwoFrameInclude.setVisibility(View.GONE);
                binding.twentythreeFrameInclude.setVisibility(View.GONE);
                binding.twentyfourFrameInclude.setVisibility(View.VISIBLE);
                binding.twentyfiveFrameInclude.setVisibility(View.GONE);
                binding.twentysixFrameInclude.setVisibility(View.GONE);
                binding.twentysevenFrameInclude.setVisibility(View.GONE);
                binding.twentyeightFrameInclude.setVisibility(View.GONE);

                break;
            case 24:
                binding.firstFrameInclude.setVisibility(View.GONE);
                binding.thirdFrameInclude.setVisibility(View.GONE);
                binding.secondFrameInclude.setVisibility(View.GONE);
                binding.forthFrameInclude.setVisibility(View.GONE);
                binding.fifthFrameInclude.setVisibility(View.GONE);
                binding.sixthFrameInclude.setVisibility(View.GONE);
                binding.seventhFrameInclude.setVisibility(View.GONE);
                binding.eightFrameInclude.setVisibility(View.GONE);
                binding.ninethFrameInclude.setVisibility(View.GONE);
                binding.tenthFrameInclude.setVisibility(View.GONE);
                binding.eleventhFrameInclude.setVisibility(View.GONE);
                binding.twelvethFrameInclude.setVisibility(View.GONE);
                binding.thirteenFrameInclude.setVisibility(View.GONE);
                binding.fourteenFrameInclude.setVisibility(View.GONE);
                binding.fifteenFrameInclude.setVisibility(View.GONE);
                binding.sixteenthFrameInclude.setVisibility(View.GONE);
                binding.seventeenthFrameInclude.setVisibility(View.GONE);
                binding.eighteenFrameInclude.setVisibility(View.GONE);
                binding.nineteenFrameInclude.setVisibility(View.GONE);
                binding.twentyFrameInclude.setVisibility(View.GONE);
                binding.twentyoneFrameInclude.setVisibility(View.GONE);
                binding.twentytwoFrameInclude.setVisibility(View.GONE);
                binding.twentythreeFrameInclude.setVisibility(View.GONE);
                binding.twentyfourFrameInclude.setVisibility(View.GONE);
                binding.twentyfiveFrameInclude.setVisibility(View.VISIBLE);
                binding.twentysixFrameInclude.setVisibility(View.GONE);
                binding.twentysevenFrameInclude.setVisibility(View.GONE);
                binding.twentyeightFrameInclude.setVisibility(View.GONE);

                break;
            case 25:
                binding.firstFrameInclude.setVisibility(View.GONE);
                binding.thirdFrameInclude.setVisibility(View.GONE);
                binding.secondFrameInclude.setVisibility(View.GONE);
                binding.forthFrameInclude.setVisibility(View.GONE);
                binding.fifthFrameInclude.setVisibility(View.GONE);
                binding.sixthFrameInclude.setVisibility(View.GONE);
                binding.seventhFrameInclude.setVisibility(View.GONE);
                binding.eightFrameInclude.setVisibility(View.GONE);
                binding.ninethFrameInclude.setVisibility(View.GONE);
                binding.tenthFrameInclude.setVisibility(View.GONE);
                binding.eleventhFrameInclude.setVisibility(View.GONE);
                binding.twelvethFrameInclude.setVisibility(View.GONE);
                binding.thirteenFrameInclude.setVisibility(View.GONE);
                binding.fourteenFrameInclude.setVisibility(View.GONE);
                binding.fifteenFrameInclude.setVisibility(View.GONE);
                binding.sixteenthFrameInclude.setVisibility(View.GONE);
                binding.seventeenthFrameInclude.setVisibility(View.GONE);
                binding.eighteenFrameInclude.setVisibility(View.GONE);
                binding.nineteenFrameInclude.setVisibility(View.GONE);
                binding.twentyFrameInclude.setVisibility(View.GONE);
                binding.twentyoneFrameInclude.setVisibility(View.GONE);
                binding.twentytwoFrameInclude.setVisibility(View.GONE);
                binding.twentythreeFrameInclude.setVisibility(View.GONE);
                binding.twentyfourFrameInclude.setVisibility(View.GONE);
                binding.twentyfiveFrameInclude.setVisibility(View.GONE);
                binding.twentysixFrameInclude.setVisibility(View.VISIBLE);
                binding.twentysevenFrameInclude.setVisibility(View.GONE);
                binding.twentyeightFrameInclude.setVisibility(View.GONE);

                break;
            case 26:
                binding.firstFrameInclude.setVisibility(View.GONE);
                binding.thirdFrameInclude.setVisibility(View.GONE);
                binding.secondFrameInclude.setVisibility(View.GONE);
                binding.forthFrameInclude.setVisibility(View.GONE);
                binding.fifthFrameInclude.setVisibility(View.GONE);
                binding.sixthFrameInclude.setVisibility(View.GONE);
                binding.seventhFrameInclude.setVisibility(View.GONE);
                binding.eightFrameInclude.setVisibility(View.GONE);
                binding.ninethFrameInclude.setVisibility(View.GONE);
                binding.tenthFrameInclude.setVisibility(View.GONE);
                binding.eleventhFrameInclude.setVisibility(View.GONE);
                binding.twelvethFrameInclude.setVisibility(View.GONE);
                binding.thirteenFrameInclude.setVisibility(View.GONE);
                binding.fourteenFrameInclude.setVisibility(View.GONE);
                binding.fifteenFrameInclude.setVisibility(View.GONE);
                binding.sixteenthFrameInclude.setVisibility(View.GONE);
                binding.seventeenthFrameInclude.setVisibility(View.GONE);
                binding.eighteenFrameInclude.setVisibility(View.GONE);
                binding.nineteenFrameInclude.setVisibility(View.GONE);
                binding.twentyFrameInclude.setVisibility(View.GONE);
                binding.twentyoneFrameInclude.setVisibility(View.GONE);
                binding.twentytwoFrameInclude.setVisibility(View.GONE);
                binding.twentythreeFrameInclude.setVisibility(View.GONE);
                binding.twentyfourFrameInclude.setVisibility(View.GONE);
                binding.twentyfiveFrameInclude.setVisibility(View.GONE);
                binding.twentysixFrameInclude.setVisibility(View.GONE);
                binding.twentysevenFrameInclude.setVisibility(View.VISIBLE);
                binding.twentyeightFrameInclude.setVisibility(View.GONE);

                break;
            case 27:
                binding.firstFrameInclude.setVisibility(View.GONE);
                binding.thirdFrameInclude.setVisibility(View.GONE);
                binding.secondFrameInclude.setVisibility(View.GONE);
                binding.forthFrameInclude.setVisibility(View.GONE);
                binding.fifthFrameInclude.setVisibility(View.GONE);
                binding.sixthFrameInclude.setVisibility(View.GONE);
                binding.seventhFrameInclude.setVisibility(View.GONE);
                binding.eightFrameInclude.setVisibility(View.GONE);
                binding.ninethFrameInclude.setVisibility(View.GONE);
                binding.tenthFrameInclude.setVisibility(View.GONE);
                binding.eleventhFrameInclude.setVisibility(View.GONE);
                binding.twelvethFrameInclude.setVisibility(View.GONE);
                binding.thirteenFrameInclude.setVisibility(View.GONE);
                binding.fourteenFrameInclude.setVisibility(View.GONE);
                binding.fifteenFrameInclude.setVisibility(View.GONE);
                binding.sixteenthFrameInclude.setVisibility(View.GONE);
                binding.seventeenthFrameInclude.setVisibility(View.GONE);
                binding.eighteenFrameInclude.setVisibility(View.GONE);
                binding.nineteenFrameInclude.setVisibility(View.GONE);
                binding.twentyFrameInclude.setVisibility(View.GONE);
                binding.twentyoneFrameInclude.setVisibility(View.GONE);
                binding.twentytwoFrameInclude.setVisibility(View.GONE);
                binding.twentythreeFrameInclude.setVisibility(View.GONE);
                binding.twentyfourFrameInclude.setVisibility(View.GONE);
                binding.twentyfiveFrameInclude.setVisibility(View.GONE);
                binding.twentysixFrameInclude.setVisibility(View.GONE);
                binding.twentysevenFrameInclude.setVisibility(View.GONE);
                binding.twentyeightFrameInclude.setVisibility(View.VISIBLE);

                break;
            default:
                binding.firstFrameInclude.setVisibility(View.VISIBLE);
                binding.thirdFrameInclude.setVisibility(View.GONE);
                binding.secondFrameInclude.setVisibility(View.GONE);
                binding.forthFrameInclude.setVisibility(View.GONE);
                binding.fifthFrameInclude.setVisibility(View.GONE);
                binding.sixthFrameInclude.setVisibility(View.GONE);
                binding.seventhFrameInclude.setVisibility(View.GONE);
                binding.eightFrameInclude.setVisibility(View.GONE);
                binding.ninethFrameInclude.setVisibility(View.GONE);
                binding.tenthFrameInclude.setVisibility(View.GONE);
                binding.eleventhFrameInclude.setVisibility(View.GONE);
                binding.twelvethFrameInclude.setVisibility(View.GONE);
                binding.thirteenFrameInclude.setVisibility(View.GONE);
                binding.fourteenFrameInclude.setVisibility(View.GONE);
                binding.fifteenFrameInclude.setVisibility(View.GONE);
                binding.sixteenthFrameInclude.setVisibility(View.GONE);
                binding.seventeenthFrameInclude.setVisibility(View.GONE);
                binding.eighteenFrameInclude.setVisibility(View.GONE);
                binding.nineteenFrameInclude.setVisibility(View.GONE);
                binding.twentyFrameInclude.setVisibility(View.GONE);
                binding.twentyoneFrameInclude.setVisibility(View.GONE);
                binding.twentytwoFrameInclude.setVisibility(View.GONE);
                binding.twentythreeFrameInclude.setVisibility(View.GONE);
                binding.twentyfourFrameInclude.setVisibility(View.GONE);
                binding.twentyfiveFrameInclude.setVisibility(View.GONE);
                binding.twentysixFrameInclude.setVisibility(View.GONE);
                binding.twentysevenFrameInclude.setVisibility(View.GONE);
                binding.twentyeightFrameInclude.setVisibility(View.GONE);

                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.addText:
                if (binding.movableEditText.getVisibility() == View.GONE) {
                    binding.movableEditText.setVisibility(View.VISIBLE);

                 /*   Typeface face = Typeface.createFromAsset(getAssets(),
                            "fonts/roboto.ttf");
                    binding.movableEditText.setTypeface(face);*/
                    //  rotateZoomImageView.addText(binding.movableEditText.getText().toString(),Color.BLACK);

                } else {
                    binding.movableEditText.setVisibility(View.GONE);
                }
                break;

            case R.id.imgDone:
                File pictureFileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Brand Shaastra");
                if (!pictureFileDir.exists()) {
                    boolean isDirectoryCreated = pictureFileDir.mkdirs();

                }
                if (video_flag) {
                    shareImage("2");
                } else {
                    shareImage("3");
                }
                break;
            case R.id.video_share:
                shareImage("2");
                break;
            case R.id.imgClose:
                onBackPressed();
                break;
            case R.id.imgSticker:
                mStickerBSFragment.show(getSupportFragmentManager(), mStickerBSFragment.getTag());
                break;
           /* case R.id.business_icon_close:

                binding.businessImageRelative.setVisibility(View.GONE);
                break;*/

            case R.id.text:
                break;
            case R.id.frame:

                //  getFrameClick();
                break;

            case R.id.textlay:
                break;

            case R.id.textcolor:

                changeFontColor();
                break;

            case R.id.textsize:

                getBottomDialog("0");
                break;

            case R.id.fontstyle:
                getBottomDialog("1");
                break;

            case R.id.pickimage:

               /* if (video_flag) {
                    binding.imgSticker.performClick();
                } else {
*/
                ImagePicker.Companion.with(this)
                        .compress(1024)            //Final image size will be less than 1 MB(Optional)
                        .start(23);
                // }

                break;

            case R.id.edit_bg:
                setBackgroundColor();
                break;

            case R.id.image_share:
                shareImage("1");
                break;

            case R.id.movable_edit_text:
                break;

            case R.id.imageview_back_btn:
                onBackPressed();
                finish();
                break;

            case R.id.settings:
                getBottomDialog("2");
                break;
            case R.id.imageView:

                String a = binding.edittext.getText().toString();
                binding.text.setText(a);
                binding.text.setVisibility(View.VISIBLE);
                binding.textlay.setVisibility(View.GONE);

                break;

        }
    }

    private void saveimgFromBitmap() {
        Log.e("VODEO","vide create 2");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1011);
            } else {

                if (ContextCompat.checkSelfPermission(ImageCanvasActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(ImageCanvasActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                } else {

                    File file = saveBitMapFromImage(ImageCanvasActivity.this, binding.frameRelative);    //which view you want to pass that view as parameter
                    if (file != null) {
                        Log.i("SHIVAKASHI", "Drawing saved to the gallery!");
                    } else {
                        Log.i("SHIVAKASHI", "Oops! Image could not be saved.");
                    }
                }
            }
        }
    }

    private File saveBitMapFromImage(Context context, View drawView) {
        File pictureFileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Brand Shaastra");
        if (!pictureFileDir.exists()) {
            boolean isDirectoryCreated = pictureFileDir.mkdirs();
            if (!isDirectoryCreated)
                Log.i("ATG", "Can't create directory to save the image");
            return null;
        }
        String filename = pictureFileDir.getPath() + File.separator + System.currentTimeMillis() + ".jpeg";
        File pictureFile = new File(filename);
        Bitmap bitmap = getBitmapFromView(drawView);
        try {
            pictureFile.createNewFile();
            FileOutputStream oStream = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, oStream);
            oStream.flush();
            oStream.close();
            Toast.makeText(ImageCanvasActivity.this, "Image saved at Pictures/Brand Shaastra in your file manager", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Log.i("TAG", "There was an issue saving the image.");
        }
        return pictureFile;
    }

    private static Bitmap takeScreenShot(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay().getHeight();

        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height - statusBarHeight);
        view.destroyDrawingCache();
        return b;
    }

    public Bitmap fastblur(Bitmap sentBitmap, int radius) {
        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }

    @SuppressLint("MissingPermission")
    private void saveImage() {

        Log.e("VODEO","vide create");
        Bitmap map = takeScreenShot(ImageCanvasActivity.this);

        Bitmap fast = fastblur(map, 10);
        final Drawable draw = new BitmapDrawable(getResources(), fast);

        WindowManager.LayoutParams lp = progressDialog.getWindow().getAttributes();
        //lp.dimAmount=0.0f;
        progressDialog.getWindow().setAttributes(lp);
        progressDialog.setMessage("Creating video for you");
        progressDialog.show();
        //   progressDialog.getWindow().setBackgroundDrawable(draw);

        if (selected_theme_no == 0) {

            //binding.first.frame1.setPadding(5, 5, 5, 5);

            saveBitMapForVideo(ImageCanvasActivity.this, binding.frameRelative);    //which view you want to pass that view as parameter
        } else if (selected_theme_no == 1) {
            saveBitMapForVideo(ImageCanvasActivity.this, binding.frameRelative);    //which view you want to pass that view as parameter
        } else if (selected_theme_no == 2) {
            saveBitMapForVideo(ImageCanvasActivity.this, binding.frameRelative);    //which view you want to pass that view as parameter
        } else if (selected_theme_no == 3) {
            saveBitMapForVideo(ImageCanvasActivity.this, binding.frameRelative);    //which view you want to pass that view as parameter
        } else if (selected_theme_no == 4) {
            saveBitMapForVideo(ImageCanvasActivity.this, binding.frameRelative);    //which view you want to pass that view as parameter
        } else if (selected_theme_no == 5) {
            saveBitMapForVideo(ImageCanvasActivity.this, binding.frameRelative);    //which view you want to pass that view as parameter
        } else if (selected_theme_no == 6) {
            saveBitMapForVideo(ImageCanvasActivity.this, binding.frameRelative);    //which view you want to pass that view as parameter
        } else if (selected_theme_no == 7) {
            saveBitMapForVideo(ImageCanvasActivity.this, binding.frameRelative);    //which view you want to pass that view as parameter
        } else if (selected_theme_no == 8) {
            saveBitMapForVideo(ImageCanvasActivity.this, binding.frameRelative);    //which view you want to pass that view as parameter
        } else if (selected_theme_no == 9) {
            saveBitMapForVideo(ImageCanvasActivity.this, binding.frameRelative);    //which view you want to pass that view as parameter
        } else if (selected_theme_no == 10) {
            saveBitMapForVideo(ImageCanvasActivity.this, binding.frameRelative);    //which view you want to pass that view as parameter
        } else if (selected_theme_no == 11) {
            saveBitMapForVideo(ImageCanvasActivity.this, binding.frameRelative);    //which view you want to pass that view as parameter
        } else if (selected_theme_no == 12) {
            saveBitMapForVideo(ImageCanvasActivity.this, binding.frameRelative);    //which view you want to pass that view as parameter
        } else if (selected_theme_no == 13) {
            saveBitMapForVideo(ImageCanvasActivity.this, binding.frameRelative);    //which view you want to pass that view as parameter
        } else if (selected_theme_no == 14) {
            saveBitMapForVideo(ImageCanvasActivity.this, binding.frameRelative);    //which view you want to pass that view as parameter
        } else if (selected_theme_no == 15) {
            saveBitMapForVideo(ImageCanvasActivity.this, binding.frameRelative);    //which view you want to pass that view as parameter
        } else if (selected_theme_no == 16) {
            saveBitMapForVideo(ImageCanvasActivity.this, binding.frameRelative);    //which view you want to pass that view as parameter
        } else if (selected_theme_no == 17) {
            saveBitMapForVideo(ImageCanvasActivity.this, binding.frameRelative);    //which view you want to pass that view as parameter
        } else if (selected_theme_no == 18) {
            saveBitMapForVideo(ImageCanvasActivity.this, binding.frameRelative);    //which view you want to pass that view as parameter
        } else if (selected_theme_no == 19) {
            saveBitMapForVideo(ImageCanvasActivity.this, binding.frameRelative);    //which view you want to pass that view as parameter
        } else if (selected_theme_no == 20) {
            saveBitMapForVideo(ImageCanvasActivity.this, binding.frameRelative);    //which view you want to pass that view as parameter
        } else if (selected_theme_no == 21) {
            saveBitMapForVideo(ImageCanvasActivity.this, binding.frameRelative);    //which view you want to pass that view as parameter
        } else if (selected_theme_no == 22) {
            saveBitMapForVideo(ImageCanvasActivity.this, binding.frameRelative);    //which view you want to pass that view as parameter
        } else if (selected_theme_no == 23) {
            saveBitMapForVideo(ImageCanvasActivity.this, binding.frameRelative);    //which view you want to pass that view as parameter
        } else if (selected_theme_no == 24) {
            saveBitMapForVideo(ImageCanvasActivity.this, binding.frameRelative);    //which view you want to pass that view as parameter
        } else if (selected_theme_no == 25) {
            saveBitMapForVideo(ImageCanvasActivity.this, binding.frameRelative);    //which view you want to pass that view as parameter
        } else if (selected_theme_no == 26) {
            saveBitMapForVideo(ImageCanvasActivity.this, binding.frameRelative);    //which view you want to pass that view as parameter
        } else if (selected_theme_no == 27) {
            saveBitMapForVideo(ImageCanvasActivity.this, binding.frameRelative);    //which view you want to pass that view as parameter
        }


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //       checkFolder();
        /*File file, outputnew;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            file = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "shivam.png");
            Log.e("root", String.valueOf(file));

        } else {
            // root = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + context.getString(R.string.app_name) + "/temp");
//            file = new File(Environment.getExternalStorageDirectory() +
//                    fileN);
            file = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "shivam.png");
            outputnew = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "output.mp4");
        }*/

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + File.separator + +System.currentTimeMillis()
                + ".png");


        //video_img_path = file.getPath();
        //String path = FileUtility.getPath(ImageCanvasActivity.this, Uri.parse(video_img_path));

        //File image_file = new File(path);

        //  Log.e("video_img_path", "" + video_img_path);
        //   Log.e("video_img_path", " img_path " + file.toString());
      /*  try {
//            file.createNewFile();

            SaveSettings saveSettings = new SaveSettings.Builder()
                    *//*.setClearViewsEnabled(true)
                    .setTransparencyEnabled(true)*//*
                    .build();


            mPhotoEditor.saveAsFile(file.getAbsolutePath(), saveSettings, new PhotoEditor.OnSaveListener() {
                @SuppressLint("MissingPermission")
                @Override
                public void onSuccess(@NonNull String imagePath) {
                    ImageCanvasActivity.this.imagePath = imagePath;
                    Log.d("imagePath>>", imagePath);
                    Log.d("imagePath2>>", Uri.fromFile(new File(imagePath)).toString());
                    binding.videoImage.getSource().setImageURI(Uri.fromFile(new File(imagePath)));
                    //    Toast.makeText(ImageCanvasActivity.this, "Saved successfully...", Toast.LENGTH_SHORT).show();
                    applayWaterMark();
                }

                @Override
                public void onFailure(@NonNull Exception exception) {
                    //    Toast.makeText(ImageCanvasActivity.this, "Saving Failed...", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();

        }*/

        downloadVideo();

    }

    private void downloadVideo() {

        downloadVideoparams.put("video_path", videoPath);
        downloadVideoparams.put("video_height", String.valueOf(DRAW_CANVASH));
        downloadVideoparams.put("video_width", String.valueOf(DRAW_CANVASW));
        downloadVideoparams.put("video_url2", video_url2);
        downloadVideoparams.put(Consts.USER_ID, userDTO.getUser_id());

        Log.e("downloadVideo_res", " video_url2:--  " + video_url2);
        Log.e("downloadVideo_res", " params " + downloadVideoparams.toString());

        new HttpsRequest(Consts.VISEO_FFMPEG_API, downloadVideoparams, this).stringPos2("downloadVideo", new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                if (flag) {
                    Log.e("downloadVideo_res", "" + response.toString());

                    String uploaded_video_url = null;
                    try {
                        uploaded_video_url = response.getString("data");
                        Log.e("uploaded_video_url", "" + uploaded_video_url);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    VIDEO_FFMPEG_flag = true;
                    DownloadTask downloadTask = new DownloadTask(ImageCanvasActivity.this);
                    downloadTask.execute(uploaded_video_url);
                } else {

                }
            }
        });
    }

    public void saveBitMapImage(Context context, Bitmap b, String imageName) throws IOException {
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(imageName, Context.MODE_PRIVATE);
            b.compress(Bitmap.CompressFormat.PNG, 100, fos);

            Log.e("IMAGE_COM", "" + b.compress(Bitmap.CompressFormat.PNG, 100, fos));
        } catch (FileNotFoundException e) {
            Log.d("TAG", "file not found");
            e.printStackTrace();
        } finally {
            fos.close();
        }
    }

    public class DownloadTask extends AsyncTask<String, Integer, String> {
        private Context context;
        private PowerManager.WakeLock mWakeLock;
        String vid_url = "";

        public DownloadTask(Context context) {
            this.context = context;
        }

        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(ImageCanvasActivity.this);

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                java.net.URL url = new URL(sUrl[0]);
                vid_url = sUrl[0];
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                int fileLength = connection.getContentLength();

                input = connection.getInputStream();
                fileN = "/TBS_" + UUID.randomUUID().toString().substring(0, 10) + ".mp4";
//                fileN = "/.shivam.mp4";
                File pictureFileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Brand Shaastra");

                File filename = new File(pictureFileDir, fileN);
                output = new FileOutputStream(filename);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    if (fileLength > 0)
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            LayoutInflater dialogLayout = LayoutInflater.from(ImageCanvasActivity.this);
            // downloadDialog.setContentView(DialogView);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            // lp.copyFrom(downloadDialog.getWindow().getAttributes());
            lp.width = (getResources().getDisplayMetrics().widthPixels);
            lp.height = (int) (getResources().getDisplayMetrics().heightPixels * 0.65);
            //downloadDialog.setAttributes(lp);

            // final Button cancel = (Button) DialogView.findViewById(R.id.cancel_btn);
            downloadDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cancel(true);
                    dialog.dismiss();
                }
            });

            downloadDialog.setCancelable(false);
          /*  bnp = (NumberProgressBar)DialogView.findViewById(R.id.number_progress_bar);
            bnp.setProgress(0);
            bnp.setMax(100);*/
            // downloadDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            //bnp.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
//            Toast.makeText(ImageCanvasActivity.this, "Completed", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            Log.e("SHIVAKASHI", String.valueOf(fileN));
            /*String newpath = Environment.getExternalStorageDirectory().getAbsolutePath() + fileN;
            if (VIDEO_FFMPEG_flag) {

                Intent i = new Intent(ImageCanvasActivity.this, VideoPreviewActivity.class);
                i.putExtra("DATA", newpath);
                startActivity(i);
                finish();
            }

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(String.valueOf(newpath));
            String metaRotation = retriever.extractMetadata(METADATA_KEY_VIDEO_ROTATION);

            int rotation = metaRotation == null ? 0 : Integer.parseInt(metaRotation);
            if (rotation == 90 || rotation == 270) {
                DRAW_CANVASH = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
                DRAW_CANVASW = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
            } else {
                DRAW_CANVASW = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
                DRAW_CANVASH = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
            }*/
            /*if (result != null) {

                //  Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
            } else*/
            //  Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
            File pictureFileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Brand Shaastra");
            MediaScannerConnection.scanFile(ImageCanvasActivity.this,
                    new String[]{pictureFileDir + fileN}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String newpath, Uri newuri) {
                            Log.i("ExternalStorage", "Scanned " + newpath + ":");
                            Log.i("ExternalStorage", "-> uri=" + newuri);

                            if (VIDEO_FFMPEG_flag) {

                                Intent i = new Intent(ImageCanvasActivity.this, VideoPreviewActivity.class);
                                i.putExtra("DATA", newpath);
                                startActivity(i);
                                finish();
                            }

                            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                            retriever.setDataSource(String.valueOf(newpath));
                            String metaRotation = retriever.extractMetadata(METADATA_KEY_VIDEO_ROTATION);

                            int rotation = metaRotation == null ? 0 : Integer.parseInt(metaRotation);
                            if (rotation == 90 || rotation == 270) {
                                DRAW_CANVASH = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
                                DRAW_CANVASW = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
                            } else {
                                DRAW_CANVASW = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
                                DRAW_CANVASH = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
                            }
                            setCanvasAspectRatio();
                        }
                    });
        }
    }

    /*private void applayWaterMark() {

        File output = new File(Environment.getExternalStorageDirectory()
                + File.separator + ""
                + System.currentTimeMillis() + ".mp4");
        try {
            output.createNewFile();

            exeCmd.add("-y");
            exeCmd.add("-i");
            exeCmd.add("/storage/emulated/0/.shivam.mp4");
            exeCmd.add("-i");
            exeCmd.add("/storage/emulated/0/.shivam.png");
            exeCmd.add("-filter_complex");
            exeCmd.add("[1:v]scale=" + DRAW_CANVASW + ":" + DRAW_CANVASH + "[ovrl];[0:v][ovrl]overlay=x=0:y=0");
            exeCmd.add("-c:v");
            exeCmd.add("libx264");
            exeCmd.add("-preset");
            exeCmd.add("ultrafast");
            exeCmd.add(output.getAbsolutePath());

            newCommand = new String[exeCmd.size()];
            for (int j = 0; j < exeCmd.size(); j++) {
                newCommand[j] = exeCmd.get(j);
            }


            for (int k = 0; k < newCommand.length; k++) {
                Log.d("CMD==>>", newCommand[k] + "");
            }
            Log.e("TAG", "applayWaterMark:" + Arrays.toString(newCommand));
            executeCommand(newCommand, output.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void executeCommand(String[] command, final String absolutePath) {
        try {
            fFmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onSuccess(String s) {
                    Log.d("CommandExecute", "onSuccess" + "  " + s);
                    //   Toast.makeText(getApplicationContext(), "Sucess", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(ImageCanvasActivity.this, VideoPreviewActivity.class);
                    i.putExtra("DATA", absolutePath);
                    startActivity(i);
                    finish();
                }

                @Override
                public void onProgress(String s) {
                    //progressDialog.setMessage(s);
                    Log.d("CommandExecute", "onProgress" + "  " + s);

                }

                @Override
                public void onFailure(String s) {
                    //Log.d("CommandExecute", "onFailure" + "  " + s);
                    progressDialog.hide();

                }

                @Override
                public void onStart() {
                    progressDialog.setIcon(R.drawable.brand_shaastra_logo);
                    progressDialog.setTitle("Preccesing");
                    progressDialog.setMessage("Creating video for you...");
                    progressDialog.show();
                }

                @Override
                public void onFinish() {

                    File dir = getFilesDir();
                    File file = new File(dir, ".shivam.mp4");
                    boolean deleted = file.delete();
                    progressDialog.hide();
                }
            });
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
        }
    }*/

/*
    private void getFrameClick() {
        if (frame_flag) {
            Log.e("frame1234", "1");
            frame_flag = false;

            binding.bgView.setVisibility(View.GONE);
            if (image_background_flag && image_background_color != -1) {
                binding.first.frame1.setBackground(getResources().getDrawable(R.drawable.border_frame));
                GradientDrawable myGrad = (GradientDrawable) binding.first.frame1.getBackground();
                myGrad.setColor(image_background_color);

                binding.second.frame2.setBackground(getResources().getDrawable(R.drawable.border_frame));
                GradientDrawable myGrad2 = (GradientDrawable) binding.second.frame2.getBackground();
                myGrad2.setColor(image_background_color);

                binding.third.frame3.setBackground(getResources().getDrawable(R.drawable.border_frame));
                GradientDrawable myGrad3 = (GradientDrawable) binding.third.frame3.getBackground();
                myGrad3.setColor(image_background_color);

                binding.forth.frame4.setBackground(getResources().getDrawable(R.drawable.border_frame));
                GradientDrawable myGrad4 = (GradientDrawable) binding.forth.frame4.getBackground();
                myGrad4.setColor(image_background_color);

                binding.fifth.frame5.setBackground(getResources().getDrawable(R.drawable.border_frame));
                GradientDrawable myGrad5 = (GradientDrawable) binding.fifth.frame5.getBackground();
                myGrad5.setColor(image_background_color);

                binding.sixth.frame6.setBackground(getResources().getDrawable(R.drawable.border_frame));
                GradientDrawable myGrad6 = (GradientDrawable) binding.sixth.frame6.getBackground();
                myGrad6.setColor(image_background_color);

                binding.seventh.frame7.setBackground(getResources().getDrawable(R.drawable.border_frame));
                GradientDrawable myGrad7 = (GradientDrawable) binding.seventh.frame7.getBackground();
                myGrad7.setColor(image_background_color);

                binding.eight.frame8.setBackground(getResources().getDrawable(R.drawable.border_frame));
                GradientDrawable myGrad8 = (GradientDrawable) binding.eight.frame8.getBackground();
                myGrad8.setColor(image_background_color);

                binding.nineth.frame9.setBackground(getResources().getDrawable(R.drawable.border_frame));
                GradientDrawable myGrad9 = (GradientDrawable) binding.nineth.frame9.getBackground();
                myGrad9.setColor(image_background_color);

                binding.tenth.frame10.setBackground(getResources().getDrawable(R.drawable.border_frame));
                GradientDrawable myGrad10 = (GradientDrawable) binding.tenth.frame10.getBackground();
                myGrad10.setColor(image_background_color);

                binding.eleven.frame11.setBackground(getResources().getDrawable(R.drawable.border_frame));
                GradientDrawable myGrad11 = (GradientDrawable) binding.eleven.frame11.getBackground();
                myGrad11.setColor(image_background_color);

                binding.twelve.frame12.setBackground(getResources().getDrawable(R.drawable.border_frame));
                GradientDrawable myGrad12 = (GradientDrawable) binding.twelve.frame12.getBackground();
                myGrad12.setColor(image_background_color);

                binding.thirteen.frame13.setBackground(getResources().getDrawable(R.drawable.border_frame));
                GradientDrawable myGrad13 = (GradientDrawable) binding.thirteen.frame13.getBackground();
                myGrad13.setColor(image_background_color);

                binding.fourteen.frame14.setBackground(getResources().getDrawable(R.drawable.border_frame));
                GradientDrawable myGrad14 = (GradientDrawable) binding.fourteen.frame14.getBackground();
                myGrad14.setColor(image_background_color);

                binding.fifteen.frame15.setBackground(getResources().getDrawable(R.drawable.border_frame));
                GradientDrawable myGrad15 = (GradientDrawable) binding.fifteen.frame15.getBackground();
                myGrad15.setColor(image_background_color);

                binding.sixteenth.frame16.setBackground(getResources().getDrawable(R.drawable.border_frame));
                GradientDrawable myGrad16 = (GradientDrawable) binding.sixteenth.frame16.getBackground();
                myGrad16.setColor(image_background_color);

                binding.seventeenth.frame17.setBackground(getResources().getDrawable(R.drawable.border_frame));
                GradientDrawable myGrad17 = (GradientDrawable) binding.seventeenth.frame17.getBackground();
                myGrad17.setColor(image_background_color);

                binding.eighteen.frame18.setBackground(getResources().getDrawable(R.drawable.border_frame));
                GradientDrawable myGrad18 = (GradientDrawable) binding.eighteen.frame18.getBackground();
                myGrad18.setColor(image_background_color);

                binding.nineteen.frame19.setBackground(getResources().getDrawable(R.drawable.border_frame));
                GradientDrawable myGrad19 = (GradientDrawable) binding.nineteen.frame19.getBackground();
                myGrad19.setColor(image_background_color);

                binding.twenty.frame20.setBackground(getResources().getDrawable(R.drawable.border_frame));
                GradientDrawable myGrad20 = (GradientDrawable) binding.twenty.frame20.getBackground();
                myGrad20.setColor(image_background_color);

                binding.twentyone.frame21.setBackground(getResources().getDrawable(R.drawable.border_frame));
                GradientDrawable myGrad21 = (GradientDrawable) binding.twentyone.frame21.getBackground();
                myGrad21.setColor(image_background_color);

                binding.twentytwo.frame22.setBackground(getResources().getDrawable(R.drawable.border_frame));
                GradientDrawable myGrad22 = (GradientDrawable) binding.twentytwo.frame22.getBackground();
                myGrad22.setColor(image_background_color);

                binding.twentythree.frame23.setBackground(getResources().getDrawable(R.drawable.border_frame));
                GradientDrawable myGrad23 = (GradientDrawable) binding.twentythree.frame23.getBackground();
                myGrad23.setColor(image_background_color);

                binding.twentyfour.frame24.setBackground(getResources().getDrawable(R.drawable.border_frame));
                GradientDrawable myGrad24 = (GradientDrawable) binding.twentyfour.frame24.getBackground();
                myGrad24.setColor(image_background_color);

                binding.twentyfive.frame25.setBackground(getResources().getDrawable(R.drawable.border_frame));
                GradientDrawable myGrad25 = (GradientDrawable) binding.twentyfive.frame25.getBackground();
                myGrad25.setColor(image_background_color);

                binding.twentysix.frame26.setBackground(getResources().getDrawable(R.drawable.border_frame));
                GradientDrawable myGrad26 = (GradientDrawable) binding.twentysix.frame26.getBackground();
                myGrad26.setColor(image_background_color);

                binding.twentyseven.frame27.setBackground(getResources().getDrawable(R.drawable.border_frame));
                GradientDrawable myGrad27 = (GradientDrawable) binding.twentyseven.frame27.getBackground();
                myGrad27.setColor(image_background_color);

                binding.twentyeight.frame28.setBackground(getResources().getDrawable(R.drawable.border_frame));
                GradientDrawable myGrad28 = (GradientDrawable) binding.twentyeight.frame28.getBackground();
                myGrad28.setColor(image_background_color);

            }
            // binding.rel.setBackground(ContextCompat.getDrawable(this, R.drawable.border_frame));
            //   binding.rel2.setBackgroundColor(frame_border_color);
               */
/*     GradientDrawable myGrad = (GradientDrawable) binding.rel.getBackground();
                    myGrad.setStroke(3, frame_border_color);
*//*

     */
/*                    ViewGroup.MarginLayoutParams layoutParams =
                            (ViewGroup.MarginLayoutParams) binding.rel.getLayoutParams();
                    layoutParams.(2, 2, 2, 2);*//*

            if (frame_border_color != 0) {
                Log.e("FRAME", " if ma gayu");
                */
/*GradientDrawable myGrad = (GradientDrawable) binding.frameRelative.getBackground();
                myGrad.setColor(frame_border_color);*//*

                binding.frameRelative.setBackground(ContextCompat.getDrawable(this, R.drawable.top_curved_color_card_bg));
                GradientDrawable myGrad = (GradientDrawable) binding.frameRelative.getBackground();
                myGrad.setStroke(5, frame_border_color);

            }
            //binding.frameRelative.setBackgroundColor(frame_border_color);
            binding.frameRelative.setPadding(5, 5, 5, 5);
        } else {
            Log.e("frame1234", "2");
            frame_flag = true;
            binding.frameRelative.setBackground(ContextCompat.getDrawable(this, R.drawable.top_curved_card_bg));

            if (image_background_flag && image_background_color != -1) {

                binding.bgView.setVisibility(View.GONE);
                binding.first.frame1.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                GradientDrawable myGrad = (GradientDrawable) binding.first.frame1.getBackground();
                myGrad.setColor(image_background_color);

                binding.second.frame2.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                GradientDrawable myGrad2 = (GradientDrawable) binding.second.frame2.getBackground();
                myGrad2.setColor(image_background_color);

                binding.third.frame3.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                GradientDrawable myGrad3 = (GradientDrawable) binding.third.frame3.getBackground();
                myGrad3.setColor(image_background_color);

                binding.forth.frame4.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                GradientDrawable myGrad4 = (GradientDrawable) binding.forth.frame4.getBackground();
                myGrad4.setColor(image_background_color);

                binding.fifth.frame5.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                GradientDrawable myGrad5 = (GradientDrawable) binding.fifth.frame5.getBackground();
                myGrad5.setColor(image_background_color);

                binding.sixth.frame6.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                GradientDrawable myGrad6 = (GradientDrawable) binding.sixth.frame6.getBackground();
                myGrad6.setColor(image_background_color);

                binding.seventh.frame7.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                GradientDrawable myGrad7 = (GradientDrawable) binding.seventh.frame7.getBackground();
                myGrad7.setColor(image_background_color);

                binding.eight.frame8.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                GradientDrawable myGrad8 = (GradientDrawable) binding.eight.frame8.getBackground();
                myGrad8.setColor(image_background_color);

                binding.nineth.frame9.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                GradientDrawable myGrad9 = (GradientDrawable) binding.nineth.frame9.getBackground();
                myGrad9.setColor(image_background_color);

                binding.tenth.frame10.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                GradientDrawable myGrad10 = (GradientDrawable) binding.tenth.frame10.getBackground();
                myGrad10.setColor(image_background_color);

                binding.eleven.frame11.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                GradientDrawable myGrad11 = (GradientDrawable) binding.eleven.frame11.getBackground();
                myGrad11.setColor(image_background_color);

                binding.twelve.frame12.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                GradientDrawable myGrad12 = (GradientDrawable) binding.twelve.frame12.getBackground();
                myGrad12.setColor(image_background_color);

                binding.thirteen.frame13.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                GradientDrawable myGrad13 = (GradientDrawable) binding.thirteen.frame13.getBackground();
                myGrad10.setColor(image_background_color);

                binding.fourteen.frame14.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                GradientDrawable myGrad14 = (GradientDrawable) binding.fourteen.frame14.getBackground();
                myGrad14.setColor(image_background_color);

                binding.fifteen.frame15.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                GradientDrawable myGrad15 = (GradientDrawable) binding.fifteen.frame15.getBackground();
                myGrad15.setColor(image_background_color);

                binding.sixteenth.frame16.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                GradientDrawable myGrad16 = (GradientDrawable) binding.sixteenth.frame16.getBackground();
                myGrad16.setColor(image_background_color);

                binding.seventeenth.frame17.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                GradientDrawable myGrad17 = (GradientDrawable) binding.seventeenth.frame17.getBackground();
                myGrad17.setColor(image_background_color);

                binding.eighteen.frame18.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                GradientDrawable myGrad18 = (GradientDrawable) binding.eighteen.frame18.getBackground();
                myGrad18.setColor(image_background_color);

                binding.nineteen.frame19.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                GradientDrawable myGrad19 = (GradientDrawable) binding.nineteen.frame19.getBackground();
                myGrad19.setColor(image_background_color);

                binding.twenty.frame20.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                GradientDrawable myGrad20 = (GradientDrawable) binding.twenty.frame20.getBackground();
                myGrad20.setColor(image_background_color);

                binding.twentyone.frame21.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                GradientDrawable myGrad21 = (GradientDrawable) binding.twentyone.frame21.getBackground();
                myGrad21.setColor(image_background_color);

                binding.twentytwo.frame22.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                GradientDrawable myGrad22 = (GradientDrawable) binding.twentytwo.frame22.getBackground();
                myGrad22.setColor(image_background_color);

                binding.twentythree.frame23.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                GradientDrawable myGrad23 = (GradientDrawable) binding.twentythree.frame23.getBackground();
                myGrad23.setColor(image_background_color);

                binding.twentyfour.frame24.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                GradientDrawable myGrad24 = (GradientDrawable) binding.twentyfour.frame24.getBackground();
                myGrad24.setColor(image_background_color);

                binding.twentyfive.frame25.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                GradientDrawable myGrad25 = (GradientDrawable) binding.twentyfive.frame25.getBackground();
                myGrad25.setColor(image_background_color);

                binding.twentysix.frame26.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                GradientDrawable myGrad26 = (GradientDrawable) binding.twentysix.frame26.getBackground();
                myGrad26.setColor(image_background_color);

                binding.twentyseven.frame27.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                GradientDrawable myGrad27 = (GradientDrawable) binding.twentyseven.frame27.getBackground();
                myGrad27.setColor(image_background_color);

                binding.twentyeight.frame28.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                GradientDrawable myGrad28 = (GradientDrawable) binding.twentyeight.frame28.getBackground();
                myGrad28.setColor(image_background_color);

            } else {
                binding.bgView.setVisibility(View.VISIBLE);

            }
            binding.frameRelative.setPadding(0, 0, 0, 0);

            binding.imageToolsLinear.setBackground(ContextCompat.getDrawable(this, R.drawable.bottom_curved_card_bg));
        }
    }
*/

    private void changeFontColor() {

        ColorPickerDialogBuilder
                .with(ImageCanvasActivity.this)
                .setTitle("Choose color")
                .initialColor(getResources().getColor(R.color.blue_color))
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {

                    }
                })
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        //binding.rel.setBackgroundColor(selectedColor);
                        binding.movableEditText.setHintTextColor(selectedColor);
                        binding.movableEditText.setTextColor(selectedColor);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }

    private void setBackgroundColor() {


        ColorPickerDialogBuilder
                .with(ImageCanvasActivity.this)
                .setTitle("Choose color")
                .initialColor(getResources().getColor(R.color.blue_color))
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {

                    }
                })
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        //binding.rel.setBackgroundColor(selectedColor);
                        image_background_color = selectedColor;

                        Log.e("image_background_color", "" + image_background_color);
                        image_background_flag = true;
                        //   binding.bgView.setVisibility(View.GONE);
                        if (frame_flag) {

                            binding.first.frame1.setBackground(getResources().getDrawable(R.drawable.border_frame));
                            GradientDrawable myGrad = (GradientDrawable) binding.first.frame1.getBackground();
                            myGrad.setColor(image_background_color);

                            binding.second.frame2.setBackground(getResources().getDrawable(R.drawable.border_frame));
                            GradientDrawable myGrad2 = (GradientDrawable) binding.second.frame2.getBackground();
                            myGrad2.setColor(image_background_color);

                            binding.third.frame3.setBackground(getResources().getDrawable(R.drawable.border_frame));
                            GradientDrawable myGrad3 = (GradientDrawable) binding.third.frame3.getBackground();
                            myGrad3.setColor(image_background_color);

                            binding.forth.frame4.setBackground(getResources().getDrawable(R.drawable.border_frame));
                            GradientDrawable myGrad4 = (GradientDrawable) binding.forth.frame4.getBackground();
                            myGrad4.setColor(image_background_color);

                            binding.fifth.frame5.setBackground(getResources().getDrawable(R.drawable.border_frame));
                            GradientDrawable myGrad5 = (GradientDrawable) binding.fifth.frame5.getBackground();
                            myGrad5.setColor(image_background_color);

                            binding.sixth.frame6.setBackground(getResources().getDrawable(R.drawable.border_frame));
                            GradientDrawable myGrad6 = (GradientDrawable) binding.sixth.frame6.getBackground();
                            myGrad6.setColor(image_background_color);

                            binding.seventh.frame7.setBackground(getResources().getDrawable(R.drawable.border_frame));
                            GradientDrawable myGrad7 = (GradientDrawable) binding.seventh.frame7.getBackground();
                            myGrad7.setColor(image_background_color);

                            binding.eight.frame8.setBackground(getResources().getDrawable(R.drawable.border_frame));
                            GradientDrawable myGrad8 = (GradientDrawable) binding.eight.frame8.getBackground();
                            myGrad8.setColor(image_background_color);

                            binding.nineth.frame9.setBackground(getResources().getDrawable(R.drawable.border_frame));
                            GradientDrawable myGrad9 = (GradientDrawable) binding.nineth.frame9.getBackground();
                            myGrad9.setColor(image_background_color);

                            binding.tenth.frame10.setBackground(getResources().getDrawable(R.drawable.border_frame));
                            GradientDrawable myGrad10 = (GradientDrawable) binding.tenth.frame10.getBackground();
                            myGrad10.setColor(image_background_color);

                            binding.eleven.frame11.setBackground(getResources().getDrawable(R.drawable.border_frame));
                            GradientDrawable myGrad11 = (GradientDrawable) binding.eleven.frame11.getBackground();
                            myGrad11.setColor(image_background_color);

                            binding.twelve.frame12.setBackground(getResources().getDrawable(R.drawable.border_frame));
                            GradientDrawable myGrad12 = (GradientDrawable) binding.twelve.frame12.getBackground();
                            myGrad12.setColor(image_background_color);

                            binding.thirteen.frame13.setBackground(getResources().getDrawable(R.drawable.border_frame));
                            GradientDrawable myGrad13 = (GradientDrawable) binding.thirteen.frame13.getBackground();
                            myGrad13.setColor(image_background_color);

                            binding.fourteen.frame14.setBackground(getResources().getDrawable(R.drawable.border_frame));
                            GradientDrawable myGrad14 = (GradientDrawable) binding.fourteen.frame14.getBackground();
                            myGrad14.setColor(image_background_color);

                            binding.fifteen.frame15.setBackground(getResources().getDrawable(R.drawable.border_frame));
                            GradientDrawable myGrad15 = (GradientDrawable) binding.fifteen.frame15.getBackground();
                            myGrad15.setColor(image_background_color);

                            binding.sixteenth.frame16.setBackground(getResources().getDrawable(R.drawable.border_frame));
                            GradientDrawable myGrad16 = (GradientDrawable) binding.sixteenth.frame16.getBackground();
                            myGrad16.setColor(image_background_color);

                            binding.seventeenth.frame17.setBackground(getResources().getDrawable(R.drawable.border_frame));
                            GradientDrawable myGrad17 = (GradientDrawable) binding.seventeenth.frame17.getBackground();
                            myGrad17.setColor(image_background_color);

                            binding.eighteen.frame18.setBackground(getResources().getDrawable(R.drawable.border_frame));
                            GradientDrawable myGrad18 = (GradientDrawable) binding.eighteen.frame18.getBackground();
                            myGrad18.setColor(image_background_color);

                            binding.nineteen.frame19.setBackground(getResources().getDrawable(R.drawable.border_frame));
                            GradientDrawable myGrad19 = (GradientDrawable) binding.nineteen.frame19.getBackground();
                            myGrad19.setColor(image_background_color);

                            binding.twenty.frame20.setBackground(getResources().getDrawable(R.drawable.border_frame));
                            GradientDrawable myGrad20 = (GradientDrawable) binding.twenty.frame20.getBackground();
                            myGrad20.setColor(image_background_color);

                            binding.twentyone.frame21.setBackground(getResources().getDrawable(R.drawable.border_frame));
                            GradientDrawable myGrad21 = (GradientDrawable) binding.twentyone.frame21.getBackground();
                            myGrad21.setColor(image_background_color);

                            binding.twentytwo.frame22.setBackground(getResources().getDrawable(R.drawable.border_frame));
                            GradientDrawable myGrad22 = (GradientDrawable) binding.twentytwo.frame22.getBackground();
                            myGrad22.setColor(image_background_color);

                            binding.twentythree.frame23.setBackground(getResources().getDrawable(R.drawable.border_frame));
                            GradientDrawable myGrad23 = (GradientDrawable) binding.twentythree.frame23.getBackground();
                            myGrad23.setColor(image_background_color);

                            binding.twentyfour.frame24.setBackground(getResources().getDrawable(R.drawable.border_frame));
                            GradientDrawable myGrad24 = (GradientDrawable) binding.twentyfour.frame24.getBackground();
                            myGrad24.setColor(image_background_color);

                            binding.twentyfive.frame25.setBackground(getResources().getDrawable(R.drawable.border_frame));
                            GradientDrawable myGrad25 = (GradientDrawable) binding.twentyfive.frame25.getBackground();
                            myGrad25.setColor(image_background_color);

                            binding.twentysix.frame26.setBackground(getResources().getDrawable(R.drawable.border_frame));
                            GradientDrawable myGrad26 = (GradientDrawable) binding.twentysix.frame26.getBackground();
                            myGrad26.setColor(image_background_color);

                            binding.twentyseven.frame27.setBackground(getResources().getDrawable(R.drawable.border_frame));
                            GradientDrawable myGrad27 = (GradientDrawable) binding.twentyseven.frame27.getBackground();
                            myGrad27.setColor(image_background_color);

                            binding.twentyeight.frame28.setBackground(getResources().getDrawable(R.drawable.border_frame));
                            GradientDrawable myGrad28 = (GradientDrawable) binding.twentyeight.frame28.getBackground();
                            myGrad28.setColor(image_background_color);


                        } else {

                            binding.first.frame1.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                            GradientDrawable myGrad = (GradientDrawable) binding.first.frame1.getBackground();
                            myGrad.setColor(image_background_color);

                            binding.second.frame2.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                            GradientDrawable myGrad2 = (GradientDrawable) binding.second.frame2.getBackground();
                            myGrad2.setColor(image_background_color);

                            binding.third.frame3.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                            GradientDrawable myGrad3 = (GradientDrawable) binding.third.frame3.getBackground();
                            myGrad3.setColor(image_background_color);

                            binding.forth.frame4.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                            GradientDrawable myGrad4 = (GradientDrawable) binding.forth.frame4.getBackground();
                            myGrad4.setColor(image_background_color);

                            binding.fifth.frame5.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                            GradientDrawable myGrad5 = (GradientDrawable) binding.fifth.frame5.getBackground();
                            myGrad5.setColor(image_background_color);

                            binding.sixth.frame6.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                            GradientDrawable myGrad6 = (GradientDrawable) binding.sixth.frame6.getBackground();
                            myGrad6.setColor(image_background_color);

                            binding.seventh.frame7.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                            GradientDrawable myGrad7 = (GradientDrawable) binding.seventh.frame7.getBackground();
                            myGrad7.setColor(image_background_color);

                            binding.eight.frame8.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                            GradientDrawable myGrad8 = (GradientDrawable) binding.eight.frame8.getBackground();
                            myGrad8.setColor(image_background_color);

                            binding.nineth.frame9.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                            GradientDrawable myGrad9 = (GradientDrawable) binding.nineth.frame9.getBackground();
                            myGrad9.setColor(image_background_color);

                            binding.tenth.frame10.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                            GradientDrawable myGrad10 = (GradientDrawable) binding.tenth.frame10.getBackground();
                            myGrad10.setColor(image_background_color);

                            binding.eleven.frame11.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                            GradientDrawable myGrad11 = (GradientDrawable) binding.eleven.frame11.getBackground();
                            myGrad11.setColor(image_background_color);

                            binding.twelve.frame12.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                            GradientDrawable myGrad12 = (GradientDrawable) binding.twelve.frame12.getBackground();
                            myGrad12.setColor(image_background_color);

                            binding.thirteen.frame13.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                            GradientDrawable myGrad13 = (GradientDrawable) binding.thirteen.frame13.getBackground();
                            myGrad13.setColor(image_background_color);

                            binding.fourteen.frame14.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                            GradientDrawable myGrad14 = (GradientDrawable) binding.fourteen.frame14.getBackground();
                            myGrad14.setColor(image_background_color);

                            binding.fifteen.frame15.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                            GradientDrawable myGrad15 = (GradientDrawable) binding.fifteen.frame15.getBackground();
                            myGrad15.setColor(image_background_color);

                            binding.sixteenth.frame16.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                            GradientDrawable myGrad16 = (GradientDrawable) binding.sixteenth.frame16.getBackground();
                            myGrad16.setColor(image_background_color);

                            binding.seventeenth.frame17.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                            GradientDrawable myGrad17 = (GradientDrawable) binding.seventeenth.frame17.getBackground();
                            myGrad17.setColor(image_background_color);

                            binding.eighteen.frame18.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                            GradientDrawable myGrad18 = (GradientDrawable) binding.eighteen.frame18.getBackground();
                            myGrad18.setColor(image_background_color);

                            binding.nineteen.frame19.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                            GradientDrawable myGrad19 = (GradientDrawable) binding.nineteen.frame19.getBackground();
                            myGrad19.setColor(image_background_color);

                            binding.twenty.frame20.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                            GradientDrawable myGrad20 = (GradientDrawable) binding.twenty.frame20.getBackground();
                            myGrad20.setColor(image_background_color);

                            binding.twentyone.frame21.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                            GradientDrawable myGrad21 = (GradientDrawable) binding.twentyone.frame21.getBackground();
                            myGrad21.setColor(image_background_color);

                            binding.twentytwo.frame22.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                            GradientDrawable myGrad22 = (GradientDrawable) binding.twentytwo.frame22.getBackground();
                            myGrad22.setColor(image_background_color);

                            binding.twentythree.frame23.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                            GradientDrawable myGrad23 = (GradientDrawable) binding.twentythree.frame23.getBackground();
                            myGrad23.setColor(image_background_color);

                            binding.twentyfour.frame24.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                            GradientDrawable myGrad24 = (GradientDrawable) binding.twentyfour.frame24.getBackground();
                            myGrad24.setColor(image_background_color);

                            binding.twentyfive.frame25.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                            GradientDrawable myGrad25 = (GradientDrawable) binding.twentyfive.frame25.getBackground();
                            myGrad25.setColor(image_background_color);

                            binding.twentysix.frame26.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                            GradientDrawable myGrad26 = (GradientDrawable) binding.twentysix.frame26.getBackground();
                            myGrad26.setColor(image_background_color);

                            binding.twentyseven.frame27.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                            GradientDrawable myGrad27 = (GradientDrawable) binding.twentyseven.frame27.getBackground();
                            myGrad27.setColor(image_background_color);

                            binding.twentyeight.frame28.setBackground(getResources().getDrawable(R.drawable.frame_rounded_border));
                            GradientDrawable myGrad28 = (GradientDrawable) binding.twentyeight.frame28.getBackground();
                            myGrad28.setColor(image_background_color);

                        }


                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        cameraUtils.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == 23) {

                Uri imageUri = data.getData();

                if (video_flag) {
                    Log.e("CATEGORY_NAME", " second if ");
                    Bitmap bitmap = BitmapFactory.decodeFile(imageUri.getPath());
                    //  mPhotoEditor.addImage(bitmap);
                    rotateZoomImageView_video.setVisibility(View.VISIBLE);
                    image_loaded_flag = true;
                    rotateZoomImageView_video.setImageURI(imageUri);
                    if (image_loaded_flag) {


                        rotateZoomImageView_video.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                return rotateZoomImageView_video.onTouch(v, event);
                            }
                        });
                        rotateZoomImageView_video.setOnTouchListener(new OnDragTouchListener(rotateZoomImageView_video) {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                return rotateZoomImageView_video.onTouch(v, event);
                            }
                        });
                    }
                } else if (getIntent().hasExtra(Consts.POSITION)) {
                    Log.e("CATEGORY_NAME", " first if ");

                    ImageCanvasAdapter adapter1 = new ImageCanvasAdapter(ImageCanvasActivity.this, stringImageList, true, String.valueOf(imageUri), cat_tracker, category_name);

                    Log.e("CATEGORY_NAME", " 2 " + category_name);
                    //  if (category_name.equalsIgnoreCase("Greetings")) {

                    CustomGridLayoutManager customGridLayoutManager = new CustomGridLayoutManager(this);
                    customGridLayoutManager.setScrollEnabled(false);
                    binding.imageRecyclerview.setLayoutManager(customGridLayoutManager);
                   /* } else {
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                        binding.imageRecyclerview.setLayoutManager(linearLayoutManager);
                        binding.imageRecyclerview.setNestedScrollingEnabled(false);
                    }*/
                    binding.imageRecyclerview.scrollToPosition(Integer.parseInt(position));

                    if (binding.imageRecyclerview.getOnFlingListener() == null) {

                        SnapHelper snapHelper = new LinearSnapHelper();
                        snapHelper.attachToRecyclerView(binding.imageRecyclerview);
                    }
                    binding.imageRecyclerview.setAdapter(adapter1);
                } else if (image_flag) {
                    Log.e("CATEGORY_NAME", " third if ");
                    rotateZoomImageView.setVisibility(View.VISIBLE);
                    image_loaded_flag = true;
                    rotateZoomImageView.setImageURI(imageUri);
                    if (image_loaded_flag) {


                        rotateZoomImageView.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                return rotateZoomImageView.onTouch(v, event);
                            }
                        });
                        rotateZoomImageView.setOnTouchListener(new OnDragTouchListener(rotateZoomImageView) {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                return rotateZoomImageView.onTouch(v, event);
                            }
                        });
                    }
                }
                //img_path = imageUri.getPath();
            }
        }
    }

    private boolean shareImage(String image_flag) {

        params.put(Consts.USER_ID, userDTO.getUser_id());

        new HttpsRequest(Consts.CHECK_APPROVE, params, ImageCanvasActivity.this).stringPost("TAG", Consts.USER_CONTROLLER, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {

                try {
                    String status = response.getString("status");
                    Log.e("responsestr", "" + status);

                    if (status.equalsIgnoreCase("4")) {
                        startActivity(new Intent(ImageCanvasActivity.this, SubscriptionActivity.class).putExtra("from_image", true));
                    } else {

                        if (image_flag.equalsIgnoreCase("1")) {

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1011);
                                } else {

                                    File file = saveBitMap(ImageCanvasActivity.this, binding.frameRelative);    //which view you want to pass that view as parameter
                                    if (file != null) {
                                        Log.i("SHIVAKASHI", "Drawing saved to the gallery!");
                                    } else {
                                        Log.i("SHIVAKASHI", "Oops! Image could not be saved.");
                                    }
                                }
                            }
                        } else if (image_flag.equalsIgnoreCase("2")) {
                            saveImage();
                        } else if (image_flag.equalsIgnoreCase("3")) {
                            saveimgFromBitmap();

                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        return share_flag;

    }

    private void saveBitMapForVideo(Context context, View drawView) {
        File pictureFileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Handcare");
        if (!pictureFileDir.exists()) {
            Log.e("TRACKER12345", "1");
            boolean isDirectoryCreated = pictureFileDir.mkdirs();
            if (!isDirectoryCreated)
                Log.e("TRACKER12345", "2");
            Log.i("Tag", "Can't create directory to save the image");
            // return null;
        }
        String filename = pictureFileDir.getPath() + File.separator + System.currentTimeMillis() + ".jpg";
        File pictureFile = new File(filename);
        Bitmap bitmap = getBitmapFromView(drawView);

        if (video_flag) {
            Log.e("video_img_path", " base64 ");
            //  bitmap.setPixel(1080,1080,0);
            //  Bitmap bm = BitmapFactory.decodeFile(pictureFile);
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bOut);
            String base64Image = Base64.encodeToString(bOut.toByteArray(), Base64.DEFAULT);

            Log.e("video_img_path", " base64 " + base64Image);
            downloadVideoparams.put("img_path", base64Image);
            //imageMap.put("img_path",file);
        }

        mPhotoEditor.addImage(bitmap);

        //return pictureFile;
    }//create bitmap from view and returns it

    private File saveBitMap(Context context, View drawView) {
        File pictureFileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Brand Shaastra");
        if (!pictureFileDir.exists()) {
            Log.e("TRACKER12345", "1");
            boolean isDirectoryCreated = pictureFileDir.mkdirs();
            if (!isDirectoryCreated)
                Log.e("TRACKER12345", "2");
            Log.i("Tag", "Can't create directory to save the image");
            return null;
        }
        String filename = pictureFileDir.getPath() + File.separator + System.currentTimeMillis() + ".jpg";
        File pictureFile = new File(filename);
        Bitmap bitmap = getBitmapFromView(drawView);
        try {
            pictureFile.createNewFile();
            FileOutputStream oStream = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, oStream);
            oStream.flush();
            oStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("TAG", "There was an issue saving the image.");
        }
        scanGallery(context, pictureFile.getAbsolutePath());
        return pictureFile;
    }//create bitmap from view and returns it

    private void scanGallery(Context cntx, String path) {
        try {
            MediaScannerConnection.scanFile(cntx, new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                    Log.e("TRACKER12345", "5");
                    Log.e("TRACKER12345", "" + uri.toString());

                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    sharingIntent.setType("image/jpeg");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share Business-Card");
                    // sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, "Share via"));

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Log.e("TRACKER12345", "3");

        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        } else {
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    private void getBottomDialog(String fontstyle_flag) {

        List<String> fontList = new ArrayList<>();
        fontList.add("0");
        fontList.add("1");
        fontList.add("2");
        fontList.add("3");
        fontList.add("4");
        fontList.add("5");
        fontList.add("6");
        fontList.add("7");
        fontList.add("8");
        fontList.add("9");
        fontList.add("10");
        fontList.add("11");
        fontList.add("12");
        fontList.add("13");
        fontList.add("14");
        fontList.add("15");
        fontStyleAdapter = new FontStyleAdapter(this, fontList, this);
        font_rv.setLayoutManager(new GridLayoutManager(this, 3));
        font_rv.setAdapter(fontStyleAdapter);


        if (fontstyle_flag.equalsIgnoreCase("1")) {

            bottomsheet_title.setText("Select font style");
            bottomsheet_relative.setVisibility(View.GONE);
            bottomsheet_relative3.setVisibility(View.GONE);
            bottomsheet_relative2.setVisibility(View.VISIBLE);
        } else if (fontstyle_flag.equalsIgnoreCase("0")) {
            bottomsheet_title.setText("Select text size");
            bottomsheet_relative.setVisibility(View.VISIBLE);
            bottomsheet_relative2.setVisibility(View.GONE);
            bottomsheet_relative3.setVisibility(View.GONE);
        } else if (fontstyle_flag.equalsIgnoreCase("2")) {
            bottomsheet_title.setText("Change Theme");
            bottomsheet_relative.setVisibility(View.GONE);
            bottomsheet_relative2.setVisibility(View.GONE);
            bottomsheet_relative3.setVisibility(View.VISIBLE);
        }

        bottomsheet_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
        bottom_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                bottomsheet_text_size.setText(String.valueOf(progress));
                binding.movableEditText.setTextSize(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        setting_theme_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialogBuilder
                        .with(ImageCanvasActivity.this)
                        .setTitle("Choose color")
                        .initialColor(getResources().getColor(R.color.blue_color))
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {

                            }
                        })
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                //binding.rel.setBackgroundColor(selectedColor);

                                setting_theme_color.setBackgroundColor(selectedColor);
                                frame_border_color = selectedColor;
                                binding.first.leftFirstFDot.setColorFilter(selectedColor);
                                binding.first.leftFirstFDotFooter.setColorFilter(selectedColor);
                                binding.first.headerFooterLine.setBackgroundColor(selectedColor);
                                binding.first.headerLine.setBackgroundColor(selectedColor);
                                binding.first.rightFirstFDot.setColorFilter(selectedColor);
                                binding.first.rightFirstFDotFooter.setColorFilter(selectedColor);

                                GradientDrawable myGrad = (GradientDrawable) binding.second.footerRelative.getBackground();
                                myGrad.setStroke(1, selectedColor);


                                Drawable unwrappedDrawable = AppCompatResources.getDrawable(getApplicationContext(), R.drawable.ic_third_frame_bg);
                                Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
                                DrawableCompat.setTint(wrappedDrawable, selectedColor);

                                /*VectorDrawable frame3_1 = (VectorDrawable) binding.third.footerCallImage.getBackground();
                                frame3_1.setTint(selectedColor);

                                VectorDrawable frame3_2 = (VectorDrawable) binding.third.footerWebsiteImage.getBackground();
                                frame3_2.setTint(selectedColor);

                                VectorDrawable frame3_3 = (VectorDrawable) binding.third.footerLocationImage.getBackground();
                                frame3_3.setTint(selectedColor);

                                VectorDrawable frame3_4 = (VectorDrawable) binding.third.footerMailImage.getBackground();
                                frame3_4.setTint(selectedColor);
*/
                                changeDrawablecolor(binding.third.footerCallImage.getBackground(), selectedColor);
                                changeDrawablecolor(binding.third.footerWebsiteImage.getBackground(), selectedColor);
                                changeDrawablecolor(binding.third.footerLocationImage.getBackground(), selectedColor);
                                changeDrawablecolor(binding.third.footerMailImage.getBackground(), selectedColor);

                                changeDrawablecolor(binding.forth.footerCallImage.getBackground(), selectedColor);
                                changeDrawablecolor(binding.forth.footerLocationImage.getBackground(), selectedColor);
                                changeDrawablecolor(binding.forth.footerMailImage.getBackground(), selectedColor);

                                /*VectorDrawable frame4 = (VectorDrawable) binding.forth.footerCallImage.getBackground();
                                frame4.setTint(selectedColor);
*/
                              /*  GradientDrawable myGrad123 = (GradientDrawable) binding.frameRelative.getBackground();
                                myGrad123.setColor(frame_border_color);
*/

                                // setDrawableBackground(binding.second.footerRelative.getBackground(),selectedColor);
                   /*             Drawable unwrappedDrawable2 = AppCompatResources.getDrawable(ImageCanvasActivity.this, R.drawable.border_frame);
                                Drawable wrappedDrawable2 = DrawableCompat.wrap(unwrappedDrawable2);
                                DrawableCompat.setTint(wrappedDrawable2, selectedColor);*/

                                binding.third.twitterIcon.setColorFilter(selectedColor);
                                binding.third.facebookIcon.setColorFilter(selectedColor);
                                binding.third.whatsappIcon.setColorFilter(selectedColor);
                                binding.third.instagramIcon.setColorFilter(selectedColor);

                                binding.fifth.twitterIcon.setColorFilter(selectedColor);
                                binding.fifth.facebookIcon.setColorFilter(selectedColor);
                                binding.fifth.whatsappIcon.setColorFilter(selectedColor);
                                binding.fifth.instagramIcon.setColorFilter(selectedColor);

                                binding.sixth.rightCornerBg.setColorFilter(selectedColor);
                                binding.sixth.leftCornerBg.setColorFilter(selectedColor);
                                binding.sixth.footerRelative.setBackgroundColor(selectedColor);

                                binding.seventh.twitterIcon.setColorFilter(selectedColor);
                                binding.seventh.facebookIcon.setColorFilter(selectedColor);
                                binding.seventh.whatsappIcon.setColorFilter(selectedColor);
                                binding.seventh.instagramIcon.setColorFilter(selectedColor);


/*
                                VectorDrawable frame8 = (VectorDrawable) binding.eight.footerCallImage.getBackground();
                                frame8.setTint(selectedColor);



                                binding.eight.footerCallImage.setColorFilter(selectedColor);
                                binding.eight.footerWebsiteImage.setColorFilter(selectedColor);
                                binding.eight.footerMailImage.setColorFilter(selectedColor);
                                binding.eight.footerLocationImage.setColorFilter(selectedColor);
*/
                                binding.nineth.ninenthBottomRelative.setBackgroundColor(selectedColor);

                                binding.tenth.twitterIcon.setColorFilter(selectedColor);
                                binding.tenth.facebookIcon.setColorFilter(selectedColor);
                                binding.tenth.whatsappIcon.setColorFilter(selectedColor);
                                binding.tenth.instagramIcon.setColorFilter(selectedColor);

                                changeDrawablecolor(binding.tenth.locationMainRelative.getBackground(), selectedColor);
                                changeDrawablecolor(binding.tenth.mailMainRelative.getBackground(), selectedColor);
                                changeDrawablecolor(binding.tenth.callMainRelative.getBackground(), selectedColor);
                                changeDrawablecolor(binding.tenth.websiteMainRelative.getBackground(), selectedColor);

                                binding.eleven.twitterIcon.setColorFilter(selectedColor);
                                binding.eleven.facebookIcon.setColorFilter(selectedColor);
                                binding.eleven.whatsappIcon.setColorFilter(selectedColor);
                                binding.eleven.instagramIcon.setColorFilter(selectedColor);

                                binding.twelve.twitterIcon.setColorFilter(selectedColor);
                                binding.twelve.facebookIcon.setColorFilter(selectedColor);
                                binding.twelve.whatsappIcon.setColorFilter(selectedColor);
                                binding.twelve.instagramIcon.setColorFilter(selectedColor);

                                binding.twelve.footerCallImage.setBackgroundColor(selectedColor);
                                binding.twelve.footerCallImage.setAlpha(0.9f);
                                binding.twelve.footerLocationImage.setAlpha(0.9f);
                                binding.twelve.footerWebsiteImage.setBackgroundColor(selectedColor);
                                binding.twelve.footerLocationImage.setBackgroundColor(selectedColor);

                                binding.thirteen.twitterIcon.setColorFilter(selectedColor);
                                binding.thirteen.facebookIcon.setColorFilter(selectedColor);
                                binding.thirteen.whatsappIcon.setColorFilter(selectedColor);
                                binding.thirteen.instagramIcon.setColorFilter(selectedColor);


                                binding.thirteen.footerCallImage.setBackgroundColor(selectedColor);
                                binding.thirteen.footerWebsiteImage.setBackgroundColor(selectedColor);
                                binding.thirteen.footerLocationImage.setBackgroundColor(selectedColor);
                                binding.thirteen.footerMailImage.setBackgroundColor(selectedColor);


                                changeSvgColor(binding.fourteen.callMainRelative, binding.fourteen.callMainRelative.getBackground(), selectedColor);
                                changeSvgColor(binding.fourteen.locationMainRelative, binding.fourteen.locationMainRelative.getBackground(), selectedColor);


                                binding.fifteen.view1.setBackgroundColor(selectedColor);
                                binding.fifteen.view2.setBackgroundColor(selectedColor);

                                Drawable buttonDrawable = binding.fourteen.mailMainRelative.getBackground();
                                buttonDrawable = DrawableCompat.wrap(buttonDrawable);
                                //the color is a direct color int and not a color resource
                                DrawableCompat.setTint(buttonDrawable, selectedColor);
                                binding.fourteen.mailMainRelative.setBackground(buttonDrawable);

                                Drawable buttonDrawable2 = binding.fourteen.websiteMainRelative.getBackground();
                                buttonDrawable2 = DrawableCompat.wrap(buttonDrawable2);
                                //the color is a direct color int and not a color resource
                                DrawableCompat.setTint(buttonDrawable2, selectedColor);
                                binding.fourteen.websiteMainRelative.setBackground(buttonDrawable2);


                              /*  changeSvgColor(binding.fourteen.mailMainRelative,binding.fourteen.mailMainRelative.getBackground(),selectedColor);
                                changeSvgColor(binding.fourteen.websiteMainRelative,binding.fourteen.websiteMainRelative.getBackground(),selectedColor);
*/
                                changeDrawablecolor(binding.fifteen.footerCallImage.getBackground(), selectedColor);
                                changeDrawablecolor(binding.fifteen.footerWebsiteImage.getBackground(), selectedColor);
                                changeDrawablecolor(binding.fifteen.footerMailImage.getBackground(), selectedColor);


                                changeDrawablecolor(binding.twentytwo.footerCallImage.getBackground(), selectedColor);
                                changeDrawablecolor(binding.twentytwo.footerLocationImage.getBackground(), selectedColor);
                                changeDrawablecolor(binding.twentytwo.footerMailImage.getBackground(), selectedColor);

                                binding.seventeenth.footerCallImage.setBackgroundColor(selectedColor);
                                binding.seventeenth.footerWebsiteImage.setBackgroundColor(selectedColor);
                                binding.seventeenth.businessWebsiteDetails.setBackgroundColor(selectedColor);
                                binding.seventeenth.businessCallDetails.setBackgroundColor(selectedColor);

                                binding.eighteen.footerCallImage.setColorFilter(selectedColor);
                                binding.eighteen.footerWebsiteImage.setColorFilter(selectedColor);
                                binding.eighteen.footerMailImage.setColorFilter(selectedColor);

                                binding.eighteen.rightCornerBg.setColorFilter(selectedColor);
                                binding.eighteen.leftCornerBg.setColorFilter(selectedColor);


                                binding.nineteen.footerRelative.setBackgroundColor(selectedColor);

                              /*  Drawable buttonDrawable = binding.twentyone.businessMailDetails.getBackground();
                                buttonDrawable = DrawableCompat.wrap(buttonDrawable);
                                //the color is a direct color int and not a color resource
                                DrawableCompat.setTint(buttonDrawable, Color.RED);
                                binding.twentyone.businessMailDetails.setBackground(buttonDrawable);
*/


                                changeSvgColor(binding.twenty.bottomTwentyFrameLinear, binding.twenty.bottomTwentyFrameLinear.getBackground(), selectedColor);
                                changeSvgColor(binding.twentyone.businessMailDetails, binding.twentyone.businessMailDetails.getBackground(), selectedColor);
                                changeSvgColor(binding.twentyone.businessCallDetails, binding.twentyone.businessCallDetails.getBackground(), selectedColor);

                                changeSvgColor(binding.twentythree.frame23TopLeftImg, binding.twentythree.frame23TopLeftImg.getBackground(), selectedColor);
                                changeSvgColor(binding.twentythree.callMainRelative, binding.twentythree.callMainRelative.getBackground(), selectedColor);
                                changeSvgColor(binding.twentyfour.frame24TopBg, binding.twentyfour.frame24TopBg.getBackground(), selectedColor);
                                changeSvgColor(binding.twentyfour.frame24BottomBg, binding.twentyfour.frame24BottomBg.getBackground(), selectedColor);
                                changeSvgColor(binding.twentyfive.frame25TopBg, binding.twentyfive.frame25TopBg.getBackground(), selectedColor);
                                changeSvgColor(binding.twentyfive.businessCallDetails, binding.twentyfive.businessCallDetails.getBackground(), selectedColor);
                                changeSvgColor(binding.twentyfive.businessMailDetails, binding.twentyfive.businessMailDetails.getBackground(), selectedColor);

                                changeSvgColor(binding.twentysix.bg, binding.twentysix.bg.getBackground(), selectedColor);
                                changeSvgColor(binding.twentysix.bottomTwentysixFrameLinear, binding.twentysix.bottomTwentysixFrameLinear.getBackground(), selectedColor);
                                changeSvgColor(binding.twentyseven.bottomTwentysevenFrameLinear, binding.twentyseven.bottomTwentysevenFrameLinear.getBackground(), selectedColor);
                                // changeSvgColor(binding.twentyseven.frame27TopRight, binding.twentyseven.frame27TopRight.getBackground(), selectedColor);
                                changeSvgColor(binding.twentyeight.frame28TopLeftBg, binding.twentyeight.frame28TopLeftBg.getBackground(), selectedColor);
                                changeSvgColor(binding.twentyeight.bottomTwentyeightFrameLinear, binding.twentyeight.bottomTwentyeightFrameLinear.getBackground(), selectedColor);
                                binding.twentyseven.frame27TopRight.setBackgroundColor(selectedColor);
                                /*   GradientDrawable frame10_1 = (GradientDrawable) binding.tenth.locationMainRelative.getBackground();
                                frame10_1.setColor(selectedColor);

                                GradientDrawable frame10_2 = (GradientDrawable) binding.tenth.mailMainRelative.getBackground();
                                frame10_2.setColor(selectedColor);

                                GradientDrawable frame10_3 = (GradientDrawable) binding.tenth.callMainRelative.getBackground();
                                frame10_3.setColor(selectedColor);

                                GradientDrawable frame10_4 = (GradientDrawable) binding.tenth.websiteMainRelative.getBackground();
                                frame10_4.setColor(selectedColor);

*/
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .build()
                        .show();
            }
        });

        setting_font_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ColorPickerDialogBuilder
                        .with(ImageCanvasActivity.this)
                        .setTitle("Choose color")
                        .initialColor(getResources().getColor(R.color.blue_color))
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {

                            }
                        })
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                //binding.rel.setBackgroundColor(selectedColor);
                                setting_font_color.setBackgroundColor(selectedColor);
                                // binding.movableEditText.setTextColor(selectedColor);
                                binding.first.businessWebsiteDetails.setTextColor(selectedColor);
                                binding.second.businessWebsiteDetails.setTextColor(selectedColor);
                                binding.third.businessWebsiteDetails.setTextColor(selectedColor);
                                binding.sixth.businessWebsiteDetails.setTextColor(selectedColor);
                                binding.nineth.businessWebsiteDetails.setTextColor(selectedColor);
                                binding.tenth.businessWebsiteDetails.setTextColor(selectedColor);
                                binding.eleven.businessWebsiteDetails.setTextColor(selectedColor);
                                binding.twelve.businessWebsiteDetails.setTextColor(selectedColor);
                                binding.thirteen.businessWebsiteDetails.setTextColor(selectedColor);
                                binding.fourteen.businessWebsiteDetails.setTextColor(selectedColor);
                                binding.fifteen.businessWebsiteDetails.setTextColor(selectedColor);
                                binding.sixteenth.businessWebsiteDetails.setTextColor(selectedColor);
                                binding.seventeenth.businessWebsiteDetails.setTextColor(selectedColor);
                                binding.eighteen.businessWebsiteDetails.setTextColor(selectedColor);

                                binding.first.businessMailDetails.setTextColor(selectedColor);
                                binding.second.businessMailDetails.setTextColor(selectedColor);
                                binding.third.businessMailDetails.setTextColor(selectedColor);
                                binding.forth.businessMailDetails.setTextColor(selectedColor);
                                binding.fifth.businessMailDetails.setTextColor(selectedColor);
                                binding.sixth.businessMailDetails.setTextColor(selectedColor);
                                binding.seventh.businessMailDetails.setTextColor(selectedColor);
                                binding.eight.businessMailDetails.setTextColor(selectedColor);
                                binding.tenth.businessMailDetails.setTextColor(selectedColor);
                                binding.eleven.businessMailDetails.setTextColor(selectedColor);
                                binding.twelve.businessMailDetails.setTextColor(selectedColor);
                                binding.thirteen.businessMailDetails.setTextColor(selectedColor);
                                binding.fourteen.businessMailDetails.setTextColor(selectedColor);
                                binding.fifteen.businessMailDetails.setTextColor(selectedColor);
                                binding.eighteen.businessMailDetails.setTextColor(selectedColor);
                                binding.nineteen.businessMailDetails.setTextColor(selectedColor);
                                binding.twenty.businessMailDetails.setTextColor(selectedColor);
                                binding.twentyone.businessMailDetails.setTextColor(selectedColor);


//                                binding.twentyone.businessMailDetails.setBackgroundTintList(getResources().getColorStateList(selectedColor));


                                binding.twentytwo.businessMailDetails.setTextColor(selectedColor);
                                binding.twentythree.businessMailDetails.setTextColor(selectedColor);
                                binding.twentyfour.businessMailDetails.setTextColor(selectedColor);
                                binding.twentyfive.businessMailDetails.setTextColor(selectedColor);
                                binding.twentysix.businessMailDetails.setTextColor(selectedColor);
                                binding.twentyseven.businessMailDetails.setTextColor(selectedColor);
                                binding.twentyeight.businessMailDetails.setTextColor(selectedColor);

                                binding.first.businessCallDetails.setTextColor(selectedColor);
                                binding.second.businessCallDetails.setTextColor(selectedColor);
                                binding.third.businessCallDetails.setTextColor(selectedColor);
                                binding.forth.businessCallDetails.setTextColor(selectedColor);
                                binding.fifth.businessCallDetails.setTextColor(selectedColor);
                                binding.sixth.businessCallDetails.setTextColor(selectedColor);
                                binding.seventh.businessCallDetails.setTextColor(selectedColor);
                                binding.eight.businessCallDetails.setTextColor(selectedColor);
                                binding.nineth.businessCallDetails.setTextColor(selectedColor);
                                binding.tenth.businessCallDetails.setTextColor(selectedColor);
                                binding.eleven.businessCallDetails.setTextColor(selectedColor);
                                binding.twelve.businessCallDetails.setTextColor(selectedColor);
                                binding.thirteen.businessCallDetails.setTextColor(selectedColor);
                                binding.fourteen.businessCallDetails.setTextColor(selectedColor);
                                binding.fifteen.businessCallDetails.setTextColor(selectedColor);
                                binding.sixteenth.businessCallDetails.setTextColor(selectedColor);
                                binding.seventeenth.businessCallDetails.setTextColor(selectedColor);
                                binding.eighteen.businessCallDetails.setTextColor(selectedColor);
                                binding.nineteen.businessCallDetails.setTextColor(selectedColor);
                                binding.twenty.businessCallDetails.setTextColor(selectedColor);
                                binding.twentyone.businessCallDetails.setTextColor(selectedColor);
                                binding.twentytwo.businessCallDetails.setTextColor(selectedColor);
                                binding.twentythree.businessCallDetails.setTextColor(selectedColor);
                                binding.twentyfour.businessCallDetails.setTextColor(selectedColor);
                                binding.twentyfive.businessCallDetails.setTextColor(selectedColor);
                                binding.twentysix.businessCallDetails.setTextColor(selectedColor);
                                binding.twentyseven.businessCallDetails.setTextColor(selectedColor);
                                binding.twentyeight.businessCallDetails.setTextColor(selectedColor);

                                binding.first.businessLocationDetails.setTextColor(selectedColor);
                                binding.second.businessLocationDetails.setTextColor(selectedColor);
                                binding.third.businessLocationDetails.setTextColor(selectedColor);
                                binding.forth.businessLocationDetails.setTextColor(selectedColor);
                                binding.fifth.businessLocationDetails.setTextColor(selectedColor);
                                binding.sixth.businessLocationDetails.setTextColor(selectedColor);
                                binding.seventh.businessLocationDetails.setTextColor(selectedColor);
                                binding.eight.businessLocationDetails.setTextColor(selectedColor);
                                binding.tenth.businessLocationDetails.setTextColor(selectedColor);
                                binding.twelve.businessLocationDetails.setTextColor(selectedColor);
                                binding.thirteen.businessLocationDetails.setTextColor(selectedColor);
                                binding.fourteen.businessLocationDetails.setTextColor(selectedColor);
                                binding.nineteen.businessLocationDetails.setTextColor(selectedColor);
                                binding.twenty.businessLocationDetails.setTextColor(selectedColor);
                                binding.twentytwo.businessLocationDetails.setTextColor(selectedColor);

                                binding.twentythree.businessName.setTextColor(selectedColor);
                                binding.twentyfour.businessName.setTextColor(selectedColor);
                                binding.twentyfive.businessName.setTextColor(selectedColor);
                                binding.twentysix.businessName.setTextColor(selectedColor);
                                binding.twentyseven.businessName.setTextColor(selectedColor);
                                binding.twentyeight.businessName.setTextColor(selectedColor);


                                binding.first.footerCallImage.setColorFilter(selectedColor);
                                binding.second.footerCallImage.setColorFilter(selectedColor);
                                binding.third.footerCallImage.setColorFilter(selectedColor);
                                binding.forth.footerCallImage.setColorFilter(selectedColor);
                                binding.fifth.footerCallImage.setColorFilter(selectedColor);
                                binding.sixth.footerCallImage.setColorFilter(selectedColor);
                                binding.seventh.footerCallImage.setColorFilter(selectedColor);
                                binding.nineth.footerCallImage.setColorFilter(selectedColor);
                                binding.tenth.footerCallImage.setColorFilter(selectedColor);
                                binding.eleven.footerCallImage.setColorFilter(selectedColor);
                                binding.twelve.footerCallImage.setColorFilter(selectedColor);
                                binding.thirteen.footerCallImage.setColorFilter(selectedColor);
                                binding.fourteen.footerCallImage.setColorFilter(selectedColor);
                                binding.fifteen.footerCallImage.setColorFilter(selectedColor);
                                binding.sixteenth.footerCallImage.setColorFilter(selectedColor);
                                binding.seventeenth.footerCallImage.setColorFilter(selectedColor);
                                binding.eighteen.footerCallImage.setColorFilter(selectedColor);
                                binding.nineteen.footerCallImage.setColorFilter(selectedColor);
                                binding.twenty.footerCallImage.setColorFilter(selectedColor);
                                binding.twentytwo.footerCallImage.setColorFilter(selectedColor);
                                binding.twentythree.footerCallImage.setColorFilter(selectedColor);
                                binding.twentyfour.footerCallImage.setColorFilter(selectedColor);
                                binding.twentyfive.footerCallImage.setColorFilter(selectedColor);
                                binding.twentysix.footerCallImage.setColorFilter(selectedColor);
                                binding.twentyseven.footerCallImage.setColorFilter(selectedColor);
                                binding.twentyeight.footerCallImage.setColorFilter(selectedColor);

                                binding.first.footerLocationImage.setColorFilter(selectedColor);
                                binding.second.footerLocationImage.setColorFilter(selectedColor);
                                binding.third.footerLocationImage.setColorFilter(selectedColor);
                                binding.forth.footerLocationImage.setColorFilter(selectedColor);
                                binding.fifth.footerLocationImage.setColorFilter(selectedColor);
                                binding.sixth.footerLocationImage.setColorFilter(selectedColor);
                                binding.seventh.footerLocationImage.setColorFilter(selectedColor);
                                binding.tenth.footerLocationImage.setColorFilter(selectedColor);
                                binding.twelve.footerLocationImage.setColorFilter(selectedColor);
                                binding.thirteen.footerLocationImage.setColorFilter(selectedColor);
                                binding.fourteen.footerLocationImage.setColorFilter(selectedColor);
                                binding.nineteen.footerLocationImage.setColorFilter(selectedColor);
                                binding.twenty.footerLocationImage.setColorFilter(selectedColor);
                                binding.twentytwo.footerLocationImage.setColorFilter(selectedColor);

                                binding.first.footerMailImage.setColorFilter(selectedColor);
                                binding.second.footerMailImage.setColorFilter(selectedColor);
                                binding.third.footerMailImage.setColorFilter(selectedColor);
                                binding.forth.footerMailImage.setColorFilter(selectedColor);
                                binding.fifth.footerMailImage.setColorFilter(selectedColor);
                                binding.sixth.footerMailImage.setColorFilter(selectedColor);
                                binding.seventh.footerMailImage.setColorFilter(selectedColor);
                                binding.tenth.footerMailImage.setColorFilter(selectedColor);
                                binding.eleven.footerMailImage.setColorFilter(selectedColor);
                                binding.thirteen.footerMailImage.setColorFilter(selectedColor);
                                binding.fourteen.footerMailImage.setColorFilter(selectedColor);
                                binding.fifteen.footerMailImage.setColorFilter(selectedColor);
                                binding.eighteen.footerMailImage.setColorFilter(selectedColor);
                                binding.nineteen.footerMailImage.setColorFilter(selectedColor);
                                binding.twenty.footerMailImage.setColorFilter(selectedColor);
                                binding.twentytwo.footerMailImage.setColorFilter(selectedColor);
                                binding.twentythree.footerMailImage.setColorFilter(selectedColor);
                                binding.twentyfour.footerMailImage.setColorFilter(selectedColor);
                                binding.twentyfive.footerMailImage.setColorFilter(selectedColor);
                                binding.twentysix.footerMailImage.setColorFilter(selectedColor);
                                binding.twentyseven.footerMailImage.setColorFilter(selectedColor);
                                binding.twentyeight.footerMailImage.setColorFilter(selectedColor);

                                binding.first.footerWebsiteImage.setColorFilter(selectedColor);
                                binding.second.footerWebsiteImage.setColorFilter(selectedColor);
                                binding.third.footerWebsiteImage.setColorFilter(selectedColor);
                                binding.sixth.footerWebsiteImage.setColorFilter(selectedColor);
                                binding.nineth.footerWebsiteImage.setColorFilter(selectedColor);
                                binding.tenth.footerWebsiteImage.setColorFilter(selectedColor);
                                binding.eleven.footerWebsiteImage.setColorFilter(selectedColor);
                                binding.twelve.footerWebsiteImage.setColorFilter(selectedColor);
                                binding.thirteen.footerWebsiteImage.setColorFilter(selectedColor);
                                binding.fourteen.footerWebsiteImage.setColorFilter(selectedColor);
                                binding.fifteen.footerWebsiteImage.setColorFilter(selectedColor);
                                binding.sixteenth.footerWebsiteImage.setColorFilter(selectedColor);
                                binding.seventeenth.footerWebsiteImage.setColorFilter(selectedColor);
                                binding.eighteen.footerWebsiteImage.setColorFilter(selectedColor);


                                //  changeSvgColor(binding.twentyseven.frameTwentysevenTopBg, binding.twentyseven.frameTwentysevenTopBg.getBackground(), selectedColor);


                                binding.sixteenth.frameSizteenthFollowTxt.setTextColor(selectedColor);
                                binding.seventeenth.frameSeventeenthFollowTxt.setTextColor(selectedColor);
                                binding.nineteen.frameSeventeenthFollowTxt.setTextColor(selectedColor);
                                binding.twenty.frameSeventeenthFollowTxt.setTextColor(selectedColor);
                                binding.twentyone.frameSeventeenthFollowTxt.setTextColor(selectedColor);
                                binding.twentytwo.frameSeventeenthFollowTxt.setTextColor(selectedColor);

                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .build()
                        .show();

            }
        });
        bottomSheetDialog.show();
    }

    public void changeSvgColor(View view, Drawable drawable, int color) {

        Drawable buttonDrawable = drawable;
        buttonDrawable = DrawableCompat.wrap(buttonDrawable);
        //the color is a direct color int and not a color resource
        DrawableCompat.setTint(buttonDrawable, color);
        view.setBackground(buttonDrawable);
    }

    public void changeDrawablecolor(Drawable drawable, int color) {

        GradientDrawable frame3 = (GradientDrawable) drawable;
        frame3.setTint(color);
    }

    @Override
    public void setStyle(int position) {

        if (position == 0) {
            binding.movableEditText.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));

        }
        if (position == 1) {
            binding.movableEditText.setTypeface(Typeface.SANS_SERIF);
        }
        if (position == 2) {
            binding.movableEditText.setTypeface(Typeface.SERIF);
        }
        if (position == 3) {
            binding.movableEditText.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
        }
        if (position == 4) {
            binding.movableEditText.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        }
        if (position == 5) {
            binding.movableEditText.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
        }
        if (position == 6) {
            binding.movableEditText.setTypeface(Typeface.DEFAULT);
        }
        if (position == 7) {
            binding.movableEditText.setTypeface(Typeface.MONOSPACE);
        }
        if (position == 8) {
            binding.movableEditText.setTypeface(FontCache.getTypeface("Lato_Regular.ttf", getApplicationContext()));
        }
        if (position == 9) {
            binding.movableEditText.setTypeface(FontCache.getTypeface("Lato_Black.ttf", getApplicationContext()));
        }
        if (position == 10) {
            binding.movableEditText.setTypeface(FontCache.getTypeface("Lato-Semibold.ttf", getApplicationContext()));
        }
        if (position == 11) {
            binding.movableEditText.setTypeface(FontCache.getTypeface("Lato-Heavy.ttf", getApplicationContext()));
        }
        if (position == 12) {
            binding.movableEditText.setTypeface(FontCache.getTypeface("Lato_BlackItalic.ttf", getApplicationContext()));
        }
        if (position == 13) {
            binding.movableEditText.setTypeface(FontCache.getTypeface("Lato_BoldItalic.ttf", getApplicationContext()));
        }
        if (position == 14) {
            binding.movableEditText.setTypeface(FontCache.getTypeface("Lato_Thin.ttf", getApplicationContext()));
        }
        if (position == 15) {
            binding.movableEditText.setTypeface(FontCache.getTypeface("Lato_ThinItalic.ttf", getApplicationContext()));
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {

            case R.id.setting_logo_switch:
                if (isChecked) {
                    show_logo();
                    Log.e("switch", "called");

                } else {
                    hide_logo();
                }
                break;
            case R.id.setting_socialmedia_switch:
                if (isChecked) {
                    show_socialmedia();
                } else {
                    hide_socialmedia();
                }
                break;
            case R.id.setting_website_switch:
                if (isChecked) {
                    show_website();
                } else {
                    hide_website();
                }
                break;
            case R.id.setting_email_switch:
                if (isChecked) {
                    show_mail();
                } else {
                    hide_mail();
                }
                break;
            case R.id.setting_phone_switch:
                if (isChecked) {
                    show_call();
                } else {
                    hide_call();
                }
                break;
            case R.id.setting_address_switch:
                if (isChecked) {
                    show_location();
                } else {
                    hide_location();
                }
                break;
        }
    }

    private void show_website() {
        binding.first.websiteMainRelative.setVisibility(View.VISIBLE);
        binding.second.websiteMainRelative.setVisibility(View.VISIBLE);
        binding.third.websiteMainRelative.setVisibility(View.VISIBLE);
        binding.sixth.websiteMainRelative.setVisibility(View.VISIBLE);
        binding.nineth.websiteMainRelative.setVisibility(View.VISIBLE);
        binding.tenth.websiteMainRelative.setVisibility(View.VISIBLE);
        binding.eleven.websiteMainRelative.setVisibility(View.VISIBLE);
        binding.thirteen.websiteMainRelative.setVisibility(View.VISIBLE);
        binding.fourteen.websiteMainRelative.setVisibility(View.VISIBLE);
        binding.fifteen.websiteMainRelative.setVisibility(View.VISIBLE);
        binding.sixteenth.websiteMainRelative.setVisibility(View.VISIBLE);
        binding.seventeenth.websiteMainRelative.setVisibility(View.VISIBLE);
        binding.eighteen.websiteMainRelative.setVisibility(View.VISIBLE);

    }

    private void hide_website() {
        binding.first.websiteMainRelative.setVisibility(View.GONE);
        binding.second.websiteMainRelative.setVisibility(View.GONE);
        binding.third.websiteMainRelative.setVisibility(View.INVISIBLE);
        binding.sixth.websiteMainRelative.setVisibility(View.GONE);
        binding.nineth.websiteMainRelative.setVisibility(View.INVISIBLE);
        binding.tenth.websiteMainRelative.setVisibility(View.GONE);
        binding.eleven.websiteMainRelative.setVisibility(View.INVISIBLE);
        binding.thirteen.websiteMainRelative.setVisibility(View.GONE);
        binding.fourteen.websiteMainRelative.setVisibility(View.INVISIBLE);
        binding.fifteen.websiteMainRelative.setVisibility(View.INVISIBLE);
        binding.sixteenth.websiteMainRelative.setVisibility(View.INVISIBLE);
        binding.seventeenth.websiteMainRelative.setVisibility(View.INVISIBLE);
        binding.eighteen.websiteMainRelative.setVisibility(View.INVISIBLE);

    }


    private void show_call() {
        binding.first.callMainRelative.setVisibility(View.VISIBLE);
        binding.second.callMainRelative.setVisibility(View.VISIBLE);
        binding.third.callMainRelative.setVisibility(View.VISIBLE);
        binding.forth.callMainRelative.setVisibility(View.VISIBLE);
        binding.fifth.callMainRelative.setVisibility(View.VISIBLE);
        binding.sixth.callMainRelative.setVisibility(View.VISIBLE);
        binding.seventh.callMainRelative.setVisibility(View.VISIBLE);
        binding.nineth.callMainRelative.setVisibility(View.VISIBLE);
        binding.tenth.callMainRelative.setVisibility(View.VISIBLE);
        binding.eleven.callMainRelative.setVisibility(View.VISIBLE);
        binding.twelve.callMainRelative.setVisibility(View.VISIBLE);
        binding.thirteen.callMainRelative.setVisibility(View.VISIBLE);
        binding.fourteen.callMainRelative.setVisibility(View.VISIBLE);
        binding.fifteen.callMainRelative.setVisibility(View.VISIBLE);
        binding.sixteenth.callMainRelative.setVisibility(View.VISIBLE);
        binding.seventeenth.callMainRelative.setVisibility(View.VISIBLE);
        binding.eighteen.callMainRelative.setVisibility(View.VISIBLE);
        binding.nineteen.callMainRelative.setVisibility(View.VISIBLE);
        binding.twenty.callMainRelative.setVisibility(View.VISIBLE);
        binding.twentyone.callMainRelative.setVisibility(View.VISIBLE);
        binding.twentytwo.callMainRelative.setVisibility(View.VISIBLE);
        binding.twentythree.callMainRelative.setVisibility(View.VISIBLE);
        binding.twentyfour.callMainRelative.setVisibility(View.VISIBLE);
        binding.twentyfive.callMainRelative.setVisibility(View.VISIBLE);
        binding.twentysix.callMainRelative.setVisibility(View.VISIBLE);
        binding.twentyseven.callMainRelative.setVisibility(View.VISIBLE);
        binding.twentyeight.callMainRelative.setVisibility(View.VISIBLE);
    }

    private void hide_call() {
        binding.first.callMainRelative.setVisibility(View.GONE);
        binding.second.callMainRelative.setVisibility(View.GONE);
        binding.third.callMainRelative.setVisibility(View.INVISIBLE);
        binding.forth.callMainRelative.setVisibility(View.INVISIBLE);
        binding.fifth.callMainRelative.setVisibility(View.INVISIBLE);
        binding.sixth.callMainRelative.setVisibility(View.GONE);
        binding.seventh.callMainRelative.setVisibility(View.INVISIBLE);
        binding.nineth.callMainRelative.setVisibility(View.INVISIBLE);
        binding.tenth.callMainRelative.setVisibility(View.GONE);
        binding.eleven.callMainRelative.setVisibility(View.INVISIBLE);
        binding.twelve.callMainRelative.setVisibility(View.INVISIBLE);
        binding.thirteen.callMainRelative.setVisibility(View.GONE);
        binding.fourteen.callMainRelative.setVisibility(View.INVISIBLE);
        binding.fifteen.callMainRelative.setVisibility(View.INVISIBLE);
        binding.sixteenth.callMainRelative.setVisibility(View.INVISIBLE);
        binding.seventeenth.callMainRelative.setVisibility(View.INVISIBLE);
        binding.eighteen.callMainRelative.setVisibility(View.INVISIBLE);
        binding.nineteen.callMainRelative.setVisibility(View.INVISIBLE);
        binding.twenty.callMainRelative.setVisibility(View.INVISIBLE);
        binding.twentyone.callMainRelative.setVisibility(View.INVISIBLE);
        binding.twentytwo.callMainRelative.setVisibility(View.INVISIBLE);
        binding.twentythree.callMainRelative.setVisibility(View.INVISIBLE);
        binding.twentyfour.callMainRelative.setVisibility(View.INVISIBLE);
        binding.twentyfive.callMainRelative.setVisibility(View.INVISIBLE);
        binding.twentysix.callMainRelative.setVisibility(View.INVISIBLE);
        binding.twentyseven.callMainRelative.setVisibility(View.INVISIBLE);
        binding.twentyeight.callMainRelative.setVisibility(View.INVISIBLE);
    }


    private void show_location() {
        binding.first.locationMainRelative.setVisibility(View.VISIBLE);
        binding.second.locationMainRelative.setVisibility(View.VISIBLE);
        binding.third.locationMainRelative.setVisibility(View.VISIBLE);
        binding.forth.locationMainRelative.setVisibility(View.VISIBLE);
        binding.fifth.locationMainRelative.setVisibility(View.VISIBLE);
        binding.sixth.locationMainRelative.setVisibility(View.VISIBLE);
        binding.seventh.locationMainRelative.setVisibility(View.VISIBLE);
        binding.tenth.locationMainRelative.setVisibility(View.VISIBLE);
        binding.twelve.locationMainRelative.setVisibility(View.VISIBLE);
        binding.thirteen.locationMainRelative.setVisibility(View.VISIBLE);
        binding.fourteen.locationMainRelative.setVisibility(View.VISIBLE);
        binding.nineteen.locationMainRelative.setVisibility(View.VISIBLE);
        binding.twenty.locationMainRelative.setVisibility(View.VISIBLE);
        binding.twentytwo.locationMainRelative.setVisibility(View.VISIBLE);
    }

    private void hide_location() {
        binding.first.locationMainRelative.setVisibility(View.GONE);
        binding.second.locationMainRelative.setVisibility(View.GONE);
        binding.third.locationMainRelative.setVisibility(View.INVISIBLE);
        binding.forth.locationMainRelative.setVisibility(View.INVISIBLE);
        binding.fifth.locationMainRelative.setVisibility(View.INVISIBLE);
        binding.sixth.locationMainRelative.setVisibility(View.GONE);
        binding.seventh.locationMainRelative.setVisibility(View.INVISIBLE);
        binding.tenth.locationMainRelative.setVisibility(View.GONE);
        binding.twelve.locationMainRelative.setVisibility(View.INVISIBLE);
        binding.thirteen.locationMainRelative.setVisibility(View.GONE);
        binding.fourteen.locationMainRelative.setVisibility(View.INVISIBLE);
        binding.nineteen.locationMainRelative.setVisibility(View.INVISIBLE);
        binding.twenty.locationMainRelative.setVisibility(View.INVISIBLE);
        binding.twentytwo.locationMainRelative.setVisibility(View.INVISIBLE);
    }


    private void show_mail() {
        binding.first.mailMainRelative.setVisibility(View.VISIBLE);
        binding.second.mailMainRelative.setVisibility(View.VISIBLE);
        binding.third.mailMainRelative.setVisibility(View.VISIBLE);
        binding.forth.mailMainRelative.setVisibility(View.VISIBLE);
        binding.fifth.mailMainRelative.setVisibility(View.VISIBLE);
        binding.sixth.mailMainRelative.setVisibility(View.VISIBLE);
        binding.seventh.mailMainRelative.setVisibility(View.VISIBLE);
        binding.tenth.mailMainRelative.setVisibility(View.VISIBLE);
        binding.eleven.mailMainRelative.setVisibility(View.VISIBLE);
        binding.twelve.mailMainRelative.setVisibility(View.VISIBLE);
        binding.thirteen.mailMainRelative.setVisibility(View.VISIBLE);
        binding.fourteen.mailMainRelative.setVisibility(View.VISIBLE);
        binding.fifteen.mailMainRelative.setVisibility(View.VISIBLE);
        binding.eighteen.mailMainRelative.setVisibility(View.VISIBLE);
        binding.nineteen.mailMainRelative.setVisibility(View.VISIBLE);
        binding.twenty.mailMainRelative.setVisibility(View.VISIBLE);
        binding.twentyone.mailMainRelative.setVisibility(View.VISIBLE);
        binding.twentytwo.mailMainRelative.setVisibility(View.VISIBLE);
        binding.twentythree.mailMainRelative.setVisibility(View.VISIBLE);
        binding.twentyfour.mailMainRelative.setVisibility(View.VISIBLE);
        binding.twentyfive.mailMainRelative.setVisibility(View.VISIBLE);
        binding.twentysix.mailMainRelative.setVisibility(View.VISIBLE);
        binding.twentyseven.mailMainRelative.setVisibility(View.VISIBLE);
        binding.twentyeight.mailMainRelative.setVisibility(View.VISIBLE);
    }

    private void hide_mail() {
        binding.first.mailMainRelative.setVisibility(View.GONE);
        binding.second.mailMainRelative.setVisibility(View.GONE);
        binding.third.mailMainRelative.setVisibility(View.INVISIBLE);
        binding.forth.mailMainRelative.setVisibility(View.INVISIBLE);
        binding.fifth.mailMainRelative.setVisibility(View.INVISIBLE);
        binding.sixth.mailMainRelative.setVisibility(View.GONE);
        binding.seventh.mailMainRelative.setVisibility(View.INVISIBLE);
        binding.tenth.mailMainRelative.setVisibility(View.GONE);
        binding.eleven.mailMainRelative.setVisibility(View.INVISIBLE);
        binding.twelve.mailMainRelative.setVisibility(View.INVISIBLE);
        binding.thirteen.mailMainRelative.setVisibility(View.GONE);
        binding.fourteen.mailMainRelative.setVisibility(View.INVISIBLE);
        binding.fifteen.mailMainRelative.setVisibility(View.INVISIBLE);
        binding.eighteen.mailMainRelative.setVisibility(View.INVISIBLE);
        binding.nineteen.mailMainRelative.setVisibility(View.INVISIBLE);
        binding.twenty.mailMainRelative.setVisibility(View.INVISIBLE);
        binding.twentyone.mailMainRelative.setVisibility(View.INVISIBLE);
        binding.twentytwo.mailMainRelative.setVisibility(View.INVISIBLE);
        binding.twentythree.mailMainRelative.setVisibility(View.INVISIBLE);
        binding.twentyfour.mailMainRelative.setVisibility(View.INVISIBLE);
        binding.twentyfive.mailMainRelative.setVisibility(View.INVISIBLE);
        binding.twentysix.mailMainRelative.setVisibility(View.INVISIBLE);
        binding.twentyseven.mailMainRelative.setVisibility(View.INVISIBLE);
        binding.twentyeight.mailMainRelative.setVisibility(View.INVISIBLE);
    }


    private void show_socialmedia() {
        binding.first.businessSocialmediaImageRelative.setVisibility(View.VISIBLE);
        binding.second.businessSocialmediaImageRelative.setVisibility(View.VISIBLE);
        binding.third.businessSocialmediaImageRelative.setVisibility(View.VISIBLE);
        binding.fifth.businessSocialmediaImageRelative.setVisibility(View.VISIBLE);
        binding.sixth.businessSocialmediaImageRelative.setVisibility(View.VISIBLE);
        binding.seventh.businessSocialmediaImageRelative.setVisibility(View.VISIBLE);
        binding.tenth.businessSocialmediaImageRelative.setVisibility(View.VISIBLE);
        binding.eleven.businessSocialmediaImageRelative.setVisibility(View.VISIBLE);
        binding.twelve.businessSocialmediaImageRelative.setVisibility(View.VISIBLE);
        binding.thirteen.businessSocialmediaImageRelative.setVisibility(View.VISIBLE);
        binding.fourteen.businessSocialmediaImageRelative.setVisibility(View.VISIBLE);
        binding.fifteen.businessSocialmediaImageRelative.setVisibility(View.VISIBLE);
        binding.sixteenth.businessSocialmediaImageRelative.setVisibility(View.VISIBLE);
        binding.seventeenth.businessSocialmediaImageRelative.setVisibility(View.VISIBLE);
        binding.eighteen.businessSocialmediaImageRelative.setVisibility(View.VISIBLE);
        binding.nineteen.businessSocialmediaImageRelative.setVisibility(View.VISIBLE);
        binding.twenty.businessSocialmediaImageRelative.setVisibility(View.VISIBLE);
        binding.twentyone.businessSocialmediaImageRelative.setVisibility(View.VISIBLE);
        binding.twentytwo.businessSocialmediaImageRelative.setVisibility(View.VISIBLE);
        binding.twentythree.businessSocialmediaImageRelative.setVisibility(View.VISIBLE);
        binding.twentyfour.businessSocialmediaImageRelative.setVisibility(View.VISIBLE);
        binding.twentyfive.businessSocialmediaImageRelative.setVisibility(View.VISIBLE);
        binding.twentysix.businessSocialmediaImageRelative.setVisibility(View.VISIBLE);
        binding.twentyseven.businessSocialmediaImageRelative.setVisibility(View.VISIBLE);
        binding.twentyeight.businessSocialmediaImageRelative.setVisibility(View.VISIBLE);
    }

    private void hide_socialmedia() {
        binding.first.businessSocialmediaImageRelative.setVisibility(View.INVISIBLE);
        binding.second.businessSocialmediaImageRelative.setVisibility(View.INVISIBLE);
        binding.third.businessSocialmediaImageRelative.setVisibility(View.INVISIBLE);
        binding.fifth.businessSocialmediaImageRelative.setVisibility(View.INVISIBLE);
        binding.sixth.businessSocialmediaImageRelative.setVisibility(View.INVISIBLE);
        binding.seventh.businessSocialmediaImageRelative.setVisibility(View.INVISIBLE);
        binding.tenth.businessSocialmediaImageRelative.setVisibility(View.INVISIBLE);
        binding.eleven.businessSocialmediaImageRelative.setVisibility(View.INVISIBLE);
        binding.twelve.businessSocialmediaImageRelative.setVisibility(View.INVISIBLE);
        binding.thirteen.businessSocialmediaImageRelative.setVisibility(View.INVISIBLE);
        binding.fourteen.businessSocialmediaImageRelative.setVisibility(View.INVISIBLE);
        binding.fifteen.businessSocialmediaImageRelative.setVisibility(View.INVISIBLE);
        binding.sixteenth.businessSocialmediaImageRelative.setVisibility(View.INVISIBLE);
        binding.seventeenth.businessSocialmediaImageRelative.setVisibility(View.INVISIBLE);
        binding.eighteen.businessSocialmediaImageRelative.setVisibility(View.INVISIBLE);
        binding.nineteen.businessSocialmediaImageRelative.setVisibility(View.INVISIBLE);
        binding.twenty.businessSocialmediaImageRelative.setVisibility(View.INVISIBLE);
        binding.twentyone.businessSocialmediaImageRelative.setVisibility(View.INVISIBLE);
        binding.twentytwo.businessSocialmediaImageRelative.setVisibility(View.INVISIBLE);
        binding.twentythree.businessSocialmediaImageRelative.setVisibility(View.INVISIBLE);
        binding.twentyfour.businessSocialmediaImageRelative.setVisibility(View.INVISIBLE);
        binding.twentyfive.businessSocialmediaImageRelative.setVisibility(View.INVISIBLE);
        binding.twentysix.businessSocialmediaImageRelative.setVisibility(View.INVISIBLE);
        binding.twentyseven.businessSocialmediaImageRelative.setVisibility(View.INVISIBLE);
        binding.twentyeight.businessSocialmediaImageRelative.setVisibility(View.INVISIBLE);
    }

    private void show_logo() {
        binding.first.businessLogoRelative.setVisibility(View.VISIBLE);
        binding.second.businessLogoRelative.setVisibility(View.VISIBLE);
        binding.third.businessLogoRelative.setVisibility(View.VISIBLE);
        binding.forth.businessLogoRelative.setVisibility(View.VISIBLE);
        binding.fifth.businessLogoRelative.setVisibility(View.VISIBLE);
        binding.sixth.businessLogoRelative.setVisibility(View.VISIBLE);
        binding.seventh.businessLogoRelative.setVisibility(View.VISIBLE);
        binding.nineth.businessLogoRelative.setVisibility(View.VISIBLE);
        binding.tenth.businessLogoRelative.setVisibility(View.VISIBLE);
        binding.eleven.businessLogoRelative.setVisibility(View.VISIBLE);
        binding.twelve.businessLogoRelative.setVisibility(View.VISIBLE);
        binding.thirteen.businessLogoRelative.setVisibility(View.VISIBLE);
        binding.fourteen.businessLogoRelative.setVisibility(View.VISIBLE);
        binding.fifteen.businessLogoRelative.setVisibility(View.VISIBLE);
        binding.sixteenth.businessLogoRelative.setVisibility(View.VISIBLE);
        binding.seventeenth.businessLogoRelative.setVisibility(View.VISIBLE);
        binding.eighteen.businessLogoRelative.setVisibility(View.VISIBLE);
        binding.nineteen.businessLogoRelative.setVisibility(View.VISIBLE);
        binding.twenty.businessLogoRelative.setVisibility(View.VISIBLE);
        binding.twentyone.businessLogoRelative.setVisibility(View.VISIBLE);
        binding.twentytwo.businessLogoRelative.setVisibility(View.VISIBLE);
        binding.twentythree.businessLogoRelative.setVisibility(View.VISIBLE);
        binding.twentyfour.businessLogoRelative.setVisibility(View.VISIBLE);
        binding.twentyfive.businessLogoRelative.setVisibility(View.VISIBLE);
        binding.twentysix.businessLogoRelative.setVisibility(View.VISIBLE);
        binding.twentyseven.businessLogoRelative.setVisibility(View.VISIBLE);
        binding.twentyeight.businessLogoRelative.setVisibility(View.VISIBLE);
    }

    private void hide_logo() {
        binding.first.businessLogoRelative.setVisibility(View.INVISIBLE);
        binding.second.businessLogoRelative.setVisibility(View.INVISIBLE);
        binding.third.businessLogoRelative.setVisibility(View.INVISIBLE);
        binding.forth.businessLogoRelative.setVisibility(View.INVISIBLE);
        binding.fifth.businessLogoRelative.setVisibility(View.INVISIBLE);
        binding.sixth.businessLogoRelative.setVisibility(View.INVISIBLE);
        binding.seventh.businessLogoRelative.setVisibility(View.INVISIBLE);
        binding.nineth.businessLogoRelative.setVisibility(View.INVISIBLE);
        binding.tenth.businessLogoRelative.setVisibility(View.INVISIBLE);
        binding.eleven.businessLogoRelative.setVisibility(View.INVISIBLE);
        binding.twelve.businessLogoRelative.setVisibility(View.INVISIBLE);
        binding.thirteen.businessLogoRelative.setVisibility(View.INVISIBLE);
        binding.fourteen.businessLogoRelative.setVisibility(View.INVISIBLE);
        binding.fifteen.businessLogoRelative.setVisibility(View.INVISIBLE);
        binding.sixteenth.businessLogoRelative.setVisibility(View.INVISIBLE);
        binding.seventeenth.businessLogoRelative.setVisibility(View.INVISIBLE);
        binding.eighteen.businessLogoRelative.setVisibility(View.INVISIBLE);
        binding.nineteen.businessLogoRelative.setVisibility(View.INVISIBLE);
        binding.twenty.businessLogoRelative.setVisibility(View.INVISIBLE);
        binding.twentyone.businessLogoRelative.setVisibility(View.INVISIBLE);
        binding.twentytwo.businessLogoRelative.setVisibility(View.INVISIBLE);
        binding.twentythree.businessLogoRelative.setVisibility(View.INVISIBLE);
        binding.twentyfour.businessLogoRelative.setVisibility(View.INVISIBLE);
        binding.twentyfive.businessLogoRelative.setVisibility(View.INVISIBLE);
        binding.twentysix.businessLogoRelative.setVisibility(View.INVISIBLE);
        binding.twentyseven.businessLogoRelative.setVisibility(View.INVISIBLE);
        binding.twentyeight.businessLogoRelative.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onEditTextChangeListener(View rootView, String text, int colorCode, int pos) {
        TextEditorDialogFragment textEditorDialogFragment =
                TextEditorDialogFragment.show(this, text, colorCode, pos);
        textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
            @Override
            public void onDone(String inputText, int colorCode, int position) {
                final TextStyleBuilder styleBuilder = new TextStyleBuilder();
                styleBuilder.withTextColor(colorCode);
                Typeface typeface = ResourcesCompat.getFont(ImageCanvasActivity.this, TextEditorDialogFragment.getDefaultFontIds(ImageCanvasActivity.this).get(position));
                styleBuilder.withTextFont(typeface);
                mPhotoEditor.editText(rootView, inputText, styleBuilder, position);
            }
        });
    }

    @Override
    public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {
        Log.d("TAG", "onAddViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + numberOfAddedViews + "]");

    }

    @Override
    public void onRemoveViewListener(ViewType viewType, int numberOfAddedViews) {
        Log.d("TAG", "onRemoveViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + numberOfAddedViews + "]");

    }

    @Override
    public void onStartViewChangeListener(ViewType viewType) {
        Log.d("TAG", "onStartViewChangeListener() called with: viewType = [" + viewType + "]");

    }

    @Override
    public void onStopViewChangeListener(ViewType viewType) {
        Log.d("TAG", "onStopViewChangeListener() called with: viewType = [" + viewType + "]");

    }

    @Override
    public void onStickerClick(Bitmap bitmap) {
        //binding.imgDraw.setBackgroundColor(ContextCompat.getColor(this, R.color.black_trasp));
        mPhotoEditor.addImage(bitmap);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}