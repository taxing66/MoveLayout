package com.movelayoutdemo;

import com.zjl.customview.MoveLayout;
import com.zjl.customview.MoveLayout.OnMovingListener;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * @author ZhengJingle
 */
public class MainActivity extends Activity {
	MoveLayout ml_main;
	ImageView iv_active;
	Button button1;
	TextView tv,tv_x,tv_y;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ml_main=(MoveLayout)findViewById(R.id.ml_main);
		
		iv_active=(ImageView)findViewById(R.id.iv_active);
		tv=(TextView)findViewById(R.id.textView1);
		tv_x=(TextView)findViewById(R.id.tv_x);
		tv_y=(TextView)findViewById(R.id.tv_y);
		button1=(Button)findViewById(R.id.button1);
		button1.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO �Զ����ɵķ������
				ml_main.moveToPrecent(1.0);//�ƶ����ٷֱ�λ��
			}
			
		});
		
		/* 
		 * �����ƶ�views
		 * 
		 * �����ı����ƶ�view��ʵ�ָ��ӵ��ƶ������������ƶ����Ͳ�Ҫ���������á�
		 * ����������OnMovingListener�е�onMoving(double movingPrecent)���Լ�д����ʵ�֡�
		 */
		ml_main.setPassiveMoveItem(button1, 50, 500);
		ml_main.setPassiveMoveItem(tv_x, 500, 0);
		ml_main.setPassiveMoveItem(tv_y, 0, 800);
		
		//�����ƶ�view
		ml_main.setActiveMoveItem(iv_active, 0, 0, false);
		
		//�ƶ�����
		ml_main.setActiveMoveArea(false, 100, 400, 200, 800);
		
		//�ƶ��м���
		ml_main.setOnMovingListener(new OnMovingListener(){

			@Override
			public void onMoving(double movingPrecent) {
				// TODO �Զ����ɵķ������
				tv.setText(movingPrecent*100+"%");
			}
			
		});
		
	}
	
}
