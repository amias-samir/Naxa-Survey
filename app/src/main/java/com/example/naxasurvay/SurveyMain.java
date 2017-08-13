package com.example.naxasurvay;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

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
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.R.attr.cacheColorHint;
import static android.R.attr.id;

public class SurveyMain extends AppCompatActivity {

    private static final String TAG = "naxa_Survay";
    ProgressDialog mProgressDlg;
    int CAMERA_PIC_REQUEST = 2;
    String imagePath, encodedImage = "", imageName = "no_photo",image;
    Bitmap thumbnail;

    GPS_TRACKER_FOR_POINT gps;
    List<Location> gpslocation = new ArrayList<>();
    double finalLat;
    double finalLong;
    boolean isGpsTracking = false;
    boolean isGpsTaken = false;

    NetworkInfo networkInfo;
    ConnectivityManager connectivityManager;

    String latLangArray = "", jsonLatLangArray = "";


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

    @BindView(R.id.id_code)
    EditText HouseHoldId;
    @BindView(R.id.Survay_municipality)
    AutoCompleteTextView Municipality;
    @BindView(R.id.Survay_ward)
    AutoCompleteTextView Ward;
    @BindView(R.id.Survay_address)
    AutoCompleteTextView Address;
    @BindView(R.id.HouseHold_type)
    RadioGroup HouseholdTypology;

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

//    @BindView(R.id.Survay_Family_member)
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
    @BindView(R.id.spinnerMulti_Wife)
    MultiSpinner spinnerWife;
    @BindView(R.id.selected_income_details_wife)
    EditText WifeIncomeDetail;
    @BindView(R.id.spinnerMulti_children)
    MultiSpinner spinnerChildren;
    @BindView(R.id.selected_income_details_children)
    EditText ChildrenIncomeDetail;
    @BindView(R.id.spinnerMulti_Relative)
    MultiSpinner spinnerRelatives;
    @BindView(R.id.selected_income_details_relatives)
    EditText RelativesIncomeDetail;
    @BindView(R.id.spinnerMulti_Others)
    MultiSpinner spinnerOthers;
    @BindView(R.id.selected_income_details_others)
    EditText OthersIncomeDetail;

    @BindView(R.id.Survay_average_monthlyIncome_of_husband)
    AutoCompleteTextView AverageMonthlyIncomeOfHusband;
    @BindView(R.id.Survay_average_monthlyIncome_of_wife)
    AutoCompleteTextView AverageMonthlyIncomeOfWife;
    @BindView(R.id.Survay_average_monthlyIncome_of_children)
    AutoCompleteTextView AverageMonthlyIncomeOfChildren;
    @BindView(R.id.Survay_average_monthlyIncome_of_relatives)
    AutoCompleteTextView AverageMonthlyIncomeOfRelatives;
    @BindView(R.id.Survay_average_monthlyIncome_of_others)
    AutoCompleteTextView AverageMonthlyIncomeOfOthers;

    @BindView(R.id.land_in_Anna)
    AutoCompleteTextView LandInAnna;
    @BindView(R.id.total_land_price)
    AutoCompleteTextView TotalLandPrice;
    @BindView(R.id.property_in_Anna)
    AutoCompleteTextView PropertyInAnna;
    @BindView(R.id.total_property_price)
    AutoCompleteTextView TotalPropertyPrice;

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

    String SurveyIdNumValue, NameOfSurveyorValue, DateOfSurveyValue, HouseHoldIdValue, MunicipalityValue, WardValue, AddressValue,
            HouseholdTypologyValue, AgeValue, SexValue, EmailValue, WorkingFamilyMemberNumberValue, NumOfHusbandValue, NumOfWifeValue,
            NumOfChildrenValue, NumOfRelativesValue, NumOfothersValue, HusbandIncomeSource, WifeIncomeSource, ChindrenIncomeSource,
            RelativesIncomeSource, OthersIncomeSource, AverageMonthlyIncomeOfHusbandValue, AverageMonthlyIncomeOfWifeValue,
            AverageMonthlyIncomeOfChildrenValue, AverageMonthlyIncomeOfRelativesValue, AverageMonthlyIncomeOfOthersValue,
            LandInAnnaValue, TotalLandPriceValue, PropertyInAnnaValue, TotalPropertyPriceValue;

