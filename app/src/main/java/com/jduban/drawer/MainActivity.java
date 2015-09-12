package com.jduban.drawer;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jduban.drawer.utils.recyclerAdapter;


public class MainActivity extends ActionBarActivity {

    private String mValues[] = {"", "Location 1","Location 2","Location 3"};
    private Toolbar mToolbar;
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    DrawerLayout mDrawer;
    ActionBarDrawerToggle mDrawerToggle;

    GestureDetector mSingleTapDetector;

    // UI COMPONENTS
    private FragmentManager fm;
    private RelativeLayout layoutMap;
    private LinearLayout layoutMenu;
    private MenuFragment menuFragment;

    private boolean landscape;
    private TextView menuTextView;
    private LinearLayout locationContainer;
    private LayoutInflater inflater;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);

        mSingleTapDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

        landscape = getResources().getBoolean(R.bool.dual_pane);

        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new recyclerAdapter(mValues);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(recyclerListener);

        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager

        mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager

        mDrawer = (DrawerLayout) findViewById(R.id.DrawerLayout);        // mDrawer object Assigned to the view
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

        };

        mDrawer.setDrawerListener(mDrawerToggle); // mDrawer Listener set to the mDrawer toggle
        mDrawerToggle.syncState();               // Finally we set the drawer toggle sync State

        layoutMenu = (LinearLayout) findViewById(R.id.fragmentMenu);

        menuTextView = (TextView) layoutMenu.findViewById(R.id.coordinate).findViewById(R.id.rowText);
        locationContainer = (LinearLayout) layoutMenu.findViewById(R.id.locationContainer);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        menuFragment = (MenuFragment ) getFragmentManager().findFragmentById(R.id.fragmentMenu);

        // Displays the menu fragment in landscape
        if(!landscape){
            layoutMenu.setVisibility(View.GONE);
        }
        else{
            layoutMenu.setVisibility(View.VISIBLE);
            locationContainer.removeAllViews();

            for (int i = 1 ; i < mValues.length ; i++) {

                View view = inflater.inflate(R.layout.location, locationContainer, false); //TODO : improve with a runnable
                ((TextView) view.findViewById(R.id.rowText)).setText(mValues[i]);
                locationContainer.addView(view);

            }
        }

        startLocationListener();

//        if (menuFragment==null || ! menuFragment.isInLayout()) {}
//        else {}

    }

    RecyclerView.OnItemTouchListener recyclerListener = new RecyclerView.OnItemTouchListener() {
        @Override
        public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
            View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

            if (child != null && mSingleTapDetector.onTouchEvent(motionEvent)) {
                mDrawer.closeDrawers();
                Fragment f;
                switch (recyclerView.getChildPosition(child)) {
                    default:
//                            f = new Fragment1();
                        break;

                }
//                    fm.beginTransaction().replace(R.id.container, f).commit();
                return true;
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {}

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean b) {
        }
    };

    private void startLocationListener(){

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, new LocationListener() {

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }

            @Override
            public void onLocationChanged(Location location) {
                Log.d("GPS", "Latitude " + location.getLatitude() + " et longitude " + location.getLongitude());
                mValues[0] = location.getLatitude() + " " + location.getLongitude(); //TODO convert to DMS format
                if(!landscape){
                    mAdapter.notifyDataSetChanged();
                }else{
                    menuTextView.setText(mValues[0]);
                }

            }

        });
    }

    public void setDrawerState(boolean isEnabled) {
        if ( isEnabled ) {
            mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_UNLOCKED);
            mDrawerToggle.setDrawerIndicatorEnabled(true);
            mDrawerToggle.syncState();

        }
        else {
            mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            mDrawerToggle.setDrawerIndicatorEnabled(false);
            mDrawerToggle.syncState();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(landscape)
            setDrawerState(false);
        else
            setDrawerState(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_settings:
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        this.openDrawer();
    }

    public void openDrawer(){
        mDrawer.openDrawer(Gravity.LEFT);
    }
}