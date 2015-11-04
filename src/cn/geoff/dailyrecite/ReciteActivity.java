package cn.geoff.dailyrecite;

import cn.geoff.dailyrecite.R;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ReciteActivity extends BaseActivity implements OnClickListener{

	private TextView wordText;
	private TextView wordMeaning;
	private MyDatabaseHelper dbHelper;
	private SQLiteDatabase wordsBookDB, dictionaryDB;
	private int todayWords;
	private Cursor cursor;
	SharedPreferences reciteData;
	SharedPreferences.Editor editor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if(getIntent().getStringExtra("sourceActivity").equals("Notification")) ActivityCollector.finishAll();
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_recite);
		
		wordText = (TextView) findViewById(R.id.word_text);
		wordMeaning = (TextView) findViewById(R.id.word_meaning);
		
		((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).cancel(1);
		((Button) findViewById(R.id.covering_button)).setOnClickListener(this);
		((Button) findViewById(R.id.known_button)).setOnClickListener(this);
		((Button) findViewById(R.id.unknown_button)).setOnClickListener(this);
		
		// ��ȡ���ݿ�
		dbHelper = new MyDatabaseHelper(this, "TestSQL.db", null, 1);
		wordsBookDB = dbHelper.getWritableDatabase();
		DictionaryDatabaseManager dictionaryDatabaseManager = new DictionaryDatabaseManager(this);
	    dictionaryDB = dictionaryDatabaseManager.openDatabase(); 
		
		// ��ȡ��̬����
		todayWords = getSharedPreferences("reciteData",0).getInt("todayWords",0);
		
		// ���µ�����ʾ
		cursor = wordsBookDB.rawQuery("select * from ReciteList",null);
		if(cursor.moveToFirst()) getNextWord(true);
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.covering_button:
			((Button)findViewById(R.id.covering_button)).setVisibility(View.INVISIBLE);
			((TextView)findViewById(R.id.word_meaning)).setVisibility(View.VISIBLE);
			break;
		case R.id.known_button:
			Cursor tmpCursor;
			((Button)findViewById(R.id.covering_button)).setVisibility(View.VISIBLE);
			((TextView)findViewById(R.id.word_meaning)).setVisibility(View.INVISIBLE);
			
			tmpCursor = wordsBookDB.rawQuery("select * from ReciteList where word = '"
					+ wordText.getText() +"'", null);
			if(tmpCursor.moveToFirst()){
				if(tmpCursor.getInt(tmpCursor.getColumnIndex("passed")) == 1){
					todayWords++;
					Long nextRecite, nextGap=(long) 300000;
					Cursor backupCursor;
					backupCursor = dictionaryDB.rawQuery("select * from BackupDictionary where "
							+ "word='" + wordText.getText() + "'", null);
					if(backupCursor.moveToFirst()){
						nextRecite = System.currentTimeMillis();
						nextRecite += Long.parseLong(backupCursor.getString(backupCursor.getColumnIndex("gap")));
						switch(backupCursor.getString(backupCursor.getColumnIndex("gap"))){
							case "300000":
								nextGap = (long) 1800000;
								break;
							case "1800000":
								nextGap = (long) 43200000;
								break;
							case "43200000":
								nextGap = (long) 86400000;
								break;
							case "86400000":
								nextGap = (long) 172800000;
								break;
							case "172800000":
								nextGap = (long) 345600000;
								break;
							case "345600000":
								nextGap = (long) 604800000;
								break;
							case "604800000":
								nextGap = (long) 1296000000;
								break;
							case "1296000000":
								nextGap = (long) 2592*1000000;
								break;
							case "2592000000":
								nextGap = (long) 7776*1000000;
								break;
							case "7776000000":
								nextGap = (long) 15552*1000000;
								break;
							case "15552000000":
								nextGap = (long) 15552*1000000;
								break;
							default:
								Toast.makeText(this, "����ʱ���bug��..."
										+ backupCursor.getString(backupCursor.getColumnIndex("gap"))
										, Toast.LENGTH_SHORT).show();
						}
						dictionaryDB.execSQL("update BackupDictionary set "
								+ "nextrecite='" + String.valueOf(nextRecite) + "',"
								+ "gap='" + String.valueOf(nextGap) + "' "
								+ "where word='" + wordText.getText() + "'"
								);
					}	
					backupCursor.close();
				}
			}
			wordsBookDB.execSQL("delete from ReciteList where "
					+ "word='" + wordText.getText().toString() + "'");
			getNextWord(false);
			
			tmpCursor.close();
			break;
		case R.id.unknown_button:
			((Button)findViewById(R.id.covering_button)).setVisibility(View.VISIBLE);
			((TextView)findViewById(R.id.word_meaning)).setVisibility(View.INVISIBLE);
			
			// ����û��һ�α��������ܽ�����һ�׶�
			wordsBookDB.execSQL("update ReciteList set passed = 0 where word = '"
					+ wordText.getText() +"'");
			
			Long nextRecite;
			Cursor backupCursor;
			backupCursor = dictionaryDB.rawQuery("select * from BackupDictionary where "
					+ "word='" + wordText.getText() + "'", null);
			if(backupCursor.moveToFirst()){
				nextRecite = System.currentTimeMillis();
				nextRecite += Long.parseLong(backupCursor.getString(backupCursor.getColumnIndex("gap")));
				dictionaryDB.execSQL("update BackupDictionary set "
						+ "nextrecite='" + String.valueOf(nextRecite) + "'"
						);
			}
			
			getNextWord(false);
			
			backupCursor.close();
			
			break;
		default:
			break;
		}
	}
	private void getNextWord(boolean firstGet){	
		Cursor countCursor;
		int wordsInDatabase=-1;
		countCursor = wordsBookDB.rawQuery("select count (*) from ReciteList",null);
		if(countCursor.moveToFirst()){
			wordsInDatabase = countCursor.getInt(0);
		}
		if(!firstGet || wordsInDatabase == 0){
			if(wordsInDatabase == 0){
				new AlertDialog.Builder(ReciteActivity.this).setTitle("��Ӵ����Ŷ")//���öԻ������   
		    	.setMessage("���Ѿ����������е����ʣ�ȥ����Ӽ�����������������~")//������ʾ������  
		    	.setPositiveButton("ȷ��",new DialogInterface.OnClickListener() {//���ȷ����ť  
		    		@Override
		    		public void onClick(DialogInterface dialog, int which) {//ȷ����ť����Ӧ�¼�  
		    			finish();
		    		}})
		    	.setCancelable(false)
	    		.show(); 
			}
			if(!cursor.moveToNext()){
				cursor=wordsBookDB.rawQuery("select * from ReciteList",null);
				cursor.moveToFirst();
			}
		}
		if(wordsInDatabase != 0){
			wordText.setText(cursor.getString(cursor.getColumnIndex("word")));
			wordMeaning.setText(cursor.getString(cursor.getColumnIndex("meaning")));
		}
		
		countCursor.close();
	}
	@Override
	public void onPause(){
		super.onPause();
		SharedPreferences.Editor editor = getSharedPreferences("reciteData",0).edit();
		editor.putInt("todayWords", todayWords);
		editor.commit();
	}
	@Override
	public void onDestroy(){
		super.onDestroy();
		cursor.close();
		wordsBookDB.close();
		dictionaryDB.close();
	}
	public static void actionStart(Context context, String sourceActivity){
		Intent intent = new Intent(context, ReciteActivity.class);
		intent.putExtra("sourceActivity", sourceActivity);
		context.startActivity(intent);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.recite, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
