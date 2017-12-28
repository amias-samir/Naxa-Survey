package com.example.naxasurvay;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;


import com.example.naxasurvay.gps.SimpleOfflineMapActivity;
import com.example.naxasurvay.mapbox.MapboxApplication;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final int PERMISSION_WRITE = 5654556;
    public static String TAG = "MainActivity";

    Toolbar toolbar;

    @BindView(R.id.survayForm)
    FrameLayout survayform;
    @BindView(R.id.savedForm)
    FrameLayout savedform;
    @BindView(R.id.mapHousehold)
    FrameLayout maphousehold;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_WRITE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    try {
                        MapboxApplication.createFolder();
                    } catch (Exception e) {
                        Default_DIalog.showDefaultDialog(this, "Unexpected Error Occurred", "Failed to create NaxaSurvey Directories, Forms can't be saved ");
                        Log.e(TAG, e.getMessage());
                        e.printStackTrace();
                    }

                    break;
                }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Survey");

        survayform.setOnClickListener(this);
        savedform.setOnClickListener(this);
        maphousehold.setOnClickListener(this);

        if (hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            try {
                MapboxApplication.createFolder();
            } catch (Exception e) {
                Default_DIalog.showDefaultDialog(this, "Unexpected Error Occurred", "Failed to create NaxaSurvey Directories, Forms can't be saved ");
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }

        } else {

            requestPermissionsSafely(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            }, PERMISSION_WRITE);

        }


//        survayform.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this,SurveyMain.class);
//                startActivity(intent);
//            }
//        });
//
//        savedform.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this,SavedFormActivity.class);
//                startActivity(intent);
//            }
//        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.survayForm):
//                Log.e(TAG, "onClick: "+ "card_view_pregnent_women" );



                Intent intent = new Intent(MainActivity.this, SurveyMain.class);
                startActivity(intent);
                break;

            case (R.id.savedForm):
                Intent intent1 = new Intent(MainActivity.this, SavedFormActivity.class);
                startActivity(intent1);

                break;
            case (R.id.mapHousehold):
                Intent intentMap = new Intent(MainActivity.this, SimpleOfflineMapActivity.class);
                startActivity(intentMap);

                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_layout, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            case R.id.action_setting:
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
//                Toast.makeText(this, "Action settings", Toast.LENGTH_SHORT).show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
