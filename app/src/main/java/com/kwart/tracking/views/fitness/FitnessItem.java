package com.kwart.tracking.views.fitness;

import android.graphics.drawable.Drawable;

public class FitnessItem {
    private Drawable icon;
    private String text;
    private int type;

    public FitnessItem(int type, Drawable icon, String text){
        this.icon = icon;
        this.text = text;
        this.type = type;
    }

    public Drawable getIcon() {
        return icon;
    }

    public String getText() {
        return text;
    }

    public int getType() {
        return type;
    }


    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "FitnessItem |> Text: "+text+", type: "+type;
    }
}
