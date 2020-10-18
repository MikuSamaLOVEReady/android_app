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
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.Cinema.myapplication.FilmDetailActivity;
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
import static com.Cinema.myapplication.tool.ServerIP.HomepageURL;


public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    public static ArrayList<Map<String, Object>> Info_list = new ArrayList<Map<String, Object>>();

    //克隆一份
    private  ArrayList<Map<String, Object>> Info_list_backup = new ArrayList<Map<String, Object>>();





    //储存 从UTF-8->bytes值
    byte[] imageByte;
    //加载一个解码器
    final Base64.Decoder decoder = Base64.getDecoder();
    private HomeFragment.Mybaseadapter list_item;
    private Button showSchedul;

    //listview 就一个
    private ListView listView;//用于获取xml中的 布局对象

    //但是适配器 可以多个
    private HomeFragment.Mybaseadapter Search_Result_Adaptor;


    private SearchView  searchView;


    //存放  搜索选项
    private String[] mStrings=new String[15];


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);

        View root = inflater.inflate(R.layout.fragment_home, container, false);



        //界面初始化
        HomepageResponse();


        //我裂开了 这里要转换一下


        showSchedul = (Button) root.findViewById(R.id.schedule_show);
        //接下用 adoptor适配器 来设置内容
        listView = (ListView) root.findViewById(R.id.listv); //得到ListView对象的引用

        //设置搜素栏
        searchView = (SearchView)root.findViewById(R.id.SearchBar);
        //设置该SearchView显示搜索按钮
        searchView.setSubmitButtonEnabled(true);
        //设置还未输入的时候的 显示
        searchView.setQueryHint("Please search here");



        for(int i= 0 ; i<Info_list.size() ;i++){
            mStrings[i] = Info_list.get(i).get("FilmID").toString();
        }

        System.out.println("size"+Info_list.size());
        //搜索选项显示 暂时有点问题 但应该问题不大。
        //final ListView SearchList = findViewById(R.id.listview);
        //SearchList.setAdapter(new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, mStrings));



        //添加搜索监听
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //find 表示有没有找到
            int find=0;

            int postion= -1;

            //激活过后
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(getActivity(), "你的选择是：" + query, Toast.LENGTH_SHORT).show();

                Info_list = (ArrayList<Map<String, Object>>)Info_list_backup.clone();
                //Info_list.clear();
                //Info_list = (ArrayList<Map<String, Object>>)Info_list_backup.clone();

                //搜索所有之前的list 看有没有
                for(int i= 0 ; i<Info_list.size() ;i++){
                    mStrings[i] = Info_list.get(i).get("FilmID").toString();
                    if(Info_list.get(i).get("FilmName").toString().equals(query)){
                        find=1;
                        postion =i;
                    }
                }
                Map<String, Object> Search_map = new HashMap<String, Object>();
                //找到了
                if(find==1){

                    Search_map.put("image",Info_list.get(postion).get("image"));
                    Search_map.put("FilmID",Info_list.get(postion).get("FilmID").toString());
                    Search_map.put("FilmName",Info_list.get(postion).get("FilmName").toString());
                    Search_map.put("Blurb",Info_list.get(postion).get("Blurb").toString());
                    Search_map.put("Director",Info_list.get(postion).get("Director").toString());//这个 有没有问题呢？
                    Search_map.put("LeadActors",Info_list.get(postion).get("LeadActors").toString());//这个 有没有问题呢？
                    Search_map.put("Detial",Info_list.get(postion).get("Detial").toString());//这个 有没有问题呢？
                    Search_map.put("Ranking",Info_list.get(postion).get("Ranking").toString());
                    Info_list.clear();
                    Info_list.add(Search_map);
                    list_item.notifyDataSetChanged();
                    //System.out.println(Info_list);
                }
                else{
                    Toast.makeText(getActivity(), "Can not find corresponding moive：" + query, Toast.LENGTH_SHORT).show();
                }


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Info_list.clear();
                Info_list = (ArrayList<Map<String, Object>>)Info_list_backup.clone();
                return false;
            }
        });

        return root;
    }

    //直接响应
    private void HomepageResponse() {
        String url = HomepageURL;
        //String url = "http://192.168.101.101:5000/user_home";
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

                Info_list.clear();
                System.out.println(res);
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
                                String Certificate = jsonObject.getString("Certificate");
                                String Director = jsonObject.getString("Director");
                                String LeadActors = jsonObject.getString("LeadActors");
                                String Ranking    =jsonObject.getString("Ranking");
                                String detail    =jsonObject.getString("Detial");



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
                                map.put("LeadActors",LeadActors);
                                map.put("Director",Director);
                                map.put("Certificate",Certificate);
                                map.put("Detial",detail);
                                map.put("Ranking",Ranking);


                                Info_list.add(map);
                               // System.out.println(Info_list.toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Info_list_backup=(ArrayList<Map<String, Object>>)Info_list.clone();
                        //Info_list.clear();
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
                viewHolder.leadactors = convertView.findViewById(R.id.leadactors);
                viewHolder.director =convertView.findViewById(R.id.director);
                viewHolder.rank  = convertView.findViewById(R.id.rank);

                viewHolder.schedule_button =(Button) convertView.findViewById(R.id.schedule_show);
                viewHolder.Film_detail = (Button)convertView.findViewById(R.id.Film_detail);

                convertView.setTag(viewHolder);}
            else {
                viewHolder = (HomeFragment.ViewHolder) convertView.getTag();
            }
            final String FID =  Info_list.get(position).get("FilmID").toString();
            final int PosterID =  position;
            viewHolder.FilmName.setText(Info_list.get(position).get("FilmName").toString());
            viewHolder.Blurb.setText("Blurb: "+Info_list.get(position).get("Blurb").toString());
            viewHolder.leadactors.setText("Leadactors: "+Info_list.get(position).get("LeadActors").toString());
            viewHolder.director.setText("Director: "+Info_list.get(position).get("Director").toString());
            viewHolder.rank.setText("Rank: "+Info_list.get(position).get("Ranking").toString());

            viewHolder.image.setImageBitmap((Bitmap) Info_list.get(position).get("image"));
            viewHolder.schedule_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (getActivity() == null)  return;
                    Intent intent=new Intent(getActivity(), ScheduleActivity.class);
                    //用getActivity()获得当前的fragmentactivit
                    intent.putExtra("FilmID",FID);
                    //暂时 没发传递出去
                    intent.putExtra("Position",PosterID);
                    startActivity(intent);
                }
            });
            viewHolder.Film_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (getActivity() == null)  return;
                    Intent intent=new Intent(getActivity(), FilmDetailActivity.class);
                    //用getActivity()获得当前的fragmentactivit
                    intent.putExtra("FilmID",FID);
                    //暂时 没发传递出去
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

        TextView leadactors;
        TextView director;
        TextView rank;

        ImageView image;
        Button   schedule_button;
        Button   Film_detail;
    }

    private void Activity_Change_back(){
        //第一个参数是 当前场景， 第二个是跳转的目的地
        if (getActivity() == null)  return;
        Intent intent =new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }
}
