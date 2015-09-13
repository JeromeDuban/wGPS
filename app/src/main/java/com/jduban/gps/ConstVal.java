package com.jduban.gps;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.widget.Toast;

public class ConstVal extends Application{


    public static String DISPLAY_POPUP = "DISPLAY_POPUP";

    @Override
    public void onCreate() {
        super.onCreate();

        if (!isNetworkAvailable(this)){
            Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
        }

        getLocationMode(this);

    }

    /**
     * Get GPS location mode
     * @param context Context
     * @return location mode from Settings.Secure
     */
    public static int getLocationMode(Context context){ //TODO : display dialog
        try {
            return Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Check network availability
     * @param context Current context
     * @return network availability
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

}
