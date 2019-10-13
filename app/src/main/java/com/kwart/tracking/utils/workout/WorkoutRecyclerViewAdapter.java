package com.kwart.tracking.utils.workout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.kwart.tracking.R;
import com.kwart.tracking.utils.ChartValueManager;
import com.kwart.tracking.utils.ColorUtil;

import java.util.ArrayList;
import java.util.List;

public class WorkoutRecyclerViewAdapter extends RecyclerView.Adapter<WorkoutRecyclerViewAdapter.ViewHolder> implements OnChartValueSelectedListener {

    private Context context;
    private Activity activity;
    private List<WorkoutItem> workoutItem;
    private List<List<WorkoutItem>> workoutItemList;

    private ChartValueManager speedChartManager, heartChartManager;
    private LineChart speedChart, pulseChart;

    public WorkoutRecyclerViewAdapter(List<List<WorkoutItem>> workoutItemList, Context context, Activity activity){
        this.context = context;
        this.workoutItemList = workoutItemList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public WorkoutRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_recycler_item, parent, false);
        return new WorkoutRecyclerViewAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final WorkoutRecyclerViewAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        workoutItem = workoutItemList.get(position);
        holder.workoutType.setText(workoutItem.get(workoutItem.size()-1).getWorkoutStringType());
        holder.maxPulseView.setText(String.valueOf(workoutItem.get(workoutItem.size()-1).getMaxPulse()));
        holder.minPulseView.setText(String.valueOf(workoutItem.get(workoutItem.size()-1).getMinPulse()));
        holder.workoutDate.setText(workoutItem.get(workoutItem.size()-1).getDate());

        holder.distanceView.setText(String.valueOf(workoutItem.get(workoutItem.size()-1).getDistance()));
        holder.stepsView.setText(String.valueOf(workoutItem.get(workoutItem.size()-1).getStepCount()));
        holder.caloriesView.setText(String.valueOf(workoutItem.get(workoutItem.size()-1).getCalories()));
        List<Float> speedValuesList = new ArrayList<>();
        List<Float> pulseValuesList = new ArrayList<>();
        List<String> timeValues = new ArrayList<>();

        float maxPulse = 0;
        float maxSpeed = 0;

        for(WorkoutItem item:workoutItem){
            if(item.getAvgSpeed()>maxSpeed){
                maxSpeed = item.getAvgSpeed();
            }
            if(item.getAvgPulse()>maxPulse){
                maxPulse = item.getAvgPulse();
            }
            speedValuesList.add(item.getAvgSpeed());
            pulseValuesList.add((float)item.getAvgPulse());
            timeValues.add(getSringTime(item.getWorckoutTimeRun()));
        }

        speedChartManager = new ChartValueManager(context, speedChart);
        speedChartManager.initChart(maxSpeed+20, 0, timeValues, ColorUtil.getAttrColor(activity, R.attr.backgroundColor), ColorUtil.getAttrColor(activity, R.attr.textColor));
        speedChartManager.setValues(speedValuesList, ColorUtil.getAttrColor(activity, R.attr.colorPrimaryDark));

        heartChartManager = new ChartValueManager(context, pulseChart);
        heartChartManager.initChart(maxPulse+20, 0, timeValues, ColorUtil.getAttrColor(activity, R.attr.backgroundColor), ColorUtil.getAttrColor(activity, R.attr.textColor));
        heartChartManager.setValues(pulseValuesList, ColorUtil.getAttrColor(activity, R.attr.colorPrimaryDark));

    }



    @Override
    public int getItemCount() {
        return workoutItemList.size();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView workoutType, workoutDate, distanceView, stepsView, caloriesView, minPulseView, maxPulseView;

        ViewHolder(final View view) {
            super(view);
            workoutType = view.findViewById(R.id.workout_recycler_stringTypeWorkout);
            workoutDate = view.findViewById(R.id.workout_recycler_date);
            speedChart = view.findViewById(R.id.workout_recycler_averageSpeedChart);
            pulseChart = view.findViewById(R.id.workout_recycler_averageHeartRateChart);
            distanceView = view.findViewById(R.id.workout_recycler_distance);
            stepsView = view.findViewById(R.id.workout_recycler_steps);
            caloriesView = view.findViewById(R.id.workout_recycler_calories);
            minPulseView = view.findViewById(R.id.workout_recycler_minPulse);
            maxPulseView = view.findViewById(R.id.workout_recycler_maxPulse);


        }
    }
    private static String getSringTime(int totalTimeInSec){
        long hours = totalTimeInSec / 3600;
        long minutes = (totalTimeInSec % 3600) / 60;
        StringBuilder stringBuilder = new StringBuilder();
        if(hours < 10){
            stringBuilder.append("0").append(hours);
        } else {
            stringBuilder.append(hours);
        }
        stringBuilder.append(":");
        if(minutes < 10){
            stringBuilder.append("0").append(minutes);
        } else {
            stringBuilder.append(minutes);
        }
        return stringBuilder.toString();
    }
}
