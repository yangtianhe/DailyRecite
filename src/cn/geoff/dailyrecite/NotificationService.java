package cn.geoff.dailyrecite;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class NotificationService extends Service {
	
	private StartTimingReceiver startTimingReceiver;
	private PauseTimingReceiver pauseTimingReceiver;
	private RestartTimingReceiver restartTimingReceiver;
	private LocalBroadcastManager localBroadcastManager;
	
	class NotificationControler {
		public void startTiming(){
			Log.d("NotificationService","startTiming");
		}
		public void pauseTiming(){
			Log.d("NotificationService","pauseTiming");
		}
		public void restartTiming(){
			Log.d("NotificationService","restartTiming");
		}
	}
	@Override
	public void onCreate(){
		super.onCreate();
		Log.d("NotificationService","onCreateService");
		
		IntentFilter startTimingFilter = new IntentFilter();
		startTimingFilter.addAction("cn.geoff.dailyrecite.START_TIMING");
		IntentFilter pauseTimingFilter = new IntentFilter();
		pauseTimingFilter.addAction("cn.geoff.dailyrecite.PAUSE_TIMING");
		IntentFilter restartTimingFilter = new IntentFilter();
		restartTimingFilter.addAction("cn.geoff.dailyrecite.RESTART_TIMING");
		startTimingReceiver = new StartTimingReceiver();
		pauseTimingReceiver = new PauseTimingReceiver();
		restartTimingReceiver = new RestartTimingReceiver();
		localBroadcastManager = LocalBroadcastManager.getInstance(this);
		localBroadcastManager.registerReceiver(startTimingReceiver, startTimingFilter);
		localBroadcastManager.registerReceiver(pauseTimingReceiver, pauseTimingFilter);
		localBroadcastManager.registerReceiver(restartTimingReceiver, restartTimingFilter);
		
		
	}
	@Override
	public void onDestroy(){
		super.onDestroy();
		Log.d("NotificationService","onDestroyService");
		localBroadcastManager.unregisterReceiver(startTimingReceiver);
		localBroadcastManager.unregisterReceiver(pauseTimingReceiver);
		localBroadcastManager.unregisterReceiver(restartTimingReceiver);
		
		// 保持后台常驻，感觉好流氓
		Intent serviceIntent = new Intent(this, NotificationService.class);
		startService(serviceIntent);
	}
	class StartTimingReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("NotificationService","startTiming");
		}
	}
	class PauseTimingReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("NotificationService","pauseTiming");
		}
	}
	class RestartTimingReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("NotificationService","restartTiming");
		}
	}
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
