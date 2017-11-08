package com.example.naxasurvay;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import android.widget.CheckBox;
import android.widget.CompoundButton;

import android.widget.DatePicker;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.naxasurvay.easy_gps.GeoPointActivity;
import com.example.naxasurvay.gps.GPS_TRACKER_FOR_POINT;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.thomashaertel.widget.MultiSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;

import static android.R.attr.cacheColorHint;
import static android.R.attr.id;
import static android.R.attr.visible;

public class SurveyMain extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "naxa_Survay";
    ProgressDialog mProgressDlg;
    int CAMERA_PIC_REQUEST = 2;
    String imagePath, encodedImage = "", imageName = "no_photo", image;
    Bitmap thumbnail;

    GPS_TRACKER_FOR_POINT gps;
    List<Location> gpslocation = new ArrayList<>();
    double finalLat;
    double finalLong;
    boolean isGpsTracking = false;
    boolean isGpsTaken = false;
    boolean send = false;


    private int year, month, day;

    public static final int GEOPOINT_RESULT_CODE = 1994;
    public static final String LOCATION_RESULT = "LOCATION_RESULT";


    NetworkInfo networkInfo;
    ConnectivityManager connectivityManager;

    String latLangArray = "", jsonLatLangArray = "";
    String a, b, c, d, e;


    static final Integer LOCATION = 0x1;
    static final Integer GPS_SETTINGS = 0x8;

    private ArrayAdapter<String> adapter;
    Context context = this;


    @BindView(R.id.id_number_surveyor)
    EditText surveyorId;
    @BindView(R.id.name_of_survayor)
    AutoCompleteTextView NameOfSurveyor;
    @BindView(R.id.date_of_survey)
    AutoCompleteTextView DateOfSurvey;
//    @BindView(R.id.Survey_date)DatePicker SurveyDate;

    @BindView(R.id.id_code)
    EditText HouseHoldId;
    @BindView(R.id.Survay_district)
    Spinner District;
    @BindView(R.id.Survay_municipality)
    Spinner Municipality;
    @BindView(R.id.Survay_ward)
    AutoCompleteTextView Ward;
    @BindView(R.id.Survay_address)
    AutoCompleteTextView Address;
//    @BindView(R.id.HouseHold_type)
//    RadioGroup HouseholdTypology;

    @BindView(R.id.single_family_detached)
    CheckBox SingleFamilyDetached;
    @BindView(R.id.multy_family_house)
    CheckBox MultyFamilyhouse;
    @BindView(R.id.apartment_block)
    CheckBox ApartmentBlock;
    @BindView(R.id.mixed_use_block)
    CheckBox MixedUseBlock;
    @BindView(R.id.number_of_floors)
    CheckBox NumberOfFloors;

    @BindView(R.id.house_GpsStart)
    Button startGps;

    @BindView(R.id.house_photo_site)
    ImageButton photo;

    @BindView(R.id.house_PhotographSiteimageViewPreview)
    ImageView previewImageSite;

    @BindView(R.id.Respondent_age)
    AutoCompleteTextView Age;
    @BindView(R.id.sex)
    RadioGroup Sex;
    @BindView(R.id.Respondent_email)
    AutoCompleteTextView Email;

//    @BindView(R.id.Survay_Total_family_member)
//    AutoCompleteTextView FamilyMemberNumber;

    @BindView(R.id.button_inc_husband)
    Button HusbandInc;
    @BindView(R.id.button_inc_wife)
    Button WifeInc;
    @BindView(R.id.button_inc_childrens)
    Button ChildrenInc;
    @BindView(R.id.button_inc_relatives)
    Button RelativesInc;
    @BindView(R.id.button_inc_others)
    Button OthersInc;
    @BindView(R.id.button_dec_husband)
    Button HusbandDec;
    @BindView(R.id.button_dec_wife)
    Button WifeDec;
    @BindView(R.id.button_dec_childrens)
    Button ChildrenDec;
    @BindView(R.id.button_dec_relatives)
    Button RelativeDec;
    @BindView(R.id.button_dec_others)
    Button OthersDec;
    @BindView(R.id.husband_no)
    EditText NumOfHusband;
    @BindView(R.id.wife_no)
    EditText NumOfWife;
    @BindView(R.id.children_no)
    EditText NumOfChildren;
    @BindView(R.id.relatives_no)
    EditText NumOfRelatives;
    @BindView(R.id.others_no)
    EditText NumOfothers;
    @BindView(R.id.Survay_Total_family_member)
    AutoCompleteTextView TotalFamilyMemberNumber;


    @BindView(R.id.Working_button_inc_husband)
    Button WorkingHusbandInc;
    @BindView(R.id.Working_button_inc_wife)
    Button WorkingWifeInc;
    @BindView(R.id.Working_button_inc_children)
    Button WorkingChildrenInc;
    @BindView(R.id.Working_button_inc_relatives)
    Button WorkingRelativesInc;
    @BindView(R.id.Working_button_inc_others)
    Button WorkingOthersInc;
    @BindView(R.id.Working_button_dec_husband)
    Button WorkingHusbandDec;
    @BindView(R.id.Working_button_dec_wife)
    Button WorkingWifeDec;
    @BindView(R.id.Working_button_dec_childrens)
    Button WorkingChildrenDec;
    @BindView(R.id.Working_button_dec_relatives)
    Button WorkingRelativeDec;
    @BindView(R.id.Working_button_dec_others)
    Button WorkingOthersDec;
    @BindView(R.id.Working_husband_no)
    EditText WorkingNumOfHusband;
    @BindView(R.id.Working_wife_no)
    EditText WorkingNumOfWife;
    @BindView(R.id.Working_childrens_no)
    EditText WorkingNumOfChildren;
    @BindView(R.id.Working_relatives_no)
    EditText WorkingNumOfRelatives;
    @BindView(R.id.Working_others_no)
    EditText WorkingNumOfothers;
    @BindView(R.id.Survay_Workin_family_member)
    AutoCompleteTextView WorkingFamilyMemberNumber;

    @BindView(R.id.spinnerMulti_Husband)
    MultiSpinner spinnerHusband;
    @BindView(R.id.selected_income_details_husband)
    EditText HusbandIncomeDetail;
    @BindView(R.id.selected_others_income_details_husband)
    EditText HusbandOthersIncomeDetail;
    @BindView(R.id.spinnerMulti_Wife)
    MultiSpinner spinnerWife;
    @BindView(R.id.selected_income_details_wife)
    EditText WifeIncomeDetail;
    @BindView(R.id.selected_others_income_details_wife)
    EditText WifeOthersIncomeDetail;
    @BindView(R.id.spinnerMulti_children)
    MultiSpinner spinnerChildren;
    @BindView(R.id.selected_income_details_children)
    EditText ChildrenIncomeDetail;
    @BindView(R.id.selected_others_income_details_children)
    EditText ChildrenOthersIncomeDetail;
    @BindView(R.id.spinnerMulti_Relative)
    MultiSpinner spinnerRelatives;
    @BindView(R.id.selected_income_details_relatives)
    EditText RelativesIncomeDetail;
    @BindView(R.id.selected_others_income_details_relatives)
    EditText RelativesOthersIncomeDetail;
    @BindView(R.id.spinnerMulti_Others)
    MultiSpinner spinnerOthers;
    @BindView(R.id.selected_income_details_others)
    EditText OthersIncomeDetail;
    @BindView(R.id.selected_others_income_details_others)
    EditText OthersOthersIncomeDetail;

    @BindView(R.id.selected_farm_income_details_husband)
    EditText HusbandFarmIncome;
    @BindView(R.id.selected_farm_income_details_wife)
    EditText WifeFarmIncome;
    @BindView(R.id.selected_farm_income_details_children)
    EditText ChildrenFarmIncome;
    @BindView(R.id.selected_farm_income_details_relatives)
    EditText RelativesFarmIncome;
    @BindView(R.id.selected_farm_income_details_others)
    EditText OthersFarmIncome;

    @BindView(R.id.Survay_average_monthlyIncome_of_husband)
    AutoCompleteTextView AverageMonthlyIncomeOfHusband;
    @BindView(R.id.husband_price_type)
    Spinner Husband_income_type;
    @BindView(R.id.Survay_average_monthlyIncome_of_wife)
    AutoCompleteTextView AverageMonthlyIncomeOfWife;
    @BindView(R.id.wife_price_type)
    Spinner Wife_income_type;
    @BindView(R.id.Survay_average_monthlyIncome_of_children)
    AutoCompleteTextView AverageMonthlyIncomeOfChildren;
    @BindView(R.id.children_price_type)
    Spinner Children_income_type;
    @BindView(R.id.Survay_average_monthlyIncome_of_relatives)
    AutoCompleteTextView AverageMonthlyIncomeOfRelatives;
    @BindView(R.id.relatives_price_type)
    Spinner Relatives_income_type;
    @BindView(R.id.Survay_average_monthlyIncome_of_others)
    AutoCompleteTextView AverageMonthlyIncomeOfOthers;
    @BindView(R.id.others_price_type)
    Spinner Others_income_type;

    @BindView(R.id.land_in_Anna)
    AutoCompleteTextView LandInAnna;
    @BindView(R.id.land_area_spinner)
    Spinner land_area_spinner;

    @BindView(R.id.total_land_price)
    AutoCompleteTextView TotalLandPrice;
    @BindView(R.id.land_price_spinner)
    Spinner land_price_spinner;

    @BindView(R.id.property_in_Anna)
    AutoCompleteTextView PropertyInAnna;
    @BindView(R.id.property_area_spinner)
    Spinner property_area_spinner;

    @BindView(R.id.total_property_price)
    AutoCompleteTextView TotalPropertyPrice;
    @BindView(R.id.property_price_spinner)
    Spinner property_price_spinner;

    @BindView(R.id.land_pooling_house)
    CheckBox poolingHouse;
    @BindView(R.id.land_pooling_land)
    CheckBox poolingland;
    @BindView(R.id.pooling_yearof_purchase)
    AutoCompleteTextView yearOfPurchase;
    @BindView(R.id.pooling_valueof_purchase)
    AutoCompleteTextView valueOfPurchase;
    @BindView(R.id.pooling_price_spinner)
    Spinner Pooling_price_spinner;

    String yearOfPurchaseValue, valueOfPurchaseValue, PoolingPriceType, HouseCode;

    SharedPreferences preferences;

    @BindView(R.id.Naxa_survay_save)
    Button Save;
    @BindView(R.id.Naxa_survay_send)
    Button Send;
    @BindView(R.id.cv_SaveSend)
    CardView cv_Send_Save;

    int Addnumber, Subnumber;
    String formid, formNameSavedForm = "";
    String jsonToSend;
    Toolbar toolbar;
    JSONArray jsonArrayGPS = new JSONArray();
    ArrayList<LatLng> listCf = new ArrayList<LatLng>();
    StringBuilder stringBuilder = new StringBuilder();

    String SurveyIdNumValue, NameOfSurveyorValue, DateOfSurveyValue, HouseHoldIdValue, DistrictValue, MunicipalityValue, WardValue, AddressValue,
            HouseholdTypologyValue = "", AgeValue, SexValue, EmailValue, WorkingFamilyMemberNumberValue, NumOfHusbandValue, NumOfWifeValue,
            NumOfChildrenValue, NumOfRelativesValue, NumOfothersValue, HusbandIncomeSource, WifeIncomeSource, ChildrenIncomeSource,
            RelativesIncomeSource, OthersIncomeSource, AverageMonthlyIncomeOfHusbandValue, AverageMonthlyIncomeOfWifeValue,
            AverageMonthlyIncomeOfChildrenValue, AverageMonthlyIncomeOfRelativesValue, AverageMonthlyIncomeOfOthersValue,
            LandInAnnaValue, TotalLandPriceValue, PropertyInAnnaValue, TotalPropertyPriceValue;

    String dataSentStatus, dateString;
    String HusbandIncomeOtherSource, WifeIncomeOtherSource, ChildrensIncomeOtherSource, RelativesIncomeOtherSource, OthersIncomeOtherSource,
            WorkingNumOfHusbandValue, WorkingNumOfWifeValue, WorkingNumOfChildrenValue, WorkingNumOfRelativesValue,
            WorkingNumOfothersValue, FamilyMemberNumberValue;

    String HusbandIncometype, WifeIncomeType, CHildrenIncomeType, RelativesIncomeTypes, OthersIncomeTypes, LandAreaType, PropertyAreaType,
            LandPriceType, PropertyPriceType;

    String HusbandFarmIncomeValue, WifeFarmIncomeValue, ChildrenFarmIncomeValue, RelativesFarmIncomeValue, OthersFarmIncomeValue;

    GoogleApiClient client;
    LocationRequest mLocationRequest;
    PendingResult<LocationSettingsResult> result;


