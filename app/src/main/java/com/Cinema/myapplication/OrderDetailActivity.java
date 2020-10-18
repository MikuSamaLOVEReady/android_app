package com.Cinema.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.Cinema.myapplication.ui.home.HomeFragment;

import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.Cinema.myapplication.tool.ServerIP.SendticketURL;

public class OrderDetailActivity extends AppCompatActivity {

    private int seatID ;

    private Integer FilmID ;

    private int orderID;

    private TextView row_view;
    private TextView col_view;

    private TextView Room;
    private TextView Date;
    private TextView Time;
    private TextView Filmname;

    private String F_Room;
    private String F_Date;
    private String F_Time;




    private int row ;
    private int col ;

    private ImageView poster;

    private Button backHomepage;

    private Button printTicket;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //获取 seatID 用于后期转换
        Intent intent=getIntent();
        //第二个参数表示没有接收到的时候 给的默认值
        seatID= intent.getIntExtra("SeatID",0);

        //把这个filmid
        FilmID = intent.getIntExtra("FilmID",-1);

        //通过这个会后台查信息
        orderID=  intent.getIntExtra("OrderID",-1);


        //播放日期
        F_Date= intent.getStringExtra("date");
        //时间
        F_Time=intent.getStringExtra("StartTime");
        //房号
        F_Room= intent.getStringExtra("Room");



        setContentView(R.layout.activiy_order_detail);


        seatAttribute();
        //intent.getByteExtra();

        backHomepage =  (Button) findViewById(R.id.buttonHome);

        backHomepage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(OrderDetailActivity.this,NavActivity.class);
                startActivity(intent);
            }
        });



         //发送打票请求
        printTicket = (Button) findViewById(R.id.printTicket);
        printTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTicketResponse();
            }
        });



    }

    private void sendTicketResponse()
    {

        String url = SendticketURL;
        //String url = "http://192.168.101.102:5000/Sendticket";
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();
        //添加键值对映射
        formBuilder.add("OrderID",String.valueOf(orderID));
        formBuilder.add("Row", String.valueOf(row+1));
        formBuilder.add("Column", String.valueOf(col));
        System.out.println( MainActivity.UserEmail);
        formBuilder.add("email", MainActivity.UserEmail);

        Request request = new Request.Builder().url(url).post(formBuilder.build()).build();

        Call call = client.newCall(request);
        call.enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        System.out.println("服务器错误");
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String res = response.body().string();
                System.out.println(res);
                runOnUiThread(new Runnable() {
                    public void run() {
                        showSuccessSweetDialog("ticket has sent please check your email");
                    }
                });
            }
        });

    }


    private void seatAttribute(){


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

        //获得该电影的海报
        //先 查看一共有多少个 电影
        int totalsize = HomeFragment.Info_list.size();

        int i = 0;
        //用这个动态查询 当前 电影所在的 Info_list数组的位置 即使有删除也不影响
        for(;i<totalsize ;i++){
            if(HomeFragment.Info_list.get(i).get("FilmID").equals(FilmID.toString())){
                break;
            }
        }

        System.out.println(i);
        //用当前 电影ID 减去 剩余电影数量
        Bitmap bitmap =(Bitmap) HomeFragment.Info_list.get(i).get("image");

        poster =(ImageView) findViewById(R.id.PosterView);
        row_view = (TextView) findViewById(R.id.RowNumber);
        col_view = (TextView) findViewById(R.id.ColumNumber);


        Room = findViewById(R.id.room);
        Date=findViewById(R.id.date);
        Time =findViewById(R.id.starttime);
        Filmname= findViewById(R.id.filename);

        //设置
        poster.setImageBitmap(bitmap);
        row_view.setText("The Row is "+(row+1));
        col_view.setText("The Column is "+col);
        Filmname.setText( HomeFragment.Info_list.get(i).get("FilmName").toString());
        Room.append(F_Room);
        Date.append(F_Date);
        Time.append(F_Time);


    }

    public void showSuccessSweetDialog(String info)
    {
        SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(info);
        pDialog.setCancelable(true);
        pDialog.show();
    }

}
