package com.brandshaastra.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brandshaastra.R;
import com.brandshaastra.interfaces.OnFontStyleChange;
import com.brandshaastra.utils.CustomTextView;
import com.brandshaastra.utils.FontCache;

import java.util.List;

public class FontStyleAdapter extends RecyclerView.Adapter<FontStyleAdapter.FontViewHolder> {

    Context context;
    List<String> font;
    OnFontStyleChange fontStyleChange;

    public FontStyleAdapter(Context context, List<String> font, OnFontStyleChange fontStyleChange) {
        this.context = context;
        this.font = font;
        this.fontStyleChange = fontStyleChange;
    }

    @NonNull
    @Override
    public FontViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fontstyle_recycler_layout, parent, false);
        return new FontViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FontViewHolder holder, int position) {

        if (font.get(position).equals("0")) {
            holder.text_family.setTypeface(holder.text_family.getTypeface(), Typeface.NORMAL);
        } else if (font.get(position).equals("1")) {
            holder.text_family.setTypeface(Typeface.SANS_SERIF);
        } else if (font.get(position).equals("2")) {
            holder.text_family.setTypeface(Typeface.SERIF);
        } else if (font.get(position).equals("3")) {
            holder.text_family.setTypeface(holder.text_family.getTypeface(), Typeface.ITALIC);
        } else if (font.get(position).equals("4")) {
            holder.text_family.setTypeface(holder.text_family.getTypeface(), Typeface.BOLD);
        } else if (font.get(position).equals("5")) {
            holder.text_family.setTypeface(holder.text_family.getTypeface(), Typeface.BOLD_ITALIC);
        } else if (font.get(position).equals("6")) {
            holder.text_family.setTypeface(Typeface.DEFAULT);
        } else if (font.get(position).equals("7")) {
            holder.text_family.setTypeface(Typeface.MONOSPACE);
        } else if (font.get(position).equals("8")) {
            holder.text_family.setTypeface(FontCache.getTypeface("Lato_Regular.ttf", context));
        } else if (font.get(position).equals("9")) {
            holder.text_family.setTypeface(FontCache.getTypeface("Lato_Black.ttf", context));
        } else if (font.get(position).equals("10")) {
            holder.text_family.setTypeface(FontCache.getTypeface("Lato-Semibold.ttf", context));
        } else if (font.get(position).equals("11")) {
            holder.text_family.setTypeface(FontCache.getTypeface("Lato-Heavy.ttf", context));
        } else if (font.get(position).equals("12")) {
            holder.text_family.setTypeface(FontCache.getTypeface("Lato_BlackItalic.ttf", context));
        } else if (font.get(position).equals("13")) {
            holder.text_family.setTypeface(FontCache.getTypeface("Lato_BoldItalic.ttf", context));
        } else if (font.get(position).equals("14")) {
            holder.text_family.setTypeface(FontCache.getTypeface("Lato_Thin.ttf", context));
        } else if (font.get(position).equals("15")) {
            holder.text_family.setTypeface(FontCache.getTypeface("Lato_ThinItalic.ttf", context));
        }


        holder.text_family.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Font style changed", Toast.LENGTH_SHORT).show();
                fontStyleChange.setStyle(position);

            }
        });

    }

    @Override
    public int getItemCount() {
        return font.size();
    }

    class FontViewHolder extends RecyclerView.ViewHolder {

        CustomTextView text_family;

        public FontViewHolder(@NonNull View itemView) {
            super(itemView);

            text_family = itemView.findViewById(R.id.text_family);
        }
    }
}
