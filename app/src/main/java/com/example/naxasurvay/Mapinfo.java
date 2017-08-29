package com.example.naxasurvay;

/**
 * Created by RED_DEVIL on 8/23/2017.
 */

public class Mapinfo {
  public   String id,houseCode,status,placeName;
    public double latitude,longitude;

  public String toString(){
    return "Housecode : " + houseCode + "\nPlacename : " + placeName + "\nStatus : " + status + "\nID : " + id + "\nLatitude : " + latitude + "\nLongitude : " + longitude;
  }
}
