package com.brandshaastra.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
//import com.easystudio.rotateimageview.OnDragTouchListener;
//import com.easystudio.rotateimageview.RotateZoomImageView;
import com.brandshaastra.R;
import com.brandshaastra.ui.activity.ImageCanvasActivity;
import com.easystudio.rotateimageview.OnDragTouchListener;
import com.easystudio.rotateimageview.RotateZoomImageView;

import java.util.List;

public class ImageCanvasAdapter extends RecyclerView.Adapter<ImageCanvasAdapter.ImageViewHolder> {

    Context context;
    List<String> arraylist;
    String path = "";
    String category_name = "";
    String cat_tracker = "";
    boolean showImage = false;

    public ImageCanvasAdapter(Context context, List<String> arraylist) {
        this.context = context;
        Log.e("ARRAY", " context " + context);
        this.arraylist = arraylist;
    }

    public ImageCanvasAdapter(Context context, List<String> arraylist, boolean showImage, String pathOfImage,String cat_tracker, String category_name) {
        this.context = context;
        this.category_name = category_name;
        this.cat_tracker = cat_tracker;
        this.showImage = showImage;
        Log.e("ARRAY", " context " + context);
        path = pathOfImage;
        this.arraylist = arraylist;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.image_canvas_recycler_layout, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {

        Log.e("ARRAY", "" + arraylist.get(position));

        Glide.with(context).load(arraylist.get(position)).into(holder.image_canvas_recycle_img);

        if (!path.isEmpty() || path.equalsIgnoreCase("")) {

            if (cat_tracker.equalsIgnoreCase("2")) {

                if (path != null) {

                    Log.e("tracker123","4");
                    holder.recycler_rotate_img.setVisibility(View.VISIBLE);
                    ((ImageCanvasActivity) context).rotateZoomImageView.setVisibility(View.GONE);
                    Glide.with(context).load(path).into(holder.recycler_rotate_img);

                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(250, 250);
//                    lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                    holder.recycler_rotate_img.setLayoutParams(lp);
                    holder.recycler_rotate_img2.setLayoutParams(lp);
                    if (showImage) {

                        Log.e("tracker123","3");
                        holder.recycler_rotate_img.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                return holder.recycler_rotate_img.onTouch(v, event);
                            }
                        });
                        holder.recycler_rotate_img.setOnTouchListener(new OnDragTouchListener(holder.recycler_rotate_img) {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                return holder.recycler_rotate_img.onTouch(v, event);
                            }
                        });
                    }
                }
            } else {
                Log.e("tracker123","2");
                    holder.recycler_rotate_img2.setVisibility(View.VISIBLE);
                    ((ImageCanvasActivity) context).rotateZoomImageView.setVisibility(View.GONE);
                    Glide.with(context).load(path).into(holder.recycler_rotate_img2);

                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(250, 250);
//                    lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                    holder.recycler_rotate_img2.setLayoutParams(lp);
                if (showImage) {

                    Log.e("tracker123","1");
                    holder.recycler_rotate_img2.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return holder.recycler_rotate_img2.onTouch(v, event);
                        }
                    });
                    holder.recycler_rotate_img2.setOnTouchListener(new OnDragTouchListener(holder.recycler_rotate_img2) {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return holder.recycler_rotate_img2.onTouch(v, event);
                        }
                    });
                }
            }

        }
    }

    @Override
    public int getItemCount() {
        return arraylist.size();
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView image_canvas_recycle_img;
        RotateZoomImageView recycler_rotate_img, recycler_rotate_img2;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            image_canvas_recycle_img = itemView.findViewById(R.id.image_canvas_recycle_img);
            recycler_rotate_img = itemView.findViewById(R.id.recycler_rotate_img);
            recycler_rotate_img2 = itemView.findViewById(R.id.recycler_rotate_img2);
        }
    }

}
