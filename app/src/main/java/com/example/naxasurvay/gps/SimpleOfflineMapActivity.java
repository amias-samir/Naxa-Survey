package com.example.naxasurvay.gps;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.example.naxasurvay.Database_Marker;
import com.example.naxasurvay.Mapinfo;
import com.example.naxasurvay.R;
import com.example.naxasurvay.SurveyMain;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;

import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.mapboxsdk.offline.OfflineRegion;
import com.mapbox.mapboxsdk.offline.OfflineRegionError;
import com.mapbox.mapboxsdk.offline.OfflineRegionStatus;
import com.mapbox.mapboxsdk.offline.OfflineTilePyramidRegionDefinition;

import com.mapbox.services.android.location.LostLocationEngine;
import com.mapbox.services.android.navigation.v5.MapboxNavigation;
import com.mapbox.services.android.navigation.v5.listeners.NavigationEventListener;
import com.mapbox.services.android.navigation.v5.offroute.OffRouteListener;
import com.mapbox.services.android.navigation.v5.routeprogress.ProgressChangeListener;
import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress;
import com.mapbox.services.android.telemetry.location.LocationEngine;
import com.mapbox.services.api.directions.v5.models.DirectionsResponse;
import com.mapbox.services.api.directions.v5.models.DirectionsRoute;
import com.mapbox.services.api.utils.turf.TurfConstants;
import com.mapbox.services.api.utils.turf.TurfMeasurement;
import com.mapbox.services.commons.models.Position;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;


public class SimpleOfflineMapActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationEventListener, ProgressChangeListener, OffRouteListener, MapboxMap.OnMarkerClickListener, MapboxMap.OnMapClickListener {
    private final String TAG = this.getClass().getSimpleName();
    @BindView(R.id.mapView)
    MapView mapView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.startRouteButton)
    Button startRouteButton;
    @BindView(R.id.go_to_form)
    Button GoToForm;
    @BindView(R.id.refreshMarker)
    Button RefreshMarker;
    @BindView(R.id.survey_marker)
    Button Survey_marker;

    String code, Housecode, Title;

    private boolean isEndNotified;

    private OfflineManager offlineManager;

    NetworkInfo networkInfo;
    ConnectivityManager connectivityManager;

    String errorHouseCodeList = "" ;


    public static final String JSON_CHARSET = "UTF-8";
    public static final String JSON_FIELD_REGION_NAME = "FIELD_REGION_NAME";
    private OfflineTilePyramidRegionDefinition definition;
    private byte[] metadata;
    private MapboxNavigation navigation;

    ArrayList<String> Location = new ArrayList<>();
    ArrayList<String> Code = new ArrayList<>();
    private MapboxMap mapboxMap;

    FloatingActionButton getMyLocationFAB;

    ProgressDialog mProgressDlg;

    private double routeLat;
    private double routeLon;
    private double sentlat,lon;


//    int totalProgressValue, progressValue;
//    private ProgressBar progress;
//    private TextView updatingText;
//    FrameLayout updatingFrameLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_simple);

        ButterKnife.bind(this);

//        progress = (ProgressBar) findViewById(R.id.progressBar1);
//        updatingText = (TextView) findViewById(R.id.textViewUpdating);
//        updatingFrameLayout = (FrameLayout) findViewById(R.id.updatingLayout);


        Mapbox.getInstance(this, getString(R.string.access_token));
        navigation = new MapboxNavigation(this, getString(R.string.access_token));

        LocationEngine locationEngine = LostLocationEngine.getLocationEngine(this);
        navigation.setLocationEngine(locationEngine);


        //Check internet connection
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();


        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


        try {
            sentlat = getIntent().getDoubleExtra("lat", 0);
            lon = getIntent().getDoubleExtra("lon", 0);





        } catch (NullPointerException e) {
            Log.d("Nishon"," Receiving  "+e.toString());
        }


