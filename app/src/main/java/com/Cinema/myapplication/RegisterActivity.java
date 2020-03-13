package com.Cinema.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

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
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView Title;

    private Button submit;
    private Button back;


    private TextInputEditText userNameEdit;
    private TextInputEditText passWordEdit;
    private TextInputEditText password_check_EditText;

    private String username;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register); // resource 文件夹

        //按钮绑定
        submit = (Button) findViewById(R.id.user_submit);
        back = (Button) findViewById(R.id.Go_back);
        //用户名
        userNameEdit = (TextInputEditText) findViewById(R.id.username_EditText);
        //密码
        passWordEdit = (TextInputEditText) findViewById(R.id.password_EditText);
        //检查密码
        password_check_EditText =  (TextInputEditText) findViewById(R.id.password_check_EditText);



        submit.setOnClickListener(this);
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Activity_Change_back();
            }
        } );

    }

    //由于实现了 监听接口 这里直接重写 接口方法 实现监听响应
    @Override
    public void onClick(View v)
    {
        String userName = userNameEdit.getText().toString();
        username = userName;
        String passWord = passWordEdit.getText().toString();

        String passWord_check = password_check_EditText.getText().toString();

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

        if(!passWord_check.equals(passWord))
        {
            //System.out.println("账号不能以数字开头");
            showWarnSweetDialog("两次输入的密码不一致");
            return;
        }


        switch (v.getId())
        {
            case R.id.user_submit:
                String url2 = "http://192.168.101.102:5000/app_register1";
                RegisterResponse(url2,userName,passWord);
                break;
        }
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
            public void onFailure( Call call,  IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showWarnSweetDialog("服务器裂开了！");
                    }
                });

            }

            @Override
            public void onResponse( Call call,  Response response) throws IOException {
                final String result = response.body().toString();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //返回 注册账号已存在
                        if(result.equals("1")){
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

    //注册成功的UI显示
    public void showSuccessSweetDialog(String info)
    {
        SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(info);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    //这里就是 各个活动之间跳转的地方
    private void Activity_Change_back(){

        //第一个参数是 当前场景， 第二个是跳转的目的地
        Intent intent =new Intent(RegisterActivity.this,MainActivity.class);
        startActivity(intent);

    }




}
