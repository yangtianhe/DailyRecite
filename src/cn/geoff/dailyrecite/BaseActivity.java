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
		
		// 应用回到前台时发送暂停广播
		Intent intent = new Intent("cn.geoff.dailyrecite.PAUSE_TIMING");
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}
	
	@Override
	protected void onStop(){
		super.onStop();
		ActivityCollector.hideActivity(this);
		
		// 若整个程序被收到后台则使提醒开始
		if(ActivityCollector.noTopDeckActivity()){
			// 打开提醒服务
			Intent serviceIntent = new Intent(this, NotificationService.class);
			startService(serviceIntent);
			// 发送开始广播
			Intent intent = new Intent("cn.geoff.dailyrecite.START_TIMING");
			LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
		}
	}
	
	@Override
	protected void onRestart(){
		super.onRestart();
		ActivityCollector.fetchActivity(this);
		
		// 应用回到前台时发送暂停广播
		Intent intent = new Intent("cn.geoff.dailyrecite.PAUSE_TIMING");
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		ActivityCollector.removeActivity(this);
	}
}
