package com.brandshaastra.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.brandshaastra.DTO.SubCategoryDTO;
import com.brandshaastra.R;
import com.brandshaastra.interfaces.Consts;
import com.brandshaastra.ui.activity.ProductActivity;
import com.brandshaastra.utils.CustomTextViewBold;

import java.util.List;

public class SubCatAdapter extends RecyclerView.Adapter<SubCatAdapter.SubViewHolder> {

    Context context;
    List<SubCategoryDTO> arrayList;
    String cat_name = "";
    String cat_tracker = "";
    boolean media_type = false;
    public SubCatAdapter(Context context, List<SubCategoryDTO> arrayList,String cat_tracker,String cat_name,boolean media_type) {
        this.context = context;
        this.arrayList = arrayList;
        this.cat_name = cat_name;
        this.cat_tracker = cat_tracker;
        this.media_type = media_type;
        Log.e("CATEGORY_NAME_tracker"," subcat adapter "+cat_name+" tracker ;-- "+cat_tracker);
        Log.e("MEDIA_TYPE"," subcat adapter "+media_type);
    }

    @NonNull
    @Override
    public SubViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.subcat_recycler_layout,parent,false);
        return new SubViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubViewHolder holder, int position) {

        holder.name.setText(arrayList.get(position).getSubcatgory_name());
        Glide.with(context).load(arrayList.get(position).getImage()).into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ProductActivity.class)
                        .putExtra(Consts.CATEGORY_NAME,cat_name)
                        .putExtra(Consts.TRACKER,cat_tracker)
                        .putExtra(Consts.SUBCAT_ID,arrayList.get(position).getSubcatgory_id())
                        .putExtra(Consts.SUBCAT_NAME,arrayList.get(position).getSubcatgory_name())
                        .putExtra(Consts.MEDIA_TYPE_VIDEO,media_type)
                        );
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class SubViewHolder extends RecyclerView.ViewHolder {

        CustomTextViewBold name;
        ImageView imageView;

        public SubViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.subcat_name);
            imageView = itemView.findViewById(R.id.subcat_image);
        }
    }
}

/*public class SubCatAdapter extends BaseAdapter {

    Context context;
    List<CategoryDTO> arrayList;

    public SubCatAdapter(Context context, List<CategoryDTO> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        Log.e("SUBCAT",""+arrayList.toString());
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.subcat_recycler_layout,null);
        CustomTextViewBold name;
        ImageView imageView;
        name = view.findViewById(R.id.subcat_name);
        imageView = view.findViewById(R.id.subcat_image);


        name.setText(arrayList.get(position).getCat_name());
        Glide.with(context).load(arrayList.get(position).getImgurl()).into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ProductActivity.class));
            }
        });
        return view;
    }
}*/