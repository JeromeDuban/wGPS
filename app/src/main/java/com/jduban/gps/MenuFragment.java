package com.jduban.gps;

/**
 * Created by jduban on 10/09/15.
 */

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jduban.gps.objects.Location;

import java.util.ArrayList;

public class MenuFragment extends Fragment {

    private ArrayList<String> mValues;
    private TextView accuracyTextView;
    private TextView coordinatesTextView;
    private LinearLayout locationContainer;
    private MainActivity a;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        a = (MainActivity) getActivity();

        coordinatesTextView = (TextView) view.findViewById(R.id.coordinate).findViewById(R.id.rowText);
        accuracyTextView = (TextView) view.findViewById(R.id.accuracy).findViewById(R.id.rowText);

        locationContainer = (LinearLayout) view.findViewById(R.id.locationContainer);

        return view;
    }

    /**
     * get menu values from activity
     * @param mValues coordinates, accuracy
     */
    public void setMValues(ArrayList<String> mValues) {
        this.mValues = mValues;
    }

    /**
     * Display locations in menu
     */
    public void displayLocations() {

        locationContainer.removeAllViews();
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (int i = 2 ; i < mValues.size() ; i++) {

            View view = inflater.inflate(R.layout.location, locationContainer, false); //TODO : improve with a runnable
            ((TextView) view.findViewById(R.id.rowText)).setText(mValues.get(i));

            final int child = i;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    a.clearMap();
                    Location l  = ConstVal.locationList.get(child - 2); // coord & accuracy
                    a.addMapMarker(Double.parseDouble(l.getLatitude()), Double.parseDouble(l.getLongitude()), l.getName());
                }
            });
            locationContainer.addView(view);

        }
    }

    public void setCoordinates(String mValue) {
        coordinatesTextView.setText(mValue);
    }

    public void setAccuracy(String mValue) {
        accuracyTextView.setText(mValue);
    }


}

