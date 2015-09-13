package com.jduban.gps;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.widget.Toast;

public class ConstVal extends Application{


    @Override
    public void onCreate() {
        super.onCreate();

        if (isNetworkAvailable(this)){
            Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
        }

        getLocationMode(this);

    }

    public void getLocationMode(Context context){ //TODO : display dialog
        try {
            if (Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE) != Settings.Secure.LOCATION_MODE_HIGH_ACCURACY){
                Toast.makeText(ConstVal.this, "Gps not set on high accuracy", Toast.LENGTH_SHORT).show();

                //startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)); //to be used in MainActivity
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    @SuppressWarnings("deprecation")
    public static void turnGPSOn(Context context)
    {
        Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
        intent.putExtra("enabled", true);
        context.sendBroadcast(intent);

        String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (! provider.contains("gps"))
        { //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            context.sendBroadcast(poke);
        }
    }

    @SuppressWarnings("deprecation")
    public static void turnGPSOff(Context context)
    {
        String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (provider.contains("gps"))
        { //if gps is enabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            context.sendBroadcast(poke);
        }
    }

}
