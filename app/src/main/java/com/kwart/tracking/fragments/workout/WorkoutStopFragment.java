package com.kwart.tracking.fragments.workout;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.kwart.tracking.R;
import com.kwart.tracking.activity.MainActivity;
import com.kwart.tracking.activity.WorkoutActivity;
import com.kwart.tracking.utils.ColorUtil;
import com.kwart.tracking.utils.Constants;
import com.kwart.tracking.utils.workout.WorkoutManager;

import java.util.Objects;


public class WorkoutStopFragment extends Fragment {
    private WorkoutManager workoutManager;
    private Button pauseBtn, stopBtn;
    private Drawable greenBG, redBG;

    private boolean isWorkoutPaused = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.workout_stop_fragment, container, false);
        pauseBtn = view.findViewById(R.id.pause_resume_workout);
        stopBtn = view.findViewById(R.id.stop_workout);

        greenBG = getResources().getDrawable(R.drawable.button_background_shape);
        greenBG.setTint(Color.parseColor("#FF0000"));
        redBG = getResources().getDrawable(R.drawable.button_background_shape);
        redBG.setTint(Color.parseColor("#0000FF"));
        workoutManager = WorkoutInformationFragment.getWorkoutManager();


        if(isWorkoutPaused) {
            Drawable img = Objects.requireNonNull(getContext()).getResources().getDrawable( R.drawable.resume_icon );
            pauseBtn.setText("Продолжить");
            pauseBtn.setCompoundDrawablesWithIntrinsicBounds( img, null, null, null);
        } else {
            Drawable img = Objects.requireNonNull(getContext()).getResources().getDrawable( R.drawable.pause_icon );
            pauseBtn.setText("Пауза");
            pauseBtn.setCompoundDrawablesWithIntrinsicBounds( img, null, null, null);
        }
        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(workoutManager != null){
                    if(!isWorkoutPaused) {
                        workoutManager.pauseSensorRecognition();
                        isWorkoutPaused = true;
                        Drawable img = Objects.requireNonNull(getContext()).getResources().getDrawable( R.drawable.resume_icon );
                        pauseBtn.setText("Продолжить");
                        pauseBtn.setCompoundDrawablesWithIntrinsicBounds( img, null, null, null);
                    } else {
                        workoutManager.resumeSensorRecognition();
                        isWorkoutPaused = false;
                        Drawable img = Objects.requireNonNull(getContext()).getResources().getDrawable( R.drawable.pause_icon );
                        pauseBtn.setText("Пауза");
                        pauseBtn.setCompoundDrawablesWithIntrinsicBounds( img, null, null, null);
                    }

                }
            }
        });
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(workoutManager != null){
                    workoutManager.stopSensorRecognition();
                    Objects.requireNonNull(getActivity()).finish();
                }
            }
        });


        return view;
    }
}
