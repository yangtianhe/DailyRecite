package cn.geoff.dailyrecite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

public class BaseActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		ActivityCollector.addActivity(this);
		
		// Ӧ�ûص�ǰ̨ʱ������ͣ�㲥
		Intent intent = new Intent("cn.geoff.dailyrecite.PAUSE_TIMING");
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}
	
	@Override
	protected void onStop(){
		super.onStop();
		ActivityCollector.hideActivity(this);
		
		// �����������յ���̨��ʹ���ѿ�ʼ
		if(ActivityCollector.noTopDeckActivity()){
			// �����ѷ���
			Intent serviceIntent = new Intent(this, NotificationService.class);
			startService(serviceIntent);
			// ���Ϳ�ʼ�㲥
			Intent intent = new Intent("cn.geoff.dailyrecite.START_TIMING");
			LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
		}
	}
	
	@Override
	protected void onRestart(){
		super.onRestart();
		ActivityCollector.fetchActivity(this);
		
		// Ӧ�ûص�ǰ̨ʱ������ͣ�㲥
		Intent intent = new Intent("cn.geoff.dailyrecite.PAUSE_TIMING");
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		ActivityCollector.removeActivity(this);
	}
}
