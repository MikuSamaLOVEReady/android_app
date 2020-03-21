package com.Cinema.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.Cinema.myapplication.SelectTableClass.RoomTable;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SelectSeatActivity extends AppCompatActivity {

    //我打算 安排的10X10的厅 后端直接传回 ID
    ArrayList<Integer> sold = new ArrayList<>();

    //别选定了的座位 只给出3个位置
    public ArrayList<Integer> seats_Check = new ArrayList<>(3);

    private Button Buy;

    //用于从前一个跳转 传递价格
    public int price;

    //接受一下 scheduleID

    //这里是第一个 买票 框框
    public TextView selec_info1;
    //这里是第二个 买票 框框
    public TextView selec_info2;
    //这里是第三个 买票 框框
    public TextView selec_info3;

    int flag[] = {0,0,0};
    public int number_of_ticket;

    //真鸡儿麻烦 还得转换一次
    public String SID_str;
    //这个才是最后要用的 全局SID
    public static int SID;


    public int[] ints;



    //public RoomTable seatTableView;
    public RoomTable seatTableView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        selec_info1 = (TextView) findViewById(R.id.Current_ticket1);
        selec_info1.setVisibility(View.INVISIBLE);

        selec_info2 = (TextView) findViewById(R.id.Current_ticket2);
        selec_info2.setVisibility(View.INVISIBLE);

        selec_info3 = (TextView) findViewById(R.id.Current_ticket3);
        selec_info3.setVisibility(View.INVISIBLE);


        //获取一下 所对应scheduleID  --先从int-》str——》int贼复杂！草了
        Intent intent=getIntent();
        //第二个参数表示没有接收到的时候 给的默认值
        SID_str =intent.getStringExtra("ScheduleID");
        //System.out.println(SID_str+"string 模式的局部变量——接受的内容！！！！！！！！！！！");
        SID=Integer.parseInt(SID_str);
        //System.out.println(SID+"数字版内容");

        //先查询下 所有被卖掉的座位
        CheckSoldResponse();





        final Button btn = (Button)findViewById(R.id.buy_button);
        btn.setVisibility(View.INVISIBLE);
        //购买的按钮的跳转事件 监听器

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //记录所有座位ID 初始化 全是0 表示没有一个有效ID
                int seatID[] = {0,0,0};

                //每次 都检查一哈 一共 有几个票
                int count = 0;
                for(int i =0;i<3; i++){
                    //这个i 也是对应的 [85, -1, 99] 如果是这中 seatID【85，-1，99】
                    if(seats_Check.get(i)!=-1){
                        //如果不是-1则表示有ID
                        seatID[i]=seats_Check.get(i);
                        count++;
                    }
                }
                number_of_ticket= count;

                //发给select->payactivity->customdialog->payfinish
                Intent intent =new Intent(SelectSeatActivity.this,PayActivity.class);
                //传出 订单数量
                intent.putExtra("TicketsNumber",number_of_ticket);
                //传出 每个订单的座位ID
                intent.putExtra("seatID1",seatID[0]);
                intent.putExtra("seatID2",seatID[1]);
                intent.putExtra("seatID3",seatID[2]);

                startActivity(intent);
            }
        });

        //先放三个-1 表示位置为空
        seats_Check.add(-1);
        seats_Check.add(-1);
        seats_Check.add(-1);
        seatTableView = (RoomTable) findViewById(R.id.seatView);

        //seatTableView.setScreenName("8号厅荧幕");//设置屏幕名称
        //seatTableView.setMaxSelected(3);//设置最多选中

        //RoomTable 调用那个接口  从而设定 绘制作为的方案
        seatTableView.setSeatChecker( new RoomTable.SeatChecker() {

            @Override
            public boolean isValidSeat(int row, int column) {
                if(column==2) {
                    return false;
                }
                return true;
            }

            @Override
            public boolean isSold(int row, int column) {
                /*
                if(row==6&&column==6){
                    return true;
                }
                 */
                if(ints!=null) {
                    for (int i = 0; i < ints.length; i++) {
                        if (seatTableView.getID(row, column) == ints[i])
                            return true;
                    }
                }
                return false;
            }

            @Override
            public void checked(int row, int column) {
                int num =seatTableView.getID(row,column);
                //seats_Check.add(num);

                //用这个循环找出 数组 空余部分
                int i=0;
                for(;i<3; i++){
                    if(seats_Check.get(i)==-1){
                        break;
                    }
                }

                //最后处理
                if(i==3){
                    seats_Check.set(i-1,num);
                }
                else {
                    seats_Check.set(i,num);
                }
                   //将这个 位置置成 相对应ID

                //flag 表示 是否更改当前 内容  0表示 需要修改   1表示不需要
                if(haveSelect()){
                    btn.setVisibility(View.VISIBLE);
                    if(seats_Check.get(0)!=-1){
                        selec_info1.setVisibility(View.VISIBLE);
                        if(flag[0]==0) {
                            selec_info1.setText("row" + row + "   " + "column" + column + "\n" + "Price:23$ ");
                            flag[0]=1;
                        }
                    }
                    if(seats_Check.get(1)!=-1){
                        selec_info2.setVisibility(View.VISIBLE);
                        if(flag[1]==0) {
                            selec_info2.setText("row" + row + "   " + "column" + column + "\n" + "Price:23$ ");
                            flag[1]=1;
                        }
                    }
                    if(seats_Check.get(2)!=-1){
                        selec_info3.setVisibility(View.VISIBLE);
                        if(flag[2]==0) {
                            selec_info3.setText("row" + row + "   " + "column" + column + "\n" + "Price:23$ ");
                            flag[2]=1;
                        }
                    }

                    //selec_info.append("row"+row+"   "+"column"+column+"\n"+"Price：");

                }

                System.out.println(seats_Check);
            }

            @Override
            public void unCheck(int row, int column) {
                int r_num =seatTableView.getID(row,column);
                //从选择的 位置移除
                int i=0;
                for(;i<seats_Check.size(); i++){
                    if(seats_Check.get(i)==r_num){
                        break;
                    }
                }
                if(i==0){
                    selec_info1.setVisibility(View.INVISIBLE);
                    //将退还的位子 设为-1
                    flag[0]=0;
                    seats_Check.set(i,-1);
                }
                if(i==1){
                    selec_info2.setVisibility(View.INVISIBLE);
                    //将退还的位子 设为-1
                    flag[1]=0;
                    seats_Check.set(i,-1);
                }
                if(i==2) {
                    selec_info3.setVisibility(View.INVISIBLE);
                    //将退还的位子 设为-1
                    flag[2]=0;
                    seats_Check.set(i,-1);
                }



                //暂时不remove
                //seats_Check.remove(Integer.valueOf(r_num));
                //System.out.println(seats_Check);
                //System.out.println(r_num);

                if(!haveSelect()){
                    btn.setVisibility(View.INVISIBLE);

                }

            }

        });
        seatTableView.setData(10,15); //150个座位
    }

    private void CheckSoldResponse()
    {

        String url = "http://192.168.101.102:5000/Get_sold";


        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("schedulID",SID_str);//我太猛了 居然多余的转换没白费！！！

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
                //分割开
                if(res!="") {
                    String[] tokens = res.split(" ");
                    //把字符串 在转化成数字
                    ints = new int[tokens.length];
                    for (int i = 0; i < tokens.length; i++) {
                        ints[i] = Integer.parseInt(tokens[i]);
                    }
                }


            }
        });

    }





   //如果三个位子全是 -1 则表示没选
    boolean haveSelect(){
       if(seats_Check.get(0)==-1&&seats_Check.get(1)==-1&&seats_Check.get(2)==-1)
        return false;
       else
           return true;
    }


}
