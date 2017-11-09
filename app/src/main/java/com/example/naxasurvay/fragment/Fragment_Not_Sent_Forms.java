package com.example.naxasurvay.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.naxasurvay.Database_SaveForm;
import com.example.naxasurvay.GridSpacingItemDecorator;
import com.example.naxasurvay.Not_Sent_Forms_Adapter;
import com.example.naxasurvay.R;
import com.example.naxasurvay.RecyclerItemClickListener;
import com.example.naxasurvay.SavedFormParameters;
import com.example.naxasurvay.SurveyMain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RED_DEVIL on 8/7/2017.
 */

public class Fragment_Not_Sent_Forms extends Fragment {
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    public static List<SavedFormParameters> resultCur = new ArrayList<>();
    Not_Sent_Forms_Adapter ca;
    Context context = getActivity() ;


    public Fragment_Not_Sent_Forms() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_not_send_form_list, container, false);
        recyclerView = (RecyclerView) rootview.findViewById(R.id.NewsList);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecorator(1, 5, true));
        createList();

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                alert_editlist(position);

            }

            @Override
            public void onItemLongClick(View view, int position) {


            }
        }));

        return rootview;
    }

    //-------------------------------Method Dialog Box List for << REPORT DETAIL, SEND and DELETE >>-----------------------------------//
    protected void alert_editlist(final int position) {

        // TODO Auto-generated method stub
        final CharSequence[] items = {"Open", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose Action");

        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                if (items[item] == "Open") {
                    String id = resultCur.get(position).formId;
                    String jSon = resultCur.get(position).jSON;
                    String photo = resultCur.get(position).photo;
                    String gps = resultCur.get(position).gps;
                    String DBid = resultCur.get(position).dbId;
                    String sent_Status = resultCur.get(position).status;
                    String form_name = resultCur.get(position).formName;
                    loadForm(id, jSon, photo, gps, DBid, sent_Status, form_name);

                } else if (items[item] == "Delete") {
                    DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
                    int width = metrics.widthPixels;
                    int height = metrics.heightPixels;

                    final Dialog showDialog = new Dialog(getActivity());
                    showDialog.setContentView(R.layout.delete_dialog);
                    TextView tvDisplay = (TextView) showDialog.findViewById(R.id.textViewDefaultDialog);
                    Button btnOk = (Button) showDialog.findViewById(R.id.button_delete);
                    Button cancle = (Button) showDialog.findViewById(R.id.button_cancle);
                    showDialog.setTitle("Are You Sure ??");
                    tvDisplay.setText("Are you sure you want to delete the data ??");
                    showDialog.setCancelable(true);
                    showDialog.show();
                    showDialog.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);

                    btnOk.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub


                            Database_SaveForm dataBasesaveform = new Database_SaveForm(getActivity());
                            dataBasesaveform.open();
                            dataBasesaveform.dropRowNotSentForms(resultCur.get(position).dbId);
//                Toast.makeText(getActivity() ,resultCur.get(position).date+ " Long Clicked "+id , Toast.LENGTH_SHORT ).show();
                            dataBasesaveform.close();
                            showDialog.dismiss();
                            createList();
                        }
                    });
                    cancle.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            showDialog.dismiss();
                        }
                    });

                }
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showDeleteDialog(final int position) {
        DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        final Dialog showDialog = new Dialog(getActivity());
        showDialog.setContentView(R.layout.delete_dialog);
        TextView tvDisplay = (TextView) showDialog.findViewById(R.id.textViewDefaultDialog);
        Button btnOk = (Button) showDialog.findViewById(R.id.button_delete);
        Button cancle = (Button) showDialog.findViewById(R.id.button_cancle);
        showDialog.setCancelable(true);
        showDialog.show();
        showDialog.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);

        btnOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub


                Database_SaveForm dataBasesaveform = new Database_SaveForm(getActivity());
                dataBasesaveform.open();
                dataBasesaveform.dropRowNotSentForms(resultCur.get(position).dbId);
//                Toast.makeText(getActivity() ,resultCur.get(position).date+ " Long Clicked "+id , Toast.LENGTH_SHORT ).show();
                dataBasesaveform.close();
                showDialog.dismiss();
                createList();
            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                showDialog.dismiss();
            }
        });

    }

    public void loadForm(String formId, String jsonData , String photo , String gps, String DBid, String status, String form_name){
//       if (formId.equals(1)) {
        switch (formId){

            case "1" :
           Intent intent1 = new Intent(getActivity(), SurveyMain.class);
           intent1.putExtra("JSON1", jsonData);
           intent1.putExtra("photo", photo);
           intent1.putExtra("gps", gps);
           intent1.putExtra("DBid", DBid);
           intent1.putExtra("sent_Status", status);
           intent1.putExtra("form_name", form_name);
                Log.d("fragment", "initilizeUIF: "+status);
                Log.d("fragment", "initilizeUIF: "+jsonData);
                Log.d("fragment", "initilizeUIF: "+photo);
           startActivity(intent1);
                break;
       }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void createList() {
        resultCur.clear();
//        Single_String_Title newData1 = new Single_String_Title();
//        newData1.title = "CF Detail";
//        resultCur.add(newData1);
        Database_SaveForm dataBasesaveform = new Database_SaveForm(getActivity());
        dataBasesaveform.open();
//        boolean isTableEmpty = dataBaseNepalPublicHealthNotSent.is_TABLE_MAIN_Empty();
//        if(isTableEmpty){
//            Default_DIalog.showDefaultDialog(getActivity() , R.string.app_name , "No data Saved ");
//        }else{
//            int count = dataBaseNepalPublicHealthNotSent.returnTotalNoOf_TABLE_MAIN_NUM() +1;
//            Log.e("ROW_COUNT", "createList: "+ ""+count );
//            for(int i=count ; i>=1 ; i--) {
////                String[] data = dataBaseNepalPublicHealthNotSent.return_Data_TABLE_MAIN(i);
//                String[] data = dataBaseNepalPublicHealthNotSent.return_Data_ID(i);
//                SavedFormParameters savedData = new SavedFormParameters();
//                Log.e("DATA" , "08 "+data[8] +" one: "+ data[1]+" two: "+data[2]);
////                savedData.dbId = data[0];
//                savedData.formId = data[0];
//                savedData.formName = data[1];
//                savedData.date = data[2];
//                savedData.jSON = data[3];
//                savedData.gps = data[4] ;
//                savedData.photo = data[5];
//                savedData.status = data[6];
//                savedData.deletedStatus = data[7];
//                savedData.dbId = data[8];
//
//                if(savedData.dbId!= null) {
//
//                    resultCur.add(savedData);
//                }
//
//
//            }
//        }
        resultCur.addAll(dataBasesaveform.getAllSaveForms());
        fillTable();
    }

    public void fillTable() {
        Log.e("FILLTABLE", "INSIDE FILL TABLE");
        ca = new Not_Sent_Forms_Adapter(resultCur);
        recyclerView.setAdapter(ca);
        Log.e("FILLTABLE", "AFTER FILL TABLE");
//        CheckValues.setValue();
    }



}
