package com.example.naxasurvay;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.surveyor_Rid)
    AutoCompleteTextView surveyor_Id;
    @BindView(R.id.surveyor_Rname)
    AutoCompleteTextView surveyor_name;
    @BindView(R.id.buttom_next)
    Button Next;

    SharedPreferences preferences;
    String name,id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);

        preferences = getSharedPreferences("userinfo", 0);

        Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              id =  surveyor_Id.getText().toString();
               name = surveyor_name.getText().toString();

                SharedPreferences.Editor editor = preferences.edit();

                editor.putString("Surveyor_Id",id);
                editor.putString("Surveyor_Name",name);

                editor.apply();

                Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

    }
}
