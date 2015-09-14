package com.jduban.gps.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jduban.gps.ConstVal;
import com.jduban.gps.R;
import com.jduban.gps.callback.DialogListener;
import com.jduban.gps.objects.Location;

/**
 * Created by jduban on 13/09/15.
 */
public class LocationDialog extends Dialog {

    private Context c;
    private DialogListener listener;
    private Button cancel, add;
    private EditText name;
    private double latitude;
    private double longitude;

    public LocationDialog(Context c, DialogListener listener, double latitude, double longitude) {
        super(c);
        this.c = c;
        this.listener = listener;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_location);

        name = (EditText) findViewById(R.id.editText);

        cancel = (Button) findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        add = (Button) findViewById(R.id.btn_settings);
        add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Location loc = new Location(Double.toString(latitude), Double.toString(longitude), name.getText().toString());
                ConstVal.addToList(loc);
                Toast.makeText(c, name.getText().toString() + " added", Toast.LENGTH_SHORT).show();
                listener.onMarkerAdded();
                dismiss();
            }
        });

    }

}
