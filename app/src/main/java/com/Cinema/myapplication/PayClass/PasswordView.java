package com.Cinema.myapplication.PayClass;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.Cinema.myapplication.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PasswordView extends RelativeLayout  {
	Context context;
	private String strPassword; // 输入的密码
	private TextView[] tvList; // 用数组保存6个TextView，为什么用数组？
	private LinearLayout ll_pwd; // 因为就6个输入框不会变了，用数组内存申请固定空间，比List省空间（自己认为）
	private GridView gridView; // 用GrideView布局键盘，其实并不是真正的键盘，只是模拟键盘的功能
	private ArrayList<Map<String, String>> valueList; // 有人可能有疑问，为何这里不用数组了？
														// 因为要用Adapter中适配，用数组不能往adapter中填充

	private RelativeLayout rl_bottom;
	private int currentIndex = -1; // 用于记录当前输入密码格位置

	//弹窗组件
	private PopupWindow popupWindow;

	public PasswordView(Context context) {
		this(context, null);
	}

	public PasswordView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		View view = View.inflate(context, R.layout.layout_password, null);


		valueList = new ArrayList<Map<String, String>>();
		tvList = new TextView[6];

		//整体 大的背景，整体被当作一个 按钮来用
		rl_bottom = (RelativeLayout) view.findViewById(R.id.rl_bottom);

		//输入密码的 输入栏
		ll_pwd = (LinearLayout) view.findViewById(R.id.ll_pwd);

		for (int i = 0; i < 6; i++) {
			TextView textView = new TextView(context);

			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					0, LinearLayout.LayoutParams.MATCH_PARENT, 1);


			//设计*点的出现位置和大小
			textView.setGravity(Gravity.CENTER);
			textView.setTransformationMethod(PasswordTransformationMethod.getInstance());
			textView.setTextSize(32);
			textView.setLayoutParams(layoutParams);

			//决定 把新的图像 绘制给哪个 组件
			ll_pwd.addView(textView);



			//这个是用来画出分割线的！！！！ 太sao了
			if (i != 5) {
				View view2 = new View(context);
				LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(
						1,
						LinearLayout.LayoutParams.MATCH_PARENT,
						0);
				view2.setLayoutParams(layoutParams1);
				view2.setBackgroundColor(Color.parseColor("#999999"));
				ll_pwd.addView(view2);
			}

			//把输入的 密码 记录进去
			tvList[i] = textView;
		}

		//创建 键盘显示
		View contentView = LayoutInflater.from(context).inflate(
				R.layout.layout_keyboard_demo, null);// 定义后退弹出框

		//获得 grid对象 也就是整体的 键盘大背景，里面的按键被当作子布局 分布在里面，由装饰器提供详细信息
		gridView = (GridView) contentView.findViewById(R.id.gv_keybord);// 泡泡窗口的布局

		//
		initData();  //这地方可以控制键盘的显示

		addView(view); // 必须要，不然不显示控件


		popupWindow = new PopupWindow(contentView,
				ViewGroup.LayoutParams.MATCH_PARENT,// width

				ViewGroup.LayoutParams.WRAP_CONTENT);// higth
		popupWindow.setFocusable(false);
		popupWindow.setAnimationStyle(R.style.animation);

	}

	//密码检验接口
	public interface OnPasswordInputFinish {
		void inputFinish();
	}

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		super.onWindowFocusChanged(hasWindowFocus);
		show();
	}

	public void show() {
		popupWindow.showAtLocation(rl_bottom, Gravity.BOTTOM, 0, 0); // 确定在界面中出现的位置
	}

	/**
	 * 加载模拟键盘上数据的代码
	 */
	private void initData() {
		/* 初始化按钮上应该显示的数字 */
		for (int i = 1; i < 13; i++) {
			Map<String, String> map = new HashMap<String, String>();
			if (i < 10) {
				map.put("name", String.valueOf(i));
			} else if (i == 10) {
				map.put("name", "");
			} else if (i == 11) {
				map.put("name", String.valueOf(0));
			} else if (i == 12) {
				map.put("name", "×");
			} else {
				map.put("name", "");
			}
			valueList.add(map);
		}

		gridView.setAdapter(adapter);

		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position < 11 && position != 9) { // 点击0~9按钮
					if (currentIndex >= -1 && currentIndex < 5) { // 判断输入位置————要小心数组越界
						tvList[++currentIndex].setText(valueList.get(position)
								.get("name"));
					}
				} else {
					if (position == 11) { // 点击退格键
						if (currentIndex - 1 >= -1) { // 判断是否删除完毕————要小心数组越界
							tvList[currentIndex--].setText("");
						}
					}
				}
			}
		});
	}

	// 设置监听方法，在第6位输入完成后触发
	// 这个函数的 参数是一个接口，里面有个需要有个 在其他类实现的时候所需要 重写的函数。。
	public void setOnFinishInput(final OnPasswordInputFinish pass) {
		tvList[5].addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			//这个是回退密码的时候吗
			//也就是说这个 是控制输入输出的时候的 用到的逻辑
			@Override
			public void afterTextChanged(Editable s) {
				if (s.toString().length() == 1) {
					strPassword = ""; // 每次触发都要先将strPassword置空，再重新获取，避免由于输入删除再输入造成混乱
					for (int i = 0; i < 6; i++) {
						strPassword += tvList[i].getText().toString().trim();
					}
					if (pass!=null) {
						pass.inputFinish(); // 接口中要实现的方法，完成密码输入完成后的响应逻辑						
					}
				}
			}
		});
	}

	/* 获取输入的密码 */
	public String getStrPassword() {
		return strPassword;
	}

	
	// GrideView的适配器
	BaseAdapter adapter = new BaseAdapter() {
		@Override
		public int getCount() {
			return valueList.size();
		}

		@Override
		public Object getItem(int position) {
			return valueList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressWarnings("deprecation")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				//这个  keyboard_item 组件 其实是 九宫格里面每一个 按键  它是一个子布局
				convertView = View.inflate(context, R.layout.keyboard_item, null);
				viewHolder = new ViewHolder();


				viewHolder.btnKey = (TextView) convertView
						.findViewById(R.id.btn_keys);


				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			//这里是给 键盘每个按键赋值
			//左下角 和右下角不同
			viewHolder.btnKey.setText(valueList.get(position).get("name"));
			if (position == 9||position==11) {
				//这个 Utils貌似 是个素材库 ？？
				viewHolder.btnKey.setBackgroundDrawable(Utils.getStateListDrawable(context));
				viewHolder.btnKey.setEnabled(false);
			}
			if (position == 11) {
				viewHolder.btnKey.setBackgroundDrawable(Utils.getStateListDrawable(context));
			}

			return convertView;
		}
	};
	
	
	/**
	 * 存放控件
	 */
	public final class ViewHolder {
		public TextView btnKey;
	}
}