package cn.geoff.dailyrecite;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

public class ActivityCollector {
	public static List<Activity> aliveActivities = new ArrayList<Activity>();
	public static List<Activity> topDeckActivities = new ArrayList<Activity>();
	
	public static void addActivity(Activity activity){
		aliveActivities.add(activity);
		topDeckActivities.add(activity);
	}
	
	public static void removeActivity(Activity activity){
		aliveActivities.remove(activity);
	}
	
	public static void hideActivity(Activity activity){
		topDeckActivities.remove(activity);
	}
	
	public static void fetchActivity(Activity activity){
		topDeckActivities.add(activity);
	}
	
	public static boolean noTopDeckActivity(){
		return topDeckActivities.isEmpty();
	}
	public static void finishAll(){
		for(Activity activity : aliveActivities){
			if(!activity.isFinishing()){
				activity.finish();
			}
		}
	}
}