//    ------------------------------------ spinner onitem selected listner --------------------------------------------------

    @OnItemSelected(R.id.pooling_price_spinner)
    public void priceSpinneListner() {
        String values = Pooling_price_spinner.getSelectedItem().toString();


//        valueOfPurchase.setText(currencyChanger(values, valueOfPurchase.getText().toString()));

    }

//    ------------------------------------ end of spinner onitem selected listner --------------------------------------------------


    @OnClick({R.id.button_inc_husband, R.id.button_inc_wife, R.id.button_inc_childrens, R.id.button_inc_relatives, R.id.button_inc_others})
    public void checkboxListner(View view) {
        switch (view.getId()) {
            case R.id.button_inc_husband:

                int husband_no = Integer.parseInt(NumOfHusband.getText().toString());
                Log.d("", "Addnumber: " + husband_no);
//                Add(husband_no);
                NumOfHusband.setText(Add(husband_no) + "");

                break;


            case R.id.button_inc_wife:

                int wife_no = Integer.parseInt(NumOfWife.getText().toString());
//                Add(wife_no);
                NumOfWife.setText(Add(wife_no) + "");

                break;
            case R.id.button_inc_childrens:

                int children_no = Integer.parseInt(NumOfChildren.getText().toString());
//                Add(children_no);
                NumOfChildren.setText(Add(children_no) + "");

                break;
            case R.id.button_inc_relatives:

                int relative_no = Integer.parseInt(NumOfRelatives.getText().toString());
//                Add(relative_no);
                NumOfRelatives.setText(Add(relative_no) + "");

                break;
            case R.id.button_inc_others:

                int other_no = Integer.parseInt(NumOfothers.getText().toString());
//                Add(other_no);
                NumOfothers.setText(Add(other_no) + "");

                break;


        }
    }

    @OnClick({R.id.button_dec_husband, R.id.button_dec_wife, R.id.button_dec_childrens, R.id.button_dec_relatives, R.id.button_dec_others})
    public void checkboxListner1(View view) {
        switch (view.getId()) {
            case R.id.button_dec_husband:

                int husband_no = Integer.parseInt(NumOfHusband.getText().toString());
                Log.d("", "Addnumber: " + husband_no);
//                Add(husband_no);
                NumOfHusband.setText(Sub(husband_no) + "");

                break;


            case R.id.button_dec_wife:

                int wife_no = Integer.parseInt(NumOfWife.getText().toString());
//                Add(wife_no);
                NumOfWife.setText(Sub(wife_no) + "");

                break;
            case R.id.button_dec_childrens:

                int children_no = Integer.parseInt(NumOfChildren.getText().toString());
//                Add(children_no);
                NumOfChildren.setText(Sub(children_no) + "");

                break;
            case R.id.button_dec_relatives:

                int relative_no = Integer.parseInt(NumOfRelatives.getText().toString());
//                Add(relative_no);
                NumOfRelatives.setText(Sub(relative_no) + "");

                break;
            case R.id.button_dec_others:

                int other_no = Integer.parseInt(NumOfothers.getText().toString());
//                Add(other_no);
                NumOfothers.setText(Sub(other_no) + "");

                break;


        }
    }

    @OnClick({R.id.Working_button_inc_husband, R.id.Working_button_inc_wife, R.id.Working_button_inc_children, R.id.Working_button_inc_relatives, R.id.Working_button_inc_others})
    public void family(View view) {
        switch (view.getId()) {
            case R.id.Working_button_inc_husband:
                int working_husband_no = Integer.parseInt(WorkingNumOfHusband.getText().toString());
//                Add(working_husband_no);
                WorkingNumOfHusband.setText(Add(working_husband_no) + "");

                break;


            case R.id.Working_button_inc_wife:
                int working_wife_no = Integer.parseInt(WorkingNumOfWife.getText().toString());
//                Add(working_wife_no);
                WorkingNumOfWife.setText(Add(working_wife_no) + "");

                break;

            case R.id.Working_button_inc_children:
                int working_children_no = Integer.parseInt(WorkingNumOfChildren.getText().toString());
//                Add(working_children_no);
                WorkingNumOfChildren.setText(Add(working_children_no) + "");

                break;

            case R.id.Working_button_inc_relatives:
                int working_relative_no = Integer.parseInt(WorkingNumOfRelatives.getText().toString());
//                Add(working_relative_no);
                WorkingNumOfRelatives.setText(Add(working_relative_no) + "");

                break;

            case R.id.Working_button_inc_others:
                int working_other_no = Integer.parseInt(WorkingNumOfothers.getText().toString());
//                Add(working_other_no);
                WorkingNumOfothers.setText(Add(working_other_no) + "");
                break;

        }
    }

    @OnClick({R.id.Working_button_dec_husband, R.id.Working_button_dec_wife, R.id.Working_button_dec_childrens, R.id.Working_button_dec_relatives, R.id.Working_button_dec_others})
    public void family1(View view) {
        switch (view.getId()) {
            case R.id.Working_button_dec_husband:
                int working_husband_no = Integer.parseInt(WorkingNumOfHusband.getText().toString());
//                Add(working_husband_no);
                WorkingNumOfHusband.setText(Sub(working_husband_no) + "");

                break;


            case R.id.Working_button_dec_wife:
                int working_wife_no = Integer.parseInt(WorkingNumOfWife.getText().toString());
//                Add(working_wife_no);
                WorkingNumOfWife.setText(Sub(working_wife_no) + "");

                break;

            case R.id.Working_button_dec_childrens:
                int working_children_no = Integer.parseInt(WorkingNumOfChildren.getText().toString());
//                Add(working_children_no);
                WorkingNumOfChildren.setText(Sub(working_children_no) + "");

                break;

            case R.id.Working_button_dec_relatives:
                int working_relative_no = Integer.parseInt(WorkingNumOfRelatives.getText().toString());
//                Add(working_relative_no);
                WorkingNumOfRelatives.setText(Sub(working_relative_no) + "");

                break;

            case R.id.Working_button_dec_others:
                int working_other_no = Integer.parseInt(WorkingNumOfothers.getText().toString());
//                Add(working_other_no);
                WorkingNumOfothers.setText(Sub(working_other_no) + "");
                break;

        }
    }

    DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.survey_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Household Survey");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        HouseCode = getIntent().getStringExtra("HouseCode");

        SingleFamilyDetached.setOnCheckedChangeListener(this);
        MultyFamilyhouse.setOnCheckedChangeListener(this);
        ApartmentBlock.setOnCheckedChangeListener(this);
        MixedUseBlock.setOnCheckedChangeListener(this);
        NumberOfFloors.setOnCheckedChangeListener(this);

        poolingHouse.setOnCheckedChangeListener(this);
        poolingland.setOnCheckedChangeListener(this);


        preferences = getSharedPreferences("userinfo", 0);
        String registeredSurveyorId = preferences.getString("Surveyor_Id", "");
        String registeredSurveyorName = preferences.getString("Surveyor_Name", "");


        surveyorId.setText(registeredSurveyorId);
        NameOfSurveyor.setText(registeredSurveyorName);
        HouseHoldId.setText(HouseCode);


//        fillarray();
        setCurrentDateOnView();
        addListenerOnButton();


//        new DatePickerDialog(this, datePickerListener, year, month,day);

//        DateOfSurvey.setOnClickListener(new View.OnClickListener() {
//
//            //            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//            @Override
//            public void onClick(View v) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    DateOfSurvey.setShowSoftInputOnFocus(false);
//                }
////                showDialog(DATE_DIALOG_ID);
//                datePickerDialog = new DatePickerDialog(this, datePickerListener, year, month, day);
//            }
//
//        });


        //        multispinne spinner Income source
        // create spinner list elements
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapter.add("Farm");
        adapter.add("Wage");
        adapter.add("Rent");
        adapter.add("Transfer");
        adapter.add("Value");
        adapter.add("N.Farm Entrep.");
        adapter.add("Other");

        // get spinner and set adapter

        spinnerHusband.setAdapter(adapter, false, onSelectedListener);
        spinnerWife.setAdapter(adapter, false, onSelectedListener1);
        spinnerChildren.setAdapter(adapter, false, onSelectedListener2);
        spinnerRelatives.setAdapter(adapter, false, onSelectedListener3);
        spinnerOthers.setAdapter(adapter, false, onSelectedListener4);

