package com.kwart.tracking.utils.workout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.kwart.tracking.R;
import com.kwart.tracking.activity.MapViewActivity;
import com.kwart.tracking.activity.WorkoutActivity;
import com.kwart.tracking.utils.ChartValueManager;
import com.kwart.tracking.utils.ColorUtil;
import com.kwart.tracking.views.DialogView;

import java.util.ArrayList;
import java.util.List;

public class WorkoutRecyclerViewAdapter extends RecyclerView.Adapter<WorkoutRecyclerViewAdapter.ViewHolder> implements OnChartValueSelectedListener {

    private Context context;
    private Activity activity;
    private List<WorkoutItem> workoutItem;
    private List<List<WorkoutItem>> workoutItemList;
    private WorkoutFileManager workoutFileManager;

    private ChartValueManager speedChartManager, heartChartManager;
    private LineChart speedChart, pulseChart;

    public WorkoutRecyclerViewAdapter(List<List<WorkoutItem>> workoutItemList, Context context, Activity activity){
        this.context = context;
        this.workoutItemList = workoutItemList;
        this.activity = activity;
        this.workoutFileManager = new WorkoutFileManager(context);
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
        final String filename = workoutItem.get(workoutItem.size()-1).getFilename();

        if(workoutFileManager.haveMapPath(filename)){
            holder.showMap.setVisibility(View.VISIBLE);
            holder.showMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(android.os.Build.VERSION.SDK_INT < 24) {
                        Intent mapActivity = new Intent(context, MapViewActivity.class);
                        mapActivity.putExtra("filename", filename);
                        mapActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(mapActivity);
                    } else {
                        Intent mapActivity = new Intent(context, MapViewActivity.class);
                        mapActivity.putExtra("filename", filename);
                        context.startActivity(mapActivity);
                    }
                }
            });
        } else {
            holder.showMap.setVisibility(View.GONE);
        }


        List<Float> speedValuesList = new ArrayList<>();
        List<Float> pulseValuesList = new ArrayList<>();
        List<String> timeValues = new ArrayList<>();

        float maxPulse = 0;
        float maxSpeed = 0;
        float speedArray = 0;
        float pulseArray = 0;

        for(WorkoutItem item:workoutItem){
            if(item.getAvgSpeed()>maxSpeed){
                maxSpeed = item.getAvgSpeed();
            }
            if(item.getAvgPulse()>maxPulse){
                maxPulse = item.getAvgPulse();
            }
            speedArray += item.getAvgSpeed();
            pulseArray += item.getAvgPulse();
            speedValuesList.add(item.getAvgSpeed());
            pulseValuesList.add((float)item.getAvgPulse());
            timeValues.add(getSringTime(item.getWorckoutTimeRun()));
        }

        if(speedArray < 1){
            speedChart.setVisibility(View.GONE);
            holder.noDataSpeed.setVisibility(View.VISIBLE);
        } else {
            holder.noDataSpeed.setVisibility(View.GONE);
            speedChart.setVisibility(View.VISIBLE);
            speedChartManager = new ChartValueManager(context, speedChart);
            speedChartManager.initChart(maxSpeed+20, 0, timeValues, ColorUtil.getAttrColor(activity, R.attr.backgroundColor), ColorUtil.getAttrColor(activity, R.attr.textColor));
            speedChartManager.setValues(speedValuesList, ColorUtil.getAttrColor(activity, R.attr.colorPrimaryDark));
        }

        if(pulseArray < 1){
            pulseChart.setVisibility(View.GONE);
            holder.noDataPulse.setVisibility(View.VISIBLE);
        } else {
            holder.noDataPulse.setVisibility(View.GONE);
            pulseChart.setVisibility(View.VISIBLE);
            heartChartManager = new ChartValueManager(context, pulseChart);
            heartChartManager.initChart(maxPulse+20, 0, timeValues, ColorUtil.getAttrColor(activity, R.attr.backgroundColor), ColorUtil.getAttrColor(activity, R.attr.textColor));
            heartChartManager.setValues(pulseValuesList, ColorUtil.getAttrColor(activity, R.attr.colorPrimaryDark));

        }

        holder.deteleWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogView dialogView = new DialogView(context);
                dialogView.createNewDialog();
                dialogView.setDialogTitle("Удаление");
                dialogView.setDialogText("Вы действительно хотите удалить это занятие?");
                dialogView.setCancel(View.VISIBLE, "Нет", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogView.close();
                    }
                });
                dialogView.setOk(View.VISIBLE, "Да", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogView.close();
                        workoutFileManager.deleteWorkoutByDate(filename);
                    }
                });
                dialogView.show();
            }
        });
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
        TextView workoutType, workoutDate, distanceView, stepsView, caloriesView, minPulseView, maxPulseView, noDataPulse, noDataSpeed;
        Button showMap;
        ImageView deteleWorkout;
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
            showMap = view.findViewById(R.id.workout_recycler_showMapBtn);
            noDataPulse = view.findViewById(R.id.workout_recycler_noDataPulseChart);
            noDataSpeed = view.findViewById(R.id.workout_recycler_noDataSpeedChart);
            deteleWorkout = view.findViewById(R.id.workout_recycler_deleteBtn);
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
