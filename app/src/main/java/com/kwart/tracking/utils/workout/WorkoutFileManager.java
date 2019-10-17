package com.kwart.tracking.utils.workout;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class WorkoutFileManager {
    private Context context;
    private String DATA_FOLDER = "Tracking";
    private String MAP_FILEDIR = "MapData";
    private String WORKOUT_FILEDIR = "Workout";

    public WorkoutFileManager(Context context){
        this.context = context;
    }


    public void addMapPath(List<WorkoutMapPath> workoutMapPathList, String date){
        try {
            File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+DATA_FOLDER+File.separator+MAP_FILEDIR+File.separator);
            if (!path.exists()) {
                path.mkdirs();
            }
            File file = new File(path, date);
            if(!file.exists()){
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            FileOutputStream stream = new FileOutputStream(file);
            try {
                stream.write(new Gson().toJson(workoutMapPathList).getBytes());
            } finally {
                stream.close();
            }
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }

    }

    public List<WorkoutMapPath> getMapPathList(String date){
        File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+DATA_FOLDER+File.separator+MAP_FILEDIR+File.separator);
        if (!path.exists()) {
            path.mkdirs();
        }
        File file = new File(path, date);
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        int length = (int) file.length();
        byte[] bytes = new byte[length];
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            in.read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert in != null;
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Type listType = new TypeToken<ArrayList<WorkoutMapPath>>(){}.getType();
        List<WorkoutMapPath> mapList = new Gson().fromJson(new String(bytes), listType);
        return mapList;
    }


    public void saveWorkout(List<WorkoutItem> workoutItemList, String date){
        try {
            File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+DATA_FOLDER+File.separator+WORKOUT_FILEDIR+File.separator);
            if (!path.exists()) {
                path.mkdirs();
            }
            File file = new File(path, date);
            if(!file.exists()){
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            FileOutputStream stream = new FileOutputStream(file);
            try {
                stream.write(new Gson().toJson(workoutItemList).getBytes());
            } finally {
                stream.close();
            }
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public List<WorkoutItem> getWorkoutData(String date){
        File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+DATA_FOLDER+File.separator+WORKOUT_FILEDIR+File.separator);
        if (!path.exists()) {
            path.mkdirs();
        }
        File file = new File(path, date);
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        int length = (int) file.length();
        byte[] bytes = new byte[length];
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            in.read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert in != null;
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Type listType = new TypeToken<ArrayList<WorkoutItem>>(){}.getType();
        List<WorkoutItem> workoutList = new Gson().fromJson(new String(bytes), listType);
        return workoutList;
    }

    public int getWorkoutCount(){
        File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+DATA_FOLDER+File.separator+WORKOUT_FILEDIR+File.separator);
        File[] list = path.listFiles();
        int count = 0;
        if(list != null)
        for(File item: list){
            count++;
        }
        return count;
    }

    public List<String> getWorkoutDates(){
        File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+DATA_FOLDER+File.separator+WORKOUT_FILEDIR+File.separator);
        File[] list = path.listFiles();
        List<String> workoutList = new ArrayList<>();
        for(File item: list){
            workoutList.add(item.getName());
        }
        return workoutList;
    }

    public void deleteAllWorkouts(){
        File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+DATA_FOLDER+File.separator+WORKOUT_FILEDIR+File.separator);
        File path2 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+DATA_FOLDER+File.separator+MAP_FILEDIR+File.separator);
        if(path.list() != null) {
            for (String file : path.list()) {
                new File(path, file).delete();
            }
        }
        if(path2.list() != null) {
            for (String file : path2.list()) {
                new File(path, file).delete();
            }
        }
        Intent intent = new Intent("NEED_UPDATE_RECYCLER");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

}