//        // set initial selection
//        boolean[] selectedItems = new boolean[adapter.getCount()];
////        selectedItems[1] = true; // select second item
//        spinnerHusband.setSelected(selectedItems);

        client = new GoogleApiClient.Builder(this)
                .addApi(AppIndex.API)
                .addApi(LocationServices.API)
                .build();
        askForPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION);

        //Check internet connection
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();


        initilizeUI();

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dispatchTakePictureIntent();
            }
        });

        startGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent toGeoPointActivity = new Intent(SurveyMain.this, GeoPointActivity.class);
                startActivityForResult(toGeoPointActivity, GEOPOINT_RESULT_CODE);
            }
        });

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isGpsTaken) {

//                    image = encodedImage;
                    image = mCurrentPhotoPath;

                    if (image != null && !image.isEmpty()) {

                        SurveyIdNumValue = surveyorId.getText().toString();
                        NameOfSurveyorValue = NameOfSurveyor.getText().toString();
                        DateOfSurveyValue = DateOfSurvey.getText().toString();

                        HouseHoldIdValue = HouseHoldId.getText().toString();
                        //                              MunicipalityValue = Municipality.getText().toString();
                        WardValue = Ward.getText().toString();
                        AddressValue = Address.getText().toString();


                        if (SurveyIdNumValue != null && !SurveyIdNumValue.isEmpty() && NameOfSurveyorValue != null && !NameOfSurveyorValue.isEmpty() && HouseHoldIdValue != null && !HouseHoldIdValue.isEmpty() && WardValue != null && !WardValue.isEmpty() && AddressValue != null && !AddressValue.isEmpty()) {

//                            HouseholdTypologyValue = H.getText().toString();

                            AgeValue = Age.getText().toString();
//                                String SexValue = Sex.getText().toString();

                            RadioButton checkedBtn = (RadioButton) findViewById(Sex.getCheckedRadioButtonId());
                            SexValue = checkedBtn.getText().toString();

                            Log.d("", "onCreate: " + SexValue);
                            EmailValue = Email.getText().toString();

                            NumOfHusbandValue = NumOfHusband.getText().toString();
                            NumOfWifeValue = NumOfWife.getText().toString();
                            NumOfChildrenValue = NumOfChildren.getText().toString();
                            NumOfRelativesValue = NumOfRelatives.getText().toString();
                            NumOfothersValue = NumOfothers.getText().toString();
                            FamilyMemberNumberValue = TotalFamilyMemberNumber.getText().toString();

                            WorkingNumOfHusbandValue = WorkingNumOfHusband.getText().toString();
                            WorkingNumOfWifeValue = WorkingNumOfWife.getText().toString();
                            WorkingNumOfChildrenValue = WorkingNumOfChildren.getText().toString();
                            WorkingNumOfRelativesValue = WorkingNumOfRelatives.getText().toString();
                            WorkingNumOfothersValue = WorkingNumOfothers.getText().toString();
                            WorkingFamilyMemberNumberValue = WorkingFamilyMemberNumber.getText().toString();

                            HusbandIncomeSource = HusbandIncomeDetail.getText().toString();
                            HusbandIncomeOtherSource = HusbandOthersIncomeDetail.getText().toString();
                            WifeIncomeSource = WifeIncomeDetail.getText().toString();
                            WifeIncomeOtherSource = WifeOthersIncomeDetail.getText().toString();
                            ChildrenIncomeSource = ChildrenIncomeDetail.getText().toString();
                            ChildrensIncomeOtherSource = ChildrenOthersIncomeDetail.getText().toString();
                            RelativesIncomeSource = RelativesIncomeDetail.getText().toString();
                            RelativesIncomeOtherSource = RelativesOthersIncomeDetail.getText().toString();
                            OthersIncomeSource = OthersIncomeDetail.getText().toString();
                            OthersIncomeOtherSource = OthersOthersIncomeDetail.getText().toString();

                            AverageMonthlyIncomeOfHusbandValue = AverageMonthlyIncomeOfHusband.getText().toString();
                            AverageMonthlyIncomeOfWifeValue = AverageMonthlyIncomeOfWife.getText().toString();
                            AverageMonthlyIncomeOfChildrenValue = AverageMonthlyIncomeOfChildren.getText().toString();
                            AverageMonthlyIncomeOfRelativesValue = AverageMonthlyIncomeOfRelatives.getText().toString();
                            AverageMonthlyIncomeOfOthersValue = AverageMonthlyIncomeOfOthers.getText().toString();

                            HusbandFarmIncomeValue = HusbandFarmIncome.getText().toString();
                            WifeFarmIncomeValue = WifeFarmIncome.getText().toString();
                            ChildrenFarmIncomeValue = ChildrenFarmIncome.getText().toString();
                            RelativesFarmIncomeValue = RelativesFarmIncome.getText().toString();
                            OthersFarmIncomeValue = OthersFarmIncome.getText().toString();
//                              AreaOfLandValue = AreaOfLand.getText().toString();
//                               PriceOfLandValue = PriceOfLand.getText().toString();
                            LandInAnnaValue = LandInAnna.getText().toString();
                            TotalLandPriceValue = TotalLandPrice.getText().toString();
                            PropertyInAnnaValue = PropertyInAnna.getText().toString();
                            TotalPropertyPriceValue = TotalPropertyPrice.getText().toString();

                            jsonLatLangArray = jsonArrayGPS.toString();
//                            image = encodedImage;
                            image = mCurrentPhotoPath;

                            DistrictValue = District.getSelectedItem().toString();
                            MunicipalityValue = Municipality.getSelectedItem().toString();
                            HusbandIncometype = Husband_income_type.getSelectedItem().toString();
                            WifeIncomeType = Wife_income_type.getSelectedItem().toString();
                            CHildrenIncomeType = Children_income_type.getSelectedItem().toString();
                            RelativesIncomeTypes = Relatives_income_type.getSelectedItem().toString();
                            OthersIncomeTypes = Others_income_type.getSelectedItem().toString();
                            LandAreaType = land_area_spinner.getSelectedItem().toString();
                            PropertyAreaType = property_area_spinner.getSelectedItem().toString();
                            LandPriceType = land_price_spinner.getSelectedItem().toString();
                            PropertyPriceType = property_price_spinner.getSelectedItem().toString();

//                            yearOfPurchaseValue = yearOfPurchase.getText().toString();

                            yearOfPurchaseValue = yearOfPurchase.getText().toString();
                            valueOfPurchaseValue = valueOfPurchase.getText().toString();
                            PoolingPriceType = Pooling_price_spinner.getSelectedItem().toString();


                            uniqueCode();

                            convertDataToJson();


                            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
                            final int width = metrics.widthPixels;
                            int height = metrics.heightPixels;

                            final Dialog showDialog = new Dialog(context);
                            showDialog.setContentView(R.layout.alert_dialog_before_send);
                            final Button yes = (Button) showDialog.findViewById(R.id.alertButtonYes);
                            final Button no = (Button) showDialog.findViewById(R.id.alertButtonNo);

                            showDialog.setTitle("WARNING !!!");
                            showDialog.setCancelable(false);
                            showDialog.show();
                            showDialog.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);

                            yes.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showDialog.dismiss();
                                    mProgressDlg = new ProgressDialog(context);
                                    mProgressDlg.setMessage("Please wait...");
                                    mProgressDlg.setIndeterminate(false);
                                    mProgressDlg.setCancelable(false);
                                    mProgressDlg.show();
                                    convertDataToJson();
//                                    sendDatToserver();
//                                          finish();
                                }
                            });

                            no.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showDialog.dismiss();
                                }
                            });
//
//                                    DisplayMetrics metrics = context.getResources().getDisplayMetrics();
//                                    final int width = metrics.widthPixels;
//                                    int height = metrics.heightPixels;
//
//
//                                    final Dialog showDialog = new Dialog(context);
                            showDialog.setContentView(R.layout.date_input_layout);
                            final EditText FormNameToInput = (EditText) showDialog.findViewById(R.id.input_tableName);
                            final EditText dateToInput = (EditText) showDialog.findViewById(R.id.input_date);

                            if (formNameSavedForm.equals("")) {
                                FormNameToInput.setText("Household Survey");
                            } else {
                                FormNameToInput.setText(formNameSavedForm);
                                Database_SaveForm dataBaseNsaveform = new Database_SaveForm(context);
                                dataBaseNsaveform.open();
                                dataBaseNsaveform.dropRowNotSentForms(formid);
                            }

                            long date = System.currentTimeMillis();

                            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy h:mm a");
                            String dateString = sdf.format(date);
                            dateToInput.setText(dateString);

                            AppCompatButton logIn = (AppCompatButton) showDialog.findViewById(R.id.login_button);
                            showDialog.setTitle("Save Data");
                            showDialog.setCancelable(true);
                            showDialog.show();
                            showDialog.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);

                            logIn.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    // TODO Auto-generated method stub
                                    String dateDataCollected = dateToInput.getText().toString();
                                    String formName = FormNameToInput.getText().toString();

                                    String[] data = new String[]{"1", formName, dateDataCollected, jsonToSend, jsonLatLangArray,
                                            "" + mCurrentPhotoPath, "Not Sent", "0"};


                                    Database_SaveForm dataBaseSaveform = new Database_SaveForm(context);
                                    dataBaseSaveform.open();
                                    long id = dataBaseSaveform.insertIntoTable_Main(data);
                                    Log.e("dbID", "" + id);
                                    dataBaseSaveform.close();

                                    Toast.makeText(SurveyMain.this, "Data saved successfully", Toast.LENGTH_SHORT).show();
                                    showDialog.dismiss();

                                    Database_Marker marker = new Database_Marker(context);
                                    marker.replaceSave(HouseHoldIdValue);

                                    final Dialog showDialog = new Dialog(context);
                                    showDialog.setContentView(R.layout.savedform_sent_popup);
                                    final Button yes = (Button) showDialog.findViewById(R.id.buttonYes);
                                    final Button no = (Button) showDialog.findViewById(R.id.buttonNo);

                                    showDialog.setTitle("Successfully Saved");
                                    showDialog.setCancelable(false);
                                    showDialog.show();
                                    showDialog.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);

                                    yes.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            showDialog.dismiss();
                                            Intent intent = new Intent(SurveyMain.this, SavedFormActivity.class);
                                            startActivity(intent);
