package com.example.naxasurvay;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by RED_DEVIL on 8/6/2017.
 */

public class Default_DIalog {

    public static void showDefaultDialog(Context context, int title, String displayText) {


        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        final Dialog showDialog = new Dialog(context);
        showDialog.setContentView(R.layout.default_dialog);
        TextView tvDisplay = (TextView) showDialog.findViewById(R.id.textViewDefaultDialog);
        Button btnOk = (Button) showDialog.findViewById(R.id.button_defaultDialog);
        showDialog.setTitle(title);
        tvDisplay.setText(displayText);
        showDialog.setCancelable(true);
        showDialog.show();
        showDialog.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);

        btnOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                showDialog.dismiss();
            }
        });
    }


    public static void showDefaultDialog(Context context, String title, String displayText) {


        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        final Dialog showDialog = new Dialog(context);
        showDialog.setContentView(R.layout.default_dialog);
        TextView tvDisplay = (TextView) showDialog.findViewById(R.id.textViewDefaultDialog);
        Button btnOk = (Button) showDialog.findViewById(R.id.button_defaultDialog);
        showDialog.setTitle(title);
        tvDisplay.setText(displayText);
        showDialog.setCancelable(true);
        showDialog.show();
        showDialog.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);

        btnOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                showDialog.dismiss();
            }
        });

    }
}
