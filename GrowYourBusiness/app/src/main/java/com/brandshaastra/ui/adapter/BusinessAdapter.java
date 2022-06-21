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
import com.brandshaastra.DTO.BusinessDataDto;
import com.brandshaastra.R;
import com.brandshaastra.interfaces.Consts;
import com.brandshaastra.ui.activity.UpdateBusinessActivity;
import com.brandshaastra.utils.CustomTextView;
import com.brandshaastra.utils.CustomTextViewBold;

import java.util.List;

public class BusinessAdapter extends RecyclerView.Adapter<BusinessAdapter.BusinessViewHolder> {

    Context context;
    List<BusinessDataDto> arrayList;

    public BusinessAdapter(Context context, List<BusinessDataDto> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        Log.e("SUBCAT",""+arrayList.toString());
    }

    @NonNull
    @Override
    public BusinessViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.business_recycler_layout,parent,false);
        return new BusinessViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusinessViewHolder holder, int position) {

        if (arrayList.get(position).getProfile_delete().equalsIgnoreCase("1")){
            holder.business_edit_img.setVisibility(View.GONE);
        }
        else {
            holder.business_edit_img.setVisibility(View.VISIBLE);
        }
        holder.business_rv_name.setText(arrayList.get(position).getName());
        holder.business_rv_phone.setText(arrayList.get(position).getMobile_no());
        holder.business_rv_des.setText(arrayList.get(position).getDescription());
        Glide.with(context).load(arrayList.get(position).getImage()).into(holder.business_rv_img);

        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ProductActivity.class));
            }
        });*/

        holder.business_edit_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, UpdateBusinessActivity.class)
                        .putExtra(Consts.BUSINESS_ID,arrayList.get(position).getBussiness_profile_id())
                        .putExtra(Consts.IMAGE,arrayList.get(position).getImage())
                        .putExtra(Consts.MOBILE,arrayList.get(position).getMobile_no())
                        .putExtra(Consts.NAME,arrayList.get(position).getName())
                        .putExtra(Consts.ADDRESS,arrayList.get(position).getAddress())
                        .putExtra(Consts.WEBSITE,arrayList.get(position).getWebsite())
                        .putExtra(Consts.DESCRIPTION,arrayList.get(position).getDescription())
                        .putExtra(Consts.FACEBOOK,arrayList.get(position).getC_fb())
                        .putExtra(Consts.YOUTUBE,arrayList.get(position).getC_yt())
                        .putExtra(Consts.INSTAGRAM,arrayList.get(position).getC_instagram())
                        .putExtra(Consts.WHATSAPP,arrayList.get(position).getC_whatsapp())
                        .putExtra(Consts.EMAIL,arrayList.get(position).getEmail())
                        .putExtra("social_media_str",arrayList.get(position).getSocial_media())
                        .putExtra("edit",true));
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class BusinessViewHolder extends RecyclerView.ViewHolder {

        CustomTextViewBold business_rv_name;
        CustomTextViewBold business_rv_phone;
        CustomTextView business_rv_des;
        ImageView business_rv_img;
        ImageView business_edit_img;

        public BusinessViewHolder(@NonNull View itemView) {
            super(itemView);

            business_rv_name = itemView.findViewById(R.id.business_rv_name);
            business_rv_phone = itemView.findViewById(R.id.business_rv_phone);
            business_rv_des = itemView.findViewById(R.id.business_rv_des);
            business_rv_img = itemView.findViewById(R.id.business_rv_img);
            business_edit_img = itemView.findViewById(R.id.business_edit_img);
        }
    }
}