    String dataSentStatus, dateString;

    GoogleApiClient client;
    LocationRequest mLocationRequest;
    PendingResult<LocationSettingsResult> result;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.survey_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Household Survey");

        ButterKnife.bind(this);


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
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
            }
        });

        startGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GPS_SETTINGS.equals(true) || GPS_TRACKER_FOR_POINT.GPS_POINT_INITILIZED) {

                    if (gps.canGetLocation()) {
                        gpslocation.add(gps.getLocation());
                        finalLat = gps.getLatitude();
                        finalLong = gps.getLongitude();
                        if (finalLat != 0) {
                            try {
                                JSONObject data = new JSONObject();
                                data.put("latitude", finalLat);
                                data.put("longitude", finalLong);

                                jsonArrayGPS.put(data);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            LatLng location = new LatLng(finalLat, finalLong);

                            listCf.add(location);
                            isGpsTaken = true;
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Your Location is - \nLat: " + finalLat
                                            + "\nLong: " + finalLong, Toast.LENGTH_SHORT)
                                    .show();
                            stringBuilder.append("[" + finalLat + "," + finalLong + "]" + ",");
                        }

                    }
                } else {
                    askForGPS();
                    gps = new GPS_TRACKER_FOR_POINT(SurveyMain.this);
                    Default_DIalog.showDefaultDialog(context, R.string.app_name, "Please try again, Gps not initialized");
//                        com.example.naxasurvay.gps.showSettingsAlert();
                }
            }
        });

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                SurveyIdNumValue = surveyorId.getText().toString();
                NameOfSurveyorValue = NameOfSurveyor.getText().toString();
                DateOfSurveyValue = DateOfSurvey.getText().toString();

                HouseHoldIdValue = HouseHoldId.getText().toString();
                MunicipalityValue = Municipality.getText().toString();
                WardValue = Ward.getText().toString();
                AddressValue = Address.getText().toString();
                RadioButton checkedBtn1 = (RadioButton) findViewById(HouseholdTypology.getCheckedRadioButtonId());
                HouseholdTypologyValue = checkedBtn1.getText().toString();


                AgeValue = Age.getText().toString();
//        String SexValue = Sex.getText().toString();

                RadioButton checkedBtn = (RadioButton) findViewById(Sex.getCheckedRadioButtonId());
                SexValue = checkedBtn.getText().toString();

                Log.d("", "onCreate: " + SexValue);
                EmailValue = Email.getText().toString();

//                FamilyMemberNumberValue = FamilyMemberNumber.getText().toString();
                WorkingFamilyMemberNumberValue = WorkingFamilyMemberNumber.getText().toString();

                NumOfHusbandValue = NumOfHusband.getText().toString();
                NumOfWifeValue = NumOfWife.getText().toString();
                NumOfChildrenValue = NumOfChildren.getText().toString();
                NumOfRelativesValue = NumOfRelatives.getText().toString();
                NumOfothersValue = NumOfothers.getText().toString();

                HusbandIncomeSource = HusbandIncomeDetail.getText().toString();
                WifeIncomeSource = WifeIncomeDetail.getText().toString();
                ChindrenIncomeSource = ChildrenIncomeDetail.getText().toString();
                RelativesIncomeSource = RelativesIncomeDetail.getText().toString();
                OthersIncomeSource = OthersIncomeDetail.getText().toString();

                AverageMonthlyIncomeOfHusbandValue = AverageMonthlyIncomeOfHusband.getText().toString();
                AverageMonthlyIncomeOfWifeValue = AverageMonthlyIncomeOfWife.getText().toString();
                AverageMonthlyIncomeOfChildrenValue = AverageMonthlyIncomeOfChildren.getText().toString();
                AverageMonthlyIncomeOfRelativesValue = AverageMonthlyIncomeOfRelatives.getText().toString();
                AverageMonthlyIncomeOfOthersValue = AverageMonthlyIncomeOfOthers.getText().toString();

