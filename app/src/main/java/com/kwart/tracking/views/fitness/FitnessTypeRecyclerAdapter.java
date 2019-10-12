package com.kwart.tracking.views.fitness;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.kwart.tracking.R;

import java.util.List;

public class FitnessTypeRecyclerAdapter extends FitnessRecycler.Adapter<FitnessTypeRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<FitnessItem> fitnessItemList;


    private static final int ITEM_TYPE_NORMAL = 0;
    private static final int ITEM_TYPE_HEADER = 1;
    private static final int ITEM_TYPE_FOOTER = 2;

    private final int weightAll;
    private final int weightIem;
    private final int weightIemGap;
    private final int weightBoundGap;

    private static FitnessRecyclerInterface fitnessRecyclerInterface;
    public void serInteractListener(FitnessRecyclerInterface fitnessInterface){
        fitnessRecyclerInterface = fitnessInterface;
    }

    public FitnessTypeRecyclerAdapter(List<FitnessItem> fitnessItemList, Context context){
        this.fitnessItemList = fitnessItemList;
        this.context = context;

        weightAll = 400;
        weightIem = 300;
        weightIemGap = 10;
        weightBoundGap = 50;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.train_type_item, parent, false);



        final int sizeAll = Math.min(parent.getHeight(), parent.getWidth());
        final int sizeItem = sizeAll * weightIem/weightAll;
        final int sizeGapItem = sizeAll * weightIemGap/weightAll;
        final int sizeGapBound = sizeAll * weightBoundGap/weightAll;
        RecyclerView.LayoutParams params =  new RecyclerView.LayoutParams(parent.getLayoutParams());
        switch (viewType) {
            case ITEM_TYPE_HEADER:
                params.setMargins(sizeGapBound, sizeGapItem, sizeGapItem, sizeGapItem);
                break;
            case ITEM_TYPE_FOOTER:
                params.setMargins(sizeGapItem, sizeGapItem, sizeGapBound, sizeGapItem);
                break;
            default:
                params.setMargins(sizeGapItem, sizeGapItem, sizeGapItem, sizeGapItem);
                break;
        }
        v.setLayoutParams(params);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final FitnessItem fitnessItem = fitnessItemList.get(position);
        // Set data to UI
        holder.icon.setImageDrawable(fitnessItem.getIcon());
        holder.text.setText(fitnessItem.getText());
    }

    @Override
    public int getItemCount() {
        return fitnessItemList.size();
    }

    public int getBoundGap(int sizeAll){
        return sizeAll*weightBoundGap/weightAll;
    }

    @Override
    public int getItemViewType(int position) {
        if(0 == position){
            return ITEM_TYPE_HEADER;
        }
        if(position == getItemCount() - 1){
            return ITEM_TYPE_FOOTER;
        }
        return ITEM_TYPE_NORMAL;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView icon;
        TextView text;
        ViewHolder(final View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.train_icon_type);
            text = itemView.findViewById(R.id.train_text_type);
        }
    }


}
