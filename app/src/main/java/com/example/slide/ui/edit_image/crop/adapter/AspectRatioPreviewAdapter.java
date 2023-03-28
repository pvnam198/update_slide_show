package com.example.slide.ui.edit_image.crop.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.slide.R;
import com.steelkiwi.cropiwa.AspectRatio;

import java.util.Arrays;
import java.util.List;

public class AspectRatioPreviewAdapter extends RecyclerView.Adapter<AspectRatioPreviewAdapter.ViewHolder> {

    private int lastSelectedView;
    private AspectRatioCustom selectedRatio;

    private List<AspectRatioCustom> ratios;

    private OnNewSelectedListener listener;

//    public AspectRatioPreviewAdapter() {
//        ratios = Arrays.asList(
//                new AspectRatioCustom(10, 10, R.drawable.crop_free, R.drawable.crop_free_click),
//                new AspectRatioCustom(1, 1, R.drawable.ratio_1_1, R.drawable.ratio_1_1_click),
//                new AspectRatioCustom(4, 3, R.drawable.ratio_4_3, R.drawable.ratio_4_3_click),
//                new AspectRatioCustom(3, 4, R.drawable.ratio_3_4, R.drawable.ratio_3_4_click),
//                new AspectRatioCustom(5, 4, R.drawable.ratio_5_4, R.drawable.ratio_4_5_click),
//                new AspectRatioCustom(4, 5, R.drawable.ratio_4_5, R.drawable.ratio_4_5_click),
//                new AspectRatioCustom(3, 2, R.drawable.ratio_3_2, R.drawable.ratio_3_2_click),
//                new AspectRatioCustom(2, 3, R.drawable.ratio_2_3, R.drawable.ratio_2_3_click),
//                new AspectRatioCustom(9, 16, R.drawable.ratio_9_16, R.drawable.ratio_9_16_click),
//                new AspectRatioCustom(16, 9, R.drawable.ratio_16_9, R.drawable.ratio_16_9_click));
//        selectedRatio = ratios.get(0);
//    }
//
//    public AspectRatioPreviewAdapter(boolean noFreeStyle) {
//        ratios = Arrays.asList(
//                new AspectRatioCustom(1, 1, R.drawable.ratio_1_1, R.drawable.ratio_1_1_click),
//                new AspectRatioCustom(4, 3, R.drawable.ratio_4_3, R.drawable.ratio_4_3_click),
//                new AspectRatioCustom(3, 4, R.drawable.ratio_3_4, R.drawable.ratio_3_4_click),
//                new AspectRatioCustom(5, 4, R.drawable.ratio_5_4, R.drawable.ratio_4_5_click),
//                new AspectRatioCustom(4, 5, R.drawable.ratio_4_5, R.drawable.ratio_4_5_click),
//                new AspectRatioCustom(3, 2, R.drawable.ratio_3_2, R.drawable.ratio_3_2_click),
//                new AspectRatioCustom(2, 3, R.drawable.ratio_2_3, R.drawable.ratio_2_3_click),
//                new AspectRatioCustom(9, 16, R.drawable.ratio_9_16, R.drawable.ratio_9_16_click),
//                new AspectRatioCustom(16, 9, R.drawable.ratio_16_9, R.drawable.ratio_16_9_click));
//        selectedRatio = ratios.get(0);
//    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_aspect_ratio, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AspectRatioCustom ratio = ratios.get(position);
        if (position == lastSelectedView) {
            holder.ratioView.setImageResource(ratio.getSelectedIem());
        } else {
            holder.ratioView.setImageResource(ratio.getUnselectItem());
        }
    }

    public int getLastSelectedView() {
        return lastSelectedView;
    }

    public void setLastSelectedView(int lastSelectedView) {
        this.lastSelectedView = lastSelectedView;
    }

    @Override
    public int getItemCount() {
        return ratios.size();
    }

    public void setListener(OnNewSelectedListener listener) {
        this.listener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView ratioView;

        public ViewHolder(View itemView) {
            super(itemView);
            ratioView = itemView.findViewById(R.id.aspect_ratio_preview);
            ratioView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (lastSelectedView == getAdapterPosition()) {
                return;
            }

            selectedRatio = ratios.get(getAdapterPosition());
            lastSelectedView = getAdapterPosition();

            if (listener != null) {
//                listener.onNewAspectRatioSelected(selectedRatio);
            }
            notifyDataSetChanged();
        }
    }

    public interface OnNewSelectedListener {
        void onNewAspectRatioSelected(AspectRatio ratio);
    }
}