//                AreaOfLandValue = AreaOfLand.getText().toString();
//                PriceOfLandValue = PriceOfLand.getText().toString();
                LandInAnnaValue = LandInAnna.getText().toString();
                TotalLandPriceValue = TotalLandPrice.getText().toString();
                PropertyInAnnaValue = PropertyInAnna.getText().toString();
                TotalPropertyPriceValue = TotalPropertyPrice.getText().toString();

                jsonLatLangArray = jsonArrayGPS.toString();
                image = encodedImage;

                convertDataToJson();

                DisplayMetrics metrics = context.getResources().getDisplayMetrics();
                final int width = metrics.widthPixels;
                int height = metrics.heightPixels;

                final Dialog showDialog = new Dialog(context);
                showDialog.setContentView(R.layout.date_input_layout);
                final EditText FormNameToInput = (EditText) showDialog.findViewById(R.id.input_tableName);
                final EditText dateToInput = (EditText) showDialog.findViewById(R.id.input_date);

                if (formNameSavedForm.equals("")) {
                    FormNameToInput.setText("Household Survay");
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
                                "" + imageName, "Not Sent", "0"};

//                        String[] data = new String[]{"1", formName, dateDataCollected, jsonToSend,
//                                "" , "Not Sent", "0"};

                        Database_SaveForm dataBaseSaveform = new Database_SaveForm(context);
                        dataBaseSaveform.open();
                        long id = dataBaseSaveform.insertIntoTable_Main(data);
                        Log.e("dbID", "" + id);
                        dataBaseSaveform.close();

                        Toast.makeText(SurveyMain.this, "Data saved successfully", Toast.LENGTH_SHORT).show();
                        showDialog.dismiss();

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
                });
            }
        });

        // add click listener to Button "POST"
        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isGpsTracking) {
                    Toast.makeText(getApplicationContext(), "Please end GPS Tracking.", Toast.LENGTH_SHORT).show();
                } else {

                    if (isGpsTaken) {

                        SurveyIdNumValue = surveyorId.getText().toString();
                        NameOfSurveyorValue = NameOfSurveyor.getText().toString();
                        DateOfSurveyValue = DateOfSurvey.getText().toString();

                        HouseHoldIdValue = HouseHoldId.getText().toString();
                        MunicipalityValue = Municipality.getText().toString();
                        WardValue = Ward.getText().toString();
                        AddressValue = Address.getText().toString();
                        RadioButton checkedBtn1 = (RadioButton) findViewById(HouseholdTypology.getCheckedRadioButtonId());
                        HouseholdTypologyValue = checkedBtn1.getText().toString();


                        AgeValue = Age.getText().toString();

                        RadioButton checkedBtn = (RadioButton) findViewById(Sex.getCheckedRadioButtonId());
                        SexValue = checkedBtn.getText().toString();

                        Log.d("", "onCreate: " + SexValue);
                        EmailValue = Email.getText().toString();

                        WorkingFamilyMemberNumberValue = WorkingFamilyMemberNumber.getText().toString();

                        NumOfHusbandValue = NumOfHusband.getText().toString();
                        NumOfWifeValue = NumOfWife.getText().toString();
                        NumOfChildrenValue = NumOfChildren.getText().toString();
                        NumOfRelativesValue = NumOfRelatives.getText().toString();
                        NumOfothersValue = NumOfothers.getText().toString();

                        HusbandIncomeSource = HusbandIncomeDetail.getText().toString();
                        WifeIncomeSource = WifeIncomeDetail.getText().toString();
                        ChindrenIncomeSource = ChildrenIncomeDetail.getText().toString();
                        RelativesIncomeSource = RelativesIncomeDetail.getText().toString();
                        OthersIncomeSource = OthersIncomeDetail.getText().toString();

                        AverageMonthlyIncomeOfHusbandValue = AverageMonthlyIncomeOfHusband.getText().toString();
                        AverageMonthlyIncomeOfWifeValue = AverageMonthlyIncomeOfWife.getText().toString();
                        AverageMonthlyIncomeOfChildrenValue = AverageMonthlyIncomeOfChildren.getText().toString();
                        AverageMonthlyIncomeOfRelativesValue = AverageMonthlyIncomeOfRelatives.getText().toString();
                        AverageMonthlyIncomeOfOthersValue = AverageMonthlyIncomeOfOthers.getText().toString();

                        LandInAnnaValue = LandInAnna.getText().toString();
                        TotalLandPriceValue = TotalLandPrice.getText().toString();
                        PropertyInAnnaValue = PropertyInAnna.getText().toString();
                        TotalPropertyPriceValue = TotalPropertyPrice.getText().toString();


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
                                    convertDataToJson();
                                    sendDatToserver();
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

    public void initilizeUI() {
        Intent intent = getIntent();
        if (intent.hasExtra("JSON1")) {


            CheckValues.isFromSavedFrom = true;
            startGps.setEnabled(false);
            isGpsTaken = true;
//            previewMap.setEnabled(true);
            Bundle bundle = intent.getExtras();
            String jsonToParse = (String) bundle.get("JSON1");
            imageName = (String) bundle.get("photo");
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

                loadImageFromStorage(path);

                addImage();
            }
            try {
                //new adjustment
                Log.e("HouseholdSurvey", "" + jsonToParse);
                Log.d(TAG, "HouseholdSurvey: " + jsonToParse);
//                parseArrayGPS(gpsLocationtoParse);
                parseJson(jsonToParse);
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

    // data convert
    public void convertDataToJson() {
        //function in the activity that corresponds to the hwc_human_casulty button

        try {

            JSONObject header = new JSONObject();

//            header.put("tablename", "");
            header.put("surveyor_id", SurveyIdNumValue);
            header.put("name_of_surveyor", NameOfSurveyorValue);
            header.put("date_of_survey", DateOfSurveyValue);
            header.put("house_id", HouseHoldIdValue);
            header.put("name_of_municipality", MunicipalityValue);
            header.put("ward_no", WardValue);
            header.put("address", AddressValue);
            header.put("house_typology", HouseholdTypologyValue);
            header.put("age", AgeValue);
            header.put("sex", SexValue);
            header.put("email", EmailValue);
//            header.put("num_of_family_memb", FamilyMemberNumberValue);
            header.put("num_of_working_family_memb", WorkingFamilyMemberNumberValue);
            header.put("husband_no", NumOfHusbandValue);
            header.put("wife_no", NumOfWifeValue);
            header.put("children_no", NumOfChildrenValue);
            header.put("relatives_no", NumOfRelativesValue);
            header.put("others_no", NumOfothersValue);

            header.put("income_source_of_husband", HusbandIncomeSource);
            header.put("income_source_of_wife", WifeIncomeSource);
            header.put("income_source_of_children", ChindrenIncomeSource);
            header.put("income_source_of_relatives", RelativesIncomeSource);
            header.put("income_source_of_others", OthersIncomeSource);

            header.put("average_income_of_husband", AverageMonthlyIncomeOfHusbandValue);
            header.put("average_income_of_wife", AverageMonthlyIncomeOfWifeValue);
            header.put("average_income_of_children", AverageMonthlyIncomeOfChildrenValue);
            header.put("average_income_of_relatives", AverageMonthlyIncomeOfRelativesValue);
            header.put("average_income_of_others", AverageMonthlyIncomeOfOthersValue);
//            header.put("area_land_property", AreaOfLandValue);
//            header.put("price_land_property", PriceOfLandValue);
            header.put("land_anna", LandInAnnaValue);
            header.put("land_total_price", TotalLandPriceValue);
            header.put("property_anna", PropertyInAnnaValue);
            header.put("property_total_price", TotalPropertyPriceValue);

            header.put("latitude", finalLat);
            header.put("longitude", finalLong);
            header.put("photo", encodedImage);


            jsonToSend = header.toString();
            Log.e("main_activity", "convertDataToJson: " + jsonToSend);


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
        MunicipalityValue = jsonObj.getString("name_of_municipality");
        WardValue = jsonObj.getString("ward_no");
        AddressValue = jsonObj.getString("address");
        HouseholdTypologyValue = jsonObj.getString("house_typology");
        AgeValue = jsonObj.getString("age");
        SexValue = jsonObj.getString("sex");
        EmailValue = jsonObj.getString("email");
//        FamilyMemberNumberValue = jsonObj.getString("num_of_family_memb");
        WorkingFamilyMemberNumberValue = jsonObj.getString("num_of_working_family_memb");
        NumOfHusbandValue = jsonObj.getString("husband_no");
        NumOfWifeValue = jsonObj.getString("wife_no");
        NumOfChildrenValue = jsonObj.getString("children_no");
        NumOfRelativesValue = jsonObj.getString("relatives_no");
        NumOfothersValue = jsonObj.getString("others_no");

        HusbandIncomeSource = jsonObj.getString("income_source_of_husband");
        WifeIncomeSource = jsonObj.getString("income_source_of_wife");
        ChindrenIncomeSource = jsonObj.getString("income_source_of_children");
        RelativesIncomeSource = jsonObj.getString("income_source_of_relatives");
        OthersIncomeSource = jsonObj.getString("income_source_of_others");

        AverageMonthlyIncomeOfHusbandValue = jsonObj.getString("average_income_of_husband");
        AverageMonthlyIncomeOfWifeValue = jsonObj.getString("average_income_of_wife");
        AverageMonthlyIncomeOfChildrenValue = jsonObj.getString("average_income_of_children");
        AverageMonthlyIncomeOfRelativesValue = jsonObj.getString("average_income_of_relatives");
        AverageMonthlyIncomeOfOthersValue = jsonObj.getString("average_income_of_others");

        LandInAnnaValue = jsonObj.getString("land_anna");
        TotalLandPriceValue = jsonObj.getString("land_total_price");
        PropertyInAnnaValue = jsonObj.getString("property_anna");
        TotalPropertyPriceValue = jsonObj.getString("property_total_price");

//        finalLat = Double.parseDouble(jsonObj.getString("lat"));
//        finalLong = Double.parseDouble(jsonObj.getString("lon"));
//        LatLng d = new LatLng(finalLat, finalLong);
//        listCf.add(d);

            encodedImage = jsonObj.getString("photo");


//        Log.e("Children Under Two", "Parsed data " + child2_vdc_name + child2_ward_no + weight);
//
        surveyorId.setText(SurveyIdNumValue);
        NameOfSurveyor.setText(NameOfSurveyorValue);
        HouseHoldId.setText(HouseHoldIdValue);
        Municipality.setText(MunicipalityValue);
        Ward.setText(WardValue);
        Address.setText(HouseholdTypologyValue);
        Age.setText(AgeValue);
        Address.setText(SexValue);
        Email.setText(EmailValue);
        WorkingFamilyMemberNumber.setText(WorkingFamilyMemberNumberValue);
        NumOfHusband.setText(NumOfHusbandValue);
        NumOfWife.setText(NumOfWifeValue);
        NumOfChildren.setText(NumOfChildrenValue);
        NumOfRelatives.setText(NumOfRelativesValue);
        NumOfothers.setText(NumOfothersValue);

        HusbandIncomeDetail.setText(HusbandIncomeSource);
        if (!HusbandIncomeSource.equals("")) {
            HusbandIncomeDetail.setVisibility(View.VISIBLE);
        }
        WifeIncomeDetail.setText(WifeIncomeSource);
        if (!WifeIncomeSource.equals("")) {
            WifeIncomeDetail.setVisibility(View.VISIBLE);
        }
        ChildrenIncomeDetail.setText(ChindrenIncomeSource);
        if (!ChindrenIncomeSource.equals("")) {
            ChildrenIncomeDetail.setVisibility(View.VISIBLE);
        }
        RelativesIncomeDetail.setText(RelativesIncomeSource);
        if (!RelativesIncomeSource.equals("")) {
            RelativesIncomeDetail.setVisibility(View.VISIBLE);
        }
        OthersIncomeDetail.setText(OthersIncomeSource);
        if (!OthersIncomeSource.equals("")) {
            OthersIncomeDetail.setVisibility(View.VISIBLE);
        }
        AverageMonthlyIncomeOfHusband.setText(AverageMonthlyIncomeOfHusbandValue);
        AverageMonthlyIncomeOfWife.setText(AverageMonthlyIncomeOfWifeValue);
        AverageMonthlyIncomeOfChildren.setText(AverageMonthlyIncomeOfChildrenValue);
        AverageMonthlyIncomeOfRelatives.setText(AverageMonthlyIncomeOfRelativesValue);
        AverageMonthlyIncomeOfOthers.setText(AverageMonthlyIncomeOfOthersValue);
        LandInAnna.setText(LandInAnnaValue);
        TotalLandPrice.setText(TotalLandPriceValue);
        PropertyInAnna.setText(PropertyInAnnaValue);
        TotalPropertyPrice.setText(TotalPropertyPriceValue);

//        tvchild2_age.setText(child2_age);
//        tvchild2_sex.setText(child2_sex);
//        tvWeightOfChild.setText(weight);
//        tvVisitDate.setText(visit_date);
//        tvDateOfBirth.setText(date_of_birth);
//        tvVisitTime.setText(visit_time);
//        tvcontact_details_lactating_women.setText(contact_no_lactating_women);
//
//
//        int setVDCName = vdcNameadpt.getPosition(child2_vdc_name);
//        spinnerVDCName.setSelection(setVDCName);
//
//        int setWardNo = wardNoadpt.getPosition(child2_ward_no);
//        spinnerWardNo.setSelection(setWardNo);
//
//        int setGrothMonitor = growth_monitor_adpt.getPosition(growth_monitor);
//        spinner_growth_monitor.setSelection(setGrothMonitor);
//
////        int setVaccination = vaccination_verification_adpt.getPosition(vaccination);
////        spinner_vaccination_verification.setSelection(setVaccination);
//
////        int setChildWeight = visit_weight_adpt.getPosition(weight);
////        spinner_visit_weight.setSelection(setChildWeight);
//

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
            if (resultCode == Activity.RESULT_OK) {
                Uri selectedImage = data.getData();

                String filePath = getPath(selectedImage);
                String file_extn = filePath.substring(filePath.lastIndexOf(".") + 1);

//                image_name_tv.setText(filePath);
                imagePath = filePath;
                addImage();
//                Toast.makeText(getApplicationContext(),""+encodedImage,Toast.LENGTH_SHORT).show();
//                if (file_extn.equals("img") || file_extn.equals("jpg") || file_extn.equals("jpeg") || file_extn.equals("gif") || file_extn.equals("png")) {
//                    //FINE
//
//                }
//                else{
//                    //NOT IN REQUIRED FORMAT
//                }
            }
        if (requestCode == CAMERA_PIC_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                thumbnail = (Bitmap) data.getExtras().get("data");
                //  ImageView image =(ImageView) findViewById(R.id.Photo);
                // image.setImageBitmap(thumbnail);
                previewImageSite.setVisibility(View.VISIBLE);
                previewImageSite.setImageBitmap(thumbnail);
                saveToExternalSorage(thumbnail);
                addImage();
//                Toast.makeText(getApplicationContext(), "" + encodedImage, Toast.LENGTH_SHORT).show();
            }
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

    public void addImage() {
        File file1 = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), imageName);
        String path = file1.toString();

        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inSampleSize = 1;
        options.inPurgeable = true;
        Bitmap bm = BitmapFactory.decodeFile(path, options);
//        Bitmap bm = BitmapFactory.decodeFile( imagePath ,options);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);


        // bitmap object

        byte[] byteImage_photo = baos.toByteArray();

        //generate base64 string of image
        encodedImage = Base64.encodeToString(byteImage_photo, Base64.DEFAULT);
        Log.e("IMAGE STRING", "-" + encodedImage);

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
//                long date = System.currentTimeMillis();
//
//                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy h:mm a");
//                dateString = sdf.format(date);
////                new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
////                        .setTitleText("")
////                        .setContentText("Data sent successfully!")
////                        .show();
                String[] data = new String[]{"1", "Household survay", dateString, jsonToSend, jsonLatLangArray,
                        "" + imageName, "Sent", "0"};
//
//                Log.d(TAG, "string data: " + data);
////


                Database_SentForm dataBaseSent = new Database_SentForm(context);
                dataBaseSent.open();
                long id = dataBaseSent.insertIntoTable_Main(data);
                Log.e("dbID", "" + id);
                dataBaseSent.close();

                if (CheckValues.isFromSavedFrom) {
                    Log.e(TAG, "onPostExecute: FormID : " + formid);
                    Database_SaveForm dataBase_NotSent = new Database_SaveForm(context);
                    dataBase_NotSent.open();
                    dataBase_NotSent.dropRowNotSentForms(formid);
//                    Log.e("dbID", "" + id);
                    dataBase_NotSent.close();

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
//
//                if (!CheckValues.isFromSavedFrom) {
//                    DisplayMetrics metrics = context.getResources().getDisplayMetrics();
//                    int width = metrics.widthPixels;
//                    int height = metrics.heightPixels;
//
////                    Toast.makeText(context, "Data sent successfully", Toast.LENGTH_SHORT).show();
//
//                    final Dialog showDialog = new Dialog(context);
//                    showDialog.setContentView(R.layout.thank_you_popup);
//                    final Button yes = (Button) showDialog.findViewById(R.id.buttonYes);
//                    final Button no = (Button) showDialog.findViewById(R.id.buttonNo);
//
//                    showDialog.setTitle("Successfully Sent");
//                    showDialog.setCancelable(false);
//                    showDialog.show();
//                    showDialog.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);
//
//                    yes.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            showDialog.dismiss();
//                            Intent intent = new Intent(SurveyMain.this, SurveyMain.class);
//                            startActivity(intent);
////                                finish();
//                        }
//                    });
//
//                    no.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            showDialog.dismiss();
//                            Intent intent = new Intent(SurveyMain.this, MainActivity.class);
//                            startActivity(intent);
//                        }
//                    });
//                }

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

    //=====================multispinner selection ============================//

    private MultiSpinner.MultiSpinnerListener onSelectedListener = new MultiSpinner.MultiSpinnerListener() {

        public void onItemsSelected(boolean[] selected) {
            // Do something here with the selected items

            HusbandIncomeDetail.setVisibility(View.VISIBLE);

            StringBuilder builder = new StringBuilder();

            int max = selected.length;

            for (int i = 0; i < selected.length; i++) {
                if (selected[i]) {
                    HusbandIncomeDetail.setText(builder.append(adapter.getItem(i)).append("&"));
                }

            }

//            Toast.makeText(ChildrenUnderTwo.this, builder.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    private MultiSpinner.MultiSpinnerListener onSelectedListener1 = new MultiSpinner.MultiSpinnerListener() {

        public void onItemsSelected(boolean[] selected) {
            // Do something here with the selected items
            WifeIncomeDetail.setVisibility(View.VISIBLE);

            StringBuilder builder = new StringBuilder();

            int max = selected.length;

            for (int i = 0; i < selected.length; i++) {
                if (selected[i]) {
                    WifeIncomeDetail.setText(builder.append(adapter.getItem(i)).append("&"));
                }
            }
        }
    };

    private MultiSpinner.MultiSpinnerListener onSelectedListener2 = new MultiSpinner.MultiSpinnerListener() {

        public void onItemsSelected(boolean[] selected) {
            // Do something here with the selected items
            ChildrenIncomeDetail.setVisibility(View.VISIBLE);

            StringBuilder builder = new StringBuilder();

            int max = selected.length;

            for (int i = 0; i < selected.length; i++) {
                if (selected[i]) {
                    ChildrenIncomeDetail.setText(builder.append(adapter.getItem(i)).append("&"));
                }
            }
        }
    };

    private MultiSpinner.MultiSpinnerListener onSelectedListener3 = new MultiSpinner.MultiSpinnerListener() {

        public void onItemsSelected(boolean[] selected) {
            // Do something here with the selected items
            RelativesIncomeDetail.setVisibility(View.VISIBLE);

            StringBuilder builder = new StringBuilder();

            int max = selected.length;

            for (int i = 0; i < selected.length; i++) {
                if (selected[i]) {
                    RelativesIncomeDetail.setText(builder.append(adapter.getItem(i)).append("&"));
                }
            }
        }
    };

    private MultiSpinner.MultiSpinnerListener onSelectedListener4 = new MultiSpinner.MultiSpinnerListener() {

        public void onItemsSelected(boolean[] selected) {
            // Do something here with the selected items
            OthersIncomeDetail.setVisibility(View.VISIBLE);

            StringBuilder builder = new StringBuilder();

            int max = selected.length;

            for (int i = 0; i < selected.length; i++) {
                if (selected[i]) {
                    OthersIncomeDetail.setText(builder.append(adapter.getItem(i)).append("&"));
                }
            }
        }
    };
    //==========================multiselection spinner selection code ends here========================//
}