//                                                 finish();
                                        }
                                    });

                                    no.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            showDialog.dismiss();
                                            Intent intent = new Intent(SurveyMain.this, MainActivity.class);
                                            startActivity(intent);
                                        }
                                    });


                                }
                            });
                            // this

                        } else {
                            Toast.makeText(getApplicationContext(), "Something is no fill in the form", Toast.LENGTH_SHORT).show();

                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "You need to take a house image", Toast.LENGTH_SHORT).show();

                    }

                } else {
                    Toast.makeText(getApplicationContext(), "You need to take at least one gps cooordinate", Toast.LENGTH_SHORT).show();

                }
            }


        });

        // add click listener to Button "POST"
        Send.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                send = true;
                if (isGpsTracking) {
                    Toast.makeText(getApplicationContext(), "Please end GPS Tracking.", Toast.LENGTH_SHORT).show();
                } else {

                    if (isGpsTaken) {

                        SurveyIdNumValue = surveyorId.getText().toString();
                        NameOfSurveyorValue = NameOfSurveyor.getText().toString();
                        DateOfSurveyValue = DateOfSurvey.getText().toString();

                        HouseHoldIdValue = HouseHoldId.getText().toString();
//                        MunicipalityValue = Municipality.getText().toString();
                        WardValue = Ward.getText().toString();
                        AddressValue = Address.getText().toString();
//                        HouseholdTypologyValue = checkedBtn1.getText().toString();

                        AgeValue = Age.getText().toString();

                        RadioButton checkedBtn = (RadioButton) findViewById(Sex.getCheckedRadioButtonId());
                        SexValue = checkedBtn.getText().toString();

                        Log.d("", "onCreate: " + SexValue);
                        EmailValue = Email.getText().toString();

                        NumOfHusbandValue = NumOfHusband.getText().toString();
                        NumOfWifeValue = NumOfWife.getText().toString();
                        NumOfChildrenValue = NumOfChildren.getText().toString();
                        NumOfRelativesValue = NumOfRelatives.getText().toString();
                        NumOfothersValue = NumOfothers.getText().toString();
                        FamilyMemberNumberValue = TotalFamilyMemberNumber.getText().toString();

                        WorkingNumOfHusbandValue = WorkingNumOfHusband.getText().toString();
                        WorkingNumOfWifeValue = WorkingNumOfWife.getText().toString();
                        WorkingNumOfChildrenValue = WorkingNumOfChildren.getText().toString();
                        WorkingNumOfRelativesValue = WorkingNumOfRelatives.getText().toString();
                        WorkingNumOfothersValue = WorkingNumOfothers.getText().toString();
                        WorkingFamilyMemberNumberValue = WorkingFamilyMemberNumber.getText().toString();

                        HusbandIncomeSource = HusbandIncomeDetail.getText().toString();
                        HusbandIncomeOtherSource = HusbandOthersIncomeDetail.getText().toString();
                        WifeIncomeSource = WifeIncomeDetail.getText().toString();
                        WifeIncomeOtherSource = WifeOthersIncomeDetail.getText().toString();
                        ChildrenIncomeSource = ChildrenIncomeDetail.getText().toString();
                        ChildrensIncomeOtherSource = ChildrenOthersIncomeDetail.getText().toString();
                        RelativesIncomeSource = RelativesIncomeDetail.getText().toString();
                        RelativesIncomeOtherSource = RelativesOthersIncomeDetail.getText().toString();
                        OthersIncomeSource = OthersIncomeDetail.getText().toString();
                        OthersIncomeOtherSource = OthersOthersIncomeDetail.getText().toString();

                        AverageMonthlyIncomeOfHusbandValue = AverageMonthlyIncomeOfHusband.getText().toString();
                        AverageMonthlyIncomeOfWifeValue = AverageMonthlyIncomeOfWife.getText().toString();
                        AverageMonthlyIncomeOfChildrenValue = AverageMonthlyIncomeOfChildren.getText().toString();
                        AverageMonthlyIncomeOfRelativesValue = AverageMonthlyIncomeOfRelatives.getText().toString();
                        AverageMonthlyIncomeOfOthersValue = AverageMonthlyIncomeOfOthers.getText().toString();
                        HusbandFarmIncomeValue = HusbandFarmIncome.getText().toString();
                        WifeFarmIncomeValue = WifeFarmIncome.getText().toString();
                        ChildrenFarmIncomeValue = ChildrenFarmIncome.getText().toString();
                        RelativesFarmIncomeValue = RelativesFarmIncome.getText().toString();
                        OthersFarmIncomeValue = OthersFarmIncome.getText().toString();

                        LandInAnnaValue = LandInAnna.getText().toString();
                        TotalLandPriceValue = TotalLandPrice.getText().toString();
                        PropertyInAnnaValue = PropertyInAnna.getText().toString();
                        TotalPropertyPriceValue = TotalPropertyPrice.getText().toString();

                        DistrictValue = District.getSelectedItem().toString();
                        MunicipalityValue = Municipality.getSelectedItem().toString();
                        HusbandIncometype = Husband_income_type.getSelectedItem().toString();
                        WifeIncomeType = Wife_income_type.getSelectedItem().toString();
                        CHildrenIncomeType = Children_income_type.getSelectedItem().toString();
                        RelativesIncomeTypes = Relatives_income_type.getSelectedItem().toString();
                        OthersIncomeTypes = Others_income_type.getSelectedItem().toString();
                        LandAreaType = land_area_spinner.getSelectedItem().toString();
                        PropertyAreaType = property_area_spinner.getSelectedItem().toString();
                        LandPriceType = land_price_spinner.getSelectedItem().toString();
                        PropertyPriceType = property_price_spinner.getSelectedItem().toString();


                        //                            yearOfPurchaseValue = yearOfPurchase.getText().toString();

                        yearOfPurchaseValue = yearOfPurchase.getText().toString();
                        valueOfPurchaseValue = valueOfPurchase.getText().toString();
                        PoolingPriceType = Pooling_price_spinner.getSelectedItem().toString();


                        uniqueCode();


                        if (networkInfo != null && networkInfo.isConnected()) {


                            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
                            int width = metrics.widthPixels;
                            int height = metrics.heightPixels;

                            final Dialog showDialog = new Dialog(context);
                            showDialog.setContentView(R.layout.alert_dialog_before_send);
                            final Button yes = (Button) showDialog.findViewById(R.id.alertButtonYes);
                            final Button no = (Button) showDialog.findViewById(R.id.alertButtonNo);

                            showDialog.setTitle("WARNING !!!");
                            showDialog.setCancelable(false);
                            showDialog.show();
                            showDialog.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);

                            yes.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showDialog.dismiss();
                                    mProgressDlg = new ProgressDialog(context);
                                    mProgressDlg.setMessage("Please wait...");
                                    mProgressDlg.setIndeterminate(false);
                                    mProgressDlg.setCancelable(false);
                                    mProgressDlg.show();
                                    imageB64Encoder();
                                    convertDataToJson1();
                                    sendDatToserver();

                                    Database_Marker marker = new Database_Marker(getApplicationContext());
                                    marker.replaceSend(HouseHoldIdValue);
//                                finish();
                                }
                            });

                            no.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showDialog.dismiss();
                                }
                            });


                        } else {
                            final View coordinatorLayoutView = findViewById(R.id.naxa_Survay);
                            Snackbar.make(coordinatorLayoutView, "No internet connection", Snackbar.LENGTH_LONG)
                                    .setAction("Retry", null).show();
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "You need to take at least one gps cooordinate", Toast.LENGTH_SHORT).show();

                    }
                }

            }
        });
    }

    static final int REQUEST_IMAGE_CAPTURE = 28371;
    String mCurrentPhotoPath;
    static final int DATE_DIALOG_ID = 999;

    private void dispatchTakePictureIntent() {

        //scaling down needs the imageview to be visible
        //so start early
        previewImageSite.setVisibility(View.VISIBLE);

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.naxasurvay.fileprovider",
                        photoFile);

                List<ResolveInfo> resolvedIntentActivities = context.getPackageManager().
                        queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo resolvedIntentInfo : resolvedIntentActivities) {
                    String packageName = resolvedIntentInfo.activityInfo.packageName;

                    context.grantUriPermission(packageName, photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }


                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                Log.d(TAG ," photo intent :" +photoURI );
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    // set pic
    private void setPic(ImageView mImageView, String imagePath) {
        // Get the dimensions of the View
        mImageView.setVisibility(View.VISIBLE);
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();


        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;


        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / 200, photoH / 200);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;

        //bmOptions.inSampleSize = scaleFactor;
        bmOptions.inSampleSize = scaleFactor;

        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);

//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
//        byte[] byteArray = byteArrayOutputStream.toByteArray();
//        encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

    }


    private void imageB64Encoder() {
        // Get the dimensions of the View
        previewImageSite.setVisibility(View.VISIBLE);
        int targetW = previewImageSite.getWidth();
        int targetH = previewImageSite.getHeight();


        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;


        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / 200, photoH / 200);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;

        //bmOptions.inSampleSize = scaleFactor;
        bmOptions.inSampleSize = scaleFactor;

        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
