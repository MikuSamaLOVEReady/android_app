package com.Cinema.myapplication.PayClass;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.Cinema.myapplication.R;

//会话框组件？

public class CustomDialog extends DialogFragment {

    //在这里创建了一个 键盘输入 查看
    private PasswordView pwdView;
    private String name="123456";
    private String ko;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //初始化。
        Dialog dialog = new Dialog(getActivity(), R.style.Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);

        /**设置宽度为屏宽、靠近屏幕底部*/
        Window window = dialog.getWindow();
        /**设置背景透明*/
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //WindowManager 窗口管理器？是干球的？--》》预设整体大背景属性
        WindowManager.LayoutParams wlp = window.getAttributes();

        //让弹出的窗口在 最下方
        wlp.gravity = Gravity.BOTTOM;
        dialog.setContentView(R.layout.dialog_normal);

        //长宽高 都设置ok
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.height = (int) getResources().getDimension(R.dimen.custom_hei);

        //为这个窗口设置以上属性
        window.setAttributes(wlp);


        //初始化整个窗口
        initViews(dialog);
        return dialog;
    }

    //这个是判断 输入密码是否正确
    private void initViews(Dialog dialog) {
        pwdView = dialog.findViewById(R.id.pwd_view);
        pwdView.setOnFinishInput(new PasswordView.OnPasswordInputFinish() {
            @Override
            public void inputFinish() {
                //获得 用户输入的密码
                ko = pwdView.getStrPassword();
                Log.e("111ok", "密码是" + name);

                Log.e("111ok", "密码是" + ko);

                //ko    是读取出的 用户输入的。
                //name  是数据库中 用户的密码。
                if (ko.equals(name)){
                    Intent intent =new Intent("com.Cinema.myapplication.ACTION_START");
                    //Log.e("fugang", "密码是" + pwdView.getStrPassword());
                    startActivity(intent);
                }

            }
        });


    }
}
