package com.kwart.tracking.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.kwart.tracking.R;
import com.kwart.tracking.TrackingClass;
import com.kwart.tracking.activity.WorkoutActivity;
import com.kwart.tracking.views.fitness.FitnessItem;
import com.kwart.tracking.views.fitness.FitnessRecyclerView;
import com.kwart.tracking.views.fitness.FitnessTypeRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;


public class TrainFragment extends Fragment {
    private FitnessTypeRecyclerAdapter fitnessTypeRecyclerAdapter;
    private FitnessRecyclerView fitnessRecyclerView;
    private List<FitnessItem> fitnessItems;
    private Button startWorkout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.train_fragment, container, false);
        fitnessRecyclerView = view.findViewById(R.id.train_type_recycler);
        fitnessItems = new ArrayList<>();
        fitnessItems.add(new FitnessItem(0, getContext().getDrawable(R.drawable.run_icon), "Бег"));
        fitnessItems.add(new FitnessItem(0, getContext().getDrawable(R.drawable.walking), "Ходьба"));
        fitnessItems.add(new FitnessItem(0, getContext().getDrawable(R.drawable.bycicle), "Велопоездка"));
        fitnessItems.add(new FitnessItem(0, getContext().getDrawable(R.drawable.marathon), "Марафон"));
        fitnessItems.add(new FitnessItem(0, getContext().getDrawable(R.drawable.treadmill), "Беговая дорожка"));
        fitnessRecyclerView.initLayoutManager(0, false);
        fitnessRecyclerView.setInitPosition(0);
        fitnessTypeRecyclerAdapter = new FitnessTypeRecyclerAdapter(fitnessItems, TrackingClass.getContext());
        fitnessRecyclerView.setAdapter(fitnessTypeRecyclerAdapter);
        fitnessRecyclerView.setFlingVelocity(1);


        startWorkout = view.findViewById(R.id.start_workout);
        startWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WorkoutActivity.class);
                intent.putExtra("mode", fitnessRecyclerView.getCurrentItem());
                startActivity(intent);
            }
        });


        return view;
    }

}
