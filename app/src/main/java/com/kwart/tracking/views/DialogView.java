package com.kwart.tracking.views;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.kwart.tracking.R;
import com.kwart.tracking.utils.Constants;

import java.util.Objects;

public class DialogView {
    private Context context;
    private Dialog dialog;

    private TextView dialogTitle, dialogText;
    private Button dialogCancel, dialogOk;

    private boolean isConfigureated = false;


    public DialogView(Context context){
        this.context = context;
    }


    public void createNewDialog(){
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogview_layout);
        dialogTitle = dialog.findViewById(R.id.dialogTitle);
        dialogText = dialog.findViewById(R.id.dialogText);
        dialogCancel = dialog.findViewById(R.id.dialogCancelBtn);
        dialogOk = dialog.findViewById(R.id.dialogOkBtn);
    }

    public void setDialogTitle(String title){
        this.dialogTitle.setText(title);
    }

    public void setDialogText(String text){
        this.dialogText.setText(text);
    }

    public void setCancel(int visibility, String buttonText, View.OnClickListener v){
        this.dialogCancel.setVisibility(visibility);
        this.dialogCancel.setText(buttonText);
        this.dialogCancel.setOnClickListener(v);
    }

    public void setOk(int visibility, String buttonText, View.OnClickListener v){
        this.dialogOk.setVisibility(visibility);
        this.dialogOk.setText(buttonText);
        this.dialogOk.setOnClickListener(v);
    }

    public void show(){
        if(dialogTitle.getText().equals("null") || dialogText.getText().equals("null") ||
        dialogOk.getText().equals("null")||dialogCancel.getText().equals("null")){
            Log.e(Constants.APP_TAG, "Fatal exception: dialog not configured!");
        } else {
            dialog.show();
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    public void close(){
        dialog.cancel();
    }

}
