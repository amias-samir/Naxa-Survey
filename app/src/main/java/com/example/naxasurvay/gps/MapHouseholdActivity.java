package com.example.naxasurvay.gps;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.example.naxasurvay.R;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MapHouseholdActivity extends AppCompatActivity {
    private static final String TAG = "MarkerFollowingRoute";

    private MapView mapView;
    private MapboxMap map;
    private Handler handler;
    private Runnable runnable;
    FloatingActionButton getMyLocationFAB;

    private static int count = 0;
    private long distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, getString(R.string.access_token));

        // This contains the MapView in XML and needs to be called after the account manager
        setContentView(R.layout.activity_map_household);


        // Initialize the map view
        mapView = (MapView) findViewById(R.id.mapView);

//        curent position marker
        getMyLocationFAB = (FloatingActionButton) findViewById(R.id.myLocationButton);
        getMyLocationFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.e(TAG, "onClick: SAMIR"+ map.getMyLocation().toString() );

                if (map.getMyLocation() != null) { // Check to ensure coordinates aren't null, probably a better way of doing this...
//                    map.setCenterCoordinate(new LatLngZoom(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude(), 20), true);

                    Double latitude = map.getMyLocation().getLatitude();
                    Double longitude = map.getMyLocation().getLatitude();

                    IconFactory iconFactory = IconFactory.getInstance(MapHouseholdActivity.this);
//                    Drawable iconDrawable = ContextCompat.getDrawable(MapHouseholdActivity.this, R.drawable.marker_current);
                    Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.marker_current);
                    Icon icon = iconFactory.fromBitmap(largeIcon);

                    map.addMarker(new MarkerOptions()
                            .position(new LatLng(latitude, longitude))
                            .icon(icon)
                            .title("Your Current Location"));


                    // Animate camera to geocoder result location
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(latitude, longitude))
                            .zoom(15)
                            .build();
                    map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 5000, null);

                }
            }
        });


        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                map = mapboxMap;

                // Load and Draw the GeoJSON. The marker animation is also handled here.
                new MapHouseholdActivity.DrawGeoJson().execute();

//                updateMap(45.52214,-122.63748);


            }
        });




    } // End onCreate

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        // When the activity is resumed we restart the marker animating.
        if (handler != null && runnable != null) {
            handler.post(runnable);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        // Check if the marker is currently animating and if so, we pause the animation so we aren't
        // using resources when the activities not in view.
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    // We want to load in the GeoJSON file asynchronous so the UI thread isn't handling the file
// loading. The GeoJSON file we are using is stored in the assets folder, you could also get
// this information from the Mapbox map matching API during runtime.
    private class DrawGeoJson extends AsyncTask<Void, Void, List<LatLng>> {
        @Override
        protected List<LatLng> doInBackground(Void... voids) {

            ArrayList<LatLng> points = new ArrayList<>();

            try {
                // Load GeoJSON file
                InputStream inputStream = getAssets().open("all_household_data.geojson");
                Log.e(TAG, "doInBackground: "+inputStream.toString() );
                BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
                StringBuilder sb = new StringBuilder();
                int cp;
                while ((cp = rd.read()) != -1) {
                    sb.append((char) cp);
                }

                inputStream.close();

                // Parse JSON
                JSONObject json = new JSONObject(sb.toString());
                JSONArray features = json.getJSONArray("features");

                JSONObject feature = features.getJSONObject(0);
                Log.e(TAG, "doInBackground: feature : "+feature.length() );
                Log.e(TAG, "doInBackground: features : "+features.length() );
//                JSONObject properties = feature.getJSONObject("properties");
                for (int i = 0; i < features.length(); i++ ) {
                    JSONObject jobj = features.getJSONObject(i);
                    JSONObject geometry = jobj.getJSONObject("geometry");
                    Log.e(TAG, "doInBackground: geometry"+geometry.toString() );
                    if (geometry != null) {
                        String type = geometry.getString("type");

                        // Our GeoJSON only has one feature: a line string
                        if (!TextUtils.isEmpty(type) && type.equalsIgnoreCase("Point")) {

                            // Get the Coordinates
                            JSONArray coords = geometry.getJSONArray("coordinates");
//                            for (int lc = 0; lc < coords.length(); lc++) {
//                                JSONArray coord = coords.getJSONArray(lc);
                                LatLng latLng = new LatLng(coords.getDouble(1), coords.getDouble(0));
                                points.add(latLng);

//                            }
                        }
                    }
                }

            } catch (Exception exception) {
                Log.e(TAG, "Exception Loading GeoJSON: " + exception.toString());
            }

            return points;
        }

        @Override
        protected void onPostExecute(List<LatLng> points) {
            super.onPostExecute(points);
            Log.d(TAG, "Station: " + points.toString());

            if (points.size() > 0) {

                // Draw polyline on map
//                map.addPolyline(new PolylineOptions()
//                        .addAll(points)
//                        .color(Color.parseColor("#d0423b"))
//                        .width(2));

                for (int i = 0; i<points.size(); i++){

                    updateMap(points.get(i).getLatitude(),points.get(i).getLongitude(), i);
                }
            }
        }
    }

    private void updateMap(double latitude, double longitude, int i) {
        // Build marker
        map.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title("Household No."+i));

        // Animate camera to geocoder result location
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude))
                .zoom(15)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 5000, null);
    }






}