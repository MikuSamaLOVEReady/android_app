package com.Cinema.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.Cinema.myapplication.tool.ServerIP.check_ordersURL;

public class CheckOrderActivity extends AppCompatActivity {
    //先获取一首用户ID
    Integer uid =MainActivity.UID;

    public  ArrayList<Map<String, Object>> Order_list = new ArrayList<Map<String, Object>>();


    private ListView listView_order;//用于获取xml中的 布局对象
    private Mybaseadapter list_item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        if(SelectSeatActivity.isLogin==1){
            CheckOrderResponse();
            listView_order = (ListView)findViewById(R.id.orderList); //得到ListView对象的引用
        }
        else{
            Toast.makeText(this, "Please Login first", Toast.LENGTH_SHORT).show();
            Intent login = new Intent(CheckOrderActivity.this, MainActivity.class);
            startActivityForResult(login, 0);
        }



    }

    private void CheckOrderResponse()
    {

        String url =check_ordersURL;
        //String url = "http://192.168.101.102:5000/check_orders";
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();
        //添加键值对映射
        formBuilder.add("UserID", uid.toString());

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
                        try{
                            JSONObject Jobject = new JSONObject(res);
                            //在这个 集合中找到名字为 own_order_info 的json数组。
                            JSONArray Jarray = Jobject.getJSONArray("own_order_info");
                            //数组中的每个元素 其实是包含了一种电影的所有信息
                            for(int i=0;i<Jarray.length();i++)
                            {
                                Map<String, Object> map = new HashMap<String, Object>();
                                JSONObject jsonObject=Jarray.getJSONObject(i);
                                //解析的时候 也要带上ID


                                int    ID  = jsonObject.getInt("OrderID");
                                int    FID  = jsonObject.getInt("FilmID");
                                String room=jsonObject.getString("Room");//这个地方包含了影厅吧。。
                                String seatID=jsonObject.getString("SeatID");
                                String time=jsonObject.getString("CurrentTime");
                                String start = jsonObject.getString("StartTime");
                                String date = jsonObject.getString("date");
                                //String price=jsonObject.getString("Price");

                                map.put("FID",FID);
                                map.put("OrderID",ID);
                                map.put("Room",room);
                                map.put("SeatID",seatID);
                                map.put("CurrentTime",time);
                                map.put("StartTime",start);
                                map.put("date",date);

                                Order_list.add(map);
                                //System.out.println(Arrange_list.toString());
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        list_item = new Mybaseadapter();
                        listView_order.setAdapter(list_item);

                    }
                });


            }
        });

    }

    public class Mybaseadapter extends BaseAdapter {


        @Override
        public int getCount() {
            return Order_list.size();
        }

        @Override
        public Object getItem(int position) {
            return Order_list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

           ViewHolder viewHolder = new ViewHolder();

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.view_order, null);

                viewHolder.Film_room    = (TextView) convertView.findViewById(R.id.Film_room);
                viewHolder.Start_time   = (TextView) convertView.findViewById(R.id.Start_time);
                viewHolder.date   = (TextView) convertView.findViewById(R.id.date);
                viewHolder.SeatID   = (TextView) convertView.findViewById(R.id.SeatID);
                //viewHolder.price  =(TextView) convertView.findViewById(R.id.price);
                viewHolder.select_order =(Button) convertView.findViewById(R.id.OrderButton);

                convertView.setTag(viewHolder);}
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.Film_room.setText(Order_list.get(position).get("Room").toString());
            viewHolder.Start_time.setText(Order_list.get(position).get("StartTime").toString());
            viewHolder.date.setText(Order_list.get(position).get("date").toString());
            viewHolder.SeatID.setText(Order_list.get(position).get("SeatID").toString());

            final String SeatID = Order_list.get(position).get("SeatID").toString();
            final int FilmID = (int)Order_list.get(position).get("FID");
            final int orderID = (int)Order_list.get(position).get("OrderID");

            final String date = Order_list.get(position).get("date").toString();
            final String StartTime = Order_list.get(position).get("StartTime").toString();
            final String Room = Order_list.get(position).get("Room").toString();


            //final String FID = Arrange_list.get(position).get("ID").toString();
            viewHolder.select_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //还没有单独设计 每个订单的页面。。我感觉不需要吧。。？？待定
                    Intent intent=new Intent(CheckOrderActivity.this,OrderDetailActivity.class);
                    //这里还需要传递一个price 才对。
                    //先把座位ID 传过去 做解析
                    intent.putExtra("SeatID",Integer.valueOf(SeatID));
                    //再传递一个 电影ID 用于显示图片
                    intent.putExtra("FilmID",FilmID);
                    //把订单ID传回去
                    intent.putExtra("OrderID",orderID);

                    //日期
                    intent.putExtra("date",date);
                    //时间
                    intent.putExtra("StartTime",StartTime);
                    //房号
                    intent.putExtra("Room",Room);


                    startActivity(intent);
                }
            });

            return convertView;
        }
    }

    //这里放的都是 子layout中的ID
    class ViewHolder {
        TextView Film_room;
        TextView Start_time;
        TextView date;
        TextView SeatID;

        //TextView price;
        Button select_order;

    }



}
