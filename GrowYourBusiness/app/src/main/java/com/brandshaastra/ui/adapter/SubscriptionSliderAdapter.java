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

import com.brandshaastra.ui.activity.PackagePurchaseActivity;
import com.bumptech.glide.Glide;
import com.brandshaastra.DTO.UserDTO;
import com.brandshaastra.R;
import com.brandshaastra.interfaces.Consts;
import com.brandshaastra.interfaces.OnPlanSelected;
import com.brandshaastra.preferences.SharedPrefrence;
import com.brandshaastra.ui.model.SliderModel;

import java.util.List;

public class SubscriptionSliderAdapter extends RecyclerView.Adapter<SubscriptionSliderAdapter.SubscriptionViewholder> {

    Context context;
    List<SliderModel> arrayList;
    UserDTO userDTO;
    private SharedPrefrence prefrence;
    String plan_id = "";
    OnPlanSelected onPlanSelected;
    String selected_plan_id = "";
    String razorpay = "";
    boolean from_image = false;

    public SubscriptionSliderAdapter(Context context, List<SliderModel> arrayList, boolean from_image, String razorpay) {
        this.context = context;
        this.arrayList = arrayList;
        this.from_image = from_image;
        this.onPlanSelected = onPlanSelected;
        this.razorpay = razorpay;
        prefrence = SharedPrefrence.getInstance(context);
        userDTO = prefrence.getParentUser(Consts.USER_DTO);
        selected_plan_id = userDTO.getPlan_id();
        Log.e("selected_plan_id", "" + selected_plan_id);
    }

    @NonNull
    @Override
    public SubscriptionViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.subscription_recycle_layout, parent, false);
        return new SubscriptionViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubscriptionViewholder holder, int position) {
        Glide.with(context).load(arrayList.get(position).getPlan_image()).into(holder.sub_image);

        if (arrayList.get(position).getActivate().equalsIgnoreCase("1")) {

            holder.selected_plan_img.setVisibility(View.VISIBLE);
        } else {
            holder.selected_plan_img.setVisibility(View.GONE);
        }

        if (!arrayList.get(position).getPlan_price().equalsIgnoreCase("0")) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    context.startActivity(new Intent(context, PackagePurchaseActivity.class).putExtra("plan_id", arrayList.get(position).getPlan_id()).putExtra("plan_name", arrayList.get(position).getPlan_name()).putExtra("plan_image", arrayList.get(position).getPlan_image()).putExtra("plan_amt", arrayList.get(position).getPlan_price()).putExtra("from_image", from_image).putExtra("razorpay", razorpay));
                    //  onPlanSelected.planSelected(position,arrayList.get(position).getPlan_price(),arrayList.get(position).getPlan_id());
                }
            });
        }

        //holder.sub_image.setBackgroundResource(arrayList.get(position).getImage());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    class SubscriptionViewholder extends RecyclerView.ViewHolder {
        ImageView sub_image, selected_plan_img;

        public SubscriptionViewholder(@NonNull View itemView) {
            super(itemView);

            selected_plan_img = itemView.findViewById(R.id.selected_plan_img);
            sub_image = itemView.findViewById(R.id.sub_image);
        }
    }
}
