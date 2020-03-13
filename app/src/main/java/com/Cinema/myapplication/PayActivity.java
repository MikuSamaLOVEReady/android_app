package com.Cinema.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.Cinema.myapplication.PayClass.CustomDialog;

public class PayActivity extends AppCompatActivity {

    private Button btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        btn = findViewById(R.id.btn_dialog);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CustomDialog customDialog = new CustomDialog();
                //
                customDialog.show(getSupportFragmentManager(), "");

                //Log.e("fugang","11111");
            }
        });

    }
}
