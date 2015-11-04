package cn.geoff.dailyrecite;

import java.util.ArrayList;
import java.util.List;

import cn.geoff.dailyrecite.R;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

public class SearchListActivity extends BaseActivity {

	private List<SingleWord> wordResultList = new ArrayList<SingleWord>();
	private EditText keyWord;
	private static String keyWordText;
	private MyDatabaseHelper dbHelper;
	private SQLiteDatabase wordsBookDB, dictionaryDB;
	WordAdapter adapter;
	ListView listView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_search_list);
		
		// 打开数据库
		dbHelper = new MyDatabaseHelper(this, "TestSQL.db", null, 1);
		wordsBookDB = dbHelper.getWritableDatabase();
	    DictionaryDatabaseManager dictionaryDatabaseManager = new DictionaryDatabaseManager(this);
	    dictionaryDB = dictionaryDatabaseManager.openDatabase(); 
		
        listView = (ListView) findViewById(R.id.word_result_list);
        listView.setOnItemClickListener(new OnItemClickListener(){
        	@Override
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        		SingleWord singleWord = wordResultList.get(position);
        		SearchResultActivity.actionStart(SearchListActivity.this,
        				singleWord.getWord(), singleWord.getMeaning(), singleWord.getAdded());	
        	}
        });
		
        // 文本框内容变化监听
		keyWord = (EditText) findViewById(R.id.key_word);
		keyWord.addTextChangedListener(keyWordWatcher);
		 
        
        keyWordText = getIntent().getStringExtra("keyWord");
        keyWord.setText(keyWordText);
        keyWord.setSelection(keyWord.getText().length());
        
	}
	
	public TextWatcher keyWordWatcher = new TextWatcher() {  
		
		@Override  
		public void onTextChanged(CharSequence s, int start, int before, int count) {    
			// Log.d("KEY_WORD","-1-onTextChanged-->"  + keyWord.getText().toString() + "<--");  
		}  
		  
		@Override  
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {  
			// Log.d("KEY_WORD","-2-beforeTextChanged-->" + keyWord.getText().toString() + "<--");    
		}  
		  
		@Override  
		public void afterTextChanged(Editable s) {  	
			int lines = keyWord.getLineCount();
			if(lines>1){
				String str = s.toString();
				int cursorStart = keyWord.getSelectionStart();
				int cursorEnd = keyWord.getSelectionEnd();
				if(cursorStart == cursorEnd && cursorStart <str.length() && cursorStart >0){
					str = str.substring(0,cursorStart-1) + str.substring(cursorStart);
				}else{
					str = str.substring(0, s.length()-1);
				}
				keyWord.setText(str);
				keyWord.setSelection(keyWord.getText().length());
			}else{
				keyWordText = keyWord.getText().toString();
				initResultList();
				adapter = new WordAdapter(SearchListActivity.this, R.layout.word_item, wordResultList);
		        listView.setAdapter(adapter);
			}
		}  
	};
	
	@Override
	public void onStart(){
		super.onStart();
		
		keyWord.setText(keyWordText);
		keyWord.setSelection(keyWord.getText().length());
		
		initResultList();
		adapter = new WordAdapter(SearchListActivity.this, R.layout.word_item, wordResultList);
        listView.setAdapter(adapter);
	}
	
	private void initResultList(){
		Cursor cursor, cursorForMeaning = null;
		SingleWord singleWord;
		String sql;
		wordResultList.clear();
		if(getIntent().getStringExtra("sourceActivity").equals("RecitePageFragment")){
			cursor = wordsBookDB.query("WordsBook", null, "word like '%" + keyWordText + "%'", 
					null, null, null, null, "20");
			if(cursor.moveToFirst()){
				do{
					sql = "select * from BackupDictionary " +
				    		"where word like '" + cursor.getString(cursor.getColumnIndex("word")) + "' ";
					cursorForMeaning = dictionaryDB.rawQuery(sql, null);
					if(cursorForMeaning.moveToFirst()){
						singleWord = new SingleWord(
								cursor.getString(cursor.getColumnIndex("word")),
								cursorForMeaning.getString(cursorForMeaning.getColumnIndex("meaning")),
								true);
						wordResultList.add(singleWord);
					}
				}while(cursor.moveToNext());
			}
			cursor.close();
			if(cursorForMeaning != null) cursorForMeaning.close();
		}else{   
			sql = "select * from BackupDictionary " +
		    		"where word like '%" + keyWordText + "%' limit 20";
		    cursor =  dictionaryDB.rawQuery(sql, null);
		    if(cursor.moveToFirst()){
				do{
					singleWord = new SingleWord(
							cursor.getString(cursor.getColumnIndex("word")),
							cursor.getString(cursor.getColumnIndex("meaning")),
							cursor.getString(cursor.getColumnIndex("nextrecite")) != null);
					wordResultList.add(singleWord);
				}while(cursor.moveToNext());
			}
			cursor.close();
		}
		
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		wordsBookDB.close();
		dictionaryDB.close();
	}
	
	public static void actionStart(Context context, String sourceActivity, String keyWord){
		Intent intent = new Intent(context, SearchListActivity.class);
		intent.putExtra("sourceActivity", sourceActivity);
		intent.putExtra("keyWord", keyWord);
		keyWordText = keyWord;
		context.startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search_list, menu);
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
