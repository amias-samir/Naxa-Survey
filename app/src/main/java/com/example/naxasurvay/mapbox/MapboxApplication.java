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

//   public static String IMAGES_PATH = Environment.getExternalStorageState()+File.separator+"NaxaSurvey";

    public static String mainFolder = "/NaxaSurvey";
    public static String photoFolder = "/Photos";
    public static String dataFolder = "/Data";
    public static String extSdcard = Environment.getExternalStorageDirectory().toString();

    public static String PHOTO_PATH = extSdcard + mainFolder + photoFolder;

    @Override
    public void onCreate() {
        super.onCreate();

        // Mapbox Access token
        Mapbox.getInstance(getApplicationContext(), "sk.eyJ1IjoicGVhY2VuZXBhbCIsImEiOiJjajZhZDdpbGwxMW0yMnFsc3J6dDhqaTRhIn0.-6QCbw7vfpHyuIcNcXGByg");

//        createODKDirs();


    }


    public static void createFolder() throws Exception {

        Log.i("MapboxApplication", "Trying to create required folders");

        File dirPhoto = new File(extSdcard + mainFolder + photoFolder);
        File dirData = new File(extSdcard + mainFolder + dataFolder);


        if (dirPhoto.mkdirs() && dirData.mkdirs()) {
            Log.i("MapboxApplication", "Directory Created");
        } else {
            throw new Exception("Failed to create required directories");
        }


    }
}