//        mImageView.setImageBitmap(bitmap);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

    }


    public void initilizeUI() {
        Intent intent = getIntent();
        if (intent.hasExtra("JSON1")) {


            CheckValues.isFromSavedFrom = true;
            startGps.setEnabled(false);
            isGpsTaken = true;
//            previewMap.setEnabled(true);
            Bundle bundle = intent.getExtras();
            String jsonToParse = (String) bundle.get("JSON1");
            mCurrentPhotoPath = (String) bundle.get("photo");
            Log.d("Retrive Image", "Retrive Image :" + mCurrentPhotoPath);
            String gpsLocationtoParse = (String) bundle.get("gps");
            formid = (String) bundle.get("DBid");
            String sent_Status = (String) bundle.get("sent_Status");
            formNameSavedForm = (String) bundle.get("form_name");
            Log.d(TAG, "initilizeUI: " + sent_Status);
            Log.d(TAG, "initilizeUI: " + formNameSavedForm);


            if (sent_Status.equals("Sent")) {
                NameOfSurveyor.setEnabled(false);
                DateOfSurvey.setEnabled(false);
                Municipality.setEnabled(false);
                Ward.setEnabled(false);
                Address.setEnabled(false);
                Age.setEnabled(false);
                Email.setEnabled(false);
                WorkingFamilyMemberNumber.setEnabled(false);
                AverageMonthlyIncomeOfHusband.setEnabled(false);
                AverageMonthlyIncomeOfWife.setEnabled(false);
                AverageMonthlyIncomeOfChildren.setEnabled(false);
                AverageMonthlyIncomeOfRelatives.setEnabled(false);
                AverageMonthlyIncomeOfOthers.setEnabled(false);
                LandInAnna.setEnabled(false);
                PropertyInAnna.setEnabled(false);
                TotalLandPrice.setEnabled(false);
                TotalPropertyPrice.setEnabled(false);
                photo.setEnabled(false);
                startGps.setEnabled(false);
                cv_Send_Save.setVisibility(View.GONE);


            }


            Log.e("Household_Survey", "i-" + imageName);

            if (imageName.equals("no_photo")) {
            } else {
                File file1 = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), imageName);
                String path = file1.toString();
                Toast.makeText(getApplicationContext(), path, Toast.LENGTH_SHORT).show();

//                loadImageFromStorage(path);


            }
            try {
                //new adjustment
                Log.e("HouseholdSurvey", "" + jsonToParse);
//                parseArrayGPS(gpsLocationtoParse);
                parseJson(jsonToParse);
                previewImageSite.setVisibility(View.VISIBLE);
                setPic(previewImageSite, mCurrentPhotoPath);

                startGps.setText("Location Recorded");
            } catch (JSONException e) {
                Log.d(TAG, "HouseholdSurv: " + e.toString());
                e.printStackTrace();
            }
        } else {
            gps = new GPS_TRACKER_FOR_POINT(SurveyMain.this);
            gps.canGetLocation();
            startGps.setEnabled(true);

        }
    }

    // data convert for save
    public void convertDataToJson() {
        //function in the activity that corresponds to the hwc_human_casulty button

        try {

            JSONObject header = new JSONObject();

//            header.put("unique_code", uniCode);
            header.put("surveyor_id", SurveyIdNumValue);
            header.put("name_of_surveyor", NameOfSurveyorValue);
            header.put("date_of_survey", DateOfSurveyValue);

            header.put("unique_code", uniCode);

            Log.d("unicode", "unicode :" + uniCode);

            header.put("house_id", HouseHoldIdValue);
            header.put("name_of_district", DistrictValue);
            header.put("name_of_municipality", MunicipalityValue);
            header.put("ward_no", WardValue);
            header.put("address", AddressValue);
            header.put("house_typology", HouseholdTypologyValue);

            header.put("age", AgeValue);
            header.put("sex", SexValue);
            header.put("email", EmailValue);
//            header.put("num_of_family_memb", FamilyMemberNumberValue);
            header.put("total_num_of_family_memb", FamilyMemberNumberValue);
            header.put("husband_no", NumOfHusbandValue);
            header.put("wife_no", NumOfWifeValue);
            header.put("children_no", NumOfChildrenValue);
            header.put("relatives_no", NumOfRelativesValue);
            header.put("others_no", NumOfothersValue);

            header.put("num_of_working_family_memb", WorkingFamilyMemberNumberValue);
            header.put("working_husband_no", WorkingNumOfHusbandValue);
            header.put("working_wife_no", WorkingNumOfWifeValue);
            header.put("working_children_no", WorkingNumOfChildrenValue);
            header.put("working_relatives_no", WorkingNumOfRelativesValue);
            header.put("working_others_no", WorkingNumOfothersValue);

            header.put("income_source_of_husband", HusbandIncomeSource);
            header.put("income_other_source_of_husband", HusbandIncomeOtherSource);
            header.put("income_source_of_wife", WifeIncomeSource);
            header.put("income_other_source_of_wife", WifeIncomeOtherSource);
            header.put("income_source_of_children", ChildrenIncomeSource);
            header.put("income_other_source_of_children", ChildrensIncomeOtherSource);
            header.put("income_source_of_relatives", RelativesIncomeSource);
            header.put("income_other_source_of_relatives", RelativesIncomeOtherSource);
            header.put("income_source_of_others", OthersIncomeSource);
            header.put("income_other_source_of_others", OthersIncomeOtherSource);

            header.put("average_income_of_husband", AverageMonthlyIncomeOfHusbandValue);
            header.put("average_income_of_wife", AverageMonthlyIncomeOfWifeValue);
            header.put("average_income_of_children", AverageMonthlyIncomeOfChildrenValue);
            header.put("average_income_of_relatives", AverageMonthlyIncomeOfRelativesValue);
            header.put("average_income_of_others", AverageMonthlyIncomeOfOthersValue);

            header.put("farm_income_of_husband", HusbandFarmIncomeValue);
            header.put("farm_income_of_wife", WifeFarmIncomeValue);
            header.put("farm_income_of_children", ChildrenFarmIncomeValue);
            header.put("farm_income_of_relatives", RelativesFarmIncomeValue);
            header.put("farm_income_of_others", OthersFarmIncomeValue);

            header.put("husband_price_type", HusbandIncometype);
            header.put("wife_price_type", WifeIncomeType);
            header.put("children_price_type", CHildrenIncomeType);
            header.put("relatives_price_type", RelativesIncomeTypes);
            header.put("others_price_type", OthersIncomeTypes);

            header.put("land_anna", LandInAnnaValue);
            header.put("land_anna_type", LandAreaType);

            header.put("land_total_price", TotalLandPriceValue);
            header.put("land_total_price_type", LandPriceType);

            header.put("property_anna", PropertyInAnnaValue);
            header.put("property_anna_type", PropertyAreaType);

            header.put("property_total_price", TotalPropertyPriceValue);
            header.put("property_total_price_type", PropertyPriceType);

            header.put("pooling_type", PoolingValue);
            header.put("pooling_year_of_purchase", yearOfPurchaseValue);
            header.put("pooling_value_of_purchase", valueOfPurchaseValue);
            header.put("pooling_total_price_type", PoolingPriceType);

            header.put("latitude", finalLat);
            header.put("longitude", finalLong);

            header.put("photo", mCurrentPhotoPath);


            jsonToSend = header.toString();
            Log.e("main_activity", "convertDataToJson save : " + jsonToSend);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // data convert for send
    public void convertDataToJson1() {
        //function in the activity that corresponds to the hwc_human_casulty button

        try {

            JSONObject header = new JSONObject();

//            header.put("unique_code", uniCode);
            header.put("surveyor_id", SurveyIdNumValue);
            header.put("name_of_surveyor", NameOfSurveyorValue);
            header.put("date_of_survey", DateOfSurveyValue);

            header.put("unique_code", uniCode);

            Log.d("unicode", "unicode :" + uniCode);

            header.put("house_id", HouseHoldIdValue);
            header.put("name_of_district", DistrictValue);
            header.put("name_of_municipality", MunicipalityValue);
            header.put("ward_no", WardValue);
            header.put("address", AddressValue);
            header.put("house_typology", HouseholdTypologyValue);

            header.put("age", AgeValue);
            header.put("sex", SexValue);
            header.put("email", EmailValue);
//            header.put("num_of_family_memb", FamilyMemberNumberValue);
            header.put("total_num_of_family_memb", FamilyMemberNumberValue);
            header.put("husband_no", NumOfHusbandValue);
            header.put("wife_no", NumOfWifeValue);
            header.put("children_no", NumOfChildrenValue);
            header.put("relatives_no", NumOfRelativesValue);
            header.put("others_no", NumOfothersValue);

            header.put("num_of_working_family_memb", WorkingFamilyMemberNumberValue);
            header.put("working_husband_no", WorkingNumOfHusbandValue);
            header.put("working_wife_no", WorkingNumOfWifeValue);
            header.put("working_children_no", WorkingNumOfChildrenValue);
            header.put("working_relatives_no", WorkingNumOfRelativesValue);
            header.put("working_others_no", WorkingNumOfothersValue);

            header.put("income_source_of_husband", HusbandIncomeSource);
            header.put("income_other_source_of_husband", HusbandIncomeOtherSource);
            header.put("income_source_of_wife", WifeIncomeSource);
            header.put("income_other_source_of_wife", WifeIncomeOtherSource);
            header.put("income_source_of_children", ChildrenIncomeSource);
            header.put("income_other_source_of_children", ChildrensIncomeOtherSource);
            header.put("income_source_of_relatives", RelativesIncomeSource);
            header.put("income_other_source_of_relatives", RelativesIncomeOtherSource);
            header.put("income_source_of_others", OthersIncomeSource);
            header.put("income_other_source_of_others", OthersIncomeOtherSource);

            header.put("average_income_of_husband", AverageMonthlyIncomeOfHusbandValue);
            header.put("average_income_of_wife", AverageMonthlyIncomeOfWifeValue);
            header.put("average_income_of_children", AverageMonthlyIncomeOfChildrenValue);
            header.put("average_income_of_relatives", AverageMonthlyIncomeOfRelativesValue);
            header.put("average_income_of_others", AverageMonthlyIncomeOfOthersValue);

            header.put("farm_income_of_husband", HusbandFarmIncomeValue);
            header.put("farm_income_of_wife", WifeFarmIncomeValue);
            header.put("farm_income_of_children", ChildrenFarmIncomeValue);
            header.put("farm_income_of_relatives", RelativesFarmIncomeValue);
            header.put("farm_income_of_others", OthersFarmIncomeValue);

            header.put("husband_price_type", HusbandIncometype);
            header.put("wife_price_type", WifeIncomeType);
            header.put("children_price_type", CHildrenIncomeType);
            header.put("relatives_price_type", RelativesIncomeTypes);
            header.put("others_price_type", OthersIncomeTypes);

            header.put("land_anna", LandInAnnaValue);
            header.put("land_anna_type", LandAreaType);

            header.put("land_total_price", TotalLandPriceValue);
            header.put("land_total_price_type", LandPriceType);

            header.put("property_anna", PropertyInAnnaValue);
            header.put("property_anna_type", PropertyAreaType);

            header.put("property_total_price", TotalPropertyPriceValue);
            header.put("property_total_price_type", PropertyPriceType);

            header.put("pooling_type", PoolingValue);
            header.put("pooling_year_of_purchase", yearOfPurchaseValue);
            header.put("pooling_value_of_purchase", valueOfPurchaseValue);
            header.put("pooling_total_price_type", PoolingPriceType);

            header.put("latitude", finalLat);
            header.put("longitude", finalLong);

            header.put("photo", encodedImage);


            jsonToSend = header.toString();
            Log.e("main_activity", "convertDataToJson send : " + jsonToSend);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendDatToserver() {

        if (jsonToSend.length() > 0) {

            RestApii restApii = new RestApii();
            restApii.execute();
        }
    }

    public void parseJson(String jsonToParse) throws JSONException {

        JSONObject jsonObj = new JSONObject(jsonToParse);
        Log.e("householdsurvey", "json : " + jsonObj.toString());

        SurveyIdNumValue = jsonObj.getString("surveyor_id");
        NameOfSurveyorValue = jsonObj.getString("name_of_surveyor");
        DateOfSurveyValue = jsonObj.getString("date_of_survey");
        HouseHoldIdValue = jsonObj.getString("house_id");
        DistrictValue = jsonObj.getString("name_of_district");
        MunicipalityValue = jsonObj.getString("name_of_municipality");
        WardValue = jsonObj.getString("ward_no");
        AddressValue = jsonObj.getString("address");

        HouseholdTypologyValue = jsonObj.getString("house_typology");
        splitString(HouseholdTypologyValue);

        AgeValue = jsonObj.getString("age");
        SexValue = jsonObj.getString("sex");
        EmailValue = jsonObj.getString("email");
//        FamilyMemberNumberValue = jsonObj.getString("num_of_family_memb");

        NumOfHusbandValue = jsonObj.getString("husband_no");
        NumOfWifeValue = jsonObj.getString("wife_no");
        NumOfChildrenValue = jsonObj.getString("children_no");
        NumOfRelativesValue = jsonObj.getString("relatives_no");
        NumOfothersValue = jsonObj.getString("others_no");
        FamilyMemberNumberValue = jsonObj.getString("total_num_of_family_memb");

        WorkingNumOfHusbandValue = jsonObj.getString("working_husband_no");
        WorkingNumOfWifeValue = jsonObj.getString("working_wife_no");
        WorkingNumOfChildrenValue = jsonObj.getString("working_children_no");
        WorkingNumOfRelativesValue = jsonObj.getString("working_relatives_no");
        WorkingNumOfothersValue = jsonObj.getString("working_others_no");
        WorkingFamilyMemberNumberValue = jsonObj.getString("num_of_working_family_memb");


        HusbandIncomeSource = jsonObj.getString("income_source_of_husband");
        HusbandIncomeOtherSource = jsonObj.getString("income_other_source_of_husband");
        WifeIncomeSource = jsonObj.getString("income_source_of_wife");
        WifeIncomeOtherSource = jsonObj.getString("income_other_source_of_wife");
        ChildrenIncomeSource = jsonObj.getString("income_source_of_children");
        ChildrensIncomeOtherSource = jsonObj.getString("income_other_source_of_children");
        RelativesIncomeSource = jsonObj.getString("income_source_of_relatives");
        RelativesIncomeOtherSource = jsonObj.getString("income_other_source_of_relatives");
        OthersIncomeSource = jsonObj.getString("income_source_of_others");
        OthersIncomeOtherSource = jsonObj.getString("income_other_source_of_others");


        AverageMonthlyIncomeOfHusbandValue = jsonObj.getString("average_income_of_husband");
        AverageMonthlyIncomeOfWifeValue = jsonObj.getString("average_income_of_wife");
        AverageMonthlyIncomeOfChildrenValue = jsonObj.getString("average_income_of_children");
        AverageMonthlyIncomeOfRelativesValue = jsonObj.getString("average_income_of_relatives");
        AverageMonthlyIncomeOfOthersValue = jsonObj.getString("average_income_of_others");

        HusbandFarmIncomeValue = jsonObj.getString("farm_income_of_husband");
        WifeFarmIncomeValue = jsonObj.getString("farm_income_of_wife");
        ChildrenFarmIncomeValue = jsonObj.getString("farm_income_of_children");
        RelativesFarmIncomeValue = jsonObj.getString("farm_income_of_relatives");
        OthersFarmIncomeValue = jsonObj.getString("farm_income_of_others");

        HusbandIncometype = jsonObj.getString("husband_price_type");
        WifeIncomeType = jsonObj.getString("wife_price_type");
        CHildrenIncomeType = jsonObj.getString("children_price_type");
        RelativesIncomeTypes = jsonObj.getString("relatives_price_type");
        OthersIncomeTypes = jsonObj.getString("others_price_type");

        LandInAnnaValue = jsonObj.getString("land_anna");
        LandAreaType = jsonObj.getString("land_anna_type");

        TotalLandPriceValue = jsonObj.getString("land_total_price");
        LandPriceType = jsonObj.getString("land_total_price_type");

        PropertyInAnnaValue = jsonObj.getString("property_anna");
        PropertyAreaType = jsonObj.getString("property_anna_type");

        TotalPropertyPriceValue = jsonObj.getString("property_total_price");
        PropertyPriceType = jsonObj.getString("property_total_price_type");

        PoolingValue = jsonObj.getString("pooling_type");
        splitString1(PoolingValue);

        yearOfPurchaseValue = jsonObj.getString("pooling_year_of_purchase");
        valueOfPurchaseValue = jsonObj.getString("pooling_value_of_purchase");
        PoolingPriceType = jsonObj.getString("pooling_total_price_type");

        finalLat = Double.parseDouble(jsonObj.getString("latitude"));
        finalLong = Double.parseDouble(jsonObj.getString("longitude"));
        LatLng d = new LatLng(finalLat, finalLong);
        listCf.add(d);

        encodedImage = jsonObj.getString("photo");
        Log.d("check Image", "encoded Image : " + encodedImage);

        surveyorId.setText(SurveyIdNumValue);
        NameOfSurveyor.setText(NameOfSurveyorValue);
        DateOfSurvey.setText(DateOfSurveyValue);
        HouseHoldId.setText(HouseHoldIdValue);

        List<String> DistrictName = Arrays.asList(getResources().getStringArray(R.array.district_name));
        int setDistrictName = DistrictName.indexOf(DistrictValue);
        District.setSelection(setDistrictName);


        Log.e("Children Under Two", "Parsed data " + DistrictValue);
        Log.e("Children Under Two", "Parsed data " + DistrictName);
        Log.e("Children Under Two", "Parsed data " + setDistrictName);

        List<String> MunicipalityName = Arrays.asList(getResources().getStringArray(R.array.municipality_name));
        int setMunicipalityName = MunicipalityName.indexOf(MunicipalityValue);
        Municipality.setSelection(setMunicipalityName);


        Ward.setText(WardValue);
        Address.setText(AddressValue);


        if (check1.equals("single family detached")) {
            SingleFamilyDetached.setChecked(true);
        }
        if (check2.equals("Multi family house")) {
            MultyFamilyhouse.setChecked(true);
        }
        if (check3.equals("apartment block")) {
            ApartmentBlock.setChecked(true);
        }
        if (check4.equals("mixed use block")) {
            MixedUseBlock.setChecked(true);
        }
        if (check5.equals("number of floors")) {
            NumberOfFloors.setChecked(true);
        }


        Age.setText(AgeValue);

//        Sex.setText(SexValue);

        if (SexValue.equals("Male")) {
            ((RadioButton) findViewById(R.id.male)).setChecked(true);
        } else if (SexValue.equals("Female")) {
            ((RadioButton) findViewById(R.id.female)).setChecked(true);
        } else {
            ((RadioButton) findViewById(R.id.Other)).setChecked(true);
        }


        Email.setText(EmailValue);

        NumOfHusband.setText(NumOfHusbandValue);
        NumOfWife.setText(NumOfWifeValue);
        NumOfChildren.setText(NumOfChildrenValue);
        NumOfRelatives.setText(NumOfRelativesValue);
        NumOfothers.setText(NumOfothersValue);
        TotalFamilyMemberNumber.setText(FamilyMemberNumberValue);

        WorkingNumOfHusband.setText(WorkingNumOfHusbandValue);
        WorkingNumOfWife.setText(WorkingNumOfWifeValue);
        WorkingNumOfChildren.setText(WorkingNumOfChildrenValue);
        WorkingNumOfRelatives.setText(WorkingNumOfRelativesValue);
        WorkingNumOfothers.setText(WorkingNumOfothersValue);
        WorkingFamilyMemberNumber.setText(WorkingFamilyMemberNumberValue);


        HusbandIncomeDetail.setText(HusbandIncomeSource);
        if (!HusbandIncomeSource.equals("")) {
            HusbandIncomeDetail.setVisibility(View.VISIBLE);
        }
        HusbandOthersIncomeDetail.setText(HusbandIncomeOtherSource);
        if (!HusbandIncomeOtherSource.equals("")) {
            HusbandOthersIncomeDetail.setVisibility(View.VISIBLE);
        }
        HusbandFarmIncome.setText(HusbandFarmIncomeValue);
        if (!HusbandFarmIncomeValue.equals("")) {
            HusbandFarmIncome.setVisibility(View.VISIBLE);
        }

        WifeIncomeDetail.setText(WifeIncomeSource);
        if (!WifeIncomeSource.equals("")) {
            WifeIncomeDetail.setVisibility(View.VISIBLE);
        }
        WifeOthersIncomeDetail.setText(WifeIncomeOtherSource);
        if (!WifeIncomeOtherSource.equals("")) {
            WifeOthersIncomeDetail.setVisibility(View.VISIBLE);
        }
        WifeFarmIncome.setText(WifeFarmIncomeValue);
        if (!WifeFarmIncomeValue.equals("")) {
            WifeFarmIncome.setVisibility(View.VISIBLE);
        }

        ChildrenIncomeDetail.setText(ChildrenIncomeSource);
        if (!ChildrenIncomeSource.equals("")) {
            ChildrenIncomeDetail.setVisibility(View.VISIBLE);
        }
        ChildrenOthersIncomeDetail.setText(ChildrensIncomeOtherSource);
        if (!ChildrensIncomeOtherSource.equals("")) {
            ChildrenOthersIncomeDetail.setVisibility(View.VISIBLE);
        }
        ChildrenFarmIncome.setText(ChildrenFarmIncomeValue);
        if (!ChildrenFarmIncomeValue.equals("")) {
            ChildrenFarmIncome.setVisibility(View.VISIBLE);
        }

        RelativesIncomeDetail.setText(RelativesIncomeSource);
        if (!RelativesIncomeSource.equals("")) {
            RelativesIncomeDetail.setVisibility(View.VISIBLE);
        }
        RelativesOthersIncomeDetail.setText(RelativesIncomeOtherSource);
        if (!RelativesIncomeOtherSource.equals("")) {
            RelativesOthersIncomeDetail.setVisibility(View.VISIBLE);
        }
        RelativesFarmIncome.setText(RelativesFarmIncomeValue);
        if (!RelativesFarmIncomeValue.equals("")) {
            RelativesFarmIncome.setVisibility(View.VISIBLE);
        }

        OthersIncomeDetail.setText(OthersIncomeSource);
        if (!OthersIncomeSource.equals("")) {
            OthersIncomeDetail.setVisibility(View.VISIBLE);
        }
        OthersOthersIncomeDetail.setText(OthersIncomeOtherSource);
        if (!OthersIncomeOtherSource.equals("")) {
            OthersOthersIncomeDetail.setVisibility(View.VISIBLE);
        }
        OthersFarmIncome.setText(OthersFarmIncomeValue);
        if (!OthersFarmIncomeValue.equals("")) {
            OthersFarmIncome.setVisibility(View.VISIBLE);
        }


        AverageMonthlyIncomeOfHusband.setText(AverageMonthlyIncomeOfHusbandValue);
        List<String> ItemPriceValue = Arrays.asList(getResources().getStringArray(R.array.item_price));
        int setHusbandIncome = ItemPriceValue.indexOf(HusbandIncometype);
        Husband_income_type.setSelection(setHusbandIncome);

        AverageMonthlyIncomeOfWife.setText(AverageMonthlyIncomeOfWifeValue);
//        List<String> DistrictName = Arrays.asList(getResources().getStringArray(R.array.item_price));
        int setWifeIncome = ItemPriceValue.indexOf(WifeIncomeType);
        Wife_income_type.setSelection(setWifeIncome);

        AverageMonthlyIncomeOfChildren.setText(AverageMonthlyIncomeOfChildrenValue);
//        List<String> DistrictName = Arrays.asList(getResources().getStringArray(R.array.item_price));
        int setChildrenIncome = ItemPriceValue.indexOf(CHildrenIncomeType);
        Children_income_type.setSelection(setChildrenIncome);

        AverageMonthlyIncomeOfRelatives.setText(AverageMonthlyIncomeOfRelativesValue);
//        List<String> DistrictName = Arrays.asList(getResources().getStringArray(R.array.item_price));
        int setRelativesIncome = ItemPriceValue.indexOf(RelativesIncomeTypes);
        Relatives_income_type.setSelection(setRelativesIncome);

        AverageMonthlyIncomeOfOthers.setText(AverageMonthlyIncomeOfOthersValue);
//        List<String> DistrictName = Arrays.asList(getResources().getStringArray(R.array.item_price));
        int setOthersIncome = ItemPriceValue.indexOf(OthersIncomeTypes);
        Others_income_type.setSelection(setOthersIncome);

        LandInAnna.setText(LandInAnnaValue);
        List<String> ItemAreaValue = Arrays.asList(getResources().getStringArray(R.array.item_land));
        int setLandArea = ItemAreaValue.indexOf(LandAreaType);
        land_area_spinner.setSelection(setLandArea);

        TotalLandPrice.setText(TotalLandPriceValue);
//        List<String> DistrictName = Arrays.asList(getResources().getStringArray(R.array.item_price));
        int setLandPrice = ItemPriceValue.indexOf(LandPriceType);
        land_price_spinner.setSelection(setLandPrice);

        PropertyInAnna.setText(PropertyInAnnaValue);
//        List<String> DistrictName = Arrays.asList(getResources().getStringArray(R.array.district_name));
        int setPropertyArea = ItemAreaValue.indexOf(PropertyAreaType);
        property_area_spinner.setSelection(setPropertyArea);

        TotalPropertyPrice.setText(TotalPropertyPriceValue);
//        List<String> DistrictName = Arrays.asList(getResources().getStringArray(R.array.item_price));
        int setPropertyPrice = ItemPriceValue.indexOf(PropertyPriceType);
        property_price_spinner.setSelection(setPropertyPrice);

        if (pcheck1.equals("House")) {
            poolingHouse.setChecked(true);
        }
        if (pcheck2.equals("Land")) {
            poolingland.setChecked(true);
        }
        yearOfPurchase.setText(yearOfPurchaseValue);
        valueOfPurchase.setText(valueOfPurchaseValue);
        int setPoolingPrice = ItemPriceValue.indexOf(PoolingPriceType);
        Pooling_price_spinner.setSelection(setPoolingPrice);

    }

    private class RestApii extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {

            String text = null;
            text = POST(UrlClass.URL_DATA_SEND);
            Log.d(TAG, "RAW resposne" + text);

            return text;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub

            if (mProgressDlg != null && mProgressDlg.isShowing()) {
                mProgressDlg.dismiss();
            }


            Log.d(TAG, "on post resposne" + result);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(result);
                dataSentStatus = jsonObject.getString("status");
                Log.d(TAG, "onPostExecute: " + dataSentStatus);

            } catch (JSONException e) {
                Log.d(TAG, "dataSentStatus: " + e.toString());
                e.printStackTrace();
            }


            if (dataSentStatus.equals("200")) {
                Toast.makeText(context, "Data sent successfully", Toast.LENGTH_SHORT).show();
                previewImageSite.setVisibility(View.GONE);

//                surveyorId.setText(SurveyIdNumValue);
//                NameOfSurveyor.setText(NameOfSurveyorValue);
//                HouseHoldId.setText(HouseHoldIdValue);
//                Municipality.setText(MunicipalityValue);
//                Ward.setText(WardValue);
//                Address.setText(HouseholdTypologyValue);
//                Age.setText(AgeValue);
//                Address.setText(SexValue);
//                Email.setText(EmailValue);
//                WorkingFamilyMemberNumber.setText(WorkingFamilyMemberNumberValue);
//                NumOfHusband.setText(NumOfHusbandValue);
//                NumOfWife.setText(NumOfWifeValue);
//                NumOfChildren.setText(NumOfChildrenValue);
//                NumOfRelatives.setText(NumOfRelativesValue);
//                NumOfothers.setText(NumOfothersValue);
//
//                tvsmName.setText(child2_sm_name);
//                tvchild_motherName.setText(child2_mother_name);
//                tvMultispinnerVaccination.setText(vaccination);
////                tvchildrenWardNo.setText(child2_ward_no);
//                tvchild2_age.setText(child2_age);
//                tvchild2_sex.setText(child2_sex);
//                tvWeightOfChild.setText(weight);
//                tvVisitDate.setText(visit_date);
//                tvVisitTime.setText(visit_time);
//                tvDateOfBirth.setText(date_of_birth);
//                tvcontact_details_lactating_women.setText(contact_no_lactating_women);
//                previewImageSite.setImageBitmap(thumbnail);
//
//
                long date = System.currentTimeMillis();
//
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy h:mm a");
                dateString = sdf.format(date);
//                new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
//                        .setTitleText("")
//                        .setContentText("Data sent successfully!")
//                        .show();
                convertDataToJson();
                String[] data = new String[]{"1", "Household survay", dateString, jsonToSend, jsonLatLangArray,
                        "" + mCurrentPhotoPath, "Sent", "0"};
////
                Log.d(TAG, "sent data form: " + jsonToSend);
////


                Database_SentForm dataBaseSent = new Database_SentForm(context);
                dataBaseSent.open();
//                long id =
                dataBaseSent.insertIntoTable_Main(data);
//                Log.e("dbID", "" + id);
                dataBaseSent.close();

                if (CheckValues.isFromSavedFrom) {
                    Log.e(TAG, "onPostExecute: FormID : " + formid);
                    Database_SaveForm dataBase_NotSent = new Database_SaveForm(context);
                    dataBase_NotSent.open();
                    dataBase_NotSent.dropRowNotSentForms(formid);
//                    Log.e("dbID", "" + id);
                    dataBase_NotSent.close();
//
                    DisplayMetrics metrics = context.getResources().getDisplayMetrics();
                    int width = metrics.widthPixels;
                    int height = metrics.heightPixels;

//                    Toast.makeText(context, "Data sent successfully", Toast.LENGTH_SHORT).show();

                    final Dialog showDialog = new Dialog(context);
                    showDialog.setContentView(R.layout.thank_you_popup);
                    final Button yes = (Button) showDialog.findViewById(R.id.buttonYes);
                    final Button no = (Button) showDialog.findViewById(R.id.buttonNo);

                    showDialog.setTitle("Successfully Sent");
                    showDialog.setCancelable(false);
                    showDialog.show();
                    showDialog.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);

                    yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialog.dismiss();
                            Intent intent = new Intent(SurveyMain.this, SurveyMain.class);
                            startActivity(intent);
//                                finish();
                        }
                    });
//
                    no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialog.dismiss();
                            Intent intent = new Intent(SurveyMain.this, MainActivity.class);
                            startActivity(intent);
                        }
                    });
                }
