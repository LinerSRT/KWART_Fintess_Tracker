package com.kwart.tracking.utils.workout;

import android.content.Context;
import android.provider.Settings;

import com.kwart.tracking.utils.Constants;

public class WorkOutHelper {

    public static float getUserStepSize(Context context){
        return Settings.System.getFloat(context.getContentResolver(), Constants.USER_STEP_SIZE, 0.80f);
    }

    public static int getUserWeight(Context context){
        return Settings.System.getInt(context.getContentResolver(), Constants.USER_WEIGHT, 60);
    }

    public static int getUserHeight(Context context){
        return Settings.System.getInt(context.getContentResolver(), Constants.USER_HEIGHT, 170);
    }
}
