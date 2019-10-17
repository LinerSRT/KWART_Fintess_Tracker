package com.kwart.tracking.activity;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.kwart.tracking.R;
import com.kwart.tracking.utils.Constants;
import com.kwart.tracking.utils.ThemeManager;
import com.kwart.tracking.utils.workout.WorkoutFileManager;
import com.kwart.tracking.utils.workout.WorkoutMapInterface;
import com.kwart.tracking.utils.workout.WorkoutMapManager;
import com.kwart.tracking.utils.workout.WorkoutMapPath;
import com.kwart.tracking.views.DialogView;

import java.util.List;

public class MapViewActivity extends Activity {
    private WorkoutMapManager workoutMapManager;
    private WorkoutFileManager workoutFileManager;
    private List<WorkoutMapPath> workoutMapPathList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeManager themeManager = new ThemeManager(this);
        themeManager.initTheme(this);
        setContentView(R.layout.activity_map_view);
        MapFragment map = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapView));
        workoutFileManager = new WorkoutFileManager(this);
        workoutMapManager = new WorkoutMapManager(this, new WorkoutMapInterface() {
            @Override
            public void onMapLoaded(GoogleMap googleMap) {
                workoutMapManager.setMapType(4);
                String filename = getIntent().getStringExtra("filename");
                workoutMapPathList = workoutFileManager.getMapPathList(filename);
                if(workoutMapPathList.size() != 0)
                    for(WorkoutMapPath item:workoutMapPathList){
                        workoutMapManager.drawPath(item, Color.GREEN, 4);
                    }
                Log.d(Constants.APP_TAG, "FileMapName: "+filename);
            }
        });
        workoutMapManager.initActivityMapManager(map);

    }

    @Override
    public void onBackPressed() {
        final DialogView dialogView = new DialogView(this);
        dialogView.createNewDialog();
        dialogView.setDialogTitle("Внимание");
        dialogView.setDialogText("Выйти из просмотра карты?");
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
                finish();
            }
        });
        dialogView.show();
    }
}
