package com.jduban.gps;

/**
 * Created by jduban on 14/09/15.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jduban.gps.objects.Location;
import com.jduban.gps.utils.SwipeDismissTouchListener;

import java.util.ArrayList;

public class ManageLocationsActivity extends Activity{


    private LinearLayout locationLayout;
    private Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);

        Toast.makeText(ManageLocationsActivity.this, "Swipe a location to delete it", Toast.LENGTH_LONG).show();

        locationLayout = (LinearLayout) findViewById(R.id.listLocations);
        continueButton = (Button) findViewById(R.id.continueButton);

        final ArrayList<Integer> index = new ArrayList<>();

        if (ConstVal.locationList != null){

            final ViewGroup dismissableContainer = locationLayout;

            for (int i = 0; i < ConstVal.locationList.size(); i++) {
                index.add(i);

                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.location_element, dismissableContainer, false);

                Location l = ConstVal.locationList.get(i);

                final RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.content);

                // set Title
                TextView title = (TextView) view.findViewById(R.id.locationTitle);
                title.setText(l.getName());

                // set Coordinates
                TextView room = (TextView) view.findViewById(R.id.locationCoordinates);
                room.setText(l.getLatitude() + " " + l.getLongitude());

                final int j = i;
                
                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });


                // Create a generic swipe-to-dismiss touch listener.
                layout.setOnTouchListener(new SwipeDismissTouchListener(
                        layout,
                        null,
                        new SwipeDismissTouchListener.OnDismissCallback() {
                            @Override
                            public void onDismiss(View view, Object token) {
                                dismissableContainer.removeView(layout);
                                int k;
                                for (k = 0; k < index.size() ; k++){
                                    if (index.get(k) == j){
                                        index.remove(k);
                                        ConstVal.locationList.remove(k);
                                    }
                                }
                            }
                        }));

                locationLayout.addView(view);
            }
        }
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishActivity();
            }
        });
    }

    private void finishActivity() {
        ConstVal.writeList();
        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);
        finish();
    }


    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            finishActivity();
        }
        return super.onKeyDown(keyCode, event);
    }


}

