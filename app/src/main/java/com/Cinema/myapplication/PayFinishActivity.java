package com.Cinema.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.Cinema.myapplication.tool.ServerIP.order_uploadURL;
import static com.Cinema.myapplication.tool.ServerIP.tourist_ticketURL;




public class PayFinishActivity extends AppCompatActivity {

    public int number;

    public int UserID;
    public int scheduleID;
    public int FilmID;
    private Button back;
    private Button ToPersonalPage;

    private int row;
    private int col;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent intent=getIntent();
        //总共订单数
        number=intent.getIntExtra("TicketsNumber",-1);

        //座位ID 检查
        ArrayList<Integer> all_seat_ID = new ArrayList<>();
        all_seat_ID.add(PayActivity.seaID_1);
        all_seat_ID.add(PayActivity.seaID_2);
        all_seat_ID.add(PayActivity.seaID_3);

        //在登陆的时候准备的UID
        UserID = MainActivity.UID;
        //在选择好 场次之后 准备好SID;  在 schedule中传出来，在选座的时候记录
        scheduleID=SelectSeatActivity.SID;

        //给订单增加的新属性 FID
        FilmID=SelectSeatActivity.FID;


        //System.out.println(number+"sssssssssssssssss");

        String Time_Now = new Timestamp(new Date().getTime()).toString();
        //System.out.println(Time_Now);

        int j = 0;
        //循环定单
        for(int i= 0;i< number; i++) {
            //这个循环来查看有效座位ID
            while (j<3){
                if(all_seat_ID.get(j)!=0){
                    //如果查到了一个则 发送一次请求 更新数据库//这个是座位ID
                    //差点弄错MLGB
                    UploadOrderResponse(all_seat_ID.get(j),UserID,scheduleID,Time_Now,FilmID);

                    //如果是游客 的话就直接发票
                    if(NewMemberActivity.tourist==1){


                        //重新 从seatID 获取到相应的 座位排号和行号
                        for(int m=0;m<10; m++){
                            for(int n=0;n<15; n++){
                                if(m*15+n+1==all_seat_ID.get(j)){
                                    row=m;
                                    col=n;
                                    TouristSendtickResponse(row,col,scheduleID,Time_Now,FilmID);
                                    break;
                                }
                            }
                        }

                    }

                    //跳出去 再查一次 剩余的订单情况
                    j++;
                    break;
                }
                else{
                    j++;
                }
            }
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_end);

        back =  (Button) findViewById(R.id.back);
        //ToPersonalPage = findViewById(R.id.ToorderList);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果游客购买后 他将被踢出购票模式  完成一次性购买
                if(NewMemberActivity.tourist==1){
                    NewMemberActivity.tourist=0;
                }

                Intent intent =new Intent(PayFinishActivity.this,NavActivity.class);
                startActivity(intent);
            }
        });

        /*
        ToPersonalPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果游客购买后 他将被踢出购票模式  完成一次性购买
                if(NewMemberActivity.tourist==1){
                    NewMemberActivity.tourist=0;
                }

                Intent intent =new Intent(PayFinishActivity.this, NavActivity.class);
                startActivity(intent);
            }
        });
         */



    }

    private void TouristSendtickResponse(int row , int col ,Integer SID, String NowTime,Integer FID)
    {

        //这里放 加入order的 URL
        String url = tourist_ticketURL;
        //String url = "http://192.168.101.102:5000/tourist_ticket";


        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();

        formBuilder.add("schedulID", SID.toString());
        formBuilder.add("Row", String.valueOf(row));
        formBuilder.add("Column", String.valueOf(col));
        formBuilder.add("CurrentTime", NowTime);
        formBuilder.add("FilmID",FID.toString());
        formBuilder.add("email", MainActivity.UserEmail.toString());

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
                runOnUiThread(new Runnable() {
                    public void run() {

                    }
                });


            }
        });

    }


    private void UploadOrderResponse(Integer seatID,Integer UserID,Integer SID, String NowTime,Integer FID)
    {

        //这里放 加入order的 URL

        String url =order_uploadURL;
        //String url = "http://192.168.101.102:5000/order_upload";


        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();
        //添加键值对映射
        //发送的表单
        formBuilder.add("UserID", UserID.toString());
        formBuilder.add("schedulID", SID.toString());
        formBuilder.add("SeatID", seatID.toString());
        formBuilder.add("CurrentTime", NowTime);
        formBuilder.add("FilmID",FID.toString());

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



                runOnUiThread(new Runnable() {
                    public void run() {

                    }
                });


            }
        });

    }


}
