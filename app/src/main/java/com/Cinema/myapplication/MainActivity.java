package com.Cinema.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
//文字输入栏
//响应对话框UI
//原来是尼玛 spport。design 里面的东西



// 使用 按钮 监听器 接口
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView Title;

    private Button logButton;
    private Button signButton;

    private TextInputEditText userNameEdit;
    private TextInputEditText passWordEdit;

    private SharedPreferences sharedPreferences;
   // private CardView cardView;
    private String username;


    private Button JumpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // resource 文件夹

        logButton = (Button) findViewById(R.id.login_Button);
        signButton = (Button) findViewById(R.id.Sign_Button);

        userNameEdit = (TextInputEditText) findViewById(R.id.username_EditText);
        passWordEdit = (TextInputEditText) findViewById(R.id.password_EditText);


        logButton.setOnClickListener(this);
        signButton.setOnClickListener(this);


        //测试button
        JumpButton =  (Button) findViewById(R.id.Jump_Button);
        JumpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //第一个参数是 当前场景， 第二个是跳转的目的地
               // Intent intent =new Intent(MainActivity.this,HomePageActivity.class);
               // startActivity(intent);
                Activity_Change();

            }

        });
    }

    //由于实现了 监听接口 这里直接重写 接口方法 实现监听响应
    @Override
    public void onClick(View v)
    {
        String userName = userNameEdit.getText().toString();
        username = userName;
        String passWord = passWordEdit.getText().toString();


        if(userName.equals("")||passWord.equals(""))
        {

            //System.out.println("账号密码不能为空");
            showWarnSweetDialog("账号密码不能为空");
            return;
        }

        if(Character.isDigit(userName.charAt(0)))
        {
            //System.out.println("账号不能以数字开头");
            showWarnSweetDialog("账号不能以数字开头");
            return;
        }


        switch (v.getId())
        {
            case R.id.login_Button:

                String url = "http://192.168.101.102:5000/user";
                //String url = "  http://softwareproject.pythonanywhere.com/user";
                LoginResponse(url,userName,passWord);
                System.out.println("发送链接～");
                break;
                /*
            case R.id.Sign_Button:
                String url2 = "192.168.101.102/register";
                RegisterResponse(url2,userName,passWord);
                break;
                */


        }
    }

    //登陆 响应
    private void LoginResponse(String url,final String userName,String passWord)
    {
        //创建链接
        OkHttpClient client = new OkHttpClient();
        //创建okhttp 的表单——————>创建器 它还不是最终的 内容
        FormBody.Builder formBuilder = new FormBody.Builder();
        //添加键值对映射
        formBuilder.add("username", userName);
        formBuilder.add("password", passWord);
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
                //将重负荷的任务移除到工作线程避免主线程阻塞，
                // 当需要更新UI的时候我们需要“返回”到主线程，因为只有它才可以更新应用 UI。
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (res.equals("0"))
                        {
                            //System.out.println("无此账号,请先注册");
                            showWarnSweetDialog("无此账号,请先注册");
                        }
                        else if(res.equals("1"))
                        {
                            //System.out.println("密码不正确");
                            showWarnSweetDialog("密码不正确");
                        }
                        else//成功
                        {
                            System.out.println(res);
                            //登陆成功后切换到 主界面
                            Activity_Change();
                            /*还不是很明白这是在干啥？ 记录用户的登陆状态吗？
                            sharedPreferences = getSharedPreferences("UserIDAndPassword", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("username", userName);
                            editor.apply();

                             */
                        }

                    }
                });
            }
        });

    }

//注册响应 与登陆响应相似

    private void RegisterResponse(String url,final String userName,String passWord){
        OkHttpClient client  = new OkHttpClient();

        FormBody.Builder register_form_bulider = new FormBody.Builder();
        register_form_bulider.add("username",userName);
        register_form_bulider.add("password",passWord);

        //
        Request  register_request = new Request.Builder().url(url).post(register_form_bulider.build()).build();

        Call call = client.newCall(register_request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showWarnSweetDialog("服务器裂开了！");
                    }
                });

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String result = response.body().toString();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //返回 注册账号已存在
                        if(result.equals("0")){
                            showWarnSweetDialog("WDNMD 你号被人注册了");
                        }
                        //返回 注册成功 这样可以直接 进入主界面了
                        else{
                            showSuccessSweetDialog("注册成功，快试试登陆吧～");
                        }
                    }
                });

            }
        });


    }




    // 妈蛋一会再sweet吧 没装上插件
    public void showWarnSweetDialog(String info)
    {
        //
        SweetAlertDialog pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE);
        //getProgressHelper()**调用materialish-progress中下面这些方法，来动态改变进度条的样式
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        //将服务器返回信息 info设置为显示内容
        pDialog.setTitleText(info);
        //显示一会消失
        pDialog.setCancelable(true);
        //显示
        pDialog.show();
    }

    //注册成功的UI显示
    public void showSuccessSweetDialog(String info)
    {
        SweetAlertDialog pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.SUCCESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(info);
        pDialog.setCancelable(true);
        pDialog.show();
    }

//这里就是 各个活动之间跳转的地方
    private void Activity_Change(){

        Intent intent =new Intent(MainActivity.this,HomePageActivity.class);
        startActivity(intent);
    }




}
