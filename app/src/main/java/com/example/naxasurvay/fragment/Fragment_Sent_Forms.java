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

import com.example.naxasurvay.Database_SentForm;
import com.example.naxasurvay.GridSpacingItemDecorator;
import com.example.naxasurvay.R;
import com.example.naxasurvay.RecyclerItemClickListener;
import com.example.naxasurvay.SavedFormParameters;
import com.example.naxasurvay.Sent_Forms_Adapter;
import com.example.naxasurvay.SurveyMain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RED_DEVIL on 8/7/2017.
 */

public class Fragment_Sent_Forms extends Fragment {

    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    public static List<SavedFormParameters> resultCur = new ArrayList<>();
    Sent_Forms_Adapter ca;
    Context context = getActivity() ;

    public Fragment_Sent_Forms() {
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
        View rootview = inflater.inflate(R.layout.fragment__sent__form_list, container, false);
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
                    Log.d("sent form fragment", "data in save form" + jSon);
                    loadForm(id, jSon, photo, gps, DBid, sent_Status);

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


                            Database_SentForm dataBaseSentform = new Database_SentForm(getActivity());
                            dataBaseSentform.open();
                            dataBaseSentform.dropRowSentForms(resultCur.get(position).dbId);
//                Toast.makeText(getActivity() ,resultCur.get(position).date+ " Long Clicked "+id , Toast.LENGTH_SHORT ).show();
                            dataBaseSentform.close();
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


                Database_SentForm dataBaseSentform = new Database_SentForm(getActivity());
                dataBaseSentform.open();
                int id = (int) dataBaseSentform.updateTable_DeleteFlag(resultCur.get(position).dbId);
//                Toast.makeText(getActivity() ,resultCur.get(position).date+ " Long Clicked "+id , Toast.LENGTH_SHORT ).show();
                dataBaseSentform.close();
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

    public void loadForm(String formId, String jsonData , String photo , String gps, String DBid , String status){
        switch (formId){

            case "1" :
                Intent intent1 = new Intent(getActivity(), SurveyMain.class);
                intent1.putExtra("JSON1", jsonData);
                intent1.putExtra("photo", photo);
                intent1.putExtra("gps", gps);
                intent1.putExtra("DBid", DBid);
                intent1.putExtra("sent_Status", status);
//                intent1.putExtra("form_name", form_name);
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
        Database_SentForm dataBaseSentform = new Database_SentForm(getActivity());
        dataBaseSentform.open();

        resultCur.addAll(dataBaseSentform.getAllSentForms());
        Log.e("sent form fragment", "data in save form" + dataBaseSentform.getAllSentForms());
        Log.e("sent form fragment", "data in save form" + resultCur.toString());
        fillTable();
    }

    public void fillTable() {
        Log.e("FILLTABLE", "INSIDE FILL TABLE");
        ca = new Sent_Forms_Adapter(resultCur);
        recyclerView.setAdapter(ca);
        Log.e("FILLTABLE", "AFTER FILL TABLE");
//        CheckValues.setValue();
    }


}
