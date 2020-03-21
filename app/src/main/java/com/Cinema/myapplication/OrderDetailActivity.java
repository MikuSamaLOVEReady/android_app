package com.Cinema.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class OrderDetailActivity extends AppCompatActivity {

    private int seatID ;

    private int row ;
    private int col ;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //获取 seatID 用于后期转换
        Intent intent=getIntent();
        //第二个参数表示没有接收到的时候 给的默认值
        seatID= intent.getIntExtra("SeatID",0);
        System.out.println("the Seat ID is !!!"+seatID);

        //判断是否找到
        int flag=0;

        //重新 从seatID 获取到相应的 座位排号和行号
        for(int i=0;i<10; i++){
            for(int j=0;j<15; j++){
                //System.out.println(i*15+j+1);
                if(i*15+j+1==seatID){
                    flag=1;
                    row=i;
                    col=j;
                    break;
                }
            }
            if(flag==1){
                break;
            }
        }

        //接下用 adoptor适配器 来设置内容
        //listView = (ListView)findViewById(R.id.listv); //得到ListView对象的引用
    }
}
