package com.Cinema.myapplication.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.Cinema.myapplication.CheckOrderActivity;
import com.Cinema.myapplication.MainActivity;
import com.Cinema.myapplication.NavActivity;
import com.Cinema.myapplication.R;
import com.Cinema.myapplication.SelectSeatActivity;
import com.Cinema.myapplication.VerificationActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.Cinema.myapplication.tool.ServerIP.LogoutURL;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    private  View view;

    private Button btn;
    private Button logout;

    private Button login_please;
    private Button register;

    private TextView NickName;




    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            if (msg.what == 0){ //log out
                try{
                    if(msg.obj.equals("right")) {
                        Toast.makeText(getActivity(), "登出成功", Toast.LENGTH_SHORT).show();
                        Intent logout = new Intent(getActivity(), NavActivity.class);
                        startActivityForResult(logout, 0);
                    }
                    else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Error!");
                        String s0 = msg.obj.toString();
                        builder.setMessage("Error. " + s0);
                        builder.setPositiveButton("Ok.", null);
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
                catch (Exception e){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Error!");
                    String s0 = e.toString();
                    builder.setMessage("Fail to log out. " + s0);
                    builder.setPositiveButton("Ok.", null);
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        }
    };




    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);

        //两种 layout 对应 登陆用户与非登陆用户
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        View require_login = inflater.inflate(R.layout.fragment_login_required, container, false);



        if(SelectSeatActivity.isLogin==0) {
            login_please = (Button) require_login.findViewById(R.id.hasaccount);
            login_please.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                }
            });

            register = (Button)require_login.findViewById(R.id.newcostumer);
            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), VerificationActivity.class);
                    startActivity(intent);

                }
            });

            return require_login;
        }

        //这里设置成跳转到查看 自己订单的 位置
        btn = (Button) root.findViewById(R.id.button_orders);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CheckOrderActivity.class);
                startActivity(intent);
            }
        });

        NickName = root.findViewById(R.id.Nickname);
        NickName.append(MainActivity.UserNickName);

        logout  =  (Button)root.findViewById(R.id.button_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), NavActivity.class);
                SelectSeatActivity.isLogin=0;
                HomepageResponse();
                startActivity(intent);
            }
        });

        return root;
    }

    private void HomepageResponse() {
        String url =LogoutURL;
        //String url = "http://192.168.101.102:5000/app_logout";
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

                final String res = response.body().string();
                if (getActivity() == null)  return;
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Message msg_logout = Message.obtain();
                        msg_logout.what = 0;
                        if(res.equals("1")) {
                            msg_logout.obj = "right";
                        }
                        else{
                            msg_logout.obj = "Error about logout.";
                        }
                        handler.sendMessage(msg_logout);
                    }
                });
            }
        });
    }


}
