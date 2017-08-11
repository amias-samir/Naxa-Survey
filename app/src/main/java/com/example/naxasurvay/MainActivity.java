package com.example.naxasurvay;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.example.naxasurvay.gps.MapHouseholdActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static String TAG = "MainActivity";

    Toolbar toolbar ;

    @BindView(R.id.survayForm)FrameLayout survayform;
    @BindView(R.id.savedForm)FrameLayout savedform;
    @BindView(R.id.mapHousehold)FrameLayout maphousehold;

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
                Intent intentMap = new Intent(MainActivity.this, MapHouseholdActivity.class);
                startActivity(intentMap);

                break;
        }
    }
}
