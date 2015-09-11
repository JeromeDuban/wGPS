package com.jduban.drawer.utils;

/**
 * Created by jduban on 17/03/2015.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jduban.drawer.R;


public class recyclerAdapter extends RecyclerView.Adapter<recyclerAdapter.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_COORDINATE = 1;
    private static final int TYPE_LOCATION = 2;

    private String mValues[];


    public static class ViewHolder extends RecyclerView.ViewHolder {
        int Holderid;

        TextView textView;
        ImageView imageView;

        public ViewHolder(View itemView, int ViewType) {
            super(itemView);

            if (TYPE_LOCATION == ViewType){
                textView = (TextView) itemView.findViewById(R.id.rowText);
                imageView = (ImageView) itemView.findViewById(R.id.rowIcon);
                Holderid = 2;
            }
            else if (TYPE_COORDINATE == ViewType) {
                textView = (TextView) itemView.findViewById(R.id.rowText);
                imageView = (ImageView) itemView.findViewById(R.id.rowIcon);
                Holderid = 1;
            }
        }
    }


    public recyclerAdapter(String Titles[]) {
        mValues = Titles;
    }


    @Override
    public recyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        if (viewType == TYPE_COORDINATE) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.coordinate, parent, false);
            return new ViewHolder(v, viewType);

        } else if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.header, parent, false);
            return new ViewHolder(v, viewType);


        }else if (viewType == TYPE_LOCATION) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.location, parent, false);
            return new ViewHolder(v, viewType);

        }

        return null;

    }


    @Override
    public void onBindViewHolder(recyclerAdapter.ViewHolder holder, int position) {

        if (holder.Holderid == 1 || holder.Holderid == 2) // Coordinates and saved locations
            holder.textView.setText(mValues[position - 1]);

    }

    // This method returns the number of items present in the list
    @Override
    public int getItemCount() {
        return mValues.length + 1; // the number of items in the list will be +1 the titles including the header view.
    }


    // Witht the following method we check what type of view is being passed
    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        else if (position == 1)
            return TYPE_COORDINATE;
        else
            return TYPE_LOCATION;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

}
