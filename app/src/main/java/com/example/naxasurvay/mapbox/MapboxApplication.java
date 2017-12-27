package com.example.naxasurvay.mapbox;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.mapbox.mapboxsdk.Mapbox;

import java.io.File;

/**
 * Created by Samir on 8/13/2017.
 */

public class MapboxApplication extends Application {

//   public static String IMAGES_PATH = Environment.getExternalStorageState()+File.separator+"NaxaSurvey";

    public static String mainFolder = "/NaxaSurvey";
    public static String photoFolder = "/Photos";
    public static String dataFolder = "/Data";
    public static String extSdcard = Environment.getExternalStorageDirectory().toString();

    @Override
    public void onCreate() {
        super.onCreate();

        // Mapbox Access token
        Mapbox.getInstance(getApplicationContext(), "sk.eyJ1IjoicGVhY2VuZXBhbCIsImEiOiJjajZhZDdpbGwxMW0yMnFsc3J6dDhqaTRhIn0.-6QCbw7vfpHyuIcNcXGByg");

//        createODKDirs();

        createFolder();
    }

    public static void createODKDirs() throws RuntimeException {

        String IMAGES_PATH = Environment.getExternalStorageState()+File.separator+"NaxaSurvey";

        String cardstatus = Environment.getExternalStorageState();
        if (!cardstatus.equals(Environment.MEDIA_MOUNTED)) {
            throw new RuntimeException("sd card not mounted");
        }


        String[] dirs = {
                IMAGES_PATH
        };


        for (String dirName : dirs) {
            File dir = new File(dirName);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    RuntimeException e =
                            new RuntimeException("ODK reports :: Cannot create directory: "
                                    + dirName);
                    throw e;
                }
            } else {
                if (!dir.isDirectory()) {
                    RuntimeException e =
                            new RuntimeException("ODK reports :: " + dirName
                                    + " exists, but is not a directory");
                    throw e;
                }
            }
        }
    }

    public void createFolder (){

        File dirPhoto = new File(extSdcard+ mainFolder+photoFolder);
        File dirData = new File(extSdcard+ mainFolder+dataFolder);
        try{
            if(dirPhoto.mkdir() && dirData.mkdir() ) {
                System.out.println("Directory created");
                Log.d("APPLICATION_CLASS", "Directory created: "+extSdcard+ mainFolder);

            } else {
                System.out.println("Directory is not created");
                Log.d("APPLICATION_CLASS", "Directory is not created");

            }
        }catch(Exception e){
            e.printStackTrace();
        }

        Log.d("APPLICATION_CLASS", "createFolder: IMAGE_PATH : "+extSdcard+ mainFolder);
    }
}
