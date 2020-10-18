package com.Cinema.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static  com.Cinema.myapplication.tool.ServerIP.SendEmailCodeURL;
import static  com.Cinema.myapplication.tool.ServerIP.CheckEmailCodeURL;




public class VerificationActivity  extends AppCompatActivity implements View.OnClickListener{

    //private static String cookie = "";
    private ConcurrentHashMap<String, List<Cookie>> cookieStore = new ConcurrentHashMap<>();


    EditText et_email;
    EditText et_code;


    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            if (msg.what == 0){ //发送验证码
                try{
                    AlertDialog.Builder builder = new AlertDialog.Builder(VerificationActivity.this);
                }
                catch (Exception e){
                    AlertDialog.Builder builder = new AlertDialog.Builder(VerificationActivity.this);
                    builder.setTitle("Error!");
                    String s0 = e.toString();
                    builder.setMessage("Fail to send the verification code. " + s0);
                    builder.setPositiveButton("Ok.", null);
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
            else if(msg.what == 1){ //检查验证码
                try{
                    if(msg.obj.equals("right")){
                        if(NewMemberActivity.touristFlag==0){
                            Intent intent = new Intent(VerificationActivity.this, RegisterActivity.class);
                            String email  = et_email.getText().toString();
                            intent.putExtra("email",email);
                            startActivityForResult(intent, 0);
                        }
                        //若是游客 即认证完就可以 买票
                        else {
                            NewMemberActivity.tourist=1;
                            Intent intent = new Intent(VerificationActivity.this, NavActivity.class);
                            String email  = et_email.getText().toString();
                            MainActivity.UserEmail=email;
                            startActivityForResult(intent, 0);
                        }
                    }
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(VerificationActivity.this);
                        builder.setMessage(msg.obj.toString());
                        builder.setPositiveButton("Ok.", null);
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }catch (Exception e){
                    AlertDialog.Builder builder = new AlertDialog.Builder(VerificationActivity.this);
                    String s1 = e.toString();
                    builder.setMessage("Verification code wrong. " + s1);
                    builder.setPositiveButton("Ok.", null);
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifiy);

        //发送验证码
        Button send_button = findViewById(R.id.send_button);
        send_button.setOnClickListener(this);

        Button next_button = findViewById(R.id.next);
        next_button.setOnClickListener(VerificationActivity.this);

        ImageButton back_veri = findViewById(R.id.back_veri);
        back_veri.setOnClickListener(this);

        et_email = findViewById(R.id.email_address);
        et_code = findViewById(R.id.edit_text_code);
    }


    public void onClick(View v){ // 点击事件的处理方法

        if (v.getId() == R.id.send_button){ // 判断是否是send_button被点击
            new Thread(){
                public void run(){
                    try{
                        String email_number = et_email.getText().toString();
                        String URL = SendEmailCodeURL;

                        //String URL = "http://192.168.101.102:5000/SendEmailCode";

                        EmailResponse(URL,email_number);


                       // System.out.println("Send Email code debug message: " + detail_phone);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    handler.sendEmptyMessage(0);
                };
            }.start();
        }
        if (v.getId() == R.id.next){ //判断是否是register_button被点击
            new Thread(){
                public void run(){
                    String detail_code = " ";
                    try{

                        //用户的输入
                        String veri_code = et_code.getText().toString();
                        System.out.println(veri_code);

                        String URL = CheckEmailCodeURL;
                        //String URL = "http://192.168.101.102:5000/CheckEmailCode";
                        CheckResponse(URL,veri_code);

                    }catch(Exception e){
                        e.printStackTrace();
                    }
                };
            }.start();

        }
        if (v.getId() == R.id.back_veri){
            Intent back_intent = new Intent(VerificationActivity.this, NavActivity.class);
            startActivityForResult(back_intent, 0);
        }
    }




    //登陆 响应
    private void EmailResponse(String url,final String email)
    {
        //创建链接
        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(new CookieJar()
                {//这里可以做cookie传递，保存等操作
                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies)
                    {//可以做保存cookies操作
                        cookieStore.put(url.host(), cookies);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url)
                    {//加载新的cookies
                        List<Cookie> cookies = cookieStore.get(url.host());
                        return cookies != null ? cookies : new ArrayList<Cookie>();
                    }
                })
                .build();
        //创建okhttp 的表单——————>创建器 它还不是最终的 内容
        FormBody.Builder formBuilder = new FormBody.Builder();
        //添加键值对映射
        formBuilder.add("email",email );
        //创建请求 指定请求类型为post 在post内正式创建内容，并且对于request 来说直接可以通过new 他的builder来创建本体
        Request request = new Request.Builder().url(url).post(formBuilder.build()).build();
        //发送请求
        Call call = client.newCall(request);
        //创建消息队列
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
                //将重负荷的任务移除到工作线程避免主线程阻塞，
                // 当需要更新UI的时候我们需要“返回”到主线程，因为只有它才可以更新应用 UI。
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (res.equals("1")) {
                            showSuccessSweetDialog("send a verification code to your email");
                        }
                        else if(res.equals("2")){
                            showWarnSweetDialog("Email is registered please login");
                        }

                    }
                });
            }
        });

    }


    private void CheckResponse(String url,final String code)
    {
        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(new CookieJar()
                {//这里可以做cookie传递，保存等操作
                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies)
                    {//可以做保存cookies操作
                        cookieStore.put(url.host(), cookies);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url)
                    {//加载新的cookies
                        List<Cookie> cookies = cookieStore.get(url.host());
                        return cookies != null ? cookies : new ArrayList<Cookie>();
                    }
                })
                .build();
        //创建链接
      //  OkHttpClient client = new OkHttpClient().newBuilder()


        //创建okhttp 的表单——————>创建器 它还不是最终的 内容
        FormBody.Builder formBuilder = new FormBody.Builder();
        //添加键值对映射
        formBuilder.add("code",code );
        //创建请求 指定请求类型为post 在post内正式创建内容，并且对于request 来说直接可以通过new 他的builder来创建本体
        Request request = new Request.Builder().url(url).post(formBuilder.build()).build();

        //发送请求
        Call call = client.newCall(request);
        //创建消息队列
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

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message msg_code = Message.obtain();
                        msg_code.what = 1;
                        if(res.equals("1")){
                            msg_code.obj = "wrong";
                        }
                        else if(res.equals("2")){
                            msg_code.obj = "please get first";
                        }
                        else if(res.equals("0")){
                            msg_code.obj = "right";
                        }
                        else{
                            msg_code.obj = "Error about checking the code.";
                            System.out.println("check " + code);
                        }
                        handler.sendMessage(msg_code);
                    }
                });
            }
        });

    }




    public void showSuccessSweetDialog(String info)
    {
        SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(info);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    //
    public void showWarnSweetDialog(String info)
    {
        //
        SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
        //getProgressHelper()**调用materialish-progress中下面这些方法，来动态改变进度条的样式
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        //将服务器返回信息 info设置为显示内容
        pDialog.setTitleText(info);
        //显示一会消失
        pDialog.setCancelable(true);
        //显示
        pDialog.show();
    }


}
