package com.Cinema.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class NewMemberActivity   extends AppCompatActivity  implements View.OnClickListener {


    private Button login;
    private Button register;
    private Button justbuy;

    //0 表示不是游客
    public static int tourist =0;

    //0表示 不是游客认证 界面
    public static int touristFlag =0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        login =findViewById(R.id.Login);
        login.setOnClickListener(this);
        register=findViewById(R.id.createNew);
        register.setOnClickListener(this);
        justbuy=findViewById(R.id.justBuy);
        justbuy.setOnClickListener(this);



        //发送验证码

    }

    public void onClick(View v) {
        if (v.getId() == R.id.Login) { // 判断是否是send_button被点击
            Intent intent=new Intent(NewMemberActivity.this, MainActivity.class);
            touristFlag=0;
            startActivity(intent);
        }
        if (v.getId() == R.id.createNew) { // 判断是否是send_button被点击
            Intent intent=new Intent(NewMemberActivity.this, VerificationActivity.class);
            touristFlag=0;
            startActivity(intent);

        }
        // 游客购买
        if (v.getId() == R.id.justBuy) { // 游客购买
            Intent intent=new Intent(NewMemberActivity.this, VerificationActivity.class);
            //表示游客登陆 只买一次票
            //tourist=1;
            touristFlag=1;
            startActivity(intent);

        }
    }

}
