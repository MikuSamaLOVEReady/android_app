package com.Cinema.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.Cinema.myapplication.PayClass.CustomDialog;

public class PayActivity extends AppCompatActivity {

    private Button btn;

    //静态变量 全局使用
    public static int Tic_number;

    public static int seaID_1;
    public static int seaID_2;
    public static int seaID_3;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);


        Intent intent=getIntent();
        //第二个参数表示没有接收到的时候 给的默认值
        Tic_number=intent.getIntExtra("TicketsNumber",-1);

        //接受座位
        seaID_1  =intent.getIntExtra("seatID1",0);
        seaID_2  =intent.getIntExtra("seatID2",0);
        seaID_3  =intent.getIntExtra("seatID3",0);

        //这里它是拿到了票的数量了
        //接下来 需要传递给 完成 活动

        btn = findViewById(R.id.btn_dialog);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CustomDialog customDialog = new CustomDialog();

                customDialog.show(getSupportFragmentManager(), "");

            }
        });
    }
    public void card(View view){
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = layoutInflater.inflate(R.layout.dialog_card, null);
        new AlertDialog.Builder(this)
                .setTitle("请输入银行卡号：")
                .setView(dialogView)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText et = (EditText)dialogView.findViewById(R.id.edit);
                        String cardinput=et.getText().toString();
                        System.out.println(cardinput);
                        TextView card=(TextView)findViewById(R.id.card);
                        //按下确定键后的事件
                        card.setText("XX银行："+ cardinput);
                    }
                }).setNegativeButton("取消",null).show();
    }
}
