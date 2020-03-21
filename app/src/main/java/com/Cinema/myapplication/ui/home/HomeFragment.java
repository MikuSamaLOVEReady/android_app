package com.Cinema.myapplication.ui.home;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.Cinema.myapplication.MainActivity;
import com.Cinema.myapplication.R;
import com.Cinema.myapplication.ScheduleActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    public  ArrayList<Map<String, Object>> Info_list = new ArrayList<Map<String, Object>>();
    //储存 从UTF-8->bytes值
    byte[] imageByte;
    //加载一个解码器
    final Base64.Decoder decoder = Base64.getDecoder();
    private HomeFragment.Mybaseadapter list_item;
    private Button showSchedul;
    private ListView listView;//用于获取xml中的 布局对象

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        HomepageResponse();
        showSchedul = (Button) root.findViewById(R.id.schedule_show);
        //接下用 adoptor适配器 来设置内容
        listView = (ListView) root.findViewById(R.id.listv); //得到ListView对象的引用
        return root;
    }

    //直接响应
    private void HomepageResponse() {
        String url = "http://192.168.101.102:5000/user/home";
        //创建链接
        OkHttpClient client = new OkHttpClient();
        //直接链接 不多bb
        //这次发送get请求 想办法获得 所有电影属性
        Request request = new Request.Builder().url(url).get().build();
        //发送请求
        Call call = client.newCall(request);
        //创建消息队列
        call.enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                if (getActivity() == null)  return;//用getActivity()获得当前的activity,可能为空
                getActivity().runOnUiThread(new Runnable()
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
                response.header("Connection","close");
                final String res = response.body().string();

                //System.out.println(res);
                //将重负荷的任务移除到工作线程避免主线程阻塞，
                // 当需要更新UI的时候我们需要“返回”到主线程，因为只有它才可以更新应用 UI。
                if (getActivity() == null)  return;
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        try{
                            //将返回的 json 大数组（集合？） 转换成引用
                            JSONObject Jobject = new JSONObject(res);
                            //在这个 集合中找到名字为 All_info 的json数组。
                            JSONArray Jarray = Jobject.getJSONArray("All_info");
                            //数组中的每个元素 其实是包含了一种电影的所有信息
                            for(int i=0;i<Jarray.length();i++)
                            {
                                //返回过来的 是一个数组名？
                                Map<String, Object> map = new HashMap<String, Object>();
                                // 也就是每个电影的全部属性为一个元素，字典组？
                                JSONObject jsonObject=Jarray.getJSONObject(i);
                                String name=jsonObject.getString("FilmName");
                                String blurb=jsonObject.getString("Blurb");
                                String FilmID=jsonObject.getString("FilmID");

                                //图片 由于传过来的时候进过了从  bytes-> sting的过程 使用UTF-8编码
                                String encodeStr=jsonObject.getString("Image");
                                //这里要解码一下 变成base64编码的文件
                                try {
                                    imageByte = encodeStr.getBytes("utf-8");
                                }catch (IOException e) {
                                    e.printStackTrace();
                                }
                                //这里 设置 base64解码后存放的位置
                                byte [] after_base64_decode=new byte[imageByte.length*2];
                                //这里开始解码 base64
                                decoder.decode(imageByte,after_base64_decode);
                                //Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                //这里把字节数组转化成 bitmap
                                Bitmap bitmap = BitmapFactory.decodeByteArray(after_base64_decode, 0, after_base64_decode.length);
                                //用数据结构 hashmap 提高搜索效率
                                map.put("FilmID",FilmID);
                                map.put("FilmName",name);
                                map.put("Blurb",blurb);
                                map.put("image",bitmap);
                                Info_list.add(map);
                               // System.out.println(Info_list.toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        list_item = new HomeFragment.Mybaseadapter();
                        listView.setAdapter(list_item);
                    }
                });
            }
        });
    }

    public class Mybaseadapter extends BaseAdapter {


        @Override
        public int getCount() {
            return Info_list.size();
        }

        @Override
        public Object getItem(int position) {
            return Info_list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            HomeFragment.ViewHolder viewHolder = new HomeFragment.ViewHolder();

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.view_main, null);

                viewHolder.image    = (ImageView) convertView.findViewById(R.id.image_view);
                viewHolder.FilmName = (TextView) convertView.findViewById(R.id.filmname);
                viewHolder.Blurb = (TextView) convertView.findViewById(R.id.blurb);
                viewHolder.schedule_button =(Button) convertView.findViewById(R.id.schedule_show);

                convertView.setTag(viewHolder);}
            else {
                viewHolder = (HomeFragment.ViewHolder) convertView.getTag();
            }
            final String FID =  Info_list.get(position).get("FilmID").toString();
            final int PosterID =  position;
            viewHolder.FilmName.setText(Info_list.get(position).get("FilmName").toString());
            viewHolder.Blurb.setText(Info_list.get(position).get("Blurb").toString());
            viewHolder.image.setImageBitmap((Bitmap) Info_list.get(position).get("image"));
            viewHolder.schedule_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (getActivity() == null)  return;
                    Intent intent=new Intent(getActivity(), ScheduleActivity.class);//用getActivity()获得当前的
                    intent.putExtra("FilmID",FID);
                    intent.putExtra("Position",PosterID);
                    startActivity(intent);
                }
            });
            return convertView;
        }
    }

    class ViewHolder {
        TextView FilmName;
        TextView Blurb;
        ImageView image;
        Button   schedule_button;
    }

    private void Activity_Change_back(){
        //第一个参数是 当前场景， 第二个是跳转的目的地
        if (getActivity() == null)  return;
        Intent intent =new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }
}
