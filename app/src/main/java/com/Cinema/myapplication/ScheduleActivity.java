package com.Cinema.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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


import static com.Cinema.myapplication.tool.ServerIP.user_scheduleURL;



public class ScheduleActivity  extends AppCompatActivity {


    ArrayList<Map<String, Object>> Arrange_list = new ArrayList<Map<String, Object>>();

    private Mybaseadapter list_item;

    private ListView listView;


    private String FilmID;

    public int PosterID;

    private ImageView image;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedul);

        //拿到这个ID
        Intent intent=getIntent();
        FilmID=intent.getStringExtra("FilmID");

        //接受海报ID
        PosterID =intent.getIntExtra("Position",-1);

        ScheduleResponse();
        //需不需要呢？？？？
        //每个电影的 整体海报

        /*
        image =(ImageView) findViewById(R.id.Select_poster);
        //设置
        image.setImageBitmap((Bitmap) HomeFragment.Info_list.get(PosterID).get("image"));
        System.out.println(HomeFragment.Info_list.get(PosterID).get("image"));
         */



        //System.out.println(FilmID);
        listView = (ListView)findViewById(R.id.listv2);


    }

    private void ScheduleResponse()
    {


        String url = user_scheduleURL;
       // String url = "http://192.168.101.102:5000/user_schedule";


        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();
        //添加键值对映射
        formBuilder.add("filmid", FilmID);

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


                //System.out.println(res);


                runOnUiThread(new Runnable() {
                    public void run() {
                        try{
                            JSONObject Jobject = new JSONObject(res);
                            //在这个 集合中找到名字为 All_info 的json数组。
                            JSONArray Jarray = Jobject.getJSONArray("all_schedul_info");
                            //数组中的每个元素 其实是包含了一种电影的所有信息
                            for(int i=0;i<Jarray.length();i++)
                            {
                                Map<String, Object> map = new HashMap<String, Object>();
                                JSONObject jsonObject=Jarray.getJSONObject(i);
                                //解析的时候 也要带上ID


                                int    ID  = jsonObject.getInt("ID");
                                int    FID = jsonObject.getInt("FID");
                                String room=jsonObject.getString("Room");
                                String date=jsonObject.getString("Date");
                                String time=jsonObject.getString("Time");
                                String price=jsonObject.getString("Price");


                                map.put("ID",ID);
                                map.put("FID",FID);
                                map.put("Room",room);
                                map.put("Date",date);
                                map.put("Time",time);
                                map.put("Price",price);
                                Arrange_list.add(map);
                                //System.out.println(Arrange_list.toString());
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        list_item = new Mybaseadapter();
                        listView.setAdapter(list_item);

                    }
                });


            }
        });

    }

    public class Mybaseadapter extends BaseAdapter {


        @Override
        public int getCount() {
            return Arrange_list.size();
        }

        @Override
        public Object getItem(int position) {
            return Arrange_list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder = new ViewHolder();

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.view_schedul, null);

                viewHolder.room    = (TextView) convertView.findViewById(R.id.room);
                viewHolder.date   = (TextView) convertView.findViewById(R.id.date);
                viewHolder.time   = (TextView) convertView.findViewById(R.id.time);
                viewHolder.price  =(TextView) convertView.findViewById(R.id.price);
                viewHolder.select_seats =(Button) convertView.findViewById(R.id.button_Select);

                convertView.setTag(viewHolder);}
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.room.setText(Arrange_list.get(position).get("Room").toString());
            viewHolder.date.setText(Arrange_list.get(position).get("Date").toString());
            viewHolder.time.setText(Arrange_list.get(position).get("Time").toString());
            viewHolder.price.setText(Arrange_list.get(position).get("Price").toString());
            final String SID = Arrange_list.get(position).get("ID").toString();
            final String Price = Arrange_list.get(position).get("Price").toString();
            final String FID = Arrange_list.get(position).get("FID").toString();
            viewHolder.select_seats.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(ScheduleActivity.this,SelectSeatActivity.class);
                    //这里还需要传递一个price 才对。。
                    intent.putExtra("Price",Price);

                    intent.putExtra("ScheduleID",SID);

                    //再传递 一个FID 用于 之后统计
                    intent.putExtra("FilmID",FID);



                    startActivity(intent);
                }
            });

            return convertView;
        }
    }

    //这里放的都是 子layout中的ID
    class ViewHolder {
        TextView room;
        TextView date;
        TextView time;
        TextView price;
        Button   select_seats;


        //ImageView image;
        //Button schedule_button;
    }


}
