package com.jduban.gps.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.jduban.gps.ConstVal;
import com.jduban.gps.R;

/**
 * Created by jduban on 13/09/15.
 */
public class InfoDialog extends Dialog {

    private boolean displayBox;
    private String text;
    private Context c;
    private Dialog d;
    private Button cancel, settings;
    private TextView tv;
    private CheckBox box;

    public InfoDialog(Activity a, String text, boolean displayBox) {
        super(a);
        this.c = a;
        this.text = text;
        this.displayBox = displayBox;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_info);

        tv = (TextView) findViewById(R.id.txt_dia);
        tv.setText(text);

        box = (CheckBox) findViewById(R.id.box);
        if (!displayBox) box.setVisibility(View.GONE);


        cancel = (Button) findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
                saveSettings();
            }
        });

        settings = (Button) findViewById(R.id.btn_settings);
        settings.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                c.startActivity(myIntent);
                dismiss();
                saveSettings();
            }
        });

        box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) cancel.setText("Continue");
                else cancel.setText("Cancel");
            }
        });

    }

    /**
     * Save popup settings in shared Prefs
     */
    private void saveSettings() {
        if (box.isChecked()){

            SharedPreferences sharedPref = ((Activity)c).getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(ConstVal.DISPLAY_POPUP, false);
            editor.apply();
        }

    }

}
