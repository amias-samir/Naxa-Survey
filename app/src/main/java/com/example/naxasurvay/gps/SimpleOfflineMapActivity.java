package com.example.naxasurvay.gps;

import android.content.Intent;
import android.location.Location;
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

import com.example.naxasurvay.R;
import com.mapbox.mapboxsdk.Mapbox;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

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


    private boolean isEndNotified;

    private OfflineManager offlineManager;


    public static final String JSON_CHARSET = "UTF-8";
    public static final String JSON_FIELD_REGION_NAME = "FIELD_REGION_NAME";
    private OfflineTilePyramidRegionDefinition definition;
    private byte[] metadata;
    private MapboxNavigation navigation;

    ArrayList<String> Location = new ArrayList<>();
    ArrayList<String> Code = new ArrayList<>();
    private MapboxMap mapboxMap;

    private double routeLat;
    private double routeLon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_simple);
        ButterKnife.bind(this);

        Mapbox.getInstance(this, getString(R.string.access_token));
        navigation = new MapboxNavigation(this, getString(R.string.access_token));

        LocationEngine locationEngine = LostLocationEngine.getLocationEngine(this);
        navigation.setLocationEngine(locationEngine);


        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


    }


    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        setupMapView(mapboxMap);
        animateMapToCurLocation(mapboxMap);
        setupOfflineMapManager(mapboxMap);
        prepareToDownloadOfflineRegion();

        mapboxMap.setOnMarkerClickListener(this);
        mapboxMap.setOnMapClickListener(this);


        new DrawGeoJson().execute();
    }


    private void setupMapView(MapboxMap mapboxMap) {
        mapboxMap.setMyLocationEnabled(true);
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

        routeLat = marker.getPosition().getLatitude();
        routeLon = marker.getPosition().getLongitude();
        startRouteButton.setVisibility(View.VISIBLE);
    }


    @OnClick(R.id.startRouteButton)
    public void routeTwoPointsInGMaps() {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?daddr=" + routeLat + "," + routeLon));
        startActivity(intent);
    }

    @Override
    public void onMapClick(@NonNull LatLng point) {
        startRouteButton.setVisibility(View.GONE);
    }


    private class DrawGeoJson extends AsyncTask<Void, Void, List<LatLng>> {
        @Override
        protected List<LatLng> doInBackground(Void... voids) {

            ArrayList<LatLng> points = new ArrayList<>();

            try {
                // Load GeoJSON file
                InputStream inputStream = getAssets().open("geojson_household.geojson");
                Log.e(TAG, "doInBackground: " + inputStream.toString());
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
                Log.e(TAG, "doInBackground: feature : " + feature.length());
                Log.e(TAG, "doInBackground: features : " + features.length());
//                JSONObject properties = feature.getJSONObject("properties");
                for (int i = 0; i < features.length(); i++) {
                    JSONObject jobj = features.getJSONObject(i);
                    JSONObject geometry = jobj.getJSONObject("geometry");
                    JSONObject properties = jobj.getJSONObject("properties");
                    Log.e(TAG, "doInBackground: geometry" + geometry.toString());
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

                            String location = properties.getString("Location");
                            String code = properties.getString("Code");

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

            if (points.size() > 0) {

                // Draw polyline on map
//                map.addPolyline(new PolylineOptions()
//                        .addAll(points)
//                        .color(Color.parseColor("#d0423b"))
//                        .width(2));

                for (int i = 0; i < points.size(); i++) {


                    updateMap(points.get(i).getLatitude(), points.get(i).getLongitude(), Code.get(i), Location.get(i));
                }
            }


        }
    }

    private void updateMap(double latitude, double longitude, String Code, String Location) {
        // Build marker
        mapboxMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title(Code + "\n" + Location));


    }

}