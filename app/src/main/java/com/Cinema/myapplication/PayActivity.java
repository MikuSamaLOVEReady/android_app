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

import java.util.ArrayList;

public class PayActivity extends AppCompatActivity {

    private Button btn;

    //静态变量 全局使用
    public static int Tic_number;

    public static int seaID_1;
    public static int seaID_2;
    public static int seaID_3;


    private TextView tic1;
    private TextView tic2;
    private TextView tic3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);


        Intent intent=getIntent();
        //第二个参数表示没有接收到的时候 给的默认值
        Tic_number=intent.getIntExtra("TicketsNumber",-1);


        ArrayList<Integer> seat_list = new ArrayList<Integer>();


        //接受座位
        seaID_1  =intent.getIntExtra("seatID1",0);
        seaID_2  =intent.getIntExtra("seatID2",0);
        seaID_3  =intent.getIntExtra("seatID3",0);

        String price  =intent.getStringExtra("each_price");

        seat_list.add(seaID_1);
        seat_list.add(seaID_2);
        seat_list.add(seaID_3);


        String row="";
        String col="";
        // 拿到3个ID    count来数有效订单
        int count=0;
        for(int i = 0 ;i<3;i++){
            if(seat_list.get(i)!=0){
                int flag=0;
                //重新 从seatID 获取到相应的 座位排号和行号
                for(int m=0;m<10; m++){
                    for(int n=0;n<15; n++){
                        //System.out.println(i*15+j+1);
                        if(m*15+n+1==seat_list.get(i)){
                            flag=1;
                            row=String.valueOf(m);
                            col=String.valueOf(n);
                            break;
                        }
                    }
                    if(flag==1){
                        break;
                    }
                }
                count++;
                if(count==1){
                    tic1=(TextView) findViewById(R.id.tic1);
                    tic1.setText("Row: "+row+"\n\r"+"Col: "+col+"\n\r"+price);

                }
                else if(count==2){
                    tic1=(TextView) findViewById(R.id.tic2);
                    tic1.setText("Row: "+row+"\n\r"+"Col: "+col+"\n\r"+price);

                }
                else if(count==3){
                    tic1=(TextView) findViewById(R.id.tic3);
                    tic1.setText("Row: "+row+"\n\r"+"Col: "+col+"\n\r"+price);

                }

            }

        }





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
