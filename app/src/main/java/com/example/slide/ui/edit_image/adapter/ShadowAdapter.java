package com.example.slide.ui.edit_image.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.slide.R;
import com.example.slide.ui.edit_image.ShadowProvider;
import com.example.slide.ui.edit_image.framework.AddTextProperties;

import java.util.List;

public class ShadowAdapter extends RecyclerView.Adapter<ShadowAdapter.ViewHolder> {

    private final List<AddTextProperties.TextShadow> lstTextShadows = ShadowProvider.INSTANCE.getSHADOWS();
    private LayoutInflater mInflater;
    private Context context;
    private ShadowItemClickListener mClickListener;
    private AddTextProperties.TextShadow selectedItem;

    // data is passed into the constructor
    public ShadowAdapter(Context context, AddTextProperties.TextShadow selectedItem) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.selectedItem = selectedItem;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_text_shadow, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AddTextProperties.TextShadow textShadow = lstTextShadows.get(position);
        holder.fontItem.setShadowLayer(textShadow.getRadius(), textShadow.getDx(), textShadow.getDy(), textShadow.getColorShadow());
        holder.border.setSelected(selectedItem == textShadow);
        holder.itemView.setOnClickListener(v -> {
            selectedItem = textShadow;
            if (mClickListener != null) mClickListener.onShadowItemClick(textShadow, position);
            notifyDataSetChanged();
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return lstTextShadows.size();
    }

    // stores and recycles views as they are scrolled off screen
    public static class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView fontItem;
        View border;

        ViewHolder(View itemView) {
            super(itemView);
            fontItem = itemView.findViewById(R.id.tvText);
            border = itemView.findViewById(R.id.border);
        }

    }

    // allows clicks events to be caught
    public void setShadowItemClickListener(ShadowItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ShadowItemClickListener {
        void onShadowItemClick(AddTextProperties.TextShadow textShadow, int textShadowIndex);
    }
}