//
                if (!CheckValues.isFromSavedFrom) {
                    DisplayMetrics metrics = context.getResources().getDisplayMetrics();
                    int width = metrics.widthPixels;
                    int height = metrics.heightPixels;

//                    Toast.makeText(context, "Data sent successfully", Toast.LENGTH_SHORT).show();

                    final Dialog showDialog = new Dialog(context);
                    showDialog.setContentView(R.layout.thank_you_popup);
                    final Button yes = (Button) showDialog.findViewById(R.id.buttonYes);
                    final Button no = (Button) showDialog.findViewById(R.id.buttonNo);

                    showDialog.setTitle("Successfully Sent");
                    showDialog.setCancelable(false);
                    showDialog.show();
                    showDialog.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);

                    yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialog.dismiss();
                            Intent intent = new Intent(SurveyMain.this, SurveyMain.class);
                            startActivity(intent);
//                                finish();
                        }
                    });

                    no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialog.dismiss();
                            Intent intent = new Intent(SurveyMain.this, MainActivity.class);
                            startActivity(intent);
                        }
                    });
                }

            }
        }


        public String POST(String urll) {
            String result = "";
            URL url;

            try {
                url = new URL(urll);
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
                        .appendQueryParameter("data", jsonToSend);

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
                        result += line;
                    }
                } else {
                    result = "";
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

    }

    // display current date
    public void setCurrentDateOnView() {

//        tvDisplayDate = (TextView) findViewById(R.id.tvDate);
//        dpResult = (DatePicker) findViewById(R.id.dpResult);

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        // set current date into textview
        DateOfSurvey.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(month + 1).append("-").append(day).append("-")
                .append(year).append(" "));

        // set current date into datepicker
