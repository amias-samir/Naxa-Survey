package com.example.naxasurvay.mapbox;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.mapbox.mapboxsdk.Mapbox;

import java.io.File;

import timber.log.Timber;

/**
 * Created by Samir on 8/13/2017.
 */

public class MapboxApplication extends Application {


    public static String mainFolder = "/NaxaSurvey";
    public static String photoFolder = "/Photos";
    public static String dataFolder = "/Data";
    public static String extSdcard = Environment.getExternalStorageDirectory().toString();

    public static String PHOTO_PATH = extSdcard + mainFolder + photoFolder;

    @Override
    public void onCreate() {
        super.onCreate();
        Mapbox.getInstance(getApplicationContext(), "sk.eyJ1IjoicGVhY2VuZXBhbCIsImEiOiJjajZhZDdpbGwxMW0yMnFsc3J6dDhqaTRhIn0.-6QCbw7vfpHyuIcNcXGByg");
    }


    public static void createFolder() throws Exception {

        Log.i("MapboxApplication", "Trying to create required folders");

        File dirPhoto = new File(extSdcard + mainFolder + photoFolder);
        File dirData = new File(extSdcard + mainFolder + dataFolder);


        if(!dirPhoto.exists()){
            if (!dirPhoto.mkdirs()){
                throw new Exception("Failed to create photo folder");
            }
        }


        if(!dirData.exists()){
            if (!dirData.mkdirs()){
                throw new Exception("Failed to create database folder");
            }
        }

    }
}
