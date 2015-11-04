package cn.geoff.dailyrecite;

import java.text.SimpleDateFormat;

import cn.geoff.dailyrecite.R;

import android.support.v4.app.Fragment;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class RecitePageFragment extends Fragment implements OnClickListener{
	
	private SharedPreferences reciteData;
	private MyDatabaseHelper dbHelper;
	private SQLiteDatabase wordsBookDB,dictionaryDB;
	private long timeNow;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		// 初始化按钮
		((Button) getActivity().findViewById(R.id.recite_button)).setOnClickListener(this);
		((Button) getActivity().findViewById(R.id.watch_button)).setOnClickListener(this);
		
		// 读取背诵情况数据并判断是否为前一日数据
		reciteData = getActivity().getSharedPreferences("reciteData", 0);
		timeNow = System.currentTimeMillis();
		SharedPreferences.Editor editor = getActivity().getSharedPreferences("reciteData",0).edit();		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String currentDate = dateFormat.format(new java.util.Date());
		if(!reciteData.getString("latestDate", "").equals(currentDate)){
			editor.putString("latestDate", currentDate);
			editor.putInt("todayWords", 0);
			editor.commit();
		}
		
		// 获取数据库
		dbHelper = new MyDatabaseHelper(getActivity(), "TestSQL.db", null, 1);
		wordsBookDB = dbHelper.getWritableDatabase();
		DictionaryDatabaseManager dictionaryDatabaseManager = new DictionaryDatabaseManager(getActivity());
	    dictionaryDB = dictionaryDatabaseManager.openDatabase();
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.recite_button:
			ReciteActivity.actionStart(getActivity(), "RecitePageFragment");
			break;
		case R.id.watch_button:
			SearchListActivity.actionStart(getActivity(), "RecitePageFragment", "");
			break;
		default:
			break;
		}
	}
	@Override
	public void onResume(){
		super.onResume();
		
		Cursor cursor,cursorForMeaning=null;
		ContentValues values = new ContentValues();
		String sql;
		Long wordNumber=(long) 0,totalData;
		
		// 构造ReciteList
		wordsBookDB.execSQL("delete from ReciteList");
		cursor = wordsBookDB.query("WordsBook", null, null, null, null, null, null);
		if(cursor.moveToFirst()){
			do{
				sql = "select * from BackupDictionary where "
						+ "word='" + cursor.getString(cursor.getColumnIndex("word")) + "'";
				cursorForMeaning = dictionaryDB.rawQuery(sql, null);
				if(cursorForMeaning.moveToFirst()){
					if(Long.parseLong(cursorForMeaning.getString(cursorForMeaning.getColumnIndex("nextrecite")))<timeNow){				
							values.put("word",cursor.getString(cursor.getColumnIndex("word")));
							values.put("meaning", cursorForMeaning.getString(cursorForMeaning.getColumnIndex("meaning")));
							values.put("passed", 1);
							wordsBookDB.insert("ReciteList", null, values);
							values.clear();
							wordNumber++;
					}
				}
			}while(cursor.moveToNext());
		}
		
		// 确定具体数字
		cursor = wordsBookDB.rawQuery("select count(*) from WordsBook",null);
		cursor.moveToFirst();
		totalData = cursor.getLong(0);
		
		// 设置页面上的数据
		((TextView) getActivity().findViewById(R.id.today_words_textview))
			.setText(" × "+reciteData.getInt("todayWords",-1));
		((TextView) getActivity().findViewById(R.id.total_words_textview))
		.setText("共"+ totalData +"个收录");
		((TextView) getActivity().findViewById(R.id.unknown_words_textview))
		.setText("有"+wordNumber+"个待背诵");
		
		// 关闭cursor
		cursor.close();
		if(cursorForMeaning!=null) cursorForMeaning.close();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.recite_page_fragment, container, false);
		return view;
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		wordsBookDB.close();
		dictionaryDB.close();
	}

}
