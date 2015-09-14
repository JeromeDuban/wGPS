package com.jduban.gps;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Toast;

import com.jduban.gps.objects.Location;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ConstVal extends Application{


    public final static String DISPLAY_POPUP = "DISPLAY_POPUP";
    private static String APP_DIRECTORY;
    public static ArrayList<Location> locationList;

    @Override
    public void onCreate() {
        super.onCreate();

        APP_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath(); //TODO to be changed

        if (!isNetworkAvailable(this)){
            Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
        }

        getList(this);

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

    public static void getList(Context context){


        File f = new File(APP_DIRECTORY,"locations.csv");
        String delims = ";";

        if (f.exists()){
            locationList = new ArrayList<>();

            BufferedReader br = null;
            try {
                String sCurrentLine;
                br = new BufferedReader(new FileReader(f));

                while ((sCurrentLine = br.readLine()) != null) {

                    String[] tokens = sCurrentLine.split(delims);
                    if(!tokens[0].equals("") && !tokens[1].equals("")){

                        if (tokens.length == 3) locationList.add(new Location(tokens[0],tokens[1], tokens[2]));
                        else locationList.add(new Location(tokens[0],tokens[1], ""));

                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (br != null)br.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private static void writeList(){


        File f = new File(APP_DIRECTORY,"locations.csv");
        String delims = ";";


        try {
            f.createNewFile();
            FileWriter fw = new FileWriter(f.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            for (int i = 0 ; i < locationList.size() ; i++){
                Location l= locationList.get(i);
                bw.write(l.getLatitude()+delims+l.getLongitude()+delims+l.getName()+"\n");
            }

            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addToList(Location location){
        if (locationList == null) locationList = new ArrayList<>();

        locationList.add(location);
        writeList();
    }

}