//        if (networkInfo != null && networkInfo.isConnected()) {
////            mProgressDlg = new ProgressDialog(getApplicationContext());
////            mProgressDlg.setMessage("Please wait...");
////            mProgressDlg.setIndeterminate(false);
////            mProgressDlg.setCancelable(false);
////            mProgressDlg.show();
//            CompletedStatusUpdate completedStatusUpdate = new CompletedStatusUpdate();
//            completedStatusUpdate.execute();
//        }

        //        curent position marker
        getMyLocationFAB = (FloatingActionButton) findViewById(R.id.myLocationButton);
        getMyLocationFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapboxMap.setMyLocationEnabled(true);


                if (mapboxMap.getMyLocation() != null) { // Check to ensure coordinates aren't null, probably a better way of doing this...
//                    map.setCenterCoordinate(new LatLngZoom(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude(), 20), true);

                    Double latitude = mapboxMap.getMyLocation().getLatitude();
                    Double longitude = mapboxMap.getMyLocation().getLongitude();

                    Log.e(TAG, "onClick: SAMIR" + latitude.toString() + "," + longitude.toString());

                    // Load and Draw the GeoJSON. The marker animation is also handled here.
//                    new MapHouseholdActivity.DrawGeoJson().execute();


                    IconFactory iconFactory = IconFactory.getInstance(SimpleOfflineMapActivity.this);
//                    Drawable iconDrawable = ContextCompat.getDrawable(MapHouseholdActivity.this, R.drawable.marker_current);
                    Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.marker_current);
                    Icon icon = iconFactory.fromBitmap(largeIcon);

