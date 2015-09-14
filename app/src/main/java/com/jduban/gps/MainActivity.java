package com.jduban.gps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jduban.gps.callback.DialogListener;
import com.jduban.gps.utils.InfoDialog;
import com.jduban.gps.utils.LocationDialog;
import com.jduban.gps.utils.RecyclerAdapter;
import com.karumi.expandableselector.ExpandableItem;
import com.karumi.expandableselector.ExpandableSelector;
import com.karumi.expandableselector.ExpandableSelectorListener;
import com.karumi.expandableselector.OnExpandableItemClickListener;

import java.util.ArrayList;
import java.util.List;

import at.markushi.ui.CircleButton;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, DialogListener{

    private static final long MIN_TIME_UPDATE = 500;
    private static final float MIN_DISTANCE_UPDATE = 1;
    private static final String MAP_TYPE = "MAP TYPE";
    private static final float DEFAULT_ZOOM = 18;
    private static final int REQUEST = 0;

    //    private String mValues[] = {"", "","Location 1","Location 2","Location 3"};
    private ArrayList<String> mValues;

    // Drawer
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    DrawerLayout mDrawer;
    ActionBarDrawerToggle mDrawerToggle;
    GestureDetector mSingleTapDetector;

    private double mLatitude;
    private double mLongitude;
    private boolean isMapReady = false;
    private boolean isGpsEnabled = true;
    private boolean isNetworkEnabled = true;
    private boolean zoomListener = false;
    private boolean landscape;
    private int mMapType = GoogleMap.MAP_TYPE_NORMAL;
    private float mZoomSetting = -1;

    // UI COMPONENTS
    private Toolbar mToolbar;
    private ScrollView mLayoutMenu;
    private MenuFragment mMenuFragment;
    private MapFragment mMapFragment;
    private TextView mConnectivityWarning;
    private TextView mGpsWarning;
    private LocationManager mLocationManager;
    private CircleButton addLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {   // Get Saved map Type (Plan / Satellite)
            mMapType = savedInstanceState.getInt(MAP_TYPE);
        }
        landscape = getResources().getBoolean(R.bool.landscape); // is device in landscape

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);

        mSingleTapDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

        // Testing
        mValues = new ArrayList<>();
        mValues.add("");
        mValues.add("");

        if (ConstVal.locationList != null){
            for (int i = 0 ; i < ConstVal.locationList.size() ; i++){
                mValues.add(ConstVal.locationList.get(i).getName());
            }
        }


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

        mMenuFragment = (MenuFragment ) getFragmentManager().findFragmentById(R.id.fragmentMenu);
        mMenuFragment.setMValues(mValues);

        mConnectivityWarning = (TextView) findViewById(R.id.connectivityWaring);
        mGpsWarning = (TextView) findViewById(R.id.gpsWarning);

        addLocation = (CircleButton) findViewById(R.id.addLocation);
        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLocation();
            }
        });
        // Display the menu fragment if landscape
        mLayoutMenu = (ScrollView) findViewById(R.id.fragmentMenu);

        if(!landscape){
            mLayoutMenu.setVisibility(View.GONE);
        }
        else{
            mLayoutMenu.setVisibility(View.VISIBLE);
            mMenuFragment.displayLocations();
        }

        startLocationListener();

        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
        initializeMapTypeSelector();

        // Start connectivity receiver
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkStateReceiver, filter);

        checkGPS();

    }

    // Drawer menu listener
    RecyclerView.OnItemTouchListener recyclerListener = new RecyclerView.OnItemTouchListener() {
        @Override
        public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
            View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

            if (child != null && mSingleTapDetector.onTouchEvent(motionEvent)) {
                mDrawer.closeDrawers();
                int position = recyclerView.getChildAdapterPosition(child);

                if (position > 2){ // No action for coordinates and accuracy
                    clearMap();
                    com.jduban.gps.objects.Location l  = ConstVal.locationList.get(recyclerView.getChildAdapterPosition(child) - 2 - 1); // coord & accuracy - index shift
                    addMapMarker(Double.parseDouble(l.getLatitude()),Double.parseDouble(l.getLongitude()),l.getName());
                }

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

    /**
     * Listen to GPS locations updates from network and GPS providers
     * Manage GPS signal loss
     * Provide the most accurate location to the app by comparing network and gps locations
     */
    private void startLocationListener(){

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        LocationListener listener = new LocationListener() {

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

                if (LocationManager.GPS_PROVIDER.equals(provider)) isGpsEnabled = true;
                else if (LocationManager.NETWORK_PROVIDER.equals(provider)) isNetworkEnabled = true;

                if (isGpsEnabled || isNetworkEnabled) mGpsWarning.setVisibility(View.GONE);
            }

            @Override
            public void onProviderDisabled(String provider) {

                if (LocationManager.GPS_PROVIDER.equals(provider)) isGpsEnabled = false;
                else if (LocationManager.NETWORK_PROVIDER.equals(provider)) isNetworkEnabled = false;

                if (!isGpsEnabled && !isNetworkEnabled) mGpsWarning.setVisibility(View.VISIBLE);

            }

            @Override
            public void onLocationChanged(Location location) {

                String provider;

                // Compare providers' accuracy
                Location gpsLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Location networkLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (gpsLocation != null && networkLocation!=null){
                    if(gpsLocation.getAccuracy() < networkLocation.getAccuracy()) {
                        provider = LocationManager.GPS_PROVIDER;
                    }
                    else{
                        provider = LocationManager.NETWORK_PROVIDER;
                    }
                    updateCoordinates(provider);
                    updateAccuracy(provider);
                }
                else if (gpsLocation != null){
                    updateCoordinates(LocationManager.GPS_PROVIDER);
                    updateAccuracy(LocationManager.GPS_PROVIDER);
                }
                else if (networkLocation != null){
                    updateCoordinates(LocationManager.NETWORK_PROVIDER);
                    updateAccuracy(LocationManager.NETWORK_PROVIDER);
                }


            }
        };

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_UPDATE, MIN_DISTANCE_UPDATE, listener);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_UPDATE, MIN_DISTANCE_UPDATE, listener);


    }

    /**
     * Enable or disable the drawer menu
     * @param isEnabled true : enable the drawer
     */
    public void setDrawerState(boolean isEnabled) {
        if ( isEnabled ) {
            mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
//            mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_UNLOCKED);
            mDrawerToggle.setDrawerIndicatorEnabled(true);
            mDrawerToggle.syncState();
        }
        else {
            mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//            mDrawerToggle.onDrawerStateChanged(DrawerLayout.STATE_SETTLING);
            mDrawerToggle.setDrawerIndicatorEnabled(false);
            mDrawerToggle.syncState();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(landscape) // disable the drawer in landscape
            setDrawerState(false);
        else
            setDrawerState(true);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkStateReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.manage_locations:
                Intent i = new Intent(MainActivity.this, ManageLocations.class);
                startActivityForResult(i,REQUEST);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        this.openDrawer();
    }

    /**
     * Opens the drawer
     */
    public void openDrawer(){
        mDrawer.openDrawer(GravityCompat.START);
    }

    /**
     * Add a marker on the map at the given lat / long
     * @param latitude latitude of the marker
     * @param longitude longitude of the marker
     */
    public void addMapMarker(double latitude, double longitude, String title) {
        GoogleMap map = mMapFragment.getMap();

        if (map != null && isMapReady) {
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title(title));
        }
    }

    /**
     * Update the accuracy value in the menu (drawer or fragment)
     * @param provider GPS provider
     */
    public void updateAccuracy(String provider){
        GoogleMap map = mMapFragment.getMap();

        if (map != null && isMapReady) {

            int accuracy = Math.round(mLocationManager.getLastKnownLocation(provider).getAccuracy());
            mValues.set(1,Integer.toString(accuracy) +" meters");

            if(!landscape){
                mAdapter.notifyDataSetChanged();
            }else{
                mMenuFragment.setAccuracy(mValues.get(1));
            }
        }
    }

    /**
     * Update coordinates value in the menu (drawer or fragment)
     * and zoom on the user location
     * @param provider GPS provider
     */
    public void updateCoordinates(String provider){
        GoogleMap map = mMapFragment.getMap();

        if (map != null && isMapReady) {

            mLatitude = mLocationManager.getLastKnownLocation(provider).getLatitude();            //FIXME : Can be improved
            mLongitude = mLocationManager.getLastKnownLocation(provider).getLongitude();          // Last known location is not obviously the best

            mValues.set(0,mLatitude + " " + mLongitude); //TODO convert to DMS format

            if (!landscape) {
                mAdapter.notifyDataSetChanged();
            } else {
                mMenuFragment.setCoordinates(mValues.get(0));
            }

            zoomOnUser(mLatitude, mLongitude);
        }
    }

    /**
     * Zoom on a location
     * @param latitude focused latitude
     * @param longitude focused longitude
     */
    public void zoomOnUser(double latitude, double longitude){

        GoogleMap map = mMapFragment.getMap();
        if (map != null && isMapReady){

            float zoom; // If the user changes the zoom, the zoom value will be kept
            if(mZoomSetting != -1)
                zoom = mZoomSetting;
            else
                zoom = DEFAULT_ZOOM; // Default value

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude), zoom);
            map.animateCamera(cameraUpdate, new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    zoomListener = true; // Activate zoom listener so mZoomSetting won't be set to 2 ( min default value)
                }

                @Override
                public void onCancel() {

                }
            });
        }
    }

    /**
     * Set the map type (normal or sattelite
     * @param type map type
     *             can be GoogleMap.MAP_TYPE_NORMAL or GoogleMap.MAP_TYPE_SATELLITE
     */
    private void setmMapType(int type){

        GoogleMap map = mMapFragment.getMap();
        if (map != null && isMapReady){
            switch (type){
                case 1:
                    mMapType = GoogleMap.MAP_TYPE_NORMAL;
                    break;
                case 2:
                    mMapType = GoogleMap.MAP_TYPE_SATELLITE;
                    break;
                default:
                    mMapType = GoogleMap.MAP_TYPE_NORMAL;
                    break;
            }
            map.setMapType(mMapType);
        }
    }

    /**
     * Remove previous markers
     */
    public void clearMap(){
        GoogleMap map = mMapFragment.getMap();
        if (map != null && isMapReady){
            map.clear();
        }

    }

    @Override
    public void onMapReady(GoogleMap map) {
        Log.i("MAP", "Map ready");

        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.setMapType(mMapType);

        isMapReady = true;

        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if(zoomListener)                            // true after user location initialization
                    mZoomSetting = cameraPosition.zoom;
            }
        });
    }
    private void addLocation() {
        GoogleMap map = mMapFragment.getMap();
        if (map != null && isMapReady){
            LocationDialog dialog = new LocationDialog(this,this,  map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude());
            dialog.show();
        }
    }


    /**
     * Display the map type selector
     */
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
                    setmMapType(i);

                iconsExpandableSelector.collapse();
            }
        });

    }

    // Displays a banner if internet connectivity lost
    BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
                ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

                if (!isConnected ) {
                    mConnectivityWarning.setVisibility(View.VISIBLE);
                } else {
                    mConnectivityWarning.setVisibility(View.GONE);
                }
            }
        }
    };

    /**
     * Check GPS status and displays a popup
     */
    private void checkGPS() {
        int value = ConstVal.getLocationMode(this);
        if (value == Settings.Secure.LOCATION_MODE_OFF){
            InfoDialog dialog=new InfoDialog(this, getResources().getString(R.string.enable_GPS), false);
            dialog.show();
        }
        else if( value != Settings.Secure.LOCATION_MODE_HIGH_ACCURACY){ //FIXME && not shared prefs
            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            if (sharedPref.getBoolean(ConstVal.DISPLAY_POPUP,true)){
                InfoDialog dialog=new InfoDialog(this, getResources().getString(R.string.high_precision), true);
                dialog.show();
            }

        }
    }

    /**
     * updates locations in menus
     */
    public void updateLocations(){
        Toast.makeText(MainActivity.this, "Locations updated", Toast.LENGTH_SHORT).show();

        while (mValues.size() > 2){
            mValues.remove(2);
        }
        for (int i = 0 ; i < ConstVal.locationList.size() ; i++){
            mValues.add(ConstVal.locationList.get(i).getName());
        }

        if (landscape){
            mMenuFragment.setMValues(mValues);
            mMenuFragment.displayLocations();
        }
        else{
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("Result Category");

        switch (requestCode) {
            case (REQUEST): {
                if (resultCode == RESULT_OK) {
                    updateLocations();
                }
                break;
            }
        }
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(MAP_TYPE, mMapType); //Saves the selected map type
        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    public void onMarkerAdded() {
        updateLocations();
    }
}

