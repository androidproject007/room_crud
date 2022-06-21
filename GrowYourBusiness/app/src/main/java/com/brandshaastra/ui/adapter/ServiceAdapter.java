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
import com.brandshaastra.R;
import com.brandshaastra.ui.activity.ProductActivity;
import com.brandshaastra.ui.model.ServiceModel;
import com.brandshaastra.utils.CustomTextView;
import com.brandshaastra.utils.CustomTextViewBold;

import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    Context context;
    List<ServiceModel> arrayList;

    public ServiceAdapter(Context context, List<ServiceModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        Log.e("SUBCAT",""+arrayList.toString());
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.service_recycler_layout,parent,false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {

        holder.service_description.setText(arrayList.get(position).getService_des());
        holder.service_recycle_price.setText(arrayList.get(position).getService_price());
        holder.service_recycle_strike.setText(arrayList.get(position).getService_strike());
        Glide.with(context).load(arrayList.get(position).getService_img()).into(holder.service_recycle_img);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ProductActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class ServiceViewHolder extends RecyclerView.ViewHolder {

        CustomTextViewBold service_recycle_price,service_recycle_strike;
        CustomTextView service_description;
        ImageView service_recycle_img;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);

            service_recycle_price = itemView.findViewById(R.id.service_recycle_price);
            service_recycle_strike = itemView.findViewById(R.id.service_recycle_strike);
            service_recycle_img = itemView.findViewById(R.id.service_recycle_img);
        }
    }
}