//        SurveyDate.init(year, month, day, null);

    }

    public void addListenerOnButton() {

//        btnChangeDate = (Button) findViewById(R.id.btnChangeDate);

        DateOfSurvey.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                showDialog(DATE_DIALOG_ID);

            }

        });

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                // set date picker as current date
                return new DatePickerDialog(this, datePickerListener,
                        year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            // set selected date into textview
            DateOfSurvey.setText(new StringBuilder().append(month + 1)
                    .append("-").append(day).append("-").append(year)
                    .append(" "));

            // set selected date into datepicker also
//            SurveyDate.init(year, month, day, null);

        }
    };

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(SurveyMain.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(SurveyMain.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(SurveyMain.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(SurveyMain.this, new String[]{permission}, requestCode);
            }
        } else {
//            Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }

    private void askForGPS() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        result = LocationServices.SettingsApi.checkLocationSettings(client, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(SurveyMain.this, GPS_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {

                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            if (ActivityCompat.checkSelfPermission(SurveyMain.this, permissions[0]) == PackageManager.PERMISSION_GRANTED || grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                askForGPS();
                Log.v("Susan", "Permission: " + permissions[0] + "was " + grantResults[0]);
                //resume tasks needing this permission
                Toast.makeText(SurveyMain.this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
//                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }

        } catch (ArrayIndexOutOfBoundsException e) {
            e.getMessage();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//
//            galleryAddPic();
//            setPic(previewImageSite, mCurrentPhotoPath);
//
//
//        }
        Log.d(TAG ,"OnACtivity Result :" + data + " : " + requestCode + " : " + resultCode);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            try {
//                if(data == null){
//                    Toast.makeText(SurveyMain.this, "unable to create image thumbnail \n please try again", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                galleryAddPic();
                setPic(previewImageSite, mCurrentPhotoPath);
            } catch (Exception e){
                Log.d(TAG, "OnACtivity Result : " + e.toString());
                e.printStackTrace();
            }



        }

        if (requestCode == GEOPOINT_RESULT_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    String location = data.getStringExtra(LOCATION_RESULT);

                    String string = location;
                    String[] parts = string.split(" ");
                    String split_lat = parts[0]; // 004
                    String split_lon = parts[1]; // 034556


                    if (!split_lat.equals("") && !split_lon.equals("")) {
                        GPS_TRACKER_FOR_POINT.GPS_POINT_INITILIZED = true;

                        finalLat = Double.parseDouble(split_lat);
                        finalLong = Double.parseDouble(split_lon);

                        LatLng d = new LatLng(finalLat, finalLong);
//
                        listCf.add(d);
                        isGpsTaken = true;

                        try {
                            JSONObject locationdata = new JSONObject();
                            locationdata.put("latitude", finalLat);
                            locationdata.put("longitude", finalLong);

                            jsonArrayGPS.put(locationdata);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

//                                btnPreviewMap.setEnabled(true);
                        startGps.setText("Location Recorded");
                    }


//                    Toast.makeText(this.context, location, Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    }

    @Override
    public void onBackPressed() {

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        final Dialog showDialog = new Dialog(context);
        showDialog.setContentView(R.layout.close_dialog_english);
        final Button yes = (Button) showDialog.findViewById(R.id.buttonYes);
        final Button no = (Button) showDialog.findViewById(R.id.buttonNo);

        showDialog.setTitle("WARNING !!!");
        showDialog.setCancelable(false);
        showDialog.show();
        showDialog.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog.dismiss();
                finish();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog.dismiss();
            }
        });
    }

    private void uniqueCode() {
        String D = DistrictValue.substring(0, 1);
        String M = MunicipalityValue.substring(0, 3);

        uniCode = D + M + WardValue + HouseHoldIdValue;

        Log.e("main_activity", "unique code: " + uniCode);
    }

    private int Sub(int num) {
        if (num > 0) {
            Subnumber = 0;
            Subnumber = num - 1;
        }
        return Subnumber;
    }

    public int Add(int num) {
        Addnumber = 0;
        Addnumber = num + 1;
        Log.d("", "Addnumber: " + Addnumber);
        return Addnumber;
    }

    public void splitString(String htvString) {
        int commas = 0;
        ArrayList<String> topology = new ArrayList<>();
        for (int i = 0; i < htvString.length(); i++) {
            if (htvString.charAt(i) == ',') commas++;
        }

        Log.d(TAG, "commas: " + commas);
        for (int i = 0; i < commas; i++) {
            String[] parts = htvString.split(", ");
            topology.add(parts[i]);

        }
        Log.e("Household_Survey", "HouseholdTypologyValue1 :" + topology);
//        single family detached, Multi family house, apartment block, mixed use block, number of floors,

//
//        Log.e("Household_Survey", "HouseholdTypologyValue2 :" + check1);
//        Log.e("Household_Survey", "HouseholdTypologyValue3 :" + check2);
//        Log.e("Household_Survey", "HouseholdTypologyValue4 :" + check3);
//        Log.e("Household_Survey", "HouseholdTypologyValue5 :" + check4);
//        Log.e("Household_Survey", "HouseholdTypologyValue6 :" + check5);

        for (int j = 0; j < commas; j++) {
            String topologyValue = topology.get(j);
            Log.e(TAG, "splitString:  topologyValue " + topologyValue);
            if (topologyValue.equals("single family detached")) {
                check1 = topologyValue;
            } else if (topologyValue.equals("Multi family house")) {
                check2 = topologyValue;

            } else if (topologyValue.equals("apartment block")) {
                check3 = topologyValue;

            } else if (topologyValue.equals("mixed use block")) {
                check4 = topologyValue;

            } else if (topologyValue.equals("number of floors")) {
                check5 = topologyValue;

            }
        }

    }

    public void splitString1(String htvString) {
        int commas = 0;
        ArrayList<String> pooling = new ArrayList<>();
        for (int i = 0; i < htvString.length(); i++) {
            if (htvString.charAt(i) == ',') commas++;
        }
        for (int i = 0; i < commas; i++) {
            String[] parts = htvString.split(", ");
            pooling.add(parts[i]);

        }
        for (int j = 0; j < commas; j++) {
            String poolingValue = pooling.get(j);
            Log.e(TAG, "splitString:  topologyValue " + poolingValue);
            if (poolingValue.equals("House")) {
                pcheck1 = poolingValue;
            }
            if (poolingValue.equals("Land")) {
                pcheck2 = poolingValue;
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        switch (buttonView.getId()) {
            case R.id.single_family_detached:
                if (SingleFamilyDetached.isChecked() == true) {
                    HouseholdTypologyValue1 = "single family detached" + ", ";
                } else {
                    HouseholdTypologyValue1 = "";
                }


                break;
            case R.id.multy_family_house:
                if (MultyFamilyhouse.isChecked() == true) {
                    HouseholdTypologyValue2 = "Multi family house" + ", ";
                } else {
                    HouseholdTypologyValue2 = "";
                }


                break;
            case R.id.apartment_block:
                if (ApartmentBlock.isChecked() == true) {
                    HouseholdTypologyValue3 = "apartment block" + ", ";
                } else {
                    HouseholdTypologyValue3 = "";
                }


                break;
            case R.id.mixed_use_block:
                if (MixedUseBlock.isChecked() == true) {
                    HouseholdTypologyValue4 = "mixed use block" + ", ";
                } else {
                    HouseholdTypologyValue4 = "";
                }

                break;

            case R.id.number_of_floors:
                if (NumberOfFloors.isChecked() == true) {
                    HouseholdTypologyValue5 = "number of floors" + ", ";
                } else {
                    HouseholdTypologyValue5 = "";
                }

                break;
            case R.id.land_pooling_house:
                if (poolingHouse.isChecked() == true) {
                    Pooling1 = "House" + ", ";
                } else {
                    Pooling1 = "";
                }

                break;

            case R.id.land_pooling_land:
                if (poolingland.isChecked() == true) {
                    Pooling2 = "Land" + ", ";
                } else {
                    Pooling2 = "";
                }

                break;


//            HouseholdTypologyValue = topology;
        }
        PoolingValue = Pooling1 + Pooling2;
        HouseholdTypologyValue = HouseholdTypologyValue1 + HouseholdTypologyValue2 + HouseholdTypologyValue3 + HouseholdTypologyValue4 + HouseholdTypologyValue5;
        Log.e("Household_Survey", "HouseholdTypologyValue :" + HouseholdTypologyValue);
    }

    String check1 = "", check2 = "", check3 = "", check4 = "", check5 = "";
    String PoolingValue = "", Pooling1 = "", Pooling2 = "", pcheck1 = "", pcheck2 = "";
    String HouseholdTypologyValue1 = "", HouseholdTypologyValue2 = "", HouseholdTypologyValue3 = "", HouseholdTypologyValue4 = "", HouseholdTypologyValue5 = "";
    String uniCode;

    //=====================multispinner selection ============================//

    private MultiSpinner.MultiSpinnerListener onSelectedListener = new MultiSpinner.MultiSpinnerListener() {

        public void onItemsSelected(boolean[] selected) {
            // Do something here with the selected items

//            HusbandIncomeDetail.setVisibility(View.VISIBLE);

            StringBuilder builder = new StringBuilder();

            int max = selected.length;

            for (int i = 0; i < selected.length - 1; i++) {
                if (selected[i]) {
                    HusbandIncomeDetail.setVisibility(View.VISIBLE);
                    HusbandIncomeDetail.setText(builder.append(adapter.getItem(i)).append(","));

                }
            }
            if (selected[0]) {
                HusbandFarmIncome.setVisibility(View.VISIBLE);
            }
            if (selected[6]) {
                HusbandOthersIncomeDetail.setVisibility(View.VISIBLE);
            }

//            Toast.makeText(ChildrenUnderTwo.this, builder.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    private MultiSpinner.MultiSpinnerListener onSelectedListener1 = new MultiSpinner.MultiSpinnerListener() {

        public void onItemsSelected(boolean[] selected) {
            // Do something here with the selected items
//            WifeIncomeDetail.setVisibility(View.VISIBLE);

            StringBuilder builder = new StringBuilder();

            int max = selected.length;

            for (int i = 0; i < selected.length; i++) {
                if (selected[i]) {
                    WifeIncomeDetail.setVisibility(View.VISIBLE);

                    WifeIncomeDetail.setText(builder.append(adapter.getItem(i)).append(","));
                }
            }
            if (selected[0]) {
                WifeFarmIncome.setVisibility(View.VISIBLE);
            }
            if (selected[6]) {
                WifeOthersIncomeDetail.setVisibility(View.VISIBLE);
            }
        }
    };

    private MultiSpinner.MultiSpinnerListener onSelectedListener2 = new MultiSpinner.MultiSpinnerListener() {

        public void onItemsSelected(boolean[] selected) {
            // Do something here with the selected items
//            ChildrenIncomeDetail.setVisibility(View.VISIBLE);

            StringBuilder builder = new StringBuilder();

            int max = selected.length;

            for (int i = 0; i < selected.length; i++) {
                if (selected[i]) {
                    ChildrenIncomeDetail.setVisibility(View.VISIBLE);

                    ChildrenIncomeDetail.setText(builder.append(adapter.getItem(i)).append(","));
                }
            }
            if (selected[0]) {
                ChildrenFarmIncome.setVisibility(View.VISIBLE);
            }
            if (selected[6]) {
                ChildrenOthersIncomeDetail.setVisibility(View.VISIBLE);
            }
        }
    };

    private MultiSpinner.MultiSpinnerListener onSelectedListener3 = new MultiSpinner.MultiSpinnerListener() {

        public void onItemsSelected(boolean[] selected) {
            // Do something here with the selected items
//            RelativesIncomeDetail.setVisibility(View.VISIBLE);

            StringBuilder builder = new StringBuilder();

            int max = selected.length;

            for (int i = 0; i < selected.length; i++) {
                if (selected[i]) {
                    RelativesIncomeDetail.setVisibility(View.VISIBLE);

                    RelativesIncomeDetail.setText(builder.append(adapter.getItem(i)).append(","));
                }
            }
            if (selected[0]) {
                RelativesFarmIncome.setVisibility(View.VISIBLE);
            }
            if (selected[6]) {
                RelativesOthersIncomeDetail.setVisibility(View.VISIBLE);
            }
        }
    };

    private MultiSpinner.MultiSpinnerListener onSelectedListener4 = new MultiSpinner.MultiSpinnerListener() {

        public void onItemsSelected(boolean[] selected) {
            // Do something here with the selected items
//            OthersIncomeDetail.setVisibility(View.VISIBLE);

            StringBuilder builder = new StringBuilder();

            int max = selected.length;

            for (int i = 0; i < selected.length; i++) {
                if (selected[i]) {
                    OthersIncomeDetail.setVisibility(View.VISIBLE);

                    OthersIncomeDetail.setText(builder.append(adapter.getItem(i)).append(","));
                }
            }
            if (selected[0]) {
                OthersFarmIncome.setVisibility(View.VISIBLE);
            }
            if (selected[6]) {
                OthersOthersIncomeDetail.setVisibility(View.VISIBLE);
            }
        }
    };
    //==========================multiselection spinner selection code ends here========================//


    public String currencyChanger(String currency, String currencyValues) {

        String changedCurrency = "";

        if (!currencyValues.equals(null) && !currencyValues.isEmpty() && !currencyValues.equals("")) {

            if (currency.equals("Thousand")) {

                changedCurrency = currencyValues + ",000";
            }

            if (currency.equals("Lakh")) {

                changedCurrency = currencyValues + ",00,000";
            }
            if (currency.equals("Crore")) {

                changedCurrency = currencyValues + ",00,00,000";
            }

        }
        return changedCurrency;
    }

    private void loadImageFromStorage(String path) {
        try {
            previewImageSite.setVisibility(View.VISIBLE);
            File f = new File(path);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            previewImageSite.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            Toast.makeText(getApplicationContext(), "invalid path", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void saveToExternalSorage(Bitmap thumbnail) {
        // TODO Auto-generated method stub
        //String merocinema="Mero Cinema";
//        String movname=getIntent().getExtras().getString("Title");
        Calendar calendar = Calendar.getInstance();
        long timeInMillis = calendar.getTimeInMillis();

        imageName = "Household Survey" + timeInMillis;

        File file1 = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), imageName);
//        if (!file1.mkdirs()) {
//            Toast.makeText(getApplicationContext(), "Not Created", Toast.LENGTH_SHORT).show();
//        }

        if (file1.exists()) file1.delete();
        try {
            FileOutputStream out = new FileOutputStream(file1);
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Toast.makeText(getApplicationContext(), "Saved " + imageName, Toast.LENGTH_SHORT).show();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public String getPath(Uri uri) {
        // just some safety built in
        if (uri == null) {
            // TODO perform some logging or show user feedback
            return null;
        }
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
    }

    public void addImage(String Image) {

//        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
//        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//        previewImageSite.setVisibility(View.VISIBLE);
//        previewImageSite.setImageBitmap(decodedByte);


        if (!Image.equals(null) && !Image.equals("")) {
//
            previewImageSite.setVisibility(View.VISIBLE);

            galleryAddPic();
//                setPic(ivPhotographSiteimageViewPreview1, imagePath1);
            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(Image, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;


            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / 480, photoH / 640);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;

            //bmOptions.inSampleSize = scaleFactor;
            bmOptions.inSampleSize = scaleFactor;

            bmOptions.inPurgeable = true;
            Bitmap bitmap = BitmapFactory.decodeFile(Image, bmOptions);
            previewImageSite.setImageBitmap(bitmap);

//            Constant.takenimg1 = true;

        }
    }


}
