package cn.geoff.dailyrecite;

import cn.geoff.dailyrecite.R;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SearchResultActivity extends BaseActivity implements OnClickListener {
	
	private EditText keyWord;
	private MyDatabaseHelper dbHelper;
	private SQLiteDatabase wordsBookDB,dictionaryDB;
	private TextView wordText;
	private TextView wordMeaning;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_search_result);
		
		// 初始化控件
		wordText = (TextView) findViewById(R.id.word_text);
		wordMeaning = (TextView) findViewById(R.id.word_meaning);
		wordText.setText(getIntent().getStringExtra("wordText"));
		wordMeaning.setText(getIntent().getStringExtra("wordMeaning"));
		((Button) findViewById(R.id.search_button)).setOnClickListener(this);
		((Button) findViewById(R.id.add_to_wordsbook_button)).setOnClickListener(this);
		if(getIntent().getBooleanExtra("wordAdded",false)){
			((Button) findViewById(R.id.add_to_wordsbook_button)).setText("DEL");
		}
		
        keyWord = (EditText) findViewById(R.id.key_word);
        String keyWordText = getIntent().getStringExtra("wordText");
        keyWord.setText(keyWordText);
        keyWord.setSelection(keyWord.getText().length());
        
        // 文本框内容变化监听
        keyWord.addTextChangedListener(keyWordWatcher);
        
        // 创建数据库
        dbHelper = new MyDatabaseHelper(this, "TestSQL.db", null, 1);
        wordsBookDB = dbHelper.getWritableDatabase();
        DictionaryDatabaseManager dictionaryDatabaseManager = new DictionaryDatabaseManager(this);
	    dictionaryDB = dictionaryDatabaseManager.openDatabase(); 
		
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.search_button:
			
			break;
		case R.id.add_to_wordsbook_button:
			Button searchButton = (Button) findViewById(R.id.add_to_wordsbook_button);
			ContentValues values = new ContentValues();
			if(searchButton.getText().toString().equals("ADD")){				
				values.put("word",wordText.getText().toString());
				wordsBookDB.insert("WordsBook", null, values);
				values.clear();

			    dictionaryDB.execSQL("update BackupDictionary set "
			    		+ "nextrecite='" + String.valueOf(System.currentTimeMillis()) + "',"
			    		+ "gap='300000'"
			    		+ " where word='" + wordText.getText().toString() + "'");
				
				values.clear();

				searchButton.setText("DEL");
			}else{
				wordsBookDB.delete("WordsBook", "word = ?", new String[] {wordText.getText().toString()});
				dictionaryDB.execSQL("update BackupDictionary set "
						+ "nextrecite=null,"
						+ "gap=null"
			    		+ " where word='" + wordText.getText().toString() + "'");
				values.clear();
				
				searchButton.setText("ADD");
			}	
			break;
		default:
			break;
		}
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
			}else if(!keyWord.getText().toString().equals("")){
				SearchResultActivity.this.finish();
				SearchListActivity.actionStart(SearchResultActivity.this, "SearchResultActivity", keyWord.getText().toString());
			}
		} 
	};
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		wordsBookDB.close();
		dictionaryDB.close();
	}

	public static void actionStart(Context context, String wordText, String wordMeaning, Boolean wordAdded){
		Intent intent = new Intent(context, SearchResultActivity.class);
		intent.putExtra("wordText", wordText);
		intent.putExtra("wordMeaning", wordMeaning);
		intent.putExtra("wordAdded", wordAdded);
		context.startActivity(intent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search_result, menu);
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
