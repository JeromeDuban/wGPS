package com.jduban.gps;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jduban.gps.utils.RecyclerAdapter;
import com.karumi.expandableselector.ExpandableItem;
import com.karumi.expandableselector.ExpandableSelector;
import com.karumi.expandableselector.ExpandableSelectorListener;
import com.karumi.expandableselector.OnExpandableItemClickListener;

import java.util.ArrayList;
import java.util.List;

//TODO save Map type
//TODO save user zoom choice

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{

    private String mValues[] = {"", "","Location 1","Location 2","Location 3"};
    private Toolbar mToolbar;
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    DrawerLayout mDrawer;
    ActionBarDrawerToggle mDrawerToggle;

    GestureDetector mSingleTapDetector;

    private double lastLatitude;
    private double lastLongitude;
    private boolean isMapReady = false;
    private boolean landscape;

    // UI COMPONENTS
    private FragmentManager fm;
    private RelativeLayout layoutMap;
    private LinearLayout layoutMenu;
    private MenuFragment menuFragment;

    private TextView coordinatesTextView;
    private TextView accuracyTextView;
    private LinearLayout locationContainer;
    private LayoutInflater inflater;
    private MapFragment mapFragment;
    private TextView connectivityWarning;
    private TextView gpsWarning;

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
        mAdapter = new RecyclerAdapter(mValues);
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

        coordinatesTextView = (TextView) layoutMenu.findViewById(R.id.coordinate).findViewById(R.id.rowText);
        accuracyTextView = (TextView) layoutMenu.findViewById(R.id.accuracy).findViewById(R.id.rowText);
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

            for (int i = 2 ; i < mValues.length ; i++) {

                View view = inflater.inflate(R.layout.location, locationContainer, false); //TODO : improve with a runnable
                ((TextView) view.findViewById(R.id.rowText)).setText(mValues[i]);
                locationContainer.addView(view);

            }
        }

        startLocationListener();

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (!ConstVal.isNetworkAvailable()){
            Toast.makeText(MainActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
        }

        initializeMapTypeSelector();

        connectivityWarning = (TextView) findViewById(R.id.connectivityWaring);
        gpsWarning = (TextView) findViewById(R.id.gpsWarning);

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction("android.location.GPS_ENABLED_CHANGE");
        registerReceiver(networkStateReceiver, filter);

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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 1, new LocationListener() { // TODO : set variables

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                if(LocationManager.GPS_PROVIDER.equals(provider)){
                    gpsWarning.setVisibility(View.GONE);
                }
            }

            @Override
            public void onProviderDisabled(String provider) {
                if(LocationManager.GPS_PROVIDER.equals(provider)){
                    gpsWarning.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onLocationChanged(Location location) {
                Log.d("GPS", "Latitude " + location.getLatitude() + " et longitude " + location.getLongitude());
                lastLatitude = location.getLatitude();
                lastLongitude = location.getLongitude();

                mValues[0] =  lastLatitude + " " + lastLongitude ; //TODO convert to DMS format
                zoomOnUser(lastLatitude,lastLongitude);
                updateAccuracy();

                if(!landscape){
                    mAdapter.notifyDataSetChanged();
                }else{
                    coordinatesTextView.setText(mValues[0]);
                }


            }
        });



    }

    public void setDrawerState(boolean isEnabled) {
        if ( isEnabled ) {
            mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
//            mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_UNLOCKED);
            mDrawerToggle.onDrawerStateChanged(DrawerLayout.STATE_SETTLING);
            mDrawerToggle.setDrawerIndicatorEnabled(true);
            mDrawerToggle.syncState();

        }
        else {
            mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            mDrawerToggle.onDrawerStateChanged(DrawerLayout.STATE_SETTLING);
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
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkStateReceiver);
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
        mDrawer.openDrawer(GravityCompat.START);
    }


    public void addMapMarker(double latitude, double longitude) {
        GoogleMap map = mapFragment.getMap();

        if (map != null && isMapReady) {
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title("Marker"));
        }
    }

    public void updateAccuracy(){
        GoogleMap map = mapFragment.getMap();

        if (map != null && map.getMyLocation() !=null  && isMapReady) {
            int accuracy = Math.round(map.getMyLocation().getAccuracy());
            mValues[1] = Integer.toString(accuracy) +" meters";
            if(!landscape){
                mAdapter.notifyDataSetChanged();
            }else{
                accuracyTextView.setText(mValues[1]);
            }
        }
    }


    public void zoomOnUser(double latitude, double longitude){

        GoogleMap map = mapFragment.getMap();
        if (map != null && isMapReady){

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude), 18);
            map.animateCamera(cameraUpdate);
        }
    }

    public void setMapType(int type){

        GoogleMap map = mapFragment.getMap();
        if (map != null && isMapReady){
            switch (type){
                case 1:
                    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    break;
                case 2:
                    map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    break;
                default:
                    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    break;
            }

        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Log.i("MAP", "Map ready");
        isMapReady = true;

        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
    }

    private void initializeMapTypeSelector() {
        final ExpandableSelector iconsExpandableSelector = (ExpandableSelector) findViewById(R.id.selector);
        List<ExpandableItem> expandableItems = new ArrayList<>();
        ExpandableItem item = new ExpandableItem();
        item.setResourceId(R.mipmap.ic_expand);
        expandableItems.add(item);
        item = new ExpandableItem();
        item.setResourceId(R.mipmap.ic_plan);
        expandableItems.add(item);
        item = new ExpandableItem();
        item.setResourceId(R.mipmap.ic_satellite);
        expandableItems.add(item);
        iconsExpandableSelector.showExpandableItems(expandableItems);

        iconsExpandableSelector.setExpandableSelectorListener(new ExpandableSelectorListener() {
            @Override
            public void onCollapse() {
                ExpandableItem item = new ExpandableItem();
                item.setResourceId(R.mipmap.ic_expand);
                iconsExpandableSelector.updateExpandableItem(0,item);
            }

            @Override
            public void onExpand() {
                ExpandableItem item = new ExpandableItem();
                item.setResourceId(R.mipmap.ic_collapse);
                iconsExpandableSelector.updateExpandableItem(0,item);
            }

            @Override
            public void onCollapsed() {

            }

            @Override
            public void onExpanded() {

            }
        });

        iconsExpandableSelector.setOnExpandableItemClickListener(new OnExpandableItemClickListener() {
            @Override
            public void onExpandableItemClickListener(int i, View view) {
                if (i !=0)
                    setMapType(i);

                iconsExpandableSelector.collapse();
            }
        });

    }


    BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
                ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

                if (!isConnected ) {        //TODO Add animation
                    connectivityWarning.setVisibility(View.VISIBLE);
                } else {
                    connectivityWarning.setVisibility(View.GONE);
                }
            }
        }
    };

}
