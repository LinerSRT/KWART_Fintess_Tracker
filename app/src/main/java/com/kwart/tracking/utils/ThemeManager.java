package com.kwart.tracking.utils;

import android.app.Activity;
import android.content.Context;

import com.kwart.tracking.R;

public class ThemeManager {
    private Context context;


    public ThemeManager(Context context){
        this.context = context;
    }

    public void initTheme(Activity activity){
        switch (android.provider.Settings.Global.getInt(context.getContentResolver(), "system_theme", 4)){
            case 1:
                activity.setTheme(R.style.BlueDeepTheme);
                break;
            case 2:
                activity.setTheme(R.style.RedDeepTheme);
                break;
            case 3:
                activity.setTheme(R.style.GreenDeepTheme);
                break;
            case 4:
                activity.setTheme(R.style.DarkTheme);
                break;
            default:
                activity.setTheme(R.style.AppTheme);
        }
    }

    public void setTheme(int themeID, Activity activity){
        if(themeID <= 4 && themeID >= 0)
        switch (themeID){
            case 1:
                activity.setTheme(R.style.BlueDeepTheme);
                break;
            case 2:
                activity.setTheme(R.style.RedDeepTheme);
                break;
            case 3:
                activity.setTheme(R.style.GreenDeepTheme);
                break;
            case 4:
                activity.setTheme(R.style.DarkTheme);
                break;
            default:
                activity.setTheme(R.style.AppTheme);
        }
    }

}