//                    map.addMarker(new MarkerOptions()
//                            .position(new LatLng(latitude, longitude))
//                            .icon(icon)
//                            .title("Your Current Location"));


                    // Animate camera to geocoder result location
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(latitude, longitude))
                            .zoom(17)
                            .build();
                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 5000, null);

                }
            }
        });



    }


    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        setupMapView(mapboxMap);
        animateMapToCurLocation(mapboxMap);
        setupOfflineMapManager(mapboxMap);
        prepareToDownloadOfflineRegion();


        if (sentlat != 0){
            animateCamera(mapboxMap,sentlat, lon);
        }else {
            animateMapToCurLocation(mapboxMap);
        }

        mapboxMap.setOnMarkerClickListener(this);
        mapboxMap.setOnMapClickListener(this);


        // Load and Draw the GeoJSON. The marker animation is also handled here.
        new DrawGeoJson().execute();

        new Thread(new Runnable() {
            @Override
            public void run() {
                parseArray();
            }
        }).run();


    }


    private void setupMapView(MapboxMap mapboxMap) {
        mapboxMap.setMyLocationEnabled(true);
    }


    private void animateCamera(MapboxMap mapboxMap, Double latitude, Double longitude) {

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude))
                .zoom(17)
                .build();
        this.mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 1000, null);

    }

    private void animateMapToCurLocation(MapboxMap mapboxMap) {




            if (mapboxMap.getMyLocation() == null) {
                showToast("Current location failed to load, restart app to try again");
                return;
            }



        Double latitude = mapboxMap.getMyLocation().getLatitude();
        Double longitude = mapboxMap.getMyLocation().getLongitude();

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude))
                .zoom(17)
                .build();
        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 5000, null);

    }

    private void setupOfflineMapManager(MapboxMap mapboxMap) {
        offlineManager = OfflineManager.getInstance(SimpleOfflineMapActivity.this);
        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                .include(new LatLng(27.7764, 85.5026)) // Northeast
                .include(new LatLng(27.6240, 85.1974)) // Southwest
                .build();

        definition = new OfflineTilePyramidRegionDefinition(
                mapboxMap.getStyleUrl(),
                latLngBounds,
                10,
                20,
                SimpleOfflineMapActivity.this.getResources().getDisplayMetrics().density);


        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(JSON_FIELD_REGION_NAME, "Yosemite National Park");
            String json = jsonObject.toString();
            metadata = json.getBytes(JSON_CHARSET);
        } catch (Exception exception) {
            Log.e(TAG, "Failed to encode metadata: " + exception.getMessage());
            metadata = null;
        }
    }


    private void prepareToDownloadOfflineRegion() {
        if (offlineManager != null) {
            offlineManager.listOfflineRegions(new OfflineManager.ListOfflineRegionsCallback() {
                @Override
                public void onList(OfflineRegion[] offlineRegions) {
                    if (offlineRegions.length == 0) {
                        showToast("Downloading Offline Map");
                        downloadOfflineRegion();

                    } else {
                        showToast("Loading Offline Map ");
                    }
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "onListError: " + error);
                }
            });
        }
    }

    private void downloadOfflineRegion() {
        new Thread(new Runnable() {
            public void run() {

                // Create the region asynchronously
                offlineManager.createOfflineRegion(
                        definition,
                        metadata,
                        new OfflineManager.CreateOfflineRegionCallback() {
                            @Override
                            public void onCreate(OfflineRegion offlineRegion) {
                                offlineRegion.setDownloadState(OfflineRegion.STATE_ACTIVE);

                                // Display the download progress bar
                                progressBar = (ProgressBar) findViewById(R.id.progress_bar);
                                startProgress();

                                // Monitor the download progress using setObserver
                                offlineRegion.setObserver(new OfflineRegion.OfflineRegionObserver() {
                                    @Override
                                    public void onStatusChanged(OfflineRegionStatus status) {

                                        // Calculate the download percentage and update the progress bar
                                        double percentage = status.getRequiredResourceCount() >= 0
                                                ? (100.0 * status.getCompletedResourceCount() / status.getRequiredResourceCount()) :
                                                0.0;

                                        if (status.isComplete()) {
                                            // Download complete
                                            endProgress("Progress success");
                                        } else if (status.isRequiredResourceCountPrecise()) {
                                            // Switch to determinate state
                                            setPercentage((int) Math.round(percentage));
                                        }
                                    }

                                    @Override
                                    public void onError(OfflineRegionError error) {
                                        // If an error occurs, print to logcat
                                        Log.e(TAG, "onError reason: " + error.getReason());
                                        Log.e(TAG, "onError message: " + error.getMessage());

                                        showToast(error.getMessage());
                                        clearCorruptMapTile();

                                    }

                                    @Override
                                    public void mapboxTileCountLimitExceeded(long limit) {
                                        // Notify if offline region exceeds maximum tile count
                                        Log.e(TAG, "Mapbox tile count limit exceeded: " + limit);

                                        showToast("Mapbox tile count limit exceeded");
                                    }
                                });
                            }

                            @Override
                            public void onError(String error) {
                                Log.e(TAG, "Error: " + error);
                            }
                        });
            }
        }).start();
    }


    private void startProgress() {


        isEndNotified = false;
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void setPercentage(final int percentage) {
        progressBar.setIndeterminate(false);
        progressBar.setProgress(percentage);
    }

    private void endProgress(final String message) {
        // Don't notify more than once
        if (isEndNotified) {
            return;
        }

        // Stop and hide the progress bar
        isEndNotified = true;
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.GONE);

        // Show a toast
        Toast.makeText(SimpleOfflineMapActivity.this, message, Toast.LENGTH_LONG).show();
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void calculateRoute(final MapboxMap mapboxMap, Position destination) {
        Location userLocation = mapboxMap.getMyLocation();
        if (userLocation == null) {
            Timber.d("calculateRoute: User location is null, therefore, origin can't be set.");
            return;
        }

        Position origin = Position.fromCoordinates(userLocation.getLongitude(), userLocation.getLatitude());
        if (TurfMeasurement.distance(origin, destination, TurfConstants.UNIT_METERS) < 50) {
            startRouteButton.setVisibility(View.GONE);
            return;
        }


        navigation.getRoute(origin, destination, 90f, new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(
                    Call<DirectionsResponse> call, Response<DirectionsResponse> response) {

                if (response.body() != null) {
                    if (response.body().getRoutes().size() > 0) {
                        DirectionsRoute route = response.body().getRoutes().get(0);
                        showToast("We have route");


                    }
                }

            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {

            }
        });

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


        navigation.removeNavigationEventListener(this);
        navigation.removeProgressChangeListener(this);
        navigation.removeOffRouteListener(this);

        // End the navigation session
        navigation.endNavigation();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
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


    }

    private void clearCorruptMapTile() {
        if (offlineManager != null) {
            offlineManager.listOfflineRegions(new OfflineManager.ListOfflineRegionsCallback() {
                @Override
                public void onList(OfflineRegion[] offlineRegions) {
                    if (offlineRegions.length > 0) {

                        offlineRegions[(offlineRegions.length - 1)].delete(new OfflineRegion.OfflineRegionDeleteCallback() {
                            @Override
                            public void onDelete() {
                                Toast.makeText(
                                        SimpleOfflineMapActivity.this,
                                        "Clearing Corrupt Map Data",
                                        Toast.LENGTH_LONG
                                ).show();
                            }

                            @Override
                            public void onError(String error) {
                                Log.e(TAG, "On Delete error: " + error);
                            }
                        });
                    }
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "onListError: " + error);
                }
            });
        }
    }

    @Override
    public void onRunning(boolean running) {

    }

    @Override
    public void userOffRoute(Location location) {

    }

    @Override
    public void onProgressChange(Location location, RouteProgress routeProgress) {

    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        prepareToRoute(marker);
        return false;
    }

    private void prepareToRoute(Marker marker) {

        RefreshMarker.setVisibility(View.GONE);
        routeLat = marker.getPosition().getLatitude();
        routeLon = marker.getPosition().getLongitude();
        startRouteButton.setVisibility(View.VISIBLE);
        Survey_marker.setVisibility(View.VISIBLE);
        GoToForm.setVisibility(View.VISIBLE);

        Housecode = marker.getTitle();

        Log.d("check code", "codeCheck :" + Housecode);
    }


    @OnClick(R.id.startRouteButton)
    public void routeTwoPointsInGMaps() {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?daddr=" + routeLat + "," + routeLon));
        startActivity(intent);
    }

    @OnClick(R.id.go_to_form)
    public void TakeHouseholdCode() {
        Intent intent1 = new Intent(SimpleOfflineMapActivity.this, SurveyMain.class);
        intent1.putExtra("HouseCode", Housecode);
        startActivity(intent1);
    }

    @OnClick(R.id.survey_marker)
    public void TodaySurveyForm() {
        Database_Marker marker = new Database_Marker(getApplicationContext());
        marker.forSurvey(Housecode);

        Intent intent = new Intent(SimpleOfflineMapActivity.this, SimpleOfflineMapActivity.class);
        intent.putExtra("lat", routeLat);
        intent.putExtra("lon", routeLon);

        Log.d("Nishon"," Sending "+routeLat +" "+routeLon);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    @OnClick(R.id.refreshMarker)
    public void RefreshMarker() {
//        Intent intent1 = new Intent(SimpleOfflineMapActivity.this, SurveyMain.class);
//        intent1.putExtra("HouseCode", Housecode);
//        startActivity(intent1);
        Toast.makeText(this, "Refresh map data", Toast.LENGTH_SHORT).show();

        if (networkInfo != null && networkInfo.isConnected()) {
//
            CompletedStatusUpdate completedStatusUpdate = new CompletedStatusUpdate();
            completedStatusUpdate.execute();
        }else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapClick(@NonNull LatLng point) {
        RefreshMarker.setVisibility(View.VISIBLE);
        startRouteButton.setVisibility(View.GONE);
        Survey_marker.setVisibility(View.GONE);
        GoToForm.setVisibility(View.GONE);
    }


     private class DrawGeoJson extends AsyncTask<Void, Void, List<LatLng>> {
        @Override
        protected List<LatLng> doInBackground(Void... voids) {

            ArrayList<LatLng> points = new ArrayList<>();

            try {
                // Load GeoJSON file
                InputStream inputStream = getAssets().open("household_revised02.geojson");
                Log.e(TAG, "doInBackground pradip: " + inputStream.toString());
                BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
                StringBuilder sb = new StringBuilder();
                Log.e(TAG, "doInBackground:Taman : " + sb);

                int cp;
                while ((cp = rd.read()) != -1) {
                    sb.append((char) cp);
                }

                inputStream.close();
//                Log.e(TAG, "doInBackground: feature0 : " + sb);
                // Parse JSON
                JSONObject json = new JSONObject(sb.toString());

                JSONArray features = json.getJSONArray("features");
                Log.e(TAG, "doInBackground: feature1 " + features);

                JSONObject feature = features.getJSONObject(0);
//                Log.e(TAG, "doInBackground: feature2 : " + sb);
//                Log.e(TAG, "doInBackground: feature3 : " + features.length());
                int counter = 0;

//                JSONObject properties = feature.getJSONObject("properties");
                for (int i = 0; i < features.length(); i++) {
                    JSONObject jobj = features.getJSONObject(i);

                    JSONObject geometry = jobj.getJSONObject("geometry");

                    JSONObject properties = jobj.getJSONObject("properties");

                    Log.e(TAG, "doInBackground: geometry" + geometry.toString());
                    if (geometry != null) {


                        String status = properties.getString("Status");

                        String type = geometry.getString("type");

//                        Log.e(TAG, "Type : " + type);

                        // Our GeoJSON only has one feature: a line string
                        if (!TextUtils.isEmpty(type) && type.equalsIgnoreCase("Point")) {
                            Log.e(TAG, "status : " + status);
                            // Get the Coordinates
                            JSONArray coords = geometry.getJSONArray("coordinates");
//                            for (int lc = 0; lc < coords.length(); lc++) {
//                                JSONArray coord = coords.getJSONArray(lc);
                            LatLng latLng = new LatLng(coords.getDouble(1), coords.getDouble(0));
                            points.add(latLng);

                            String location = properties.getString("location");


                            code = properties.getString("code");


//                            Log.d("SUSAN", "insertIntoMarker 2 : " + code + "\n" +
//                                    coords.getDouble(1) + "\n" + coords.getDouble(0) + "\n" + location);


//                            if (points.size() > 0) {
////                                 Draw polyline on map
////                                 map.addPolyline(new PolylineOptions()
////                                .addAll(points)
////                                .color(Color.parseColor("#d0423b"))
////                                .width(2));
//
//                                for (int j = 0; j< points.size(); j++) {
//
//                                    Log.d("Pradip", "insertIntoMarker 1 : " + Code.get(j));
//                                    Log.d("Pradip", "insertIntoMarker 1 : " + points.get(j).getLatitude());
//                                    Log.d("Pradip", "insertIntoMarker 1 : " + points.get(j).getLongitude());
//                                    Log.d("Pradip", "insertIntoMarker 1 : " + Location.get(j));
//
////                                 updateMap(points.get(i).getLatitude(), points.get(i).getLongitude(), Code.get(i), Location.get(i))
                            Database_Marker marker = new Database_Marker(getApplicationContext());
                            boolean doesDataExist = marker.doesDataExistOrNot(code);

                            if (doesDataExist == false) {
                                marker.insertIntoMarker(code, coords.getDouble(1), coords.getDouble(0), status, location);
                                counter++;
                            }
//                            }

                            Location.add(location);
                            Code.add(code);

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
            Log.d(TAG, "Station:  PRADIP" + points.size());

            if (points.size() > 0) {

                // Draw polyline on map
//                map.addPolyline(new PolylineOptions()
//                        .addAll(points)
//                        .color(Color.parseColor("#d0423b"))
//                        .width(2));

                for (int i = 0; i < points.size(); i++) {


//                    updateMap(points.get(i).getLatitude(), points.get(i).getLongitude(), Code.get(i), Location.get(i));


                }
            }


        }
    }

//    private class DrawGeoJson1 extends AsyncTask<Void, Void, List<LatLng>> {
//        @Override
//        protected List<LatLng> doInBackground(Void... voids) {
//
//            ArrayList<LatLng> points = new ArrayList<>();
//
//            try {
//                // Load GeoJSON file
//                InputStream inputStream = getAssets().open("geojson1.geojson");
//                Log.e(TAG, "doInBackground pradip: " + inputStream.toString());
//                BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
//                StringBuilder sb = new StringBuilder();
//                int cp;
//                while ((cp = rd.read()) != -1) {
//                    sb.append((char) cp);
//                }
//
//                inputStream.close();
//                Log.e(TAG, "doInBackground: feature00 : " + sb);
//                // Parse JSON
//                JSONObject json = new JSONObject(sb.toString());
//                Log.e(TAG, "doInBackground: feature11 : " + json);
//                JSONArray features = json.getJSONArray("features");
//
//                JSONObject feature = features.getJSONObject(0);
//                Log.e(TAG, "doInBackground: feature11 : " + sb);
//                Log.e(TAG, "doInBackground: feature22 : " + features.length());
//                int counter = 0;
//
////                JSONObject properties = feature.getJSONObject("properties");
//                for (int i = 0; i < features.length(); i++) {
//                    JSONObject jobj = features.getJSONObject(i);
//
//                    JSONObject geometry = jobj.getJSONObject("geometry");
//
//                    JSONObject properties = jobj.getJSONObject("properties");
//
//                    Log.e(TAG, "doInBackground: geometry11" + geometry.toString());
//                    Log.e(TAG, "doInBackground: geometry22" + properties.toString());
//                    if (geometry != null) {
//                        Log.e(TAG, "doInBackground: geometry33" + properties.toString());
//
////                        String status = properties.getString("Status");
//
//                        String type = geometry.getString("type");
//
////                        Log.e(TAG, "doInBackground: geometry111" + status);
//                        Log.e(TAG, "doInBackground: geometry222" + type);
//
////                        Log.e(TAG, "Type : " + type);
//
//                        // Our GeoJSON only has one feature: a line string
//                        if (!TextUtils.isEmpty(type) && type.equalsIgnoreCase("Point")) {
////                            Log.e(TAG, "status : " + status);
//                            // Get the Coordinates
//                            JSONArray coords = geometry.getJSONArray("coordinates");
//                            Log.e(TAG, "doInBackground: geometry333" + coords.toString());
//
////                // Parse JSON
////                JSONObject json = new JSONObject(sb.toString());
////                JSONArray features = json.getJSONArray("features");
////
////                JSONObject feature = features.getJSONObject(0);
////                Log.e(TAG, "doInBackground: feature : " + feature.length());
////                Log.e(TAG, "doInBackground: features : " + features.length());
//////                JSONObject properties = feature.getJSONObject("properties");
////                for (int i = 0; i < features.length(); i++) {
////                    JSONObject jobj = features.getJSONObject(i);
////                    JSONObject geometry = jobj.getJSONObject("geometry");
////                    JSONObject properties = jobj.getJSONObject("properties");
////                    Log.e(TAG, "doInBackground: geometry" + geometry.toString());
////                    if (geometry != null) {
////                        String type = geometry.getString("type");
////
////                        // Our GeoJSON only has one feature: a line string
////                        if (!TextUtils.isEmpty(type) && type.equalsIgnoreCase("Point")) {
////
////                            // Get the Coordinates
////                            JSONArray coords = geometry.getJSONArray("coordinates");
////>>>>>>> 7326c2463ce3d9fc8c94af5be621ae2ba8352d18
////                            for (int lc = 0; lc < coords.length(); lc++) {
////                                JSONArray coord = coords.getJSONArray(lc);
//                            LatLng latLng = new LatLng(coords.getDouble(1), coords.getDouble(0));
//                            points.add(latLng);
//                            Log.e(TAG, "doInBackground: geometry444" + latLng.toString());
//                            String location = properties.getString("address");
////                            Log.e(TAG, "doInBackground: geometry444" + location);
//                            Log.e(TAG, "doInBackground: geometry555" + location);
//
//                            code = properties.getString("id");
//                            Log.e(TAG, "doInBackground: geometry666" + code);
//
//                           String Hcode = properties.getString("house_id");
//                            Log.e(TAG, "doInBackground: geometry777" + Hcode);
//
//                            String abc = (code + " or " +Hcode);
//                            Log.e(TAG, "doInBackground: geometry888" + abc);
//                            Log.d("SUSAN", "insertIntoMarker 2 : " + code + "\n" +
//                                    coords.getDouble(1) + "\n" + coords.getDouble(0) + "\n" + location);
//
//
////                            if (points.size() > 0) {
//////                                 Draw polyline on map
//////                                 map.addPolyline(new PolylineOptions()
//////                                .addAll(points)
//////                                .color(Color.parseColor("#d0423b"))
//////                                .width(2));
////
////                                for (int j = 0; j< points.size(); j++) {
////
////                                    Log.d("Pradip", "insertIntoMarker 1 : " + Code.get(j));
////                                    Log.d("Pradip", "insertIntoMarker 1 : " + points.get(j).getLatitude());
////                                    Log.d("Pradip", "insertIntoMarker 1 : " + points.get(j).getLongitude());
////                                    Log.d("Pradip", "insertIntoMarker 1 : " + Location.get(j));
////
//////                                 updateMap(points.get(i).getLatitude(), points.get(i).getLongitude(), Code.get(i), Location.get(i))
//                            Database_Marker marker = new Database_Marker(getApplicationContext());
//                            boolean doesDataExist = marker.doesDataExistOrNot(abc);
//
//                            if (doesDataExist == false) {
//                                marker.insertIntoMarker(abc, coords.getDouble(1), coords.getDouble(0), "0", location);
//                                counter++;
//                            }
////                            }
//
////                            String location = properties.getString("Location");
//                            String code = properties.getString("Code");
//
//                            Location.add(location);
//                            Code.add(code);
//
////                            }
//                        }
//                    }
//                }
//
//
//            } catch (Exception exception) {
//                Log.e(TAG, "Exception Loading GeoJSON: " + exception.toString());
//            }
//
//            return points;
//        }
//
//        @Override
//        protected void onPostExecute(List<LatLng> points) {
//            super.onPostExecute(points);
//            Log.d(TAG, "Station: " + points.toString());
//
//
//            if (points.size() > 0) {
//
//                // Draw polyline on map
////                map.addPolyline(new PolylineOptions()
////                        .addAll(points)
////                        .color(Color.parseColor("#d0423b"))
////                        .width(2));
//
//                for (int i = 0; i < points.size(); i++) {
//
//
//
//                    updateMap(points.get(i).getLatitude(), points.get(i).getLongitude(), Code.get(i), Location.get(i));
//                }
//            }
//
//
//        }
//    }

//    private void updateMap(double latitude, double longitude, String Code, String Location) {
//        // Build marker
//
//        mapboxMap.addMarker(new MarkerOptions()
//                .position(new LatLng(latitude, longitude))
//                .title(Code))
//                .setSnippet(Location);
//
//
//    }

    public void parseArray() {
//        Array database bata tana
        Database_Marker marker = new Database_Marker(getApplicationContext());

        int row = marker.getProfilesCount();
        Log.d("getUnsavedata", "getProfilesCount : " + row);

        List<Mapinfo> points = marker.getUnsavedata();
        Log.d("getUnsavedata", "pointSize : " + points.size());
        Log.d("getUnsavedata", "getUnsavedata : " + points.toString());
        if (points.size() > 0) {
//            Log.d("getUnsavedata", "getSavedata : " + points.toString());
            for (int i = 0; i < points.size(); i++) {
                Mapinfo info = points.get(i);
                String housecode = info.houseCode;
                String status = info.status;
                String placename = info.placeName;
                Double Latitude = info.latitude;
                Double Longitude = info.longitude;

                updateMap(Latitude, Longitude, housecode, placename);
            }
        }

        List<Mapinfo> points1 = marker.getsavedata();
        Log.d("getUnsavedata", "getSavedata : " + points1.toString());
        Log.d("getUnsavedata", "getSavedata : " + points1.toString());
        if (points1.size() > 0) {
//            Log.d("getUnsavedata", "getSavedata : " + points1.toString());
            for (int i = 0; i < points1.size(); i++) {
                Mapinfo info = points1.get(i);
                String housecode = info.houseCode;
                String status = info.status;
                String placename = info.placeName;
                Double Latitude = info.latitude;
                Double Longitude = info.longitude;

                updateSaveMap(Latitude, Longitude, housecode, placename);
            }
        }

        List<Mapinfo> points2 = marker.getsenddata();
        if (points2.size() > 0) {
            Log.d("getUnsavedata", "getsenddata : size " + points2.size());
            for (int i = 0; i < points2.size(); i++) {
                Mapinfo info = points2.get(i);
                String housecode = info.houseCode;
                String status = info.status;
                String placename = info.placeName;
                Double Latitude = info.latitude;
                Double Longitude = info.longitude;

                updateSendMap(Latitude, Longitude, housecode, placename);
            }
        }
        List<Mapinfo> points3 = marker.getSurveyData();
        if (points3.size() > 0) {
//            Log.d("getUnsavedata", "getSavedata : " + points1.toString());
            for (int i = 0; i < points3.size(); i++) {
                Mapinfo info = points3.get(i);
                String housecode = info.houseCode;
                String status = info.status;
                String placename = info.placeName;
                Double Latitude = info.latitude;
                Double Longitude = info.longitude;

                updateSurveyMap(Latitude, Longitude, housecode, placename);
            }
        }
    }

    public void todaylist() {
        Database_Marker marker = new Database_Marker(getApplicationContext());
        List<Mapinfo> points3 = marker.getSurveyData();
        if (points3.size() > 0) {

            for (int i = 0; i < points3.size(); i++) {
                Mapinfo info = points3.get(i);
                String housecode = info.houseCode;
                String status = info.status;
                String placename = info.placeName;
                Double Latitude = info.latitude;
                Double Longitude = info.longitude;

                updateSurveyMap(Latitude, Longitude, housecode, placename);
            }
        }
    }

    private void updateSaveMap(double latitude, double longitude, String Code, String Location) {

        IconFactory iconFactory = IconFactory.getInstance(SimpleOfflineMapActivity.this);
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.marker_saved);
        Icon icon = iconFactory.fromBitmap(largeIcon);

        // Build marker
        mapboxMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title(Code)
                .setSnippet(Location)
                .setIcon(icon));

        // Animate camera to geocoder result location
//        CameraPosition cameraPosition = new CameraPosition.Builder()
//                .target(new LatLng(latitude, longitude))
//                .zoom(15)
//                .build();
//        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 5000, null);
    }

    private void updateSendMap(double latitude, double longitude, String Code, String Location) {

        IconFactory iconFactory = IconFactory.getInstance(SimpleOfflineMapActivity.this);
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.marker_send);
        Icon icon = iconFactory.fromBitmap(largeIcon);

        // Build marker
        mapboxMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title(Code)
                .setSnippet(Location)
                .setIcon(icon));
    }

    private void updateSurveyMap(double latitude, double longitude, String Code, String Location) {

        IconFactory iconFactory = IconFactory.getInstance(SimpleOfflineMapActivity.this);
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.marker_current);
        Icon icon = iconFactory.fromBitmap(largeIcon);

        // Build marker
        mapboxMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title(Code)
                .setSnippet(Location)
                .setIcon(icon));
    }

    private class CompletedStatusUpdate extends AsyncTask<String, Void, String> {
        JSONArray data = null;

        protected String getASCIIContentFromEntity(HttpURLConnection entity)
                throws IllegalStateException, IOException {
            InputStream in = (InputStream) entity.getContent();

            StringBuffer out = new StringBuffer();
            int n = 1;
            while (n > 0) {
                byte[] b = new byte[4096];
                n = in.read(b);

                if (n > 0)
                    out.append(new String(b, 0, n));
            }

            return out.toString();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String text = "";


            if (networkInfo != null && networkInfo.isConnected()) {
                text = POST("http://naxa.com.np/householdsurvey/ApiController/surveyedhouses");




            try
            {
                JSONObject errorUpdateListoj = new JSONObject();


                JSONObject jsonObj = new JSONObject(text);
                data = jsonObj.getJSONArray("data");
                Database_Marker databaseMarker = new Database_Marker(getApplicationContext());
                Log.e("DATA", "" + data.toString());

                Log.e(TAG, "doInBackgroundDATA: "+data.length() );

//                set progressbar marker update
//                updatingFrameLayout.setVisibility(View.VISIBLE);
//                totalProgressValue = data.length();
//                progress.setMax(totalProgressValue);

                Log.d(TAG, "doInBackgroundSAMIR: SAMIR from database : "+databaseMarker.getsenddata().size());
                Log.d(TAG, "doInBackgroundSAMIR: SAMIR from API : "+data.length());

                if(databaseMarker.getsenddata().size() == data.length()){
                    Toast.makeText(SimpleOfflineMapActivity.this, "No new data found", Toast.LENGTH_SHORT).show();
                   finish();

                }


                for (int i = 0; i < data.length(); i++) {

//                    progressValue = i+1;
                    JSONObject c = data.getJSONObject(i);

                    String house_code = c.getString("house_id");
                    Log.e("", "HOUSEHOLD CODE" + house_code);

                    long j = databaseMarker.replaceSend(house_code.toUpperCase());

                    if(j == 0){
                        errorUpdateListoj.put(""+i, house_code);
//                        Log.e(TAG, "doInBackgroundSAMIR: "+house_code );
//                        Log.e(TAG, "doInBackgroundSAMIR: ERROR_LIST : "+errorUpdateListoj.toString() );

                    }
                    

//                    progress.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            updatingText.setText(progressValue +" of "+ totalProgressValue);
//                            progress.setProgress(progressValue);
//                        }
//                    });

                }





            } catch (Exception e) {
                return e.getLocalizedMessage();
            }

            } else {
                Toast.makeText(SimpleOfflineMapActivity.this, "Unable to sync map data", Toast.LENGTH_SHORT).show();

            }



            return text.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub

            if (result != null) {
                
//                if (mProgressDlg.isShowing() && !mProgressDlg.equals(null)) {
//                    mProgressDlg.dismiss();
//                }
                //                hide progress frame
//                try {
//                    if (totalProgressValue == progressValue) {
//                        updatingFrameLayout.setVisibility(View.GONE);
//                    }
//                }catch (Exception e ){
//                }


                Toast.makeText(SimpleOfflineMapActivity.this, "Marker status updated successfully", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(SimpleOfflineMapActivity.this, SimpleOfflineMapActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }

        }

        public String POST(String myurl) {

            URL url;
            String response = "";
            try {
                url = new URL(myurl);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);


                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("", "");
                String query = builder.build().getEncodedQuery();
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                int responseCode = conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                } else {
                    response = "";


                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;
        }

    }

    private void updateMap(double latitude, double longitude, String Code, String Location) {
        // Build marker
        mapboxMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title(Code + "\n" + Location));


    }

}