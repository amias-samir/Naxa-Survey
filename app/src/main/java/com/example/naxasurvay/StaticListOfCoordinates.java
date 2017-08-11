package com.example.naxasurvay;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by RED_DEVIL on 8/7/2017.
 */

public class StaticListOfCoordinates {

    static ArrayList<LatLng> list = new ArrayList<LatLng>();
    public static void setList(ArrayList<LatLng> listToAdd){
        list = listToAdd ;
    }
    public static ArrayList<LatLng> getList(){
        return list ;
    }
}
